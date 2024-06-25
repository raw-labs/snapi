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

import raw.sources.api.SourceContext
import raw.client.api.{OptionType, OptionValue, StringOptionType}
import raw.sources.jdbc.api.{JdbcLocation, JdbcLocationBuilder}

import scala.util.matching.Regex

object PostgresqlLocationBuilder {
  private val REGEX = """pgsql:(?://)?([^:/]+)(?::(\d+))?/([^/]+)""".r

  private val CONFIG_USERNAME = "username"
  private val CONFIG_PASSWORD = "password"
}

class PostgresqlLocationBuilder extends JdbcLocationBuilder {

  import PostgresqlLocationBuilder._

  override def schemes: Seq[String] = Seq("pgsql")

  override def regex: Regex = REGEX

  override def validOptions: Map[String, OptionType] = Map(
    CONFIG_USERNAME -> StringOptionType,
    CONFIG_PASSWORD -> StringOptionType
  )

  override def build(groups: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): JdbcLocation = {
    val List(host, portOrNull, dbName) = groups
    val username = getStringOption(options, CONFIG_USERNAME)
    val password = getStringOption(options, CONFIG_PASSWORD)
    val port = if (portOrNull == null) 5432 else portOrNull.toInt
    val db = new PostgresqlClient(host, port, dbName, username, password)(sourceContext.settings)
    new PostgresqlLocation(db, dbName)
  }

}
