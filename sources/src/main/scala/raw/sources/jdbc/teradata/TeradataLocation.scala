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

import raw.sources.jdbc.api.{JdbcLocation, JdbcSchemaLocation}

import java.io.Closeable

class TeradataLocation(
    cli: TeradataClient,
    dbName: String
) extends JdbcLocation(cli, "teradata", dbName) {

  override def rawUri: String = s"teradata://${cli.hostname}:${cli.port}/$dbName"

  override def listSchemas(): Iterator[JdbcSchemaLocation] with Closeable = {
    new Iterator[JdbcSchemaLocation] with Closeable {
      private val it = cli.listSchemas

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcSchemaLocation = {
        new TeradataSchema(cli, dbName, it.next())
      }

      override def close(): Unit = it.close()
    }
  }

}
