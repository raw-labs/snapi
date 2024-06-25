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

package raw.sources.jdbc.mysql

import raw.sources.api.{LocationException, SourceContext}
import raw.client.api.{LocationDescription, OptionType, OptionValue, StringOptionType, StringOptionValue}
import raw.sources.jdbc.api.{JdbcTableLocation, JdbcTableLocationBuilder}

import scala.util.matching.Regex

object MySqlTableLocationBuilder {
  private val REGEX = """mysql:(?://)?([^:/]+)(?::(\d+))?/([^/]+)/([^/]+)""".r

  private val CONFIG_USERNAME = "username"
  private val CONFIG_PASSWORD = "password"
}

class MySqlTableLocationBuilder extends JdbcTableLocationBuilder {

  import MySqlTableLocationBuilder._

  override def schemes: Seq[String] = Seq("mysql")

  override def regex: Regex = REGEX

  override def validOptions: Map[String, OptionType] = Map(
    CONFIG_USERNAME -> StringOptionType,
    CONFIG_PASSWORD -> StringOptionType
  )

  override def build(groups: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): JdbcTableLocation = {
    val List(host, portOrNull, dbName, table) = groups
    val username = getStringOption(options, CONFIG_USERNAME)
    val password = getStringOption(options, CONFIG_PASSWORD)
    val port = if (portOrNull == null) 3306 else portOrNull.toInt
    val db = new MySqlClient(host, port, dbName, username, password)(sourceContext.settings)
    new MySqlTable(db, dbName, table)
  }

}
