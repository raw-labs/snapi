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

import java.sql.{Connection, ResultSet, ResultSetMetaData}
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
  private val sourceCode: String = parsedTree.positions.getStart(parsedTree.tree).get.source.content

  // extract the subset of the source code which corresponds to the first statement.
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

  // parameters as they appear in the source code, by order of appearance. Maintaining the order
  // in the source code is important since JDBC parameters are set by their index.
  private val orderedParameterUses = {
    val allOccurrences = for (p <- parsedTree.params.valuesIterator; o <- p.occurrences) yield o
    allOccurrences.toVector.sortBy(use => parsedTree.positions.getStart(use).map(_.optOffset))
  }

  // map from parameters to their (multiple) use locations.
  private val paramLocations = {
    parsedTree.params.mapValues(p =>
      for (
        occ <- p.occurrences;
        start <- parsedTree.positions.getStart(occ);
        end <- parsedTree.positions.getFinish(occ)
      ) yield ParamLocation(node = occ, jdbcIndex = orderedParameterUses.indexWhere(_ eq occ) + 1, start, end)
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
        val token = parsedTree.params.get(use.name).flatMap(_.tipe) match {
          // if so, force the type by wrapping the question mark in a cast (validate it first)
          case Some(userType) => vvvParamType(userType).right.map(pgType => s"(?::${pgType.typeName})").getOrElse("?")
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
  case class ParamInfo(t: PostgresType, comment: Option[String])
  case class QueryInfo(parameters: Map[String, ParamInfo], outputType: PostgresRowType)

  private val stmt = conn.prepareStatement(plainCode) // throws SQLException in case of problem
  private val metadata = stmt.getParameterMetaData // throws SQLException in case of problem

  // compute the ErrorRange of a specific node (to underline it)
  private def errorRange(node: SqlBaseNode): Option[ErrorRange] = {
    for (
      start <- parsedTree.positions.getStart(node);
      startPos = ErrorPosition(start.line, start.column);
      end <- parsedTree.positions.getFinish(node);
      endPos = ErrorPosition(end.line, end.column)
    ) yield ErrorRange(startPos, endPos)
  }

  def parameterType(p: String): Either[List[ErrorMessage], ParamInfo] = {
    val paramTypes = validateParameterTypes()
    paramTypes(p)
  }

  // This is returning QueryInfo, which contains the inferred types of parameters + output type.
  // In case of SQL error, or unsupported JDBC type or..., it returns a list of error messages.
  def queryMetadata: Either[List[ErrorMessage], QueryInfo] = {
    // infer the type
    val typesStatus = validateParameterTypes()
    val errors = typesStatus.values.collect { case Left(error) => error }.toList
    if (errors.nonEmpty) Left(errors.flatten)
    else {
      Right(QueryInfo(typesStatus.mapValues(_.right.get), queryOutputType))
    }
  }

  private def vvvParamType(tipe: String): Either[String, PostgresType] = SqlTypesUtils
    .jdbcFromParameterType(tipe)
    .right
    .map(PostgresType(_, true, tipe))
    .right
    .flatMap(SqlTypesUtils.validateParamType)

  private def validateParameterTypes(): Map[String, Either[List[ErrorMessage], ParamInfo]] = {
    // parameter types are either declared with @type or inferred from their usage in the code
    paramLocations.map {
      case (p, locations) =>
        val isNullable = true
        val paramInfo = parsedTree.params(p)
        // in case of error
        val errorLocations = (paramInfo.nodes.filter(
          _.isInstanceOf[SqlParamTypeCommentNode]
        ) ++ paramInfo.occurrences).flatMap(errorRange).toList
        val tStatus =
          if (locations.isEmpty && paramInfo.tipe.isEmpty) {
            // the parameter is declared (@param) but has no explicit type, and is never used
            Left(List(ErrorMessage("cannot guess parameter type", errorLocations, ErrorCode.SqlErrorCode)))
          } else {
            parsedTree.params(p).tipe match {
              case Some(tipe) =>
                // type is specified by the user, we make sure it's supported
                SqlTypesUtils
                  .jdbcFromParameterType(tipe)
                  .right
                  .map(t => ParamInfo(PostgresType(t, isNullable, tipe), paramInfo.description))
                  .left
                  .map(msg => List(ErrorMessage(msg, errorLocations, ErrorCode.SqlErrorCode)))
              case None =>
                // type isn't specified but one can infer from the usage.
                // all locations the parameter is used are considered
                val allOptions: Seq[Either[ErrorMessage, PostgresType]] = locations.map { location =>
                  val idx = location.jdbcIndex
                  val jdbc = metadata.getParameterType(idx)
                  val pgTypeName = metadata.getParameterTypeName(idx)
                  val useLocation = errorRange(location.node).map(List(_)).getOrElse(errorLocations)
                  SqlTypesUtils
                    .validateParamType(PostgresType(jdbc, isNullable, pgTypeName))
                    .left
                    .map(msg => ErrorMessage(msg, useLocation, ErrorCode.SqlErrorCode))
                }
                assert(allOptions.nonEmpty)
                val errors = allOptions.collect { case Left(error) => error }
                if (errors.nonEmpty) {
                  Left(errors.toList)
                } else {
                  val typeOptions = allOptions.collect { case Right(t) => t }
                  SqlTypesUtils
                    .mergePgTypes(typeOptions)
                    .right
                    .map(t => ParamInfo(t, paramInfo.description))
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
