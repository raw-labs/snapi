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

package raw.sources.jdbc.teradata

import raw.sources.jdbc.api.JdbcTableLocation
import raw.utils.RawSettings

class TeradataTableLocation(
    cli: TeradataClient,
    dbName: String,
    schema: String,
    table: String
) extends JdbcTableLocation(cli, "teradata", dbName, table, Some(schema)) {

  def this(config: TeradataTableConfig)(implicit settings: RawSettings) = {
    this(
      new TeradataClient(config.host, config.port, config.dbName, config.username, config.password, config.parameters),
      config.dbName,
      config.schema,
      config.tableName
    )
  }

}
