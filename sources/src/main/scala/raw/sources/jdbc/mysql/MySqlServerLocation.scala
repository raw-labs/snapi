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
import raw.utils.RawSettings

class MySqlServerLocation private (cli: MySqlClient) extends JdbcServerLocation(cli, "mysql") {

  def this(config: MySqlServerConfig)(implicit settings: RawSettings) = {
    this(new MySqlClient(config.host, config.port, config.dbName, config.username, config.dbName))
  }

  override def listSchemas(): Iterator[JdbcSchemaLocation] with Closeable = {
    throw new JdbcLocationException("no schemas in mysql")
  }

}
