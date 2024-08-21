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

package com.rawlabs.utils.sources.jdbc.sqlite

import java.io.Closeable
import com.rawlabs.utils.sources.jdbc.api.{JdbcSchemaLocation, JdbcTableLocation}
import com.rawlabs.utils.core.RawSettings

class SqliteSchemaLocation(
    cli: SqliteClient
) extends JdbcSchemaLocation(cli, None) {

  val path: String = cli.path

  def this(path: String)(implicit settings: RawSettings) = {
    this(new SqliteClient(path))
  }

  override def listTables(): Iterator[JdbcTableLocation] with Closeable = {
    new Iterator[JdbcTableLocation] with Closeable {
      private val it = cli.listTables("")

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcTableLocation = {
        new SqliteTableLocation(cli, it.next())
      }

      override def close(): Unit = it.close()
    }
  }
}
