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

import raw.sources.{LocationDescription, LocationException, SourceContext}
import raw.sources.jdbc.{JdbcSchemaLocation, JdbcSchemaLocationBuilder}

class SqliteSchemaLocationBuilder extends JdbcSchemaLocationBuilder {

  private val sqliteRegex = """sqlite:(?://)?(.+)""".r

  override def schemes: Seq[String] = Seq("sqlite")

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcSchemaLocation = {
    location.url match {
      case sqliteRegex(locationUrl) =>
        val db = SqliteClients.get(locationUrl)
        new SqliteSchema(db)
      case _ => throw new LocationException("not a sqlite schema location")
    }
  }

}
