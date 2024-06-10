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

import raw.client.api.LocationDescription
import raw.sources.api.{LocationException, SourceContext}
import raw.sources.jdbc.api.{JdbcSchemaLocation, JdbcSchemaLocationBuilder}

class OracleSchemaLocationBuilder extends JdbcSchemaLocationBuilder {
  override def schemes: Seq[String] = Seq("oracle")

  private val schemaRegex = """oracle:(?://)?([^/]+)/([^/]+)""".r

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcSchemaLocation = {
    location.url match {
      case schemaRegex(dbName, schema) =>
        val db = OracleClients.get(dbName, location)
        new OracleSchema(db, db.database.get, schema)
      case _ => throw new LocationException("not an oracle schema location")
    }
  }
}
