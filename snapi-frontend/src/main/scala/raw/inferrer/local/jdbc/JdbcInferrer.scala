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

import java.sql.ResultSetMetaData
import com.typesafe.scalalogging.StrictLogging
import raw.inferrer.api.{SourceAttrType, SourceCollectionType, SourceNullType, SourceRecordType, SourceType}
import raw.sources.jdbc.api.{JdbcLocation, JdbcTableLocation}

import scala.collection.mutable

class JdbcInferrer extends JdbcTypeToSourceType with StrictLogging {

  def getTableType(location: JdbcTableLocation): SourceType = {
    tableMetadataToSourceType(location.getType())
  }

  def getQueryType(location: JdbcLocation, query: String): SourceType = {
    location.jdbcClient.wrapSQLException {
      val conn = location.getJdbcConnection()
      try {
        val stmt = conn.prepareStatement(query)
        try {
          val res = stmt.getMetaData
          getTypeFromResultSetMetadata(res)
        } finally {
          stmt.close()
        }
      } finally {
        conn.close()
      }
    }
  }

  private def getTypeFromResultSetMetadata(res: ResultSetMetaData): SourceType = {

    val columns = (1 to res.getColumnCount).map { n =>
      val columnName = res.getColumnName(n)
      val columnType = res.getColumnType(n)
      val nullability = res.isNullable(n)
      jdbcColumnToSourceType(columnType, nullability) match {
        case Some(t) => SourceAttrType(columnName, t)
        case None =>
          logger.warn(s"Unsupported column type $columnType for column $columnName")
          SourceAttrType(columnName, SourceNullType())
      }
    }
    SourceCollectionType(SourceRecordType(columns.to, nullable = false), nullable = false)
  }

}
