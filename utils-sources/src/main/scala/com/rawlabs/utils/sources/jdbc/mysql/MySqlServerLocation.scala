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

package com.rawlabs.utils.sources.jdbc.mysql

import java.io.Closeable
import com.rawlabs.utils.sources.jdbc.api._
import com.rawlabs.utils.core.RawSettings

class MySqlServerLocation(
    val host: String,
    val port: Int,
    val dbName: String,
    val username: String,
    val password: String
)(
    implicit settings: RawSettings
) extends JdbcServerLocation(new MySqlClient(host, port, dbName, username, password)) {

  override def listSchemas(): Iterator[JdbcSchemaLocation] with Closeable = {
    throw new JdbcLocationException("no schemas in mysql")
  }

}
