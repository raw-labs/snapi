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
import raw.client.sql.SqlCodeUtils.Token
import raw.utils.RawSettings

import java.sql.{Connection, ResultSet, ResultSetMetaData, SQLException}
import scala.util.matching.Regex
object NamedParametersPreparedStatement {
  val argRegex: Regex = """:([a-zA-Z]\w*)\W*""".r
}
/* This class is wrapping the PreparedStatement class from the JDBC API.
 * It parses the SQL code and extract the named parameters, infer their types from how they're used.
 * It also provides methods to set the parameters by name, then execute the query.
 */
class NamedParametersPreparedStatement(conn: Connection, code: String)(implicit rawSettings: RawSettings)
    extends StrictLogging {
  import NamedParametersPreparedStatement.argRegex
  /* We have the query code in `code` (with named parameters). Internally we need to replace
   * the named parameters with question marks, and keep track of the mapping between the
   * parameter names and the question marks.
   */

  // Each named parameter is mapped to a list of offsets in the original `code` where it appears (starting at the colon)
  private case class ParamLocation(index: Int, start: Int, end: Int)

  private val arguments =
    SqlCodeUtils.tokens(code).collect { case Token(argRegex(argName), pos, offset) => Token(argName, pos, offset) }

  private val paramLocations: Map[String, Seq[ParamLocation]] =
    arguments.zipWithIndex.groupBy { case (token, _) => token.token }.map {
      case (argName, items) => argName -> items.map {
          case (token, index) => ParamLocation(index + 1, token.offset, token.offset + argName.length)
        }
    }

  private val plainCode: String = {
    val plainCodeBuffer = new StringBuilder
    var lastOffset = 0
    for (arg <- arguments) {
      plainCodeBuffer.append(code.substring(lastOffset, arg.offset - 1))
      plainCodeBuffer.append("?")
      lastOffset = arg.offset + arg.token.length
    }
    plainCodeBuffer.append(code.substring(lastOffset))
    plainCodeBuffer.toString()
  }

  private def validateParameterType(tipe: RawType, name: String, typeName: String): Either[String, RawType] =
    tipe match {
      case _: RawNumberType => Right(tipe)
      case _: RawStringType => Right(tipe)
      case _: RawBoolType => Right(tipe)
      case _: RawDateType => Right(tipe)
      case _: RawTimeType => Right(tipe)
      case _: RawTimestampType => Right(tipe)
      case _: RawBinaryType => Right(tipe)
      case _ => Left(s"parameter '$name' has an unsupported type '$typeName'")
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
          // For each parameter, we infer the type from the locations where it's used
          // And we validate the if the type is supported
          val typeOptions = locations.map(location =>
            SqlTypesUtils.rawTypeFromJdbc(
              metadata.getParameterType(location.index),
              metadata.getParameterTypeName(location.index)
            ) match {
              case Right(t) => validateParameterType(t, p, metadata.getParameterTypeName(location.index))
              case Left(error) => Left(error)
            }
          )
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
        val typeName = metadata.getColumnTypeName(i)
        val nullability = metadata.isNullable(i)
        val nullable =
          nullability == ResultSetMetaData.columnNullable || nullability == ResultSetMetaData.columnNullableUnknown
        SqlTypesUtils.rawTypeFromJdbc(tipe, typeName).right.map {
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
