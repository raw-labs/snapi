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

package com.rawlabs.utils.sources.jdbc.teradata

import com.rawlabs.utils.sources.jdbc.api.{JdbcSchemaLocation, JdbcServerLocation}
import com.rawlabs.utils.core.RawSettings

import java.io.Closeable

class TeradataServerLocation(
    val host: String,
    val port: Int,
    val dbName: String,
    val username: String,
    val password: String,
    val parameters: Map[String, String]
)(
    implicit settings: RawSettings
) extends JdbcServerLocation(new TeradataClient(host, port, dbName, username, password, parameters)) {

  override def listSchemas(): Iterator[JdbcSchemaLocation] with Closeable = {
    new Iterator[JdbcSchemaLocation] with Closeable {
      private val cli = jdbcClient.asInstanceOf[TeradataClient]
      private val it = cli.listSchemas

      override def hasNext: Boolean = it.hasNext

      override def next(): JdbcSchemaLocation = {
        new TeradataSchemaLocation(cli, it.next())
      }

      override def close(): Unit = it.close()
    }
  }

}
