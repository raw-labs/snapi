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

object WordSearchWithThreeItems
    extends Completion(
      """-- columns that belong to tables having the required name in schemas having the required name
        |SELECT table_schema, table_name, column_name, data_type AS type FROM information_schema.columns
        |WHERE table_schema = ?
        |      AND table_name = ?
        |      AND starts_with(column_name, ?)
        |      AND table_schema NOT IN ('pg_catalog', 'information_schema')""".stripMargin
    ) {
  override protected def setParams(preparedStatement: PreparedStatement, items: Seq[String]): Unit = {
    val item1 = items(0)
    val item2 = items(1)
    val item3 = items(2)
    preparedStatement.setString(1, item1)
    preparedStatement.setString(2, item2)
    preparedStatement.setString(3, item3)
  }
}
