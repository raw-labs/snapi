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
import raw.sources.jdbc.api.{JdbcSchemaLocation, JdbcTableLocation}
import raw.utils.RawSettings

class TeradataSchemaLocation(
    cli: TeradataClient,
    val schema: String
) extends JdbcSchemaLocation(cli, Some(schema)) {

  val host: String = cli.hostname

  val port: Int = cli.port

  val dbName: String = cli.maybeDatabase.get

  val username: String = cli.maybeUsername.get

  val password: String = cli.maybePassword.get

  val parameters: Map[String, String] = cli.parameters

  def this(
      host: String,
      port: Int,
      dbName: String,
      username: String,
      password: String,
      schema: String,
      parameters: Map[String, String]
  )(implicit settings: RawSettings) = {
    this(
      new TeradataClient(host, port, dbName, username, password, parameters),
      schema
    )
  }

  override def listTables(): Iterator[JdbcTableLocation] with Closeable = {
    new Iterator[JdbcTableLocation] with Closeable {
      private val it = cli.listTables(schema)

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcTableLocation = {
        new TeradataTableLocation(cli, schema, it.next())
      }

      override def close(): Unit = it.close()
    }
  }

}
