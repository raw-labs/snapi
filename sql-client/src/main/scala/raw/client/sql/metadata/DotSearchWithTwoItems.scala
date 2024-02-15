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

object DotSearchWithTwoItems
    extends Completion(
      """-- columns that belong to tables having the required name in schemas having the required name
        |SELECT table_schema, table_name, column_name, data_type AS type
        |FROM information_schema.columns
        |WHERE table_schema = ?
        |    AND table_name = ?
        |    AND table_schema NOT IN ('pg_catalog', 'information_schema')""".stripMargin
    ) {
  override protected def setParams(preparedStatement: PreparedStatement, items: Seq[String]): Unit = {
    val item1 = items(0)
    val item2 = items(1)
    preparedStatement.setString(1, item1)
    preparedStatement.setString(2, item2)
  }
}
