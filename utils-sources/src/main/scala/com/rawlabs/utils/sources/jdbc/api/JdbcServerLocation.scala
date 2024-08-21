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

package com.rawlabs.utils.sources.jdbc.api

import java.sql.Connection
import com.rawlabs.utils.sources.api.Location

import java.io.Closeable

abstract class JdbcServerLocation(val jdbcClient: JdbcClient) extends Location {

  def listSchemas(): Iterator[JdbcSchemaLocation] with Closeable

  final def getJdbcConnection(): Connection = {
    jdbcClient.getConnection
  }

  final override def testAccess(): Unit = {
    jdbcClient.testAccess()
  }

}
