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

package raw.sources.jdbc.teradata

import raw.client.api.LocationDescription
import raw.sources.api.{LocationException, SourceContext}
import raw.sources.jdbc.api.{JdbcLocation, JdbcLocationBuilder}

class TeradataLocationBuilder extends JdbcLocationBuilder {

  private val teradataDbRegex = """teradata:(?://)?([^/]+)""".r

  override def schemes: Seq[String] = Seq("teradata")

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcLocation = {
    location.url match {
      case teradataDbRegex(dbName) =>
        val db = TeradataClients.get(dbName)
        new TeradataLocation(db, dbName)
      case _ => throw new LocationException("not a teradata database location")
    }
  }

}
