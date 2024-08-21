/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.compiler.snapi.inferrer.local

import com.rawlabs.utils.core.{RawTestSuite, SettingsTestContext}
import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.compiler.snapi.inferrer.api._
import com.rawlabs.compiler.snapi.inferrer.local.jdbc.JdbcInferrer
import com.rawlabs.utils.sources.jdbc.mysql.{MySqlClient, MySqlTableLocation}

class RD10439 extends RawTestSuite with SettingsTestContext with StrictLogging {

  val mysqlHostname: String = sys.env("RAW_MYSQL_TEST_HOST")
  val mysqlDb: String = sys.env("RAW_MYSQL_TEST_DB")
  val mysqlUsername: String = sys.env("RAW_MYSQL_TEST_USER")
  val mysqlPassword: String = sys.env("RAW_MYSQL_TEST_PASSWORD")

  test("infer mysql table which is repeated in another database") { _ =>
    val inferrer = new JdbcInferrer()
    val client = new MySqlClient(mysqlHostname, 3306, mysqlDb, mysqlUsername, mysqlPassword)
    val location = new MySqlTableLocation(client, "rd10439")
    val tipe = inferrer.getTableType(location)
    logger.info(s"tipe: $tipe")
    val expected = SourceCollectionType(
      SourceRecordType(
        Vector(
          SourceAttrType("id", SourceIntType(true)),
          SourceAttrType("name", SourceStringType(true)),
          SourceAttrType("salary", SourceFloatType(true))
        ),
        nullable = false
      ),
      nullable = false
    )
    assert(tipe == expected)
  }
}
