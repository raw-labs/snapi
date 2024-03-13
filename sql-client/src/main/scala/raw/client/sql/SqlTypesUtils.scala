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

  // types accepted in declarations:
  // https://www.postgresql.org/docs/current/datatype-numeric.html
  val pgMap: Map[String, Int] = Map(
    "smallint" -> java.sql.Types.SMALLINT,
    "integer" -> java.sql.Types.INTEGER,
    "bigint" -> java.sql.Types.BIGINT,
    "decimal" -> java.sql.Types.DECIMAL,
    "real" -> java.sql.Types.FLOAT,
    "double precision" -> java.sql.Types.DOUBLE,
    "date" -> java.sql.Types.DATE,
    "time" -> java.sql.Types.TIME,
    "timestamp" -> java.sql.Types.TIMESTAMP,
    "boolean" -> java.sql.Types.BOOLEAN,
    "varchar" -> java.sql.Types.VARCHAR
  )

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

  // renames the postgres type to what a user would need to write to match
  // the actual type. Or return an error.
  def validateParamType(t: PostgresType): Either[String, PostgresType] = {
    val pgTypeName = t.jdbcType match {
      case java.sql.Types.BIT | java.sql.Types.BOOLEAN => Right("boolean")
      case java.sql.Types.SMALLINT => Right("smallint")
      case java.sql.Types.INTEGER => Right("integer")
      case java.sql.Types.BIGINT => Right("bigint")
      case java.sql.Types.FLOAT => Right("real")
      case java.sql.Types.REAL => Right("real")
      case java.sql.Types.DOUBLE => Right("double precision")
      case java.sql.Types.NUMERIC | java.sql.Types.DECIMAL => Right("decimal")
      case java.sql.Types.DATE => Right("date")
      case java.sql.Types.TIME => Right("time")
      case java.sql.Types.TIMESTAMP => Right("timestamp")
      case java.sql.Types.CHAR | java.sql.Types.VARCHAR | java.sql.Types.LONGVARCHAR | java.sql.Types.NCHAR |
          java.sql.Types.NVARCHAR | java.sql.Types.LONGNVARCHAR => Right("varchar")
      case java.sql.Types.OTHER => t.typeName match {
          case "interval" | "json" | "jsonb" => Right(t.typeName)
          case unsupported => Left(s"unsupported parameter type: $unsupported")
        }
    }
    pgTypeName.right.map(name => PostgresType(t.jdbcType, name))
  }

  def rawTypeFromJdbc(tipe: PostgresType): Either[String, RawType] = rawTypeFromJdbc(tipe.jdbcType, tipe.typeName)

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
  def mergeRawTypes(options: Seq[PostgresType]): Either[String, PostgresType] = {
    val prioritizedNumberTypes = Vector(
      java.sql.Types.DECIMAL,
      java.sql.Types.NUMERIC,
      java.sql.Types.DOUBLE,
      java.sql.Types.FLOAT,
      java.sql.Types.BIGINT,
      java.sql.Types.INTEGER,
      java.sql.Types.SMALLINT,
      java.sql.Types.TINYINT
    )

    @tailrec
    def recurse(tipes: Seq[PostgresType], current: PostgresType): Either[String, PostgresType] = {
      if (tipes.isEmpty) {
        Right(current)
      } else {
        val head = tipes.head
        if (prioritizedNumberTypes.contains(current.jdbcType) && prioritizedNumberTypes.contains(head.jdbcType)) {
          if (prioritizedNumberTypes.indexOf(current.jdbcType) < prioritizedNumberTypes.indexOf(head.jdbcType)) {
            recurse(tipes.tail, current)
          } else {
            recurse(tipes.tail, head)
          }
        } else if (current == head) {
          recurse(tipes.tail, current)
        } else Left(
          s"a parameter cannot be both ${current.typeName} and ${head.typeName}"
        )
      }
    }

    recurse(options.tail, options.head)
  }

}
