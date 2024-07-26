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

package raw.sources.jdbc.snowflake

import raw.sources.jdbc.api.JdbcTableLocation
import raw.utils.RawSettings

class SnowflakeTableLocation(
    cli: SnowflakeClient,
    dbName: String,
    schema: String,
    table: String
) extends JdbcTableLocation(cli, "snowflake", dbName, table.toUpperCase, Some(schema.toUpperCase)) {

  def this(config: SnowflakeTableConfig)(implicit settings: RawSettings) = {
    this(
      new SnowflakeClient(
        config.host,
        config.dbName,
        config.username,
        config.password,
        config.accountIdentifier,
        config.parameters
      ),
      config.dbName,
      config.schema,
      config.tableName
    )
  }

}
