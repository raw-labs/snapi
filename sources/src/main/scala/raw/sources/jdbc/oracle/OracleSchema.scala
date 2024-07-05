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
import raw.sources.jdbc.api.JdbcSchemaLocation

class OracleSchema(
    cli: OracleClient,
    dbName: String,
    schema: String
) extends JdbcSchemaLocation(cli, Some(schema)) {

  override def rawUri: String = s"oracle:$dbName/$schema"

  override def listTables(): Iterator[String] with Closeable = {
    new Iterator[String] with Closeable {
      private val it = cli.listTables(schema)

      override def hasNext: Boolean = it.hasNext

      override def next(): String = s"oracle:$dbName/$schema/${it.next()}"

      override def close(): Unit = it.close()
    }
  }

}
