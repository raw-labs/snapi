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

package raw.sources.jdbc.sqlserver

import raw.sources.api.{LocationException, SourceContext}
import raw.client.api.LocationDescription
import raw.sources.jdbc.api.{JdbcLocation, JdbcLocationBuilder}

class SqlServerLocationBuilder extends JdbcLocationBuilder {

  private val sqlServerTableRegex = """sqlserver:(?://)?([^/]+)""".r

  override def schemes: Seq[String] = Seq("sqlserver")

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcLocation = {
    location.url match {
      case sqlServerTableRegex(dbName) =>
        val db = SqlServerClients.get(dbName, location)
        new SqlServerLocation(db, db.database.get)
      case _ => throw new LocationException("not a sqlserver database location")
    }
  }

}
