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

package raw.sources.jdbc.sqlserver

import java.io.Closeable
import raw.sources.jdbc.api._
import raw.utils.RawSettings

class SqlServerServerLocation(
    val host: String,
    val port: Int,
    val dbName: String,
    val username: String,
    val password: String
)(
    implicit settings: RawSettings
) extends JdbcServerLocation(new SqlServerClient(host, port, dbName, username, password)) {

  override def listSchemas(): Iterator[JdbcSchemaLocation] with Closeable = {
    new Iterator[JdbcSchemaLocation] with Closeable {
      private val cli = jdbcClient.asInstanceOf[SqlServerClient]
      private val it = cli.listSchemas

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcSchemaLocation = {
        new SqlServerSchemaLocation(cli, it.next())
      }

      override def close(): Unit = it.close()
    }
  }

}
