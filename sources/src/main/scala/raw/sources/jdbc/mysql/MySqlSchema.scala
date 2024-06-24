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

package raw.sources.jdbc.mysql

import java.io.Closeable
import raw.sources.jdbc.api.JdbcSchemaLocation

class MySqlSchema(
    cli: MySqlClient,
    dbName: String
) extends JdbcSchemaLocation(cli, None) {

  override def rawUri: String = s"mysql:${cli.host}:${cli.port}/$dbName"

  override def listTables(): Iterator[String] with Closeable = {
    new Iterator[String] with Closeable {
      private val it = cli.listTables("")

      override def hasNext: Boolean = it.hasNext

      override def next(): String = s"mysql:$dbName/${it.next()}"

      override def close(): Unit = it.close()
    }
  }
}
