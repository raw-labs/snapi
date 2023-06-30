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

import raw.sources.{LocationDescription, LocationException, SourceContext}
import raw.sources.jdbc.{JdbcLocation, JdbcLocationBuilder}

class SnowflakeLocationBuilder extends JdbcLocationBuilder {

  private val snowflakeTableRegex = """snowflake:(?://)?([^/]+)""".r

  override def schemes: Seq[String] = Seq("snowflake")

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcLocation = {
    location.url match {
      case snowflakeTableRegex(dbName) =>
        val db = SnowflakeClients.get(dbName, location)
        new SnowflakeLocation(db, dbName, location.cacheStrategy, location.retryStrategy)
      case _ => throw new LocationException("not an snowflake database location")
    }
  }

}
