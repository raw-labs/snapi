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

import com.rawlabs.utils.sources.api.Location

import java.io.Closeable

abstract class JdbcSchemaLocation(
    val jdbcClient: JdbcClient,
    maybeSchema: Option[String]
) extends Location {

  def listTables(): Iterator[JdbcTableLocation] with Closeable

  final override def testAccess(): Unit = {
    maybeSchema match {
      case Some(s) => jdbcClient.testAccess(s)
      case None => jdbcClient.testAccess()
    }
  }

}
