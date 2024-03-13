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

package raw.client.sql.metadata

import java.sql.PreparedStatement

object DotSearchWithOneItem
    extends Completion(
      """SELECT item1, item2, type FROM (
        | -- tables that belong to schemas having that name
        | SELECT table_schema AS item1, table_name AS item2, 'table' AS type FROM information_schema.tables
        |     WHERE table_schema = ?
        |         AND table_schema NOT IN ('pg_catalog', 'information_schema')
        | UNION
        | -- columns that belong to tables having that name
        | SELECT table_name AS item1, column_name AS item2, data_type AS type FROM information_schema.columns
        |     WHERE table_name = ?
        |         AND table_schema NOT IN ('pg_catalog', 'information_schema')
        |) T""".stripMargin
    ) {
  override protected def setParams(preparedStatement: PreparedStatement, items: Seq[String]): Unit = {
    val item = items.head
    preparedStatement.setString(1, item)
    preparedStatement.setString(2, item)
  }
}
