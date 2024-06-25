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

package raw.sources.jdbc.sqlserver

import raw.sources.api.SourceContext
import raw.client.api.{OptionType, OptionValue, StringOptionType}
import raw.sources.jdbc.api.{JdbcTableLocation, JdbcTableLocationBuilder}

import scala.util.matching.Regex

object SqlServerTableLocationBuilder {
  private val REGEX = """sqlserver:(?://)?([^:/]+)(?::(\d+))?/([^/]+)/([^/]+)/([^/]+)""".r

  private val CONFIG_USERNAME = "username"
  private val CONFIG_PASSWORD = "password"
}

class SqlServerTableLocationBuilder extends JdbcTableLocationBuilder {

  import SqlServerTableLocationBuilder._

  override def schemes: Seq[String] = Seq("sqlserver")

  override def regex: Regex = REGEX

  override def validOptions: Map[String, OptionType] = Map(
    CONFIG_USERNAME -> StringOptionType,
    CONFIG_PASSWORD -> StringOptionType
  )

  override def build(groups: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): JdbcTableLocation = {
    val List(host, portOrNull, dbName, schema, table) = groups
    val username = getStringOption(options, CONFIG_USERNAME)
    val password = getStringOption(options, CONFIG_PASSWORD)
    val port = if (portOrNull == null) 1433 else portOrNull.toInt
    val db = new SqlServerClient(host, port, dbName, username, password)(sourceContext.settings)
    new SqlServerTable(db, dbName, schema, table)
  }

}
