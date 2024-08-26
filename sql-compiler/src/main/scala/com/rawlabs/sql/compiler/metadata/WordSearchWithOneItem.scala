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

package com.rawlabs.sql.compiler.metadata

import java.sql.PreparedStatement

object WordSearchWithOneItem
    extends Completion(
      """SELECT name, type FROM (
        |-- schemas that have a name starting with the identifier
        |SELECT schema_name AS name, 'schema' AS type FROM information_schema.schemata
        |	WHERE starts_with(schema_name, ?) AND schema_name NOT IN ('pg_catalog', 'information_schema')
        |UNION
        |-- tables that have a name starting with the identifier
        |SELECT table_name AS name, 'table' AS type FROM information_schema.tables
        |	WHERE starts_with(table_name, ?) AND table_schema NOT IN ('pg_catalog', 'information_schema')
        |UNION
        |-- columns that have a name starting with the identifier
        |SELECT column_name AS name, data_type AS type FROM information_schema.columns
        |	WHERE starts_with(column_name, ?) AND table_schema NOT IN ('pg_catalog', 'information_schema')
        |) T""".stripMargin
    ) {
  override protected def setParams(preparedStatement: PreparedStatement, items: Seq[String]): Unit = {
    val item = items.head
    preparedStatement.setString(1, item)
    preparedStatement.setString(2, item)
    preparedStatement.setString(3, item)
  }
}
