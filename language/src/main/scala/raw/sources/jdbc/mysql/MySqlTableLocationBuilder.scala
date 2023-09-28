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

import raw.sources.api.{LocationDescription, LocationException, SourceContext}
import raw.sources.jdbc.api.{JdbcTableLocation, JdbcTableLocationBuilder}

class MySqlTableLocationBuilder extends JdbcTableLocationBuilder {

  private val mysqlTableRegex = """mysql:(?://)?([^/]+)/([^/]+)""".r

  override def schemes: Seq[String] = Seq("mysql")

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcTableLocation = {
    location.url match {
      case mysqlTableRegex(dbName, table) =>
        val db = MySqlClients.get(dbName, location)
        new MySqlTable(db, dbName, table)
      case _ => throw new LocationException("not a mysql table location")
    }
  }

}
