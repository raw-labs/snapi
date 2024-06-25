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

class OracleLocation(
    cli: OracleClient,
    dbName: String
) extends JdbcLocation(cli, "oracle", dbName) {

  override def rawUri: String = s"oracle://${cli.hostname}:${cli.port}/$dbName"

  override def listSchemas(): Iterator[JdbcSchemaLocation] with Closeable = {
    new Iterator[JdbcSchemaLocation] with Closeable {
      private val it = cli.listSchemas

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcSchemaLocation = {
        new OracleSchema(cli, dbName, it.next())
      }

      override def close(): Unit = it.close()
    }
  }
}
