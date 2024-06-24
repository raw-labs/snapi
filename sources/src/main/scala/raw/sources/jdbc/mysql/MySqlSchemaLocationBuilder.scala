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
import raw.sources.jdbc.api.{JdbcSchemaLocation, JdbcSchemaLocationBuilder}
import raw.sources.jdbc.mysql.MySqlLocationBuilder.{CONFIG_PASSWORD, CONFIG_USERNAME, REGEX}

import scala.util.matching.Regex

object MySqlSchemaLocationBuilder {
  private val REGEX = """mysql:(?://)?([^:/]+)(?::(\d+))?/(.+)""".r

  private val CONFIG_USERNAME = "username"
  private val CONFIG_PASSWORD = "password"
}

class MySqlSchemaLocationBuilder extends JdbcSchemaLocationBuilder {

  import MySqlSchemaLocationBuilder._

  override def schemes: Seq[String] = Seq("mysql")

  override def regex: Regex = REGEX

  override def validOptions: Map[String, OptionType] = Map(
    CONFIG_USERNAME -> StringOptionType,
    CONFIG_PASSWORD -> StringOptionType
  )

  override def build(groups: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): JdbcSchemaLocation = {
    val List(host, port, dbName) = groups
    val username = options
      .get(CONFIG_USERNAME)
      .map(_.asInstanceOf[StringOptionValue].value)
      .getOrElse(throw new LocationException("username is required"))
    val password = options
      .get(CONFIG_PASSWORD)
      .map(_.asInstanceOf[StringOptionValue].value)
      .getOrElse(throw new LocationException("password is required"))
    val db = new MySqlClient(host, port.toInt, dbName, username, password)(sourceContext.settings)
    new MySqlSchema(db, db.database.get)
  }

}
