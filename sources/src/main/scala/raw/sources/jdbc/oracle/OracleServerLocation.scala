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

import java.io.Closeable
import raw.sources.jdbc.api._
import raw.utils.RawSettings

class OracleServerLocation private (cli: OracleClient, dbName: String) extends JdbcServerLocation(cli, "oracle") {

  def this(config: OracleServerConfig)(implicit settings: RawSettings) = {
    this(new OracleClient(config.host, config.port, config.dbName, config.username, config.password), config.dbName)
  }

  override def listSchemas(): Iterator[JdbcSchemaLocation] with Closeable = {
    new Iterator[JdbcSchemaLocation] with Closeable {
      private val it = cli.listSchemas

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcSchemaLocation = {
        new OracleSchemaLocation(cli, dbName, it.next())
      }

      override def close(): Unit = it.close()
    }
  }
}