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

package com.rawlabs.snapi.frontend.inferrer.local.jdbc

import java.sql.ResultSetMetaData

import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.snapi.frontend.inferrer.api.{SourceAttrType, SourceCollectionType, SourceRecordType, SourceType}
import com.rawlabs.utils.sources.jdbc.api.{JdbcServerLocation, JdbcTableLocation}

import scala.collection.mutable

class JdbcInferrer extends JdbcTypeToSourceType with StrictLogging {

  def getTableType(location: JdbcTableLocation): SourceType = {
    tableMetadataToSourceType(location.getType())
  }

  def getQueryType(location: JdbcServerLocation, query: String): SourceType = {
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
    val columns = mutable.ListBuffer[SourceAttrType]()

    val incompatible = mutable.HashMap[String, String]()

    (1 to res.getColumnCount).foreach { n =>
      val columnName = res.getColumnName(n)
      val columnType = res.getColumnType(n)
      val nullability = res.isNullable(n)
      jdbcColumnToSourceType(columnType, nullability) match {
        case Some(t) => columns += SourceAttrType(columnName, t)
        case None => incompatible(columnName) = res.getColumnTypeName(n)
      }
    }

    // We are removing from the data incompatible types. It would be nice to show warning to the user
    if (incompatible.nonEmpty) {
      val errorColumns = incompatible.toSeq.map { case (name, typeName) => s"$name: $typeName" }.mkString(", ")
      logger.warn(s"columns have unsupported types: $errorColumns")
    }

    SourceCollectionType(SourceRecordType(columns.to, nullable = false), nullable = false)
  }

}
