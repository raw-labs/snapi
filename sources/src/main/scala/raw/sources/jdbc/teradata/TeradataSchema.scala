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

package raw.sources.jdbc.teradata

import java.io.Closeable
import raw.sources.jdbc.api.JdbcSchemaLocation

// This might be misleading, this is a Teradata database but works in a similar way to a Oracle schema
// so just remember that like oracle users are also 'databases/schemas'
class TeradataSchema(
    cli: TeradataClient,
    dbName: String,
    schema: String
) extends JdbcSchemaLocation(cli, Some(schema)) {

  override def rawUri: String = s"teradata:$dbName/$schema"

  override def listTables(): Iterator[String] with Closeable = {
    new Iterator[String] with Closeable {
      private val it = cli.listTables(schema)

      override def hasNext: Boolean = it.hasNext

      override def next(): String = s"teradata:$dbName/$schema/${it.next()}"

      override def close(): Unit = it.close()
    }
  }

}