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

import com.rawlabs.compiler._

import scala.annotation.tailrec

// Types accepted in @type declarations:
// https://www.postgresql.org/docs/current/datatype-numeric.html
object SqlTypesUtils {

  // Prioritized list of number types
  private val prioritizedNumberTypes = Vector(
    java.sql.Types.DECIMAL,
    java.sql.Types.NUMERIC,
    java.sql.Types.DOUBLE,
    java.sql.Types.FLOAT,
    java.sql.Types.BIGINT,
    java.sql.Types.INTEGER,
    java.sql.Types.SMALLINT,
    java.sql.Types.TINYINT
  )

  // Map from Postgres type names to JDBC types
  private val pgMap: Map[String, Int] = Map(
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

  // Map from JDBC types to RawTypes
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

  def jdbcFromParameterType(typeName: String): Either[String, Int] = {
    pgMap.get(typeName.strip.toLowerCase) match {
      case Some(value) => Right(value)
      case None => Left(s"unsupported type $typeName")
    }
  }

  /**
   * Validates and renames the parameter type and returns the PostgresType if it is valid.
   *
   * @param t the PostgresType to validate
   * @return either an error message or the validated PostgresType
   */
  def validateParamType(t: PostgresType): Either[String, PostgresType] = {
    t.jdbcType match {
      case java.sql.Types.BIT | java.sql.Types.BOOLEAN =>
        Right(PostgresType(java.sql.Types.BOOLEAN, t.nullable, "boolean"))
      case java.sql.Types.SMALLINT => Right(PostgresType(java.sql.Types.SMALLINT, t.nullable, "smallint"))
      case java.sql.Types.INTEGER => Right(PostgresType(java.sql.Types.INTEGER, t.nullable, "integer"))
      case java.sql.Types.BIGINT => Right(PostgresType(java.sql.Types.BIGINT, t.nullable, "bigint"))
      case java.sql.Types.FLOAT | java.sql.Types.REAL => Right(PostgresType(java.sql.Types.FLOAT, t.nullable, "real"))
      case java.sql.Types.DOUBLE => Right(PostgresType(java.sql.Types.DOUBLE, t.nullable, "double precision"))
      case java.sql.Types.NUMERIC | java.sql.Types.DECIMAL =>
        Right(PostgresType(java.sql.Types.DECIMAL, t.nullable, "decimal"))
      case java.sql.Types.DATE => Right(PostgresType(java.sql.Types.DATE, t.nullable, "date"))
      case java.sql.Types.TIME => Right(PostgresType(java.sql.Types.TIME, t.nullable, "time"))
      case java.sql.Types.TIMESTAMP => Right(PostgresType(java.sql.Types.TIMESTAMP, t.nullable, "timestamp"))
      case java.sql.Types.CHAR | java.sql.Types.VARCHAR | java.sql.Types.LONGVARCHAR | java.sql.Types.NCHAR |
          java.sql.Types.NVARCHAR | java.sql.Types.LONGNVARCHAR =>
        Right(PostgresType(java.sql.Types.VARCHAR, t.nullable, "varchar"))
      case _ => Left(s"unsupported parameter type ${t.typeName}")
    }
  }

  /**
   * Returns the RawType corresponding to the given PostgresType.
   *
   * @param tipe the PostgresType to convert
   * @return either an error message or the corresponding RawType
   */
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
          case None => Left(s"unsupported type: $pgTypeName")
        }
    }

    rawType.right.map {
      case any: RawAnyType => any
      case t => t.cloneWithFlags(nullable = tipe.nullable, triable = false)
    }
  }

  /**
   * Merges a sequence of PostgresTypes into a single PostgresType.
   * For example, if a parameter is used as both a double and an int, it will be inferred as a double.
   * It's very conservative and it's a;ways better rely on explicitly typed parameters.
   *
   * @param options the sequence of PostgresTypes to merge
   * @return either an error message or the merged PostgresType
   */
  def mergePgTypes(options: Seq[PostgresType]): Either[String, PostgresType] = {

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
