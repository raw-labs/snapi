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

package raw.sources.jdbc.pgsql

import java.io.Closeable
import raw.sources.jdbc.api._
import raw.utils.RawSettings

class PostgresqlServerLocation(
    cli: PostgresqlClient,
    dbName: String
) extends JdbcServerLocation(cli, "pgsql") {

  def this(config: PostgresqlServerConfig)(implicit settings: RawSettings) = {
    this(new PostgresqlClient(config.host, config.port, config.dbName, config.username, config.password), config.dbName)
  }

  override def listSchemas(): Iterator[JdbcSchemaLocation] with Closeable = {
    new Iterator[JdbcSchemaLocation] with Closeable {
      private val it = cli.listSchemas

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcSchemaLocation = {
        new PostgresqlSchemaLocation(cli, dbName, it.next())
      }

      override def close(): Unit = it.close()
    }
  }

}
