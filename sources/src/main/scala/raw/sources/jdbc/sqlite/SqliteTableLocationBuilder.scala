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

package raw.sources.jdbc.sqlite

import raw.sources.api.{LocationException, SourceContext}
import raw.client.api.{OptionType, OptionValue}
import raw.sources.jdbc.api.{JdbcTableLocation, JdbcTableLocationBuilder}

import java.nio.file.{InvalidPathException, Paths}
import scala.util.matching.Regex

object SqliteTableLocationBuilder {
  private val REGEX = """mysql:(?://)?([^?]+)\?db=([^&]+)&table=([^&]+)""".r
}

class SqliteTableLocationBuilder extends JdbcTableLocationBuilder {

  import SqliteTableLocationBuilder._

  override def schemes: Seq[String] = Seq("sqlite")

  override def regex: Regex = REGEX

  override def validOptions: Map[String, OptionType] = Map.empty

  override def build(groups: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): JdbcTableLocation = {
    val List(path, dbName, table) = groups
    val localPath =
      try {
        Paths.get(path)
      } catch {
        case _: InvalidPathException => throw new LocationException("invalid path")
      }
    val cli = new SqliteClient(localPath)(sourceContext.settings)
    new SqliteTable(cli, dbName, table)
  }

}
