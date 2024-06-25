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
import raw.sources.jdbc.api._

class SqliteLocation(
    cli: SqliteClient,
    dbName: String
) extends JdbcLocation(cli, "sqlite", dbName) {

  private val path = cli.sqlitePath.toString

  override def rawUri: String = s"sqlite://$path?db=$dbName"

  override def listSchemas(): Iterator[JdbcSchemaLocation] with Closeable = {
    throw new JdbcLocationException("no schemas in sqlite")
  }

}
