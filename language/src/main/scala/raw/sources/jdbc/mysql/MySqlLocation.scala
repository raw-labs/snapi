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
import raw.sources.jdbc.api._

class MySqlLocation(
    cli: MySqlClient,
    dbName: String
) extends JdbcLocation(cli, "mysql", dbName) {

  override def rawUri: String = s"mysql:$dbName"

  override def listSchemas(): Iterator[String] with Closeable = {
    throw new JdbcLocationException("no schemas in mysql")
  }
}
