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

import raw.client.api.{MapOptionType, OptionType, OptionValue, StringOptionType}
import raw.sources.api.SourceContext
import raw.sources.jdbc.api.{JdbcLocation, JdbcLocationBuilder}

import scala.util.matching.Regex

object TeradataLocationBuilder {
  private val REGEX = """teradata:(?://)?([^:/]+)(?::(\d+))?/([^/]+)""".r

  private val CONFIG_USERNAME = "username"
  private val CONFIG_PASSWORD = "password"
  private val CONFIG_PARAMETERS = "parameters"
}

class TeradataLocationBuilder extends JdbcLocationBuilder {

  import TeradataLocationBuilder._

  override def schemes: Seq[String] = Seq("teradata")

  override def regex: Regex = REGEX

  override def validOptions: Map[String, OptionType] = Map(
    CONFIG_USERNAME -> StringOptionType,
    CONFIG_PASSWORD -> StringOptionType,
    CONFIG_PARAMETERS -> MapOptionType(StringOptionType, StringOptionType)
  )

  override def build(groups: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): JdbcLocation = {
    val List(host, portOrNull, dbName) = groups
    val username = getStringOption(options, CONFIG_USERNAME)
    val password = getStringOption(options, CONFIG_PASSWORD)
    val parameters = getMapStringToStringOption(options, CONFIG_PARAMETERS)
    val port = if (portOrNull == null) 1025 else portOrNull.toInt
    val db = new TeradataClient(host, port, dbName, username, password, parameters)(sourceContext.settings)
    new TeradataLocation(db, dbName)
  }

}
