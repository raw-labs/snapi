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

import com.rawlabs.utils.sources.jdbc.api.JdbcTableLocation
import com.rawlabs.utils.core.RawSettings

import scala.collection.JavaConverters._

class SnowflakeTableLocation(
    cli: SnowflakeClient,
    val schema: String,
    val table: String
) extends JdbcTableLocation(cli, Some(schema.toUpperCase), table.toUpperCase) {

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
      schema: String,
      tableName: String
  )(implicit settings: RawSettings) = {
    this(
      new SnowflakeClient(
        dbName,
        username,
        password,
        accountIdentifier,
        parameters
      ),
      schema,
      tableName
    )
  }

  def this(
      dbName: String,
      username: String,
      password: String,
      accountIdentifier: String,
      parameters: java.util.Map[String, String],
      schema: String,
      tableName: String
  )(implicit settings: RawSettings) = {
    this(
      new SnowflakeClient(
        dbName,
        username,
        password,
        accountIdentifier,
        parameters.asScala.toMap
      ),
      schema,
      tableName
    )
  }

}
