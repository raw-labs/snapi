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

import raw.client.api.{OptionType, OptionValue, StringOptionType}
import raw.sources.api.SourceContext
import raw.sources.jdbc.api.{JdbcLocation, JdbcLocationBuilder}

import scala.util.matching.Regex

object OracleLocationBuilder {
  private val REGEX = """oracle:(?://)?([^:/]+)(?::(\d+))?/([^/]+)""".r

  private val CONFIG_USERNAME = "username"
  private val CONFIG_PASSWORD = "password"
}

class OracleLocationBuilder extends JdbcLocationBuilder {

  import OracleLocationBuilder._

  override def schemes: Seq[String] = Seq("oracle")

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
    val port = if (portOrNull == null) 1521 else portOrNull.toInt
    val db = new OracleClient(host, port, dbName, username, password)(sourceContext.settings)
    new OracleLocation(db, dbName)
  }

}
