/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.sql.compiler

import com.rawlabs.compiler.{
  ErrorMessage,
  ErrorPosition,
  ErrorRange,
  RawBinary,
  RawBool,
  RawByte,
  RawDate,
  RawDecimal,
  RawDouble,
  RawFloat,
  RawInt,
  RawInterval,
  RawLong,
  RawNull,
  RawShort,
  RawString,
  RawTime,
  RawTimestamp,
  RawValue
}
import com.rawlabs.sql.compiler.antlr4.{
  ParseProgramResult,
  SqlBaseNode,
  SqlParamDefaultCommentNode,
  SqlParamTypeCommentNode,
  SqlProgramNode,
  SqlStatementNode
}
import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.util.Position
import org.postgresql.util.PSQLException

import java.sql.{Connection, ResultSet, ResultSetMetaData}
import java.time.LocalTime
import scala.collection.mutable

class NamedParametersPreparedStatementException(val errors: List[ErrorMessage]) extends Exception

abstract class NamedParametersPreparedStatementExecutionResult
case class NamedParametersPreparedStatementResultSet(rs: ResultSet)
    extends NamedParametersPreparedStatementExecutionResult
case class NamedParametersPreparedStatementUpdate(count: Int) extends NamedParametersPreparedStatementExecutionResult

// A postgres type, described by its JDBC enum type + the regular postgres type name.
// The postgres type is a string among the many types that exist in postgres.
case class PostgresType(jdbcType: Int, nullable: Boolean, typeName: String)

case class PostgresColumn(name: String, tipe: PostgresType)

case class PostgresRowType(columns: Seq[PostgresColumn])

// Named parameters are mapped to a list of positions in the original `code` where they appear
private case class ParamLocation(
    node: SqlBaseNode, // original node (this permits to underline it)
    jdbcIndex: Int // index in the source code to use in JDBC `set` APIs
)

/**
 * This class is wrapping the PreparedStatement class from the JDBC API.
 * It parses the SQL code and extract the named parameters, infer their types from how they're used.
 * It also provides methods to set the parameters by name and execute the query.
 *
 * The parameter `parsedTree` implies parsing errors have been potential caught and reported upfront,
 * but we can't assume that tree is error-free. Indeed, for
 *
 * @param conn the JDBC connection
 * @param parsedTree the parsed SQL code
 * @param scopes the query scopes
 */
class NamedParametersPreparedStatement(
    conn: Connection,
    parsedTree: ParseProgramResult,
    scopes: Set[String] = Set.empty
) extends StrictLogging {

  {
    // create the `scopes` table if it doesn't exist. And delete its rows (in case it existed already).
    val stmt = conn.prepareStatement("""
      |CREATE TEMPORARY TABLE IF NOT EXISTS scopes (token VARCHAR NOT NULL);
      |DELETE FROM scopes;
      |""".stripMargin)
    // an error is reported as a CompilerServiceException
    try {
      stmt.execute()
    } finally {
      stmt.close()
    }
    // insert the query scopes
    val insert = conn.prepareStatement("INSERT INTO scopes (token) VALUES (?)")
    // an error is reported as a CompilerServiceException
    try {
      // all scopes are added as batches
      for (scope <- scopes) {
        insert.setString(1, scope)
        insert.addBatch()
      }
      // execute once all inserts
      insert.executeBatch()
    } finally {
      insert.close()
    }
  }

  private val treePositions = parsedTree.positions

  // If this crashes on None.get, that's a bug.
  private def getStart(node: SqlBaseNode): Position = treePositions.getStart(node).get

  private def getStartOffset(node: SqlBaseNode): Int = getStart(node).optOffset.get

  private def getFinish(node: SqlBaseNode): Position = treePositions.getFinish(node).get

  private def getFinishOffset(node: SqlBaseNode): Int = getFinish(node).optOffset.get

  private val kiamaSource = getStart(parsedTree.tree).source

  // Start by extracting the subset of the source code that corresponds to the first SQL statement
  // (in case there are several). That permits to ignore comments after the statement's semicolon.
  // If two SQL statements are parsed (two SELECT), the ANTL4 parser would have thrown an error. Here we're
  // sure there's one statement, followed by potential statements that are just comments.
  private val code = {
    val content = kiamaSource.content
    val SqlProgramNode(stmt: SqlStatementNode) = parsedTree.tree
    content.take(getFinishOffset(stmt))
  }

  // We collect parameters in the order they appear in the source code (by order of appearance).
  // Having a data structure of arguments in source order is needed because JDBC parameters are set by their index.
  private val orderedParameterUses = {
    val allOccurrences = for (p <- parsedTree.params.valuesIterator; o <- p.occurrences) yield o
    allOccurrences.toVector.sortBy(getStartOffset)
  }

  // This is to map a parameter to its uses in the code. It is possible that a parameter isn't used (e.g.
  // if it's declared using @type and others, but never used in the source code). In this case, it's mapped
  // to an empty list.
  private val paramUses = {
    parsedTree.params.mapValues(p =>
      for (use <- p.occurrences)
        yield ParamLocation(node = use, jdbcIndex = orderedParameterUses.indexWhere(_ eq use) + 1)
    )
  }

  // A data structure for the full query info: parameters that are mapped to their inferred types,
  // and query output type (the query type)
  protected case class QueryParamInfo(pgType: PostgresType, comment: Option[String], default: Option[RawValue]) {
    def withDefaultValue(v: RawValue): QueryParamInfo = QueryParamInfo(pgType, comment, Some(v))
    def withDescription(description: String): QueryParamInfo = QueryParamInfo(pgType, Some(description), default)
  }

  case class QueryInfo(parameters: Map[String, QueryParamInfo], outputType: PostgresRowType)

  // Now we'll populate this parameter Map with info, or errors.
  private val declaredTypeInfo = mutable.Map.empty[String, Either[List[ErrorMessage], QueryParamInfo]]

  // This Map contains only entries that have a '@type'. If the type can be parsed, the value is
  // a Right(PostgresType), else a Left(ErrorMessage).
  private val userSpecifiedTypes =
    for ((name, info) <- parsedTree.params; tipe <- info.tipe) yield name -> {
      SqlTypesUtils
        .jdbcFromParameterType(tipe) // make sure we can map the user given type to a JDBC enum
        .right
        .map(PostgresType(_, nullable = true, tipe)) // wrap it as a PostgresType (arguments are nullable)
        .left
        .map(highlightError(info.nodes.collect { case n: SqlParamTypeCommentNode => n })) // report any error to @type
    }

  // Populate the `declaredTypeInfo` Map with `@type` info.
  userSpecifiedTypes.foreach {
    case (p, diagnostic) =>
      declaredTypeInfo(p) = diagnostic.right.map(t => QueryParamInfo(t, None, None)).left.map(List(_))
  }

  private case class DefaultValue(value: RawValue, tipe: PostgresType)

  // This is a Map containing only entries that have a '@default'. If there's a Right entry in `userSpecifiedTypes`
  // (a proper `@type` was specified), we use it: force a cast, otherwise not. If the expression runs fine, we get
  // a Right(DefaultValue) that potentially confirms the expression matches with the `@type`, else a Left(ErrorMessage).
  private val userSpecifiedDefaultValues = {
    val quickStatement = conn.createStatement()
    for ((paramName, info) <- parsedTree.params; sqlCode <- info.default) yield paramName -> {
      val maybeSpec = declaredTypeInfo.get(paramName).flatMap(_.toOption)
      val selectValue = {
        val cast = maybeSpec.map(_.pgType.typeName).map("::" + _).getOrElse("")
        s"SELECT ($sqlCode)" + cast
      }
      val defaultNodes = info.nodes.collect { case n: SqlParamDefaultCommentNode => n }
      val asErrorMessage = highlightError(defaultNodes) // helper to underline
      try {
        val v = quickStatement.execute(selectValue)
        assert(v, "default value execute didn't return a ResultSet")
        val rs = quickStatement.getResultSet
        try {
          val metadata = rs.getMetaData // that could throw an error we need to make user-visible
          val jdbcType = metadata.getColumnType(1) // if it throws, that propagates to the top as a JIRA crash report
          val pgType = metadata.getColumnTypeName(1) // if it throws, that propagates to the top as a JIRA crash report
          val defaultValueType =
            SqlTypesUtils.validateParamType(PostgresType(jdbcType, nullable = true, typeName = pgType))
          defaultValueType
            .map(getDefaultValue(rs, _))
            .left
            .map(asErrorMessage)
        } finally {
          rs.close()
        }
      } catch {
        case ex: PSQLException => asErrorString(ex) match {
            case Some(message) =>
              // user-level error, report when underlining
              Left(asErrorMessage(message))
            case None =>
              // other error: a compiler failure
              throw ex
          }
      }
    }
  }

  userSpecifiedDefaultValues.foreach {
    case (p, dvDiagnostic) =>
      val newState = declaredTypeInfo.get(p) match {
        case Some(diagnostic) => diagnostic match {
            case Left(errors) =>
              // if diagnostic has errors, merge the potential new ones
              Left(dvDiagnostic.fold(errors :+ _, _ => errors))
            case Right(t) =>
              // if diagnostic was ok, still give precedence to errors. If there was an error, make it
              // the diagnostic, if not update the default value.
              dvDiagnostic.fold((error => Left(List(error))), (dv => Right(t.withDefaultValue(dv.value))))
          }
        case None => dvDiagnostic.right.map(d => QueryParamInfo(d.tipe, None, Some(d.value))).left.map(List(_))
      }
      declaredTypeInfo.put(p, newState)
  }

  // We have the single query code in `code` (with named parameters). Internally we need to replace
  // the named parameters with question marks, and keep track of the mapping between the
  // parameter names and the question marks.
  //
  // However we want to remap offset of errors returned by Postgres, to an offset in the user code.
  // For that, we can use `fromPgsqlOffset`.

  // a table of offset -> shift, meant to remap a postgres error offset to the original user's code. Used
  // by fromPgsqlOffset, computed when generating the JDBC plain code.
  private val offsets = mutable.ArrayBuffer.empty[(Int, Int)] // no shifting up to the next argument

  private def fromPgsqlOffset(pgOffset: Int): Int = {
    // pgOffset starts at 1, but we return a user offset starting at 0 (it's an index in the string).
    val zOffset = pgOffset - 1
    // `offsets` contains (offset, shift), where `offset` starts at zero.
    // The meaning of [(offset1, shift1), (offset2, shift2), ...] is "from offset1, shift values by 'shift1', etc.
    // if `zOffset` is greater or equal to `offset1` and smaller than `offset2`, its value should be shifted by `shift1`
    // before being returned to the user. We find that entry by picking the farthest entry in the list which offset is
    // smaller than zOffset. Not finding any means zOffset is small, it falls into the beginning of the query
    // where no argument had been seen, therefore shift is zero.
    val shift = offsets.collect { case (offset, shift) if zOffset >= offset => shift }.lastOption.getOrElse(0)
    zOffset + shift // that's the user's offset
  }

  // plainCode is the JDBC query. It the same as the original query, except that argument names are replaced by '?'.
  // That said, we don't simply replace argument names by a question mark:
  // * if the user entered a valid @type, or a valid @default, we use this type to force the code to use it.
  //   We replace the argument by '?::<type>' to cast it.
  // * if the user entered an invalid @type _and_ and invalid @default, or didn't enter any of these two,
  //   we don't cast and will rely on JDBC.
  // * if one of the two `@type` or `@default` is invalid, we use the valid one to cast with '?::<type>' since that
  //   permits to detect errors.
  private val plainCode = {
    var shift = 0
    val jdbc = StringBuilder.newBuilder
    var index = 0
    var pgCodeSize = 0
    orderedParameterUses.zipWithIndex.foreach {
      case (use, idx) =>
        // for each argument 'use', we copy/paste the code chunk up to its location. It's a portion of code
        // that doesn't contain any argument.
        val offset = getStartOffset(use)
        val chunk = code.substring(index, offset) // argument-free code chunk
        jdbc.append(chunk) // copy/paste it untouched
        pgCodeSize += chunk.length // postgres code length increases by the code chunk's size
        // now we need to replace the argument (by '?' or '?::<type>')
        // do we have declared type for that parameter
        val specifiedType = userSpecifiedTypes.get(use.name).flatMap(_.toOption)
        val specifiedValueType = userSpecifiedDefaultValues.get(use.name).flatMap(_.toOption.map(_.tipe))
        val knownType = (specifiedType ++ specifiedValueType).map(_.typeName).headOption
        val argToken = knownType match {
          // if so, force the type by wrapping the question mark in a cast (validate it first)
          case Some(typeName) => s"(?::$typeName)"
          case None => "?"
        }
        jdbc.append(argToken) // that's the '?' replacing the argument
        // We saw ? is eventually sent to postgres as $X. The arg size is different, and grows with the number of digits.
        // Internally, postgres starts numbering parameters at 1, so we need to shift idx by 1.
        val pgArgSize = argToken.length + (idx + 1).toString.length
        pgCodeSize += pgArgSize
        // shift that would permit to compute the user offset from the postgres offset
        shift += (1 + use.name.length) /* size of ':' + variable name */ - pgArgSize
        offsets.append((pgCodeSize, shift)) // starting at `pgCodeSize`, offsets should be shifted by `shift`
        index = getFinishOffset(use)
    }
    // paste the end of the code
    jdbc.append(code.substring(index))
    jdbc.toString()
  }

  logger.debug(plainCode)

  private val stmt =
    try {
      conn.prepareStatement(plainCode)
    } catch {
      case ex: PSQLException =>
        // Report reasonable errors to the user. Otherwise throw and it will be a compiler failure.
        asUserError(ex) match {
          case Some(e) => throw e
          case None => throw ex
        }
    }

  // When we're collecting metadata info, an SQLException can highlight a problem with the statement
  // (syntax error, semantic error).
  private val paramMetadata =
    try {
      stmt.getParameterMetaData // throws SQLException in case of problem
    } catch {
      case ex: PSQLException =>
        // Report reasonable errors to the user. Otherwise throw and it will be a compiler failure.
        asUserError(ex) match {
          case Some(e) => throw e
          case None => throw ex
        }
    }

  // At that moment, paramMetaData could be collected, it means there are neither syntax nor *type errors*.
  // - Arguments that had a valid `@type` and/or `@default` were cast to that type. Since the SQL code is valid, they
  //   are correct
  // - Those that don't have any user provided information, get now some type inference from JDBC.
  // Each type is validated against what we support. That can lead to Left values if they're not supported.
  // There's also an attempt to merge types when an argument is used in several places.
  private val inferredTypeInfo = paramUses.collect {
    // we investigate those which:
    // - are used (locations.nonEmpty)
    // - and weren't inferred as a Right(type), either because not seen as @type/@default, or they're Left.
    case (p, locations) if locations.nonEmpty && !declaredTypeInfo.get(p).exists(_.isRight) =>
      // one can infer from the usage. All locations the parameter is used are considered.
      val allOptions = locations.map { location =>
        val idx = location.jdbcIndex
        val jdbc = paramMetadata.getParameterType(idx)
        val pgTypeName = paramMetadata.getParameterTypeName(idx)
        SqlTypesUtils
          .validateParamType(PostgresType(jdbc, nullable = true, pgTypeName))
          .left
          .map(highlightError(location.node))
      }
      val errors = allOptions.collect { case Left(error) => error }
      val diagnostic =
        if (errors.nonEmpty) {
          Left(errors.toList)
        } else {
          val typeOptions = allOptions.collect { case Right(t) => t }
          SqlTypesUtils
            .mergePgTypes(typeOptions)
            .left
            .map(errorMsg => List(highlightError(locations.map(_.node))(errorMsg)))
        }
      p -> diagnostic
  }

  // Now populate the `declaredType` info with what was found through JDBC inference.
  inferredTypeInfo.foreach {
    case (p, dvDiagnostic) =>
      val newState = declaredTypeInfo.get(p) match {
        case Some(diagnostic) =>
          // if diagnostic has errors, merge the potential new ones, if it was correct
          // don't touch it, since inference just confirmed what we know
          diagnostic.left.map(errors => dvDiagnostic.fold(errors ++ _, _ => errors))
        case None => dvDiagnostic.right.map(d => QueryParamInfo(d, None, None))
      }
      declaredTypeInfo.put(p, newState)
  }

  // Finally, update all this with the description found in `@param`.
  for ((p, info) <- parsedTree.params; description <- info.description) declaredTypeInfo.get(p) match {
    case Some(entry) =>
      // If `declaredTypeInfo` has data for that parameter, if Right, update the description, else leave it untouched.
      entry.right.foreach(info => declaredTypeInfo(p) = Right(info.withDescription(description)))
    case None =>
      // If `declaredTypeInfo` doesn't have data, report an error that its type couldn't be guessed.
      declaredTypeInfo(p) = Left(List(highlightError(info.nodes)("cannot guess parameter type")))
  }

  // The query output type is obtained using JDBC's `metadata`
  private val queryOutputType: Either[String, PostgresRowType] = {
    val metadata = stmt.getMetaData // SQLException at that point would be a bug.
    if (metadata == null) {
      // an UPDATE/INSERT. We'll return a single row with a count column
      Right(
        PostgresRowType(
          Seq(PostgresColumn("update_count", PostgresType(java.sql.Types.INTEGER, nullable = false, "integer")))
        )
      )
    } else {
      val columns = (1 to metadata.getColumnCount).map { i =>
        val name = metadata.getColumnName(i)
        val tipe = metadata.getColumnType(i)
        val typeName = metadata.getColumnTypeName(i)
        val nullability = metadata.isNullable(i)
        val nullable = {
          // report nullable if it's advertised as such, or unknown (when unknown it can be nullable).
          nullability == ResultSetMetaData.columnNullable || nullability == ResultSetMetaData.columnNullableUnknown
        }
        PostgresColumn(name, PostgresType(tipe, nullable, typeName))
      }
      Right(PostgresRowType(columns))
    }
  }

  // helper for 'hover'
  def parameterInfo(p: String): Either[List[ErrorMessage], QueryParamInfo] = {
    declaredTypeInfo(p)
  }

  // This is returning QueryInfo, which contains the inferred types of parameters + output type.
  def queryMetadata: Either[List[ErrorMessage], QueryInfo] = {
    // infer the type
    val errors = declaredTypeInfo.values.collect { case Left(error) => error }.toList
    if (errors.nonEmpty) Left(errors.flatten)
    else {
      val typeInfo = declaredTypeInfo.mapValues(_.right.get).toMap
      queryOutputType.left
        .map(highlightError(parsedTree.tree))
        .left
        .map(List(_))
        .right
        .map(outputType => QueryInfo(typeInfo, outputType))
    }
  }

  private def errorRange(node: SqlBaseNode) = {
    val start = getStart(node)
    val end = getFinish(node)
    val startPos = ErrorPosition(start.line, start.column)
    val endPos = ErrorPosition(end.line, end.column)
    ErrorRange(startPos, endPos)
  }

  private def errorRange(position: Position, position1: Position) = {
    def errorPosition(p: Position): ErrorPosition = ErrorPosition(p.line, p.column)
    ErrorRange(errorPosition(position), errorPosition(position1))
  }

  def executeWith(
      parameters: Seq[(String, RawValue)]
  ): Either[String, NamedParametersPreparedStatementExecutionResult] = {
    val mandatoryParameters = {
      for (
        (name, diagnostic) <- declaredTypeInfo
        if diagnostic.exists(info => info.default.isEmpty)
      ) yield name
    }
      .to[mutable.Set]
    parameters.foreach {
      case (p, v) =>
        setParam(p, v)
        mandatoryParameters.remove(p)
    }
    if (mandatoryParameters.nonEmpty) Left(s"no value was specified for ${mandatoryParameters.mkString(", ")}")
    else
      try {
        val isResultSet = stmt.execute()
        if (isResultSet) Right(NamedParametersPreparedStatementResultSet(stmt.getResultSet))
        else {
          // successful execution of an UPDATE/INSERT (empty queries also get there)
          Right(
            NamedParametersPreparedStatementUpdate(stmt.getUpdateCount)
          )
        }
      } catch {
        // We'd catch here user-visible PSQL runtime errors (e.g. schema not found, table not found,
        // wrong credentials) hit _at runtime_ because the user FDW schema.table maps to a datasource
        // that has ... changed (e.g. the database doesn't have the table anymore, a remote service
        // account has expired (RD-10895)). We report these errors to the user.
        case ex: PSQLException =>
          // These are still considered validation errors.
          val error = ex.getMessage // it has the code, the message, hint, etc.
          Left(error)
      }
  }

  def close(): Unit = stmt.close()

  def setParam(paramName: String, value: RawValue): Unit = {
    try {
      value match {
        case RawNull() => setNull(paramName)
        case RawByte(v) => setByte(paramName, v)
        case RawShort(v) => setShort(paramName, v)
        case RawInt(v) => setInt(paramName, v)
        case RawLong(v) => setLong(paramName, v)
        case RawFloat(v) => setFloat(paramName, v)
        case RawDouble(v) => setDouble(paramName, v)
        case RawBool(v) => setBoolean(paramName, v)
        case RawString(v) => setString(paramName, v)
        case RawDecimal(v) => setBigDecimal(paramName, v)
        case RawDate(v) => setDate(paramName, java.sql.Date.valueOf(v))
        case RawTime(v) => setTime(paramName, new java.sql.Time(v.toNanoOfDay / 1000000))
        case RawTimestamp(v) => setTimestamp(paramName, java.sql.Timestamp.valueOf(v))
        case RawInterval(years, months, weeks, days, hours, minutes, seconds, millis) => ???
        case RawBinary(v) => setBytes(paramName, v)
        case _ => ???
      }
    } catch {
      case e: NoSuchElementException => logger.warn("Unknown parameter: " + e.getMessage)
    }
  }

  for ((p, status) <- userSpecifiedDefaultValues; defaultValue <- status) {
    setParam(p, defaultValue.value)
  }

  private def setNull(parameter: String): Unit = {
    for (location <- paramUses(parameter)) stmt.setNull(location.jdbcIndex, java.sql.Types.NULL)
  }

  private def setByte(parameter: String, x: Byte): Unit = {
    for (location <- paramUses(parameter)) stmt.setByte(location.jdbcIndex, x)
  }

  private def setShort(parameter: String, x: Short): Unit = {
    for (location <- paramUses(parameter)) stmt.setShort(location.jdbcIndex, x)
  }

  private def setInt(parameter: String, x: Int): Unit = {
    for (location <- paramUses(parameter)) stmt.setInt(location.jdbcIndex, x)
  }

  private def setLong(parameter: String, x: Long): Unit = {
    for (location <- paramUses(parameter)) stmt.setLong(location.jdbcIndex, x)
  }

  private def setFloat(parameter: String, x: Float): Unit = {
    for (location <- paramUses(parameter)) stmt.setFloat(location.jdbcIndex, x)
  }

  private def setDouble(parameter: String, x: Double): Unit = {
    for (location <- paramUses(parameter)) stmt.setDouble(location.jdbcIndex, x)
  }

  private def setBigDecimal(parameter: String, x: java.math.BigDecimal): Unit = {
    for (location <- paramUses(parameter)) stmt.setBigDecimal(location.jdbcIndex, x)
  }

  private def setString(parameter: String, x: String): Unit = {
    for (location <- paramUses(parameter)) stmt.setString(location.jdbcIndex, x)
  }

  private def setBoolean(parameter: String, x: Boolean): Unit = {
    for (location <- paramUses(parameter)) stmt.setBoolean(location.jdbcIndex, x)
  }

  private def setBytes(parameter: String, x: Array[Byte]): Unit = {
    for (location <- paramUses(parameter)) stmt.setBytes(location.jdbcIndex, x)
  }

  private def setDate(parameter: String, x: java.sql.Date): Unit = {
    for (location <- paramUses(parameter)) stmt.setDate(location.jdbcIndex, x)
  }

  private def setTime(parameter: String, x: java.sql.Time): Unit = {
    for (location <- paramUses(parameter)) stmt.setTime(location.jdbcIndex, x)
  }

  private def setTimestamp(parameter: String, x: java.sql.Timestamp): Unit = {
    for (location <- paramUses(parameter)) stmt.setTimestamp(location.jdbcIndex, x)
  }

  private def asErrorMessage(ex: PSQLException): Option[ErrorMessage] = {
    asErrorString(ex).map { message =>
      val maybeError = Option(ex.getServerErrorMessage)
      val codeLocation = for (psqlError <- maybeError) yield {
        // .getPosition returns 0 when no position is provided in the Exception
        if (psqlError.getPosition > 0) {
          val position = psqlError.getPosition
          val codeOffset = fromPgsqlOffset(position);
          val start = kiamaSource.offsetToPosition(codeOffset);
          val end = kiamaSource.offsetToPosition(codeOffset + 1)
          errorRange(start, end)
        } else {
          errorRange(parsedTree.tree) // no position, use the whole query
        }
      }
      val range = codeLocation.getOrElse(errorRange(parsedTree.tree))
      ErrorMessage(message, List(range), "sqlError")
    }
  }

  private def asUserError(ex: PSQLException): Option[NamedParametersPreparedStatementException] = {
    for (sqlError <- asErrorMessage(ex)) yield {
      // Since paramMetadata failed to read, parameters that weren't already investigated
      // can be flagged with "cannot guess parameter type"
      val wasInvestigated = declaredTypeInfo.keySet.contains(_)
      val unspecifiedParamTypes = parsedTree.params
        .filterNot { case (p, _) => wasInvestigated(p) }
        .mapValues(info => Left(List(highlightError(info.nodes ++ info.occurrences)("cannot guess parameter type"))))
        .toMap
      val paramErrors =
        (unspecifiedParamTypes ++ declaredTypeInfo).values.collect { case Left(errors) => errors }.flatten.toList
      if (ex.getSQLState == "42P18") {
        // undefined parameter. Ignore the postgres error since we've flagged the parameters with the same error.
        new NamedParametersPreparedStatementException(paramErrors)
      } else new NamedParametersPreparedStatementException(sqlError +: paramErrors)
    }
  }

  private def asErrorString(ex: PSQLException): Option[String] = {
    if (Set("42", "22", "0A", "3F").exists(ex.getSQLState.startsWith)) {
      // syntax error / semantic error
      val psqlError = Option(ex.getServerErrorMessage) // getServerErrorMessage can be null!
      val error = psqlError.map(_.getMessage).getOrElse(ex.getMessage)
      val hint = {
        if (ex.getSQLState == "42P01")
          // undefined table, add our hint
          Some("Did you forget to add credentials?")
        else for (info <- psqlError; hint <- Option(info.getHint)) yield hint
      }
      val message = hint.map(h => s"$error: $h").getOrElse(error)
      Some(message)
    } else {
      logger.warn(s"Unsupported PSQL error state ${ex.getSQLState}")
      None
    }
  }

  private def getDefaultValue(rs: ResultSet, postgresType: PostgresType): DefaultValue = {
    assert(rs.next(), "rs.next() was false")
    val value = postgresType.jdbcType match {
      case java.sql.Types.SMALLINT =>
        val sqlValue = rs.getShort(1)
        if (rs.wasNull()) RawNull() else RawShort(sqlValue)
      case java.sql.Types.INTEGER =>
        val sqlValue = rs.getInt(1)
        if (rs.wasNull()) RawNull() else RawInt(sqlValue)
      case java.sql.Types.BIGINT =>
        val sqlValue = rs.getLong(1)
        if (rs.wasNull()) RawNull() else RawLong(sqlValue)
      case java.sql.Types.DECIMAL =>
        val sqlValue = rs.getBigDecimal(1)
        if (rs.wasNull()) RawNull() else RawDecimal(sqlValue)
      case java.sql.Types.FLOAT =>
        val sqlValue = rs.getFloat(1)
        if (rs.wasNull()) RawNull() else RawFloat(sqlValue)
      case java.sql.Types.DOUBLE =>
        val sqlValue = rs.getDouble(1)
        if (rs.wasNull()) RawNull() else RawDouble(sqlValue)
      case java.sql.Types.DATE =>
        val sqlValue = rs.getDate(1)
        if (rs.wasNull()) RawNull() else RawDate(sqlValue.toLocalDate)
      case java.sql.Types.TIME =>
        val sqlValue = rs.getTime(1)
        if (rs.wasNull()) RawNull() else RawTime(LocalTime.ofNanoOfDay(sqlValue.getTime * 1000000))
      case java.sql.Types.TIMESTAMP =>
        val sqlValue = rs.getTimestamp(1)
        if (rs.wasNull()) RawNull() else RawTimestamp(sqlValue.toLocalDateTime)
      case java.sql.Types.BOOLEAN =>
        val sqlValue = rs.getBoolean(1)
        if (rs.wasNull()) RawNull() else RawBool(sqlValue)
      case java.sql.Types.VARCHAR =>
        val sqlValue = rs.getString(1)
        if (rs.wasNull()) RawNull() else RawString(sqlValue)
    }
    DefaultValue(value, postgresType)
  }

  private def highlightError(nodes: Seq[SqlBaseNode]): String => ErrorMessage = { msg: String =>
    val positions = for (node <- nodes) yield errorRange(node)
    ErrorMessage(msg, positions.toList, ErrorCode.SqlErrorCode)
  }

  private def highlightError(node: SqlBaseNode): String => ErrorMessage = highlightError(Seq(node))

}
