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

  // types accepted in @type declarations:
  // https://www.postgresql.org/docs/current/datatype-numeric.html

  def jdbcFromParameterType(typeName: String): Either[String, Int] = {
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
    pgMap.get(typeName.strip.toLowerCase) match {
      case Some(value) => Right(value)
      case None => Left(s"unsupported type $typeName")
    }
  }

  // a mapping from JDBC types to a RawType. We also store the name of the JDBC type for error reporting.
  private val jdbcToRawType: Map[Int, RawType] = Map(
    java.sql.Types.BIT -> RawBoolType(false, false),
    java.sql.Types.TINYINT -> RawByteType(false, false),
    java.sql.Types.SMALLINT -> RawShortType(false, false),
    java.sql.Types.INTEGER -> RawIntType(false, false),
    java.sql.Types.BIGINT -> RawLongType(false, false),
    java.sql.Types.FLOAT -> RawFloatType(false, false),
    java.sql.Types.REAL -> RawFloatType(false, false),
    java.sql.Types.DOUBLE -> RawDoubleType(false, false),
    java.sql.Types.NUMERIC -> RawDecimalType(false, false),
    java.sql.Types.DECIMAL -> RawDecimalType(false, false),
    java.sql.Types.CHAR -> RawStringType(false, false),
    java.sql.Types.VARCHAR -> RawStringType(false, false),
    java.sql.Types.LONGVARCHAR -> RawStringType(false, false),
    java.sql.Types.DATE -> RawDateType(false, false),
    java.sql.Types.TIME -> RawTimeType(false, false),
    java.sql.Types.TIMESTAMP -> RawTimestampType(false, false),
    java.sql.Types.BINARY -> RawBinaryType(false, false),
    java.sql.Types.VARBINARY -> RawBinaryType(false, false),
    java.sql.Types.LONGVARBINARY -> RawBinaryType(false, false),
    java.sql.Types.OTHER -> RawAnyType(),
    //    java.sql.Types.JAVA_OBJECT ->
    //    java.sql.Types.DISTINCT ->
    //    java.sql.Types.STRUCT ->
    //    java.sql.Types.ARRAY ->
    //    java.sql.Types.BLOB ->
    //    java.sql.Types.CLOB ->
    //    java.sql.Types.REF ->
    //    java.sql.Types.DATALINK ->
    java.sql.Types.BOOLEAN -> RawBoolType(false, false),
    //    java.sql.Types.ROWID ->
    java.sql.Types.NCHAR -> RawStringType(false, false),
    java.sql.Types.NVARCHAR -> RawStringType(false, false),
    java.sql.Types.LONGNVARCHAR -> RawStringType(false, false)
    //    java.sql.Types.NCLOB ->
    //    java.sql.Types.SQLXML ->
    //    java.sql.Types.REF_CURSOR ->
    //    java.sql.Types.TIME_WITH_TIMEZONE ->
    //    java.sql.Types.TIMESTAMP_WITH_TIMEZONE ->
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
      case _ => Left(s"unsupported parameter type ${t.typeName}")
    }
    pgTypeName.right.map(name => PostgresType(t.jdbcType, t.nullable, name))
  }

  def rawTypeFromPgType(tipe: PostgresType): Either[String, RawType] = {
    val jdbcType = tipe.jdbcType
    val pgTypeName = tipe.typeName
    val rawType = jdbcType match {
      case java.sql.Types.OTHER =>
        // The type is OTHER, we need to check the specific type of the column using the type name
        val otherTypeMap: Map[String, RawType] = Map(
          "interval" -> RawIntervalType(false, false),
          "json" -> RawAnyType(),
          "jsonb" -> RawAnyType()
        )
        otherTypeMap.get(pgTypeName) match {
          case Some(t) => Right(t)
          case None => Left(s"unsupported type: $pgTypeName")
        }
      case _ => jdbcToRawType.get(jdbcType) match {
          case Some(rawType) => Right(rawType)
          case None =>
            // this is the postgres type and the internal JDBC integer type
            logger.warn(s"unsupported type: $pgTypeName (JDBC type: #$jdbcType)")
            Left(s"unsupported type: $pgTypeName")
        }
    }

    rawType.right.map {
      case any: RawAnyType => any
      case t => t.cloneWithFlags(nullable = tipe.nullable, triable = false)
    }
  }

  // This is merging the inferred types of parameters. For example, if a parameter is used as both
  // a double and an int, it will be inferred as a double. It's very conservative and better rely on
  // explicitly typed parameters.
  def mergePgTypes(options: Seq[PostgresType]): Either[String, PostgresType] = {
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
