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

import scala.annotation.tailrec

object SqlTypesUtils extends StrictLogging {

  private case class SqlType(name: String, rawType: Option[RawType])

  private val otherTypeMap: Map[String, RawType] = Map(
    "interval" -> RawIntervalType(false, false),
    "json" -> RawAnyType(),
    "jsonb" -> RawAnyType()
  )
  // a mapping from JDBC types to a RawType. We also store the name of the JDBC type for error reporting.
  private val typeMap: Map[Int, SqlType] = Map(
    java.sql.Types.BIT -> SqlType("BIT", Some(RawBoolType(false, false))),
    java.sql.Types.TINYINT -> SqlType("TINYINT", Some(RawByteType(false, false))),
    java.sql.Types.SMALLINT -> SqlType("SMALLINT", Some(RawShortType(false, false))),
    java.sql.Types.INTEGER -> SqlType("INTEGER", Some(RawIntType(false, false))),
    java.sql.Types.BIGINT -> SqlType("BIGINT", Some(RawLongType(false, false))),
    java.sql.Types.FLOAT -> SqlType("FLOAT", Some(RawFloatType(false, false))),
    java.sql.Types.REAL -> SqlType("REAL", Some(RawFloatType(false, false))),
    java.sql.Types.DOUBLE -> SqlType("DOUBLE", Some(RawDoubleType(false, false))),
    java.sql.Types.NUMERIC -> SqlType("NUMERIC", Some(RawDecimalType(false, false))),
    java.sql.Types.DECIMAL -> SqlType("DECIMAL", Some(RawDecimalType(false, false))),
    java.sql.Types.CHAR -> SqlType("CHAR", Some(RawStringType(false, false))),
    java.sql.Types.VARCHAR -> SqlType("VARCHAR", Some(RawStringType(false, false))),
    java.sql.Types.LONGVARCHAR -> SqlType("LONGVARCHAR", Some(RawStringType(false, false))),
    java.sql.Types.DATE -> SqlType("DATE", Some(RawDateType(false, false))),
    java.sql.Types.TIME -> SqlType("TIME", Some(RawTimeType(false, false))),
    java.sql.Types.TIMESTAMP -> SqlType("TIMESTAMP", Some(RawTimestampType(false, false))),
    java.sql.Types.BINARY -> SqlType("BINARY", None),
    java.sql.Types.VARBINARY -> SqlType("VARBINARY", None),
    java.sql.Types.LONGVARBINARY -> SqlType("LONGVARBINARY", None),
    java.sql.Types.NULL -> SqlType("NULL", None),
    java.sql.Types.OTHER -> SqlType("OTHER", Some(RawAnyType())),
    java.sql.Types.JAVA_OBJECT -> SqlType("JAVA_OBJECT", None),
    java.sql.Types.DISTINCT -> SqlType("DISTINCT", None),
    java.sql.Types.STRUCT -> SqlType("STRUCT", None),
    java.sql.Types.ARRAY -> SqlType("ARRAY", None),
    java.sql.Types.BLOB -> SqlType("BLOB", None),
    java.sql.Types.CLOB -> SqlType("CLOB", None),
    java.sql.Types.REF -> SqlType("REF", None),
    java.sql.Types.DATALINK -> SqlType("DATALINK", None),
    java.sql.Types.BOOLEAN -> SqlType("BOOLEAN", Some(RawBoolType(false, false))),
    java.sql.Types.ROWID -> SqlType("ROWID", None),
    java.sql.Types.NCHAR -> SqlType("NCHAR", Some(RawStringType(false, false))),
    java.sql.Types.NVARCHAR -> SqlType("NVARCHAR", Some(RawStringType(false, false))),
    java.sql.Types.LONGNVARCHAR -> SqlType("LONGNVARCHAR", Some(RawStringType(false, false))),
    java.sql.Types.NCLOB -> SqlType("NCLOB", None),
    java.sql.Types.SQLXML -> SqlType("SQLXML", None),
    java.sql.Types.REF_CURSOR -> SqlType("REF_CURSOR", None),
    java.sql.Types.TIME_WITH_TIMEZONE -> SqlType("TIME_WITH_TIMEZONE", None),
    java.sql.Types.TIMESTAMP_WITH_TIMEZONE -> SqlType("TIMESTAMP_WITH_TIMEZONE", None)
  )

  // returns the name of the RawType. Used to report errors. Maybe RawType should have a name field?
  def rawTypeName(rawType: RawType): String = {
    rawType match {
      case RawBoolType(_, _) => "boolean"
      case RawByteType(_, _) => "byte"
      case RawShortType(_, _) => "short"
      case RawIntType(_, _) => "int"
      case RawLongType(_, _) => "long"
      case RawFloatType(_, _) => "float"
      case RawDoubleType(_, _) => "double"
      case RawDecimalType(_, _) => "decimal"
      case RawStringType(_, _) => "string"
      case RawDateType(_, _) => "date"
      case RawTimeType(_, _) => "time"
      case RawTimestampType(_, _) => "timestamp"
      case _ => throw new IllegalArgumentException(s"Unsupported type: $rawType")
    }
  }

  // returns the RawType corresponding to the given JDBC type, or an error message if the type is not supported
  def rawTypeFromJdbc(jdbcType: Int, typeName: String): Either[String, RawType] = {
    jdbcType match {
      // If the type is OTHER, we need to check the specific type of the column using the type name
      case java.sql.Types.OTHER => otherTypeMap.get(typeName) match {
          case Some(t) => Right(t)
          case None => Left(s"Unsupported SQL type: $typeName")
        }
      case _ => typeMap.get(jdbcType) match {
          case Some(sqlTypeInfo) => sqlTypeInfo.rawType match {
              case Some(t) => Right(t)
              case None => Left(s"Unsupported SQL type: ${sqlTypeInfo.name}")
            }
          case None =>
            // this is the postgres type and the internal JDBC integer type
            logger.error(
              s"Unsupported SQL type: $typeName JDBC type: $jdbcType"
            )
            Left(s"Unsupported SQL type $typeName")
        }
    }
  }

  // This is merging the inferred types of parameters. For example, if a parameter is used as both
  // a double and an int, it will be inferred as a double.
  def mergeRawTypes(options: Seq[RawType]): Either[String, RawType] = {
    val prioritizedNumberTypes = Vector(
      RawDecimalType(false, false),
      RawDoubleType(false, false),
      RawFloatType(false, false),
      RawLongType(false, false),
      RawIntType(false, false),
      RawShortType(false, false),
      RawByteType(false, false)
    )

    @tailrec
    def recurse(tipes: Seq[RawType], current: RawType): Either[String, RawType] = {
      if (tipes.isEmpty) {
        Right(current)
      } else {
        val head = tipes.head
        if (prioritizedNumberTypes.contains(current) && prioritizedNumberTypes.contains(head)) {
          if (prioritizedNumberTypes.indexOf(current) < prioritizedNumberTypes.indexOf(head)) {
            recurse(tipes.tail, current)
          } else {
            recurse(tipes.tail, head)
          }
        } else if (current == head) { recurse(tipes.tail, current) }
        else Left(
          s"a parameter cannot be both ${SqlTypesUtils.rawTypeName(current)} and ${SqlTypesUtils.rawTypeName(head)}"
        )
      }
    }
    recurse(options.tail, options.head)
  }

}
