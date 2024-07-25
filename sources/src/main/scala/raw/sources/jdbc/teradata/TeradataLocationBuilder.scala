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

import raw.sources.api.{LocationDescription, MapOptionType, OptionDefinition, SourceContext, StringOptionType}
import raw.sources.jdbc.api.{JdbcLocation, JdbcLocationBuilder}

object TeradataLocationBuilder {
  private val REGEX = """teradata:(?://)?([^:/]+)(?::(\d+))?/([^/]+)""".r

  private val CONFIG_USERNAME = "username"
  private val CONFIG_PASSWORD = "password"
  private val CONFIG_PARAMETERS = "parameters"
}

class TeradataLocationBuilder extends JdbcLocationBuilder {

  import TeradataLocationBuilder._

  override def schemes: Seq[String] = Seq("teradata")

  override def validOptions: Seq[OptionDefinition] = Seq(
    OptionDefinition(CONFIG_USERNAME, StringOptionType, mandatory = true),
    OptionDefinition(CONFIG_PASSWORD, StringOptionType, mandatory = true),
    OptionDefinition(CONFIG_PARAMETERS, MapOptionType(StringOptionType, StringOptionType), mandatory = false)
  )

  override def build(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): JdbcLocation = {
    val url = desc.url
    val groups = getRegexMatchingGroups(url, REGEX)
    val List(host, portOrNull, dbName) = groups
    val username = desc.getString(CONFIG_USERNAME)
    val password = desc.getString(CONFIG_PASSWORD)
    val parameters = desc.getMapStringStringOpt(CONFIG_PARAMETERS).getOrElse(Map.empty)
    val port = if (portOrNull == null) 1025 else portOrNull.toInt
    val db = new TeradataClient(host, port, dbName, username, password, parameters)(sourceContext.settings)
    new TeradataLocation(db, dbName)
  }

}
