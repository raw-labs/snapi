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

import raw.client.api.{OptionType, OptionValue, StringOptionType}
import raw.sources.api.SourceContext
import raw.sources.jdbc.api.{JdbcSchemaLocation, JdbcSchemaLocationBuilder}

import scala.util.matching.Regex

object SnowflakeSchemaLocationBuilder {
  private val REGEX = """snowflake:(?://)?([^/]+)/([^/]+)""".r

  private val CONFIG_USERNAME = "username"
  private val CONFIG_PASSWORD = "password"
  private val CONFIG_ACCOUNT_IDENTIFIER = "accountIdentifier"
  private val CONFIG_PARAMETERS = "parameters"
}

class SnowflakeSchemaLocationBuilder extends JdbcSchemaLocationBuilder {

  import SnowflakeSchemaLocationBuilder._

  override def schemes: Seq[String] = Seq("snowflake")

  override def regex: Regex = REGEX

  override def validOptions: Map[String, OptionType] = Map(
    CONFIG_USERNAME -> StringOptionType,
    CONFIG_PASSWORD -> StringOptionType
  )

  override def build(groups: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): JdbcSchemaLocation = {
    val List(host, dbName, schema) = groups
    val username = getStringOption(options, CONFIG_USERNAME)
    val password = getStringOption(options, CONFIG_PASSWORD)
    val accountIdentifier = getStringOption(options, CONFIG_ACCOUNT_IDENTIFIER)
    val parameters = getMapStringToStringOption(options, CONFIG_PARAMETERS)
    val db =
      new SnowflakeClient(host, dbName, username, password, accountIdentifier, parameters)(sourceContext.settings)
    new SnowflakeSchema(db, dbName, schema)
  }
}
