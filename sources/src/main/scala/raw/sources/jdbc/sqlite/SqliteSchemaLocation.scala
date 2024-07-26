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

package raw.sources.jdbc.sqlite

import java.io.Closeable
import raw.sources.jdbc.api.{JdbcSchemaLocation, JdbcTableLocation}
import raw.utils.RawSettings

class SqliteSchemaLocation(
    cli: SqliteClient,
    dbName: String
) extends JdbcSchemaLocation(cli, None) {

  def this(config: SqliteSchemaConfig)(implicit settings: RawSettings) = {
    this(new SqliteClient(config.path), config.dbName)
  }

  override def listTables(): Iterator[JdbcTableLocation] with Closeable = {
    new Iterator[JdbcTableLocation] with Closeable {
      private val it = cli.listTables("")

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcTableLocation = {
        new SqliteTableLocation(cli, dbName, it.next())
      }

      override def close(): Unit = it.close()
    }
  }
}
