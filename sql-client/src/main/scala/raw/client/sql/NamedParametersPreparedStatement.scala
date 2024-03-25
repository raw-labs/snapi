/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.client.sql

import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.util.Position
import raw.client.api._
import raw.client.sql.antlr4._

import java.sql.{Connection, ResultSet, ResultSetMetaData, SQLException}
import scala.collection.mutable

/* This class is wrapping the PreparedStatement class from the JDBC API.
 * It parses the SQL code and extract the named parameters, infer their types from how they're used.
 * It also provides methods to set the parameters by name, then execute the query.
 */

// A postgres type, described by its JDBC enum type + the regular postgres type name.
// The postgres type is a string among the many types that exist in postgres.
case class PostgresType(jdbcType: Int, nullable: Boolean, typeName: String)

case class PostgresColumn(name: String, tipe: PostgresType)

case class PostgresRowType(columns: Seq[PostgresColumn])

// Named parameters are mapped to a list of positions in the original `code` where they appear
private case class ParamLocation(node: SqlBaseNode, jdbcIndex: Int, start: Position, end: Position)

/* The parameter `parsedTree` implies parsing errors have been potential caught and reported
   upfront, but we can't assume that tree is error-free. Indeed, for
 */
class NamedParametersPreparedStatement(conn: Connection, parsedTree: ParseProgramResult) extends StrictLogging {

  // the whole code

  // extract the subset of the source code which corresponds to the first statement.
  private val code = {
    val content = parsedTree.positions.getStart(parsedTree.tree).map(_.source.content)
    val SqlProgramNode(stmt: SqlStatementNode) = parsedTree.tree
    val strippedCode = for (source <- content; end <- parsedTree.positions.getFinish(stmt); offset <- end.optOffset)
      yield source.take(offset)
    strippedCode.get // if ever `strippedCode` is None, that throws and it would file a JIRA crash report
  }

  private def underline(nodes: Seq[SqlBaseNode]): String => ErrorMessage = { msg: String =>
    val positions = for (
      node <- nodes;
      start <- parsedTree.positions.getStart(node);
      startPos = ErrorPosition(start.line, start.column);
      end <- parsedTree.positions.getFinish(node);
      endPos = ErrorPosition(end.line, end.column)
    ) yield ErrorRange(startPos, endPos)
    ErrorMessage(msg, positions.toList, ErrorCode.SqlErrorCode)
  }

  private def underline(node: SqlBaseNode): String => ErrorMessage = underline(Seq(node))

  private val userSpecifiedTypes: Map[String, Either[ErrorMessage, PostgresType]] = {
    val allInfo =
      for ((name, info) <- parsedTree.params; tipe <- info.tipe) yield name -> SqlTypesUtils
        .jdbcFromParameterType(tipe) // make sure we can find the JDBC enum
        .right
        .map(PostgresType(_, nullable = true, tipe)) // make it a postgres type
        .left
        .map(underline(info.nodes.collect { case n: SqlParamTypeCommentNode => n }))
    allInfo.toMap
  }

  private case class DefaultValue(value: RawValue, tipe: PostgresType)

  private val userSpecifiedDefaultValues: Map[String, Either[ErrorMessage, DefaultValue]] = {
    val quickStatement = conn.createStatement() // if it throws, that propagates to the top as a JIRA crash report
    try {
      val allInfo =
        for ((paramName, info) <- parsedTree.params; sqlCode <- info.default) yield paramName -> {
          val selectValue = s"SELECT ($sqlCode)"
          try {
            val v = quickStatement.execute(selectValue)
            assert(v, "default value execute didn't return a ResultSet")
            val rs = quickStatement.getResultSet
            try {
              val metadata = rs.getMetaData
              val jdbcType =
                metadata.getColumnType(1) // if it throws, that propagates to the top as a JIRA crash report
              val pgType =
                metadata.getColumnTypeName(1) // if it throws, that propagates to the top as a JIRA crash report
              val inferredType =
                SqlTypesUtils.validateParamType(PostgresType(jdbcType, nullable = true, typeName = pgType))
              val checkedType = for (
                tipe <- inferredType.toOption;
                maybeSpec <- userSpecifiedTypes.get(paramName);
                userSpecifiedType <- maybeSpec.toOption
              ) yield {
                if (userSpecifiedType == tipe) inferredType
                else {
                  val msg = s"${tipe.typeName} doesn't match specified type ${userSpecifiedType.typeName}"
                  Left(msg)
                }
              }
              val mergedType = checkedType.getOrElse(inferredType)
              val nodes = info.nodes.collect { case n: SqlParamDefaultCommentNode => n }
              mergedType.map(t => DefaultValue(asRawValue(rs, t), t)).left.map(underline(nodes))
            } finally {
              rs.close()
            }
          } catch {
            case ex: SQLException => Left(
                underline(info.nodes)(ex.getMessage)
              ) // TODO: That's where we would catch user visible errors (and others)
          }
        }
      allInfo.toMap
    } finally {
      quickStatement.close()
    }
  }

  private val declaredTypeInfo: Map[String, Either[ErrorMessage, QueryParamInfo]] =
    userSpecifiedTypes.mapValues(_.right.map(QueryParamInfo(_, None))) ++
      userSpecifiedDefaultValues.mapValues(_.right.map(ok => QueryParamInfo(ok.tipe, Some(ok.value))))

  private def asRawValue(rs: ResultSet, postgresType: PostgresType): RawValue = {
    assert(rs.next(), "rs.next() was false")
    val value = postgresType.jdbcType match {
      case java.sql.Types.SMALLINT => RawShort(rs.getShort(1))
      case java.sql.Types.INTEGER => RawInt(rs.getInt(1))
      case java.sql.Types.BIGINT => RawLong(rs.getLong(1))
      case java.sql.Types.DECIMAL => RawDecimal(rs.getBigDecimal(1))
      case java.sql.Types.FLOAT => RawFloat(rs.getFloat(1))
      case java.sql.Types.DOUBLE => RawDouble(rs.getDouble(1))
      case java.sql.Types.DATE => RawDate(rs.getDate(1).toLocalDate)
      case java.sql.Types.TIME => RawTime(rs.getTime(1).toLocalTime)
      case java.sql.Types.TIMESTAMP => RawTimestamp(rs.getTimestamp(1).toLocalDateTime)
      case java.sql.Types.BOOLEAN => RawBool(rs.getBoolean(1))
      case java.sql.Types.VARCHAR => RawString(rs.getString(1))
    }
    if (rs.wasNull()) {
      RawNull()
    } else {
      value
    }
  }

  // parameters as they appear in the source code, by order of appearance. Maintaining the order
  // in the source code is important since JDBC parameters are set by their index.
  private val orderedParameterUses = {
    val allOccurrences = for (p <- parsedTree.params.valuesIterator; o <- p.occurrences) yield o
    allOccurrences.toVector.sortBy(use => parsedTree.positions.getStart(use).map(_.optOffset))
  }

  // map from parameter names to their (potentially multiple) use locations.
  private val paramLocations = {
    parsedTree.params.mapValues(p =>
      for (
        use <- p.occurrences;
        start <- parsedTree.positions.getStart(use);
        end <- parsedTree.positions.getFinish(use)
      ) yield ParamLocation(node = use, jdbcIndex = orderedParameterUses.indexWhere(_ eq use) + 1, start, end)
    )
  }

  /* We have the single query code in `code` (with named parameters). Internally we need to replace
   * the named parameters with question marks, and keep track of the mapping between the
   * parameter names and the question marks.
   */

  // Table of offsets meant to remap a postgres error offset to the user's code
  private val offsets = mutable.ArrayBuffer.empty[(Int, Int)]
  private val plainCode = {
    var cumulatingOffset = 0
    val buffer = StringBuilder.newBuilder
    var index = 0
    for (use <- orderedParameterUses) {
      // they are ordered
      for (start <- parsedTree.positions.getStart(use); offset <- start.optOffset) {
        offsets.append((offset, cumulatingOffset))
        buffer.append(code.substring(index, offset))
        // do we have declared type for that parameter
        val token = declaredTypeInfo.get(use.name) match {
          // if so, force the type by wrapping the question mark in a cast (validate it first)
          case Some(entry) => entry.right.map(paramInfo => s"(?::${paramInfo.pgType.typeName})").getOrElse("?")
          // else not
          case _ => "?"
        }
        cumulatingOffset += (use.name.length + 1) /* :country */ - (token.length + 1) /* ? => $1 */
        buffer.append(token)
      }
      for (start <- parsedTree.positions.getFinish(use); offset <- start.optOffset) {
        index = offset
      }
    }
    // finish
    buffer.append(code.substring(index))
    offsets.append((index, cumulatingOffset))
    buffer.toString()
  }

  logger.debug(plainCode)
  paramLocations.foreach(p => logger.debug(p.toString))

  // A data structure for the full query info: parameters that are mapped to their inferred types,
  // and query output type (the query type)
  case class QueryParamInfo(pgType: PostgresType, default: Option[RawValue])

  case class QueryInfo(parameters: Map[String, QueryParamInfo], outputType: PostgresRowType)

  private val stmt = conn.prepareStatement(plainCode) // throws SQLException in case of problem
  private val metadata = stmt.getParameterMetaData // throws SQLException in case of problem

  // compute the ErrorRange of a specific node (to underline it)
  def parameterInfo(p: String): Either[List[ErrorMessage], QueryParamInfo] = {
    val paramTypes = collectParamInfo()
    paramTypes(p)
  }

  // This is returning QueryInfo, which contains the inferred types of parameters + output type.
  // In case of SQL error, or unsupported JDBC type or..., it returns a list of error messages.
  def queryMetadata: Either[List[ErrorMessage], QueryInfo] = {
    // infer the type
    val typesStatus = collectParamInfo()
    val errors = typesStatus.values.collect { case Left(error) => error }.toList
    if (errors.nonEmpty) Left(errors.flatten)
    else {
      Right(QueryInfo(typesStatus.mapValues(_.right.get), queryOutputType))
    }
  }

  private def collectParamInfo(): Map[String, Either[List[ErrorMessage], QueryParamInfo]] = {
    // parameter types are either declared with @type or inferred from their usage in the code
    paramLocations.map {
      case (p, locations) =>
        val isNullable = true
        val parserInfo = parsedTree.params(p)
        // in case of error
        val tStatus = declaredTypeInfo.get(p) match {
          case Some(diagnostic) => diagnostic.left.map(List(_))
          case None =>
            // type isn't specified but one can infer from the usage.
            // all locations the parameter is used are considered
            val allOptions: Seq[Either[ErrorMessage, QueryParamInfo]] = locations.map { location =>
              val idx = location.jdbcIndex
              val jdbc = metadata.getParameterType(idx)
              val pgTypeName = metadata.getParameterTypeName(idx)
              SqlTypesUtils
                .validateParamType(PostgresType(jdbc, isNullable, pgTypeName))
                .right
                .map(QueryParamInfo(_, None))
                .left
                .map(underline(location.node))
            }
            if (allOptions.isEmpty) Left(List(underline(parserInfo.nodes)("cannot guess parameter type")))
            else {
              val errors = allOptions.collect { case Left(error) => error }
              if (errors.nonEmpty) {
                Left(errors.toList)
              } else {
                val typeOptions = allOptions.collect { case Right(t) => t.pgType }
                SqlTypesUtils
                  .mergePgTypes(typeOptions)
                  .right
                  .map(QueryParamInfo(_, None)) // wrap it into a single param type
                  .left
                  .map(message =>
                    List(
                      ErrorMessage(
                        message,
                        locations
                          .map(location => ErrorRange(errorPosition(location.start), errorPosition(location.end)))
                          .toList,
                        ErrorCode.SqlErrorCode
                      )
                    )
                  )
              }
            }
        }
        p -> tStatus
    }.toMap
  }

  private def queryOutputType: PostgresRowType = {
    val metadata = stmt.getMetaData
    val columns = (1 to metadata.getColumnCount).map { i =>
      val name = metadata.getColumnName(i)
      val tipe = metadata.getColumnType(i)
      val typeName = metadata.getColumnTypeName(i)
      val nullability = metadata.isNullable(i)
      val nullable = {
        // report nullable if it's advertised as such, or unknown
        nullability == ResultSetMetaData.columnNullable || nullability == ResultSetMetaData.columnNullableUnknown
      }
      PostgresColumn(name, PostgresType(tipe, nullable, typeName))
    }
    PostgresRowType(columns)
  }

  def executeQuery(): ResultSet = stmt.executeQuery()

  def executeWith(parameters: Seq[(String, RawValue)]): Either[String, ResultSet] = {
    val mandatoryParameters = {
      for (
        (name, diagnostic) <- collectParamInfo()
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
    else Right(stmt.executeQuery())
  }

  def close(): Unit = stmt.close()

  private def errorPosition(p: Position): ErrorPosition = ErrorPosition(p.line, p.column)

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
        case RawTime(v) => setTime(paramName, java.sql.Time.valueOf(v))
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
    for (location <- paramLocations(parameter)) stmt.setNull(location.jdbcIndex, java.sql.Types.NULL)
  }

  private def setByte(parameter: String, x: Byte): Unit = {
    for (location <- paramLocations(parameter)) stmt.setByte(location.jdbcIndex, x)
  }

  private def setShort(parameter: String, x: Short): Unit = {
    for (location <- paramLocations(parameter)) stmt.setShort(location.jdbcIndex, x)
  }

  private def setInt(parameter: String, x: Int): Unit = {
    for (location <- paramLocations(parameter)) stmt.setInt(location.jdbcIndex, x)
  }

  private def setLong(parameter: String, x: Long): Unit = {
    for (location <- paramLocations(parameter)) stmt.setLong(location.jdbcIndex, x)
  }

  private def setFloat(parameter: String, x: Float): Unit = {
    for (location <- paramLocations(parameter)) stmt.setFloat(location.jdbcIndex, x)
  }

  private def setDouble(parameter: String, x: Double): Unit = {
    for (location <- paramLocations(parameter)) stmt.setDouble(location.jdbcIndex, x)
  }

  private def setBigDecimal(parameter: String, x: java.math.BigDecimal): Unit = {
    for (location <- paramLocations(parameter)) stmt.setBigDecimal(location.jdbcIndex, x)
  }

  private def setString(parameter: String, x: String): Unit = {
    for (location <- paramLocations(parameter)) stmt.setString(location.jdbcIndex, x)
  }

  private def setBoolean(parameter: String, x: Boolean): Unit = {
    for (location <- paramLocations(parameter)) stmt.setBoolean(location.jdbcIndex, x)
  }

  private def setBytes(parameter: String, x: Array[Byte]): Unit = {
    for (location <- paramLocations(parameter)) stmt.setBytes(location.jdbcIndex, x)
  }

  private def setDate(parameter: String, x: java.sql.Date): Unit = {
    for (location <- paramLocations(parameter)) stmt.setDate(location.jdbcIndex, x)
  }

  private def setTime(parameter: String, x: java.sql.Time): Unit = {
    for (location <- paramLocations(parameter)) stmt.setTime(location.jdbcIndex, x)
  }

  private def setTimestamp(parameter: String, x: java.sql.Timestamp): Unit = {
    for (location <- paramLocations(parameter)) stmt.setTimestamp(location.jdbcIndex, x)
  }

}
