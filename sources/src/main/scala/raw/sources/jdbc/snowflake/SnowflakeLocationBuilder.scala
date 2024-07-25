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

import raw.sources.api.{LocationDescription, MapOptionType, OptionDefinition, SourceContext, StringOptionType}
import raw.sources.jdbc.api.{JdbcLocation, JdbcLocationBuilder}

object SnowflakeLocationBuilder {
  private val REGEX = """snowflake:(?://)?([^/]+)/([^/]+)""".r

  private val CONFIG_USERNAME = "username"
  private val CONFIG_PASSWORD = "password"
  private val CONFIG_ACCOUNT_IDENTIFIER = "accountIdentifier"
  private val CONFIG_PARAMETERS = "parameters"
}

class SnowflakeLocationBuilder extends JdbcLocationBuilder {

  import SnowflakeLocationBuilder._

  override def schemes: Seq[String] = Seq("snowflake")

  override def validOptions: Seq[OptionDefinition] = Seq(
    OptionDefinition(CONFIG_USERNAME, StringOptionType, mandatory = true),
    OptionDefinition(CONFIG_PASSWORD, StringOptionType, mandatory = true),
    OptionDefinition(CONFIG_ACCOUNT_IDENTIFIER, StringOptionType, mandatory = true),
    OptionDefinition(CONFIG_PARAMETERS, MapOptionType(StringOptionType, StringOptionType), mandatory = false)
  )
  override def build(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): JdbcLocation = {
    val url = desc.url
    val groups = getRegexMatchingGroups(url, REGEX)
    val List(host, dbName) = groups
    val username = desc.getString(CONFIG_USERNAME)
    val password = desc.getString(CONFIG_PASSWORD)
    val accountIdentifier = desc.getString(CONFIG_ACCOUNT_IDENTIFIER)
    val parameters = desc.getMapStringStringOpt(CONFIG_PARAMETERS).getOrElse(Map.empty)
    val db =
      new SnowflakeClient(host, dbName, username, password, accountIdentifier, parameters)(sourceContext.settings)
    new SnowflakeLocation(db, dbName)
  }

}
