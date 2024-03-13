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
import raw.client.sql.antlr4.{ParseProgramResult, SqlBaseNode, SqlProgramNode, SqlStatementNode}
import raw.utils.RawSettings

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

/* The parameter `parsedTree` implies parsing errors have been potential caught and reported
   upfront, but we can't assume that tree is error-free. Indeed, for
 */
class NamedParametersPreparedStatement(conn: Connection, parsedTree: ParseProgramResult)(
  implicit rawSettings: RawSettings
) extends StrictLogging {

  private val sourceCode: String = parsedTree.positions.getStart(parsedTree.tree).get.source.content

  /* We have the query code in `code` (with named parameters). Internally we need to replace
   * the named parameters with question marks, and keep track of the mapping between the
   * parameter names and the question marks.
   */

  private val code = {
    val SqlProgramNode(stmt: SqlStatementNode) = parsedTree.tree
    val strippedCode = for (end <- parsedTree.positions.getFinish(stmt); offset <- end.optOffset)
      yield sourceCode.take(offset)
    strippedCode match {
      case Some(subset) => subset
      case None =>
        logger.warn("Couldn't get the statement offset!")
        sourceCode
    }
  }

  // Each named parameter is mapped to a list of offsets in the original `code` where it appears (starting at the colon)
  private case class ParamLocation(jdbcIndex: Int, start: Position, end: Position)

  // parameters as they appear in the source code, by order of appearance.
  private val orderedParameterUses = {
    val allOccurrences = for (p <- parsedTree.params.valuesIterator; o <- p.occurrences) yield o
    allOccurrences.toVector.sortBy(use => parsedTree.positions.getStart(use).map(_.optOffset))
  }

  // map from parameters to their locations
  private val paramLocations = {
    parsedTree.params.mapValues(p =>
      for (
        occ <- p.occurrences;
        start <- parsedTree.positions.getStart(occ);
        end <- parsedTree.positions.getFinish(occ)
      ) yield ParamLocation(jdbcIndex = orderedParameterUses.indexWhere(_ eq occ) + 1, start, end)
    )
  }

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
        val token = parsedTree.params.get(use.name).flatMap(_.tipe) match {
          // if so, force the type
          case Some(t) => s"(?::$t)"
          // else not
          case _ => "?"
        }
        cumulatingOffset += (use.name.length + 1) /* :country */ - (token.length + 1) /* ? => $1 */
        // todo $110
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

  // A data structure for the full query info: parameters that are mapped to their inferred types,
  // and query output type (the query type)
  case class QueryInfo(parameters: Map[String, PostgresType], outputType: PostgresRowType)

  private val stmt = conn.prepareStatement(plainCode)
  logger.debug(plainCode)
  paramLocations.foreach(p => logger.debug(p.toString))

  //  assert(paramLocations2.keySet == paramLocations.keySet)
  //  for ((key, locations) <- paramLocations2 ; loc <- locations) assert(paramLocations(key).contains(loc), loc.toString)

  private def errorRange(node: SqlBaseNode): Option[ErrorRange] = {
    for (
      start <- parsedTree.positions.getStart(node); startPos = ErrorPosition(start.line, start.column);
      end <- parsedTree.positions.getFinish(node);
      endPos = ErrorPosition(end.line, end.column)
    ) yield ErrorRange(startPos, endPos)
  }

  def parameterType(p: String): Either[List[ErrorMessage], PostgresType] = queryMetadata.right.map { info =>
    info.parameters(p)
  }

  private val metadata = stmt.getParameterMetaData // throws SQLException in case of problem

  // This is returning QueryInfo, which contains the inferred types of parameters + output type.
  // In case of SQL error, or unsupported JDBC type or..., it returns a list of error messages.
  def queryMetadata: Either[List[ErrorMessage], QueryInfo] = {
    // infer the type
    validateParameterTypes().right.map(typeMap =>
      QueryInfo(typeMap, queryOutputType)
    )
  }

  private def validateParameterTypes(): Either[List[ErrorMessage], Map[String, PostgresType]] =
    try {
      // parameter types are either declared with @type or inferred from their usage in the code
      val typesStatus: Map[String, Either[ErrorMessage, PostgresType]] = paramLocations.map {
        case (p, locations) =>
          val tStatus =
            if (locations.isEmpty && parsedTree.params(p).tipe.isEmpty) {
              // the parameter is declared (@param) but has no explicit type, and is never used
              Left(
                ErrorMessage(
                  "cannot guess parameter type",
                  parsedTree.params(p).nodes.flatMap(errorRange).toList,
                  ErrorCode.SqlErrorCode
                )
              )
            } else {
              val nullable = parsedTree.params(p).default.isDefined
              parsedTree.params(p).tipe match {
                case Some(tipe) => SqlTypesUtils.pgMap.get(tipe) match {
                  case Some(jdbc) => Right(
                    PostgresType(
                      jdbc,
                      nullable,
                      tipe
                    )
                  )
                  case None => Left(
                    ErrorMessage(
                      "unsupported type " + tipe,
                      parsedTree.params(p).nodes.flatMap(errorRange).toList,
                      ErrorCode.SqlErrorCode
                    )
                  )
                }
                case None =>
                  // For each parameter, we infer the type from the locations where it's used
                  val options: Seq[Either[ErrorMessage, PostgresType]] = locations.map { location =>
                    val t = SqlTypesUtils.validateParamType(
                      PostgresType(
                        metadata.getParameterType(location.jdbcIndex),
                        nullable,
                        metadata.getParameterTypeName(location.jdbcIndex)
                      )
                    )
                    t.left.map(
                      ErrorMessage(_, parsedTree.params(p).nodes.flatMap(errorRange).toList, ErrorCode.SqlErrorCode)
                    )
                  }
                  assert(options.nonEmpty)
                  options.collectFirst { case Left(error) => error } match {
                    case Some(error) => Left(error)
                    case None =>
                      val typeOptions = options.collect { case Right(t) => t }
                      SqlTypesUtils
                        .mergeRawTypes(typeOptions)
                        .left
                        .map(message =>
                          ErrorMessage(
                            message,
                            locations
                              .map(location =>
                                ErrorRange(errorPosition(location.start), errorPosition(location.end))
                              )
                              .toList,
                            ErrorCode.SqlErrorCode
                          )
                        )
                  }
              }
            }
          p -> tStatus
      }.toMap
      val errors = typesStatus.values.collect { case Left(error) => error }.toList
      if (errors.nonEmpty) Left(errors)
      else {
        Right(typesStatus.mapValues(_.right.get))
      }
    } catch {
      case e: SQLException => {
        Left(ErrorHandling.asErrorMessage(code, e))
      }
    }

  /*        SqlTypesUtils.rawTypeFromJdbc(tipe, typeName).right.map {
    case t: RawAnyType => t
    case t: RawType => t.cloneWithFlags(nullable, false)
  }*/

  private def queryOutputType: PostgresRowType = {
    val metadata = stmt.getMetaData
    val columns = (1 to metadata.getColumnCount).map { i =>
      val name = metadata.getColumnName(i)
      val tipe = metadata.getColumnType(i)
      val typeName = metadata.getColumnTypeName(i)
      val nullability = metadata.isNullable(i)
      val nullable =
        nullability == ResultSetMetaData.columnNullable || nullability == ResultSetMetaData.columnNullableUnknown
      PostgresColumn(name, PostgresType(tipe, nullable, typeName))
    }
    PostgresRowType(columns)
  }

  def executeQuery(): ResultSet = stmt.executeQuery()

  def close(): Unit = stmt.close()

  private def errorPosition(p: Position): ErrorPosition = ErrorPosition(p.line, p.column)

  // Parameters are considered nullable (null when not specified). The prepared statement is initialized
  // with nulls on all parameters. When a parameter is set, the null is replaced with the value.
  for (locations <- paramLocations.valuesIterator; location <- locations) {
    stmt.setNull(location.jdbcIndex, java.sql.Types.NULL)
  }

  def setNull(parameter: String): Unit = {
    for (location <- paramLocations(parameter)) stmt.setNull(location.jdbcIndex, java.sql.Types.NULL)
  }

  def setByte(parameter: String, x: Byte): Unit = {
    for (location <- paramLocations(parameter)) stmt.setByte(location.jdbcIndex, x)
  }

  def setShort(parameter: String, x: Short): Unit = {
    for (location <- paramLocations(parameter)) stmt.setShort(location.jdbcIndex, x)
  }

  def setInt(parameter: String, x: Int): Unit = {
    for (location <- paramLocations(parameter)) stmt.setInt(location.jdbcIndex, x)
  }

  def setLong(parameter: String, x: Long): Unit = {
    for (location <- paramLocations(parameter)) stmt.setLong(location.jdbcIndex, x)
  }

  def setFloat(parameter: String, x: Float): Unit = {
    for (location <- paramLocations(parameter)) stmt.setFloat(location.jdbcIndex, x)
  }

  def setDouble(parameter: String, x: Double): Unit = {
    for (location <- paramLocations(parameter)) stmt.setDouble(location.jdbcIndex, x)
  }

  def setBigDecimal(parameter: String, x: java.math.BigDecimal): Unit = {
    for (location <- paramLocations(parameter)) stmt.setBigDecimal(location.jdbcIndex, x)
  }

  def setString(parameter: String, x: String): Unit = {
    for (location <- paramLocations(parameter)) stmt.setString(location.jdbcIndex, x)
  }

  def setBoolean(parameter: String, x: Boolean): Unit = {
    for (location <- paramLocations(parameter)) stmt.setBoolean(location.jdbcIndex, x)
  }

  def setBytes(parameter: String, x: Array[Byte]): Unit = {
    for (location <- paramLocations(parameter)) stmt.setBytes(location.jdbcIndex, x)
  }

  def setDate(parameter: String, x: java.sql.Date): Unit = {
    for (location <- paramLocations(parameter)) stmt.setDate(location.jdbcIndex, x)
  }

  def setTime(parameter: String, x: java.sql.Time): Unit = {
    for (location <- paramLocations(parameter)) stmt.setTime(location.jdbcIndex, x)
  }

  def setTimestamp(parameter: String, x: java.sql.Timestamp): Unit = {
    for (location <- paramLocations(parameter)) stmt.setTimestamp(location.jdbcIndex, x)
  }

}
