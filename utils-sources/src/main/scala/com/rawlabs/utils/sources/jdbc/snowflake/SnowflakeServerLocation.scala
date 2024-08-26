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
import com.rawlabs.utils.sources.jdbc.api._
import com.rawlabs.utils.core.RawSettings
import scala.collection.JavaConverters._

class SnowflakeServerLocation(
    val dbName: String,
    val username: String,
    val password: String,
    val accountIdentifier: String,
    val parameters: Map[String, String]
)(implicit settings: RawSettings)
    extends JdbcServerLocation(
      new SnowflakeClient(
        dbName,
        username,
        password,
        accountIdentifier,
        parameters
      )
    ) {

  // Constructor for Java.
  def this(
      dbName: String,
      username: String,
      password: String,
      accountIdentifier: String,
      parameters: java.util.Map[String, String],
      settings: RawSettings
  ) = this(dbName, username, password, accountIdentifier, parameters.asScala.toMap)(settings)

  override def listSchemas(): Iterator[JdbcSchemaLocation] with Closeable = {
    new Iterator[JdbcSchemaLocation] with Closeable {
      private val cli = jdbcClient.asInstanceOf[SnowflakeClient]
      private val it = cli.listSchemas

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcSchemaLocation = {
        new SnowflakeSchemaLocation(cli, it.next())
      }

      override def close(): Unit = it.close()
    }
  }
}
