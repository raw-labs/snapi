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
import com.rawlabs.utils.sources.jdbc.api.{JdbcSchemaLocation, JdbcTableLocation}
import com.rawlabs.utils.core.RawSettings

class MySqlSchemaLocation private (cli: MySqlClient) extends JdbcSchemaLocation(cli, None) {

  val host: String = cli.hostname

  val port: Int = cli.port

  val dbName: String = cli.maybeDatabase.get

  val username: String = cli.maybeUsername.get

  val password: String = cli.maybePassword.get

  def this(host: String, port: Int, dbName: String, username: String, password: String)(
      implicit settings: RawSettings
  ) = {
    this(new MySqlClient(host, port, dbName, username, password))
  }

  override def listTables(): Iterator[JdbcTableLocation] with Closeable = {
    new Iterator[JdbcTableLocation] with Closeable {
      private val it = cli.listTables("") // Schema is ignored for MySQL.

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcTableLocation = {
        new MySqlTableLocation(cli, it.next())
      }

      override def close(): Unit = it.close()
    }
  }
}
