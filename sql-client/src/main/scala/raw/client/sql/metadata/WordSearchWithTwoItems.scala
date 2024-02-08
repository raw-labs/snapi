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

package raw.client.sql.metadata

import java.sql.PreparedStatement

object WordSearchWithTwoItems
    extends Completion(
      """SELECT
        |  item1,
        |  item2,
        |  type
        |FROM
        |  (
        |    -- tables having a similar name, that belong to schemas having the name
        |    SELECT table_schema AS item1, table_name AS item2, 'table' AS type
        |    FROM information_schema.tables
        |    WHERE table_schema = ? AND starts_with(table_name, ?)
        |    UNION
        |    -- columns having a similar name, that belong to tables having the name
        |    SELECT table_name AS item1, column_name AS item2, data_type AS type
        |    FROM information_schema.columns
        |    WHERE table_name = ? AND starts_with(column_name, ?)
        |  ) T""".stripMargin
    ) {
  override protected def setParams(preparedStatement: PreparedStatement, items: Seq[String]): Unit = {
    val item1 = items(0)
    val item2 = items(1)
    preparedStatement.setString(1, item1)
    preparedStatement.setString(2, item2)
    preparedStatement.setString(3, item1)
    preparedStatement.setString(4, item2)
  }
}
