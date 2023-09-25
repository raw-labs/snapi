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

package raw.sources.jdbc.api

import com.typesafe.scalalogging.StrictLogging
import raw.sources.api.Location

abstract class JdbcTableLocation(
    val jdbcClient: JdbcClient,
    val vendor: String,
    val dbName: String,
    val table: String,
    val maybeSchema: Option[String]
) extends Location
    with StrictLogging {

  final override def testAccess(): Unit = {
    jdbcClient.testAccess(maybeSchema, table)
  }

  final def getType(): TableMetadata = {
    jdbcClient.tableMetadata(maybeSchema, table)
  }

}
