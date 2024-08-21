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

package com.rawlabs.utils.sources.jdbc.snowflake

import java.io.Closeable
import com.rawlabs.utils.sources.jdbc.api.{JdbcSchemaLocation, JdbcTableLocation}
import com.rawlabs.utils.core.RawSettings

class SnowflakeSchemaLocation(
    cli: SnowflakeClient,
    val schema: String
) extends JdbcSchemaLocation(cli, Some(schema)) {

  val dbName: String = cli.maybeDatabase.get

  val username: String = cli.maybeUsername.get

  val password: String = cli.maybePassword.get

  val accountIdentifier: String = cli.accountIdentifier

  val parameters: Map[String, String] = cli.parameters

  def this(
      dbName: String,
      username: String,
      password: String,
      accountIdentifier: String,
      parameters: Map[String, String],
      schema: String
  )(implicit settings: RawSettings) = {
    this(
      new SnowflakeClient(
        dbName,
        username,
        password,
        accountIdentifier,
        parameters
      ),
      schema
    )
  }

  override def listTables(): Iterator[JdbcTableLocation] with Closeable = {
    new Iterator[JdbcTableLocation] with Closeable {
      private val it = cli.listTables(schema)

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcTableLocation = {
        new SnowflakeTableLocation(cli, schema, it.next())
      }

      override def close(): Unit = it.close()
    }
  }

}
