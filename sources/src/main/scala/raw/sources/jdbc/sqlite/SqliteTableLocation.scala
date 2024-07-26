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

package raw.sources.jdbc.sqlite

import raw.sources.jdbc.api._
import raw.utils.RawSettings

class SqliteTableLocation(
    cli: SqliteClient,
    dbName: String,
    table: String
) extends JdbcTableLocation(cli, "sqlite", dbName, table, None) {

  def this(config: SqliteTableConfig)(implicit settings: RawSettings) = {
    this(new SqliteClient(config.path), config.dbName, config.table)
  }

}
