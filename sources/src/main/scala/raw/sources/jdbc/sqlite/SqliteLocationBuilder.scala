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
import raw.sources.jdbc.api.{JdbcLocation, JdbcLocationBuilder}

import java.nio.file.{InvalidPathException, Paths}
import scala.util.matching.Regex

object SqliteLocationBuilder {
  private val REGEX = """mysql:(?://)?([^?]+)\?db=([^&]+)""".r
}

class SqliteLocationBuilder extends JdbcLocationBuilder {

  import SqliteLocationBuilder._

  override def schemes: Seq[String] = Seq("sqlite")

  override def regex: Regex = REGEX

  override def validOptions: Map[String, OptionType] = Map.empty

  override def build(groups: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): JdbcLocation = {
    val List(path, dbName) = groups
    val localPath =
      try {
        Paths.get(path)
      } catch {
        case _: InvalidPathException => throw new LocationException("invalid path")
      }
    val cli = new SqliteClient(localPath)(sourceContext.settings)
    new SqliteLocation(cli, dbName)
  }

}
