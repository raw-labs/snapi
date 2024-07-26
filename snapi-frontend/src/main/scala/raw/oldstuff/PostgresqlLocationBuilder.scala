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

package raw.sources.jdbc.pgsql

import raw.sources.api.{LocationDescription, OptionDefinition, SourceContext, StringOptionType}
import raw.sources.jdbc.api.{JdbcServerLocation, JdbcLocationBuilder}

object PostgresqlLocationBuilder {
  private val REGEX = """pgsql:(?://)?([^:/]+)(?::(\d+))?/([^/]+)""".r

  private val CONFIG_USERNAME = "username"
  private val CONFIG_PASSWORD = "password"
}

class PostgresqlLocationBuilder extends JdbcLocationBuilder {

  import PostgresqlLocationBuilder._

  override def schemes: Seq[String] = Seq("pgsql")

  override def validOptions: Seq[OptionDefinition] = Seq(
    OptionDefinition(CONFIG_USERNAME, StringOptionType, mandatory = true),
    OptionDefinition(CONFIG_PASSWORD, StringOptionType, mandatory = true)
  )

  override def build(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): JdbcServerLocation = {
    val url = desc.url
    val groups = getRegexMatchingGroups(url, REGEX)
    val List(host, portOrNull, dbName) = groups
    val username = desc.getString(CONFIG_USERNAME)
    val password = desc.getString(CONFIG_PASSWORD)
    val port = if (portOrNull == null) 5432 else portOrNull.toInt
    val db = new PostgresqlClient(host, port, dbName, username, password)(sourceContext.settings)
    new PostgresqlServerLocation(db, dbName)
  }

}
