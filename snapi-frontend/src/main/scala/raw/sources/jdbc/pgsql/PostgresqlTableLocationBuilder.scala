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

import raw.client.api.LocationDescription
import raw.sources.jdbc.api.{JdbcTableLocation, JdbcTableLocationBuilder}
import raw.sources.api.{LocationException, SourceContext}

class PostgresqlTableLocationBuilder extends JdbcTableLocationBuilder {

  private val postgresqlTableRegex = """pgsql:(?://)?([^/]+)/([^/]+)/([^/]+)""".r

  override def schemes: Seq[String] = Seq("pgsql")

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcTableLocation = {
    location.url match {
      case postgresqlTableRegex(dbName, schema, table) =>
        val db = PostgresqlClients.get(dbName, location)
        new PostgresqlTable(db, db.database.get, schema, table)
      case _ => throw new LocationException("not a postgresql location")
    }

  }

}
