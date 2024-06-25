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

class SqliteSchema(
    cli: SqliteClient,
    dbName: String
) extends JdbcSchemaLocation(cli, None) {

  private val path = cli.sqlitePath.toString

  override def rawUri: String = s"sqlite://$path?db=$dbName"

  override def listTables(): Iterator[JdbcTableLocation] with Closeable = {
    new Iterator[JdbcTableLocation] with Closeable {
      private val it = cli.listTables("")

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcTableLocation = {
        new SqliteTable(cli, dbName, it.next())
      }

      override def close(): Unit = it.close()
    }
  }
}
