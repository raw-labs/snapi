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

package raw.sources.jdbc.oracle

import raw.sources.api.{LocationDescription, OptionDefinition, SourceContext, StringOptionType}
import raw.sources.jdbc.api.{JdbcSchemaLocation, JdbcSchemaLocationBuilder}

object OracleSchemaLocationBuilder {
  private val REGEX = """oracle:(?://)?([^:/]+)(?::(\d+))?/([^/]+)/([^/]+)""".r

  private val CONFIG_USERNAME = "username"
  private val CONFIG_PASSWORD = "password"
}

class OracleSchemaLocationBuilder extends JdbcSchemaLocationBuilder {

  import OracleSchemaLocationBuilder._

  override def schemes: Seq[String] = Seq("oracle")

  override def validOptions: Seq[OptionDefinition] = Seq(
    OptionDefinition(CONFIG_USERNAME, StringOptionType, mandatory = true),
    OptionDefinition(CONFIG_PASSWORD, StringOptionType, mandatory = true)
  )

  override def build(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): JdbcSchemaLocation = {
    val url = desc.url
    val groups = getRegexMatchingGroups(url, REGEX)
    val List(host, portOrNull, dbName, schema) = groups
    val username = desc.getString(CONFIG_USERNAME)
    val password = desc.getString(CONFIG_PASSWORD)
    val port = if (portOrNull == null) 1521 else portOrNull.toInt
    val db = new OracleClient(host, port, dbName, username, password)(sourceContext.settings)
    new OracleSchemaLocation(db, dbName, schema)
  }
}
