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

package raw.inferrer.local.jdbc

import raw.inferrer.api.{
  InferrerException,
  SourceAttrType,
  SourceBinaryType,
  SourceBoolType,
  SourceByteType,
  SourceCollectionType,
  SourceDateType,
  SourceDecimalType,
  SourceDoubleType,
  SourceFloatType,
  SourceIntType,
  SourceIntervalType,
  SourceLongType,
  SourceNullType,
  SourceRecordType,
  SourceShortType,
  SourceStringType,
  SourceTimeType,
  SourceTimestampType,
  SourceType
}
import raw.sources.jdbc.api.{JdbcColumnType, NativeIntervalType, TableColumn, TableMetadata, UnsupportedColumnType}

import java.sql.ResultSetMetaData
import java.sql.Types._
import scala.collection.mutable

trait JdbcTypeToSourceType {

  protected def jdbcColumnToSourceType(columnType: Int, nullability: Int): Option[SourceType] = {
    // We assume nullability of a field in case nullability is true, or unknown.
    val nullable = nullability == ResultSetMetaData.columnNullable ||
      nullability == ResultSetMetaData.columnNullableUnknown
    // Following http://db.apache.org/ojb/docu/guides/jdbc-types.html
    columnType match {
      case CHAR | VARCHAR | NVARCHAR | NCHAR | LONGNVARCHAR => Some(SourceStringType(nullable))
      case DECIMAL | NUMERIC => Some(SourceDecimalType(nullable))
      case BOOLEAN | BIT => Some(SourceBoolType(nullable))
      case TINYINT => Some(SourceByteType(nullable))
      case SMALLINT => Some(SourceShortType(nullable))
      case INTEGER => Some(SourceIntType(nullable))
      case BIGINT => Some(SourceLongType(nullable))
      case REAL => Some(SourceFloatType(nullable))
      case DOUBLE | FLOAT => Some(SourceDoubleType(nullable))
      case DATE => Some(SourceDateType(None, nullable))
      case TIME | TIME_WITH_TIMEZONE => Some(SourceTimeType(None, nullable))
      case TIMESTAMP | TIMESTAMP_WITH_TIMEZONE => Some(SourceTimestampType(None, nullable))
      case BINARY | VARBINARY => Some(SourceBinaryType(nullable))
      case BLOB => Some(SourceBinaryType(nullable))
      // This is documented as a binary type in http://db.apache.org/ojb/docu/guides/jdbc-types.html
      // we can consider enabling it (LONGVARBINARY can be big!)
      //case LONGVARBINARY =>
      //  columns += SourceAttrType(columnName, SourceBinaryType(nullable))
      case _ => None
    }
  }

  protected def tableMetadataToSourceType(tableMetadata: TableMetadata): SourceType = {
    val TableMetadata(tableColumns, _) = tableMetadata

    val columns: Vector[SourceAttrType] = tableColumns.map {
      case TableColumn(columnName, columnType) => columnType match {
          case JdbcColumnType(jdbcType, jdbcNullability) => jdbcColumnToSourceType(jdbcType, jdbcNullability) match {
              case Some(t) => SourceAttrType(columnName, t)
              case None => SourceAttrType(columnName, SourceNullType())
            }
          // This UnsupportedColumnType is just used in this match, how do we get one?
          case NativeIntervalType(nullable) => SourceAttrType(columnName, SourceIntervalType(nullable))
          case UnsupportedColumnType => SourceAttrType(columnName, SourceNullType())
        }
    }.toVector

    if (tableColumns.isEmpty) throw new InferrerException("could not find table")
    SourceCollectionType(SourceRecordType(columns, nullable = false), nullable = false)
  }

}
