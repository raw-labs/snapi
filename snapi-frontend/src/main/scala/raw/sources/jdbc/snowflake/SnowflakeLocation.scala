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

package raw.sources.jdbc.snowflake

import java.io.Closeable
import raw.sources.jdbc.api._

class SnowflakeLocation(
    cli: SnowflakeClient,
    dbName: String
) extends JdbcLocation(cli, "snowflake", dbName) {

  override def rawUri: String = s"snowflake:$dbName"

  override def listSchemas(): Iterator[String] with Closeable = {
    new Iterator[String] with Closeable {
      private val it = cli.listSchemas

      override def hasNext: Boolean = it.hasNext

      override def next(): String = s"snowflake:$dbName/${it.next()}"

      override def close(): Unit = it.close()
    }
  }
}
