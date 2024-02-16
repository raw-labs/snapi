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
import raw.client.api._
import raw.utils.RawSettings

import java.sql.{Connection, ResultSet, ResultSetMetaData, SQLException}
import scala.collection.mutable

/* This class is wrapping the PreparedStatement class from the JDBC API.
 * It parses the SQL code and extract the named parameters, infer their types from how they're used.
 * It also provides methods to set the parameters by name, then execute the query.
 */
class NamedParametersPreparedStatement(conn: Connection, code: String)(implicit rawSettings: RawSettings) extends StrictLogging {

  /* We have the query code in `code` (with named parameters). Internally we need to replace
   * the named parameters with question marks, and keep track of the mapping between the
   * parameter names and the question marks.
   */

  // Each named parameter is mapped to a list of offsets in the original `code` where it appears (starting at the colon)
  private case class ParamLocation(index: Int, start: Int, end: Int)
  private val paramLocations = mutable.Map.empty[String, mutable.ListBuffer[ParamLocation]]

  private var paramIndex = 0

  private val plainCode = {
    val startSkipTokens = Array("'", "--") // tokens that start a portion of code where named parameters aren't found
    val endSkipTokens = Array("'", "\n") // tokens that end a portion of code where named parameters aren't found
    var skippingTokenIndex = -1 // the current token that is being skipped
    val codeSize = code.length

    val plainCodeBuffer = new StringBuilder
    var i = 0
    while (i < codeSize) {
      val currentChar = code.charAt(i)
      if (skippingTokenIndex >= 0) {
        val endSkipToken = endSkipTokens(skippingTokenIndex) // the token we'd need to see to stop skipping
        val skippingOver =
          code.regionMatches(i, endSkipToken, 0, endSkipToken.length) // does the token appear at the current position?
        if (skippingOver) {
          i += endSkipToken.length
          plainCodeBuffer.append(endSkipToken)
          skippingTokenIndex = -1
        } else {
          plainCodeBuffer.append(code.substring(i, i + 1))
          i += 1
        }
      } else {
        // not skipping, parse carefully
        if (currentChar == ':') {
          if (i < codeSize - 1 && code.charAt(i + 1) == ':') {
            // postgres double colon syntax
            plainCodeBuffer.append(code.substring(i, i + 2))
            i += 2
          } else {
            // we're on a named parameter
            paramIndex += 1
            i += 1 // skip the colon (do not append it to plainCodeBuffer)
            val start = i
            while (i < codeSize && code.charAt(i).isLetterOrDigit) {
              // skip the parameter name
              i += 1
            }
            val end = i
            val parameterName = code.substring(start, end)
            plainCodeBuffer += '?' // replace the parameter name with a question mark
            val location = ParamLocation(paramIndex, start, end)
            paramLocations.get(parameterName) match {
              case Some(indices) => indices += location
              case None => paramLocations(parameterName) = mutable.ListBuffer(location)
            }
          }
        } else {
          // are we by any chance on a start-skipping token
          val idx = startSkipTokens.indexWhere(token => code.regionMatches(i, token, 0, token.length))
          if (idx != -1) {
            // yes. Set that token as the current one so that we find the ending one later.
            skippingTokenIndex = idx
          } else {
            // not, just append the current character
            plainCodeBuffer.append(code.substring(i, i + 1))
            i += 1
          }
        }
      }
    }
    plainCodeBuffer.toString()
  }

  // A data structure for the full query info: parameters that are mapped to their inferred types, and output type (the query type)
  case class QueryInfo(parameters: Map[String, RawType], outputType: RawType)

  private val stmt = conn.prepareStatement(plainCode)
  logger.debug(plainCode)
  paramLocations.foreach(p => logger.debug(p.toString))

  // This is returning QueryInfo, which contains the inferred types of parameters + output type.
  // In case of SQL error, or unsupported JDBC type or..., it returns a list of error messages.
  lazy val queryMetadata: Either[List[ErrorMessage], QueryInfo] = {
    try {
      val metadata = stmt.getParameterMetaData // throws SQLException in case of problem
      val typesStatus = paramLocations.map {
        case (p, locations) =>
          val typeOptions =
            locations.map(location => SqlTypesUtils.rawTypeFromJdbc(metadata.getParameterType(location.index)))
          val errors = typeOptions.collect { case Left(error) => error }
          val typeStatus =
            if (errors.isEmpty) {
              val options = typeOptions.collect { case Right(t) => t }
              SqlTypesUtils
                .mergeRawTypes(options)
                .right
                .map { case any: RawAnyType => any; case other => other.cloneNullable }
                .left
                .map(message =>
                  ErrorMessage(
                    message,
                    locations
                      .map(location => ErrorRange(offsetToPosition(location.start), offsetToPosition(location.end)))
                      .toList,
                    ErrorCode.SqlErrorCode
                  )
                )
            } else {
              Left(
                ErrorMessage(
                  errors.mkString(", "),
                  locations
                    .map(location => ErrorRange(offsetToPosition(location.start), offsetToPosition(location.end)))
                    .toList,
                  ErrorCode.SqlErrorCode
                )
              )
            }
          p -> typeStatus
      }
      val errors = typesStatus.collect { case (_, Left(error)) => error }.toList
      if (errors.nonEmpty) {
        Left(errors)
      } else {
        val types = typesStatus.mapValues(_.right.get)
        val outputType = queryOutputType
        outputType match {
          case Right(t) => Right(QueryInfo(types.toMap, t))
          case Left(error) => Left(ErrorHandling.asMessage(code, error))
        }
      }
    } catch {
      case e: SQLException => {
        Left(ErrorHandling.asErrorMessage(code, e))
      }
    }
  }

  private def queryOutputType: Either[String, RawType] = {
    val metadata = stmt.getMetaData
    val columns = (1 to metadata.getColumnCount).map { i =>
      val name = metadata.getColumnName(i)
      val typeInfo = {
        val tipe = metadata.getColumnType(i)
        val nullability = metadata.isNullable(i)
        val nullable =
          nullability == ResultSetMetaData.columnNullable || nullability == ResultSetMetaData.columnNullableUnknown
        SqlTypesUtils.rawTypeFromJdbc(tipe).right.map {
          case t: RawAnyType => t
          case t: RawType => t.cloneWithFlags(nullable, false)
        }
      }
      typeInfo.right.map(t => RawAttrType(name, t))
    }
    val errors = columns.collect { case Left(error) => error }
    if (errors.nonEmpty) {
      Left(errors.mkString(", "))
    } else {
      val attrs = columns.collect { case Right(attr) => attr }
      Right(RawIterableType(RawRecordType(attrs.toVector, false, false), false, false))
    }
  }

  def executeQuery(): ResultSet = stmt.executeQuery()
  def close(): Unit = stmt.close()

  private def offsetToPosition(offset: Int): ErrorPosition = {
    var i = 0
    var line = 1
    var column = 1
    while (i < offset) {
      val c = code.charAt(i)
      if (c == '\n') {
        line += 1
        column = 1
      } else {
        column += 1
      }
      i += 1
    }
    ErrorPosition(line, column)
  }

  // Parameters are considered nullable (null when not specified). The prepared statement is initialized
  // with nulls on all parameters. When a parameter is set, the null is replaced with the value.
  for (locations <- paramLocations.valuesIterator; location <- locations) {
    stmt.setNull(location.index, java.sql.Types.NULL)
  }

  def setNull(parameter: String): Unit = {
    for (location <- paramLocations(parameter)) stmt.setNull(location.index, java.sql.Types.NULL)
  }
  def setByte(parameter: String, x: Byte): Unit = {
    for (location <- paramLocations(parameter)) stmt.setByte(location.index, x)
  }
  def setShort(parameter: String, x: Short): Unit = {
    for (location <- paramLocations(parameter)) stmt.setShort(location.index, x)
  }
  def setInt(parameter: String, x: Int): Unit = {
    for (location <- paramLocations(parameter)) stmt.setInt(location.index, x)
  }
  def setLong(parameter: String, x: Long): Unit = {
    for (location <- paramLocations(parameter)) stmt.setLong(location.index, x)
  }
  def setFloat(parameter: String, x: Float): Unit = {
    for (location <- paramLocations(parameter)) stmt.setFloat(location.index, x)
  }
  def setDouble(parameter: String, x: Double): Unit = {
    for (location <- paramLocations(parameter)) stmt.setDouble(location.index, x)
  }
  def setBigDecimal(parameter: String, x: java.math.BigDecimal): Unit = {
    for (location <- paramLocations(parameter)) stmt.setBigDecimal(location.index, x)
  }
  def setString(parameter: String, x: String): Unit = {
    for (location <- paramLocations(parameter)) stmt.setString(location.index, x)
  }
  def setBoolean(parameter: String, x: Boolean): Unit = {
    for (location <- paramLocations(parameter)) stmt.setBoolean(location.index, x)
  }
  def setBytes(parameter: String, x: Array[Byte]): Unit = {
    for (location <- paramLocations(parameter)) stmt.setBytes(location.index, x)
  }
  def setDate(parameter: String, x: java.sql.Date): Unit = {
    for (location <- paramLocations(parameter)) stmt.setDate(location.index, x)
  }
  def setTime(parameter: String, x: java.sql.Time): Unit = {
    for (location <- paramLocations(parameter)) stmt.setTime(location.index, x)
  }
  def setTimestamp(parameter: String, x: java.sql.Timestamp): Unit = {
    for (location <- paramLocations(parameter)) stmt.setTimestamp(location.index, x)
  }

}
