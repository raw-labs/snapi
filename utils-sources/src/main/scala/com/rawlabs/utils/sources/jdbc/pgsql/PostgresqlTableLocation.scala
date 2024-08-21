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

package com.rawlabs.utils.sources.jdbc.pgsql

import com.rawlabs.utils.sources.jdbc.api.JdbcTableLocation
import com.rawlabs.utils.core.RawSettings

class PostgresqlTableLocation(
    cli: PostgresqlClient,
    val schema: String,
    val table: String
) extends JdbcTableLocation(cli, Some(schema), table) {

  val host: String = cli.hostname

  val port: Int = cli.port

  val dbName: String = cli.maybeDatabase.get

  val username: String = cli.maybeUsername.get

  val password: String = cli.maybePassword.get

  def this(
      host: String,
      port: Int,
      dbName: String,
      username: String,
      password: String,
      schema: String,
      tableName: String
  )(implicit settings: RawSettings) = {
    this(
      new PostgresqlClient(host, port, dbName, username, password),
      schema,
      tableName
    )
  }

}
