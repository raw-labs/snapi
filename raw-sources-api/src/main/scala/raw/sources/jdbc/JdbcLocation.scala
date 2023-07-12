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

package raw.sources.jdbc

import java.sql.Connection
import com.typesafe.scalalogging.StrictLogging
import raw.sources.Location

import java.io.Closeable

abstract class JdbcLocation(val jdbcClient: JdbcClient, val vendor: String, val dbName: String)
    extends Location
    with StrictLogging {

  def listSchemas(): Iterator[String] with Closeable

  final def getJdbcConnection(): Connection = {
    withRetryStrategy {
      jdbcClient.getConnection
    }
  }

  final override def testAccess(): Unit = {
    withRetryStrategy {
      jdbcClient.testAccess()
    }
  }

}
