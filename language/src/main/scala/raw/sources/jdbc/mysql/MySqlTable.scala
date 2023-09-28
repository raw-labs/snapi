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

package raw.sources.jdbc.mysql

import raw.sources.jdbc.api.JdbcTableLocation

class MySqlTable(
    cli: MySqlClient,
    dbName: String,
    table: String
) extends JdbcTableLocation(cli, "mysql", dbName, table, None) {

  override def rawUri: String = s"mysql:$dbName/$table"

}
