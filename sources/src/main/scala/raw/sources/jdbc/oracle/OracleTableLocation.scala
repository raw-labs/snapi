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

package raw.sources.jdbc.oracle

import raw.sources.jdbc.api.JdbcTableLocation
import raw.utils.RawSettings

class OracleTableLocation(
    cli: OracleClient,
    dbName: String,
    schema: String,
    table: String
) extends JdbcTableLocation(cli, "oracle", dbName, table.toUpperCase, Some(schema.toUpperCase)) {

  def this(config: OracleTableConfig)(implicit settings: RawSettings) = {
    this(
      new OracleClient(config.host, config.port, config.dbName, config.username, config.password),
      config.dbName,
      config.schema,
      config.tableName
    )
  }

}
