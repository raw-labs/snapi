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

import raw.sources.api.{LocationDescription, LocationException, OptionDefinition, SourceContext}
import raw.sources.jdbc.api.{JdbcTableLocation, JdbcTableLocationBuilder}

import java.nio.file.{InvalidPathException, Paths}

object SqliteTableLocationBuilder {
  private val REGEX = """mysql:(?://)?([^?]+)\?db=([^&]+)&table=([^&]+)""".r
}

class SqliteTableLocationBuilder extends JdbcTableLocationBuilder {

  import SqliteTableLocationBuilder._

  override def schemes: Seq[String] = Seq("sqlite")

  override def validOptions: Seq[OptionDefinition] = Seq.empty

  override def build(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): JdbcTableLocation = {
    val url = desc.url
    val groups = getRegexMatchingGroups(url, REGEX)
    val List(path, dbName, table) = groups
    val localPath =
      try {
        Paths.get(path)
      } catch {
        case _: InvalidPathException => throw new LocationException("invalid path")
      }
    val cli = new SqliteClient(localPath)(sourceContext.settings)
    new SqliteTableLocation(cli, dbName, table)
  }

}