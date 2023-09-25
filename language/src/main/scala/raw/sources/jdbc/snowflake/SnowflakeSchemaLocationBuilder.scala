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

package raw.sources.jdbc.snowflake

import raw.sources.api.{LocationDescription, LocationException, SourceContext}
import raw.sources.jdbc.api.{JdbcSchemaLocation, JdbcSchemaLocationBuilder}

class SnowflakeSchemaLocationBuilder extends JdbcSchemaLocationBuilder {
  override def schemes: Seq[String] = Seq("snowflake")

  private val schemaRegex = """snowflake:(?://)?([^/]+)/([^/]+)""".r

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcSchemaLocation = {
    location.url match {
      case schemaRegex(dbName, schema) =>
        val db = SnowflakeClients.get(dbName, location)
        new SnowflakeSchema(db, dbName, schema)
      case _ => throw new LocationException("not an snowflake schema location")
    }
  }
}
