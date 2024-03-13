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

package raw.inferrer.local

import com.typesafe.scalalogging.StrictLogging
import raw.client.api.{LocationDescription, LocationSettingKey, LocationStringSetting}
import raw.creds.api.MySqlCredential
import raw.inferrer.api._
import raw.inferrer.local.jdbc.JdbcInferrer
import raw.sources.api.SourceContext
import raw.sources.jdbc.api.JdbcTableLocationProvider
import raw.sources.jdbc.mysql.{MySqlClient, MySqlTable}
import raw.utils.{RawTestSuite, SettingsTestContext}

class RD10439 extends RawTestSuite with SettingsTestContext with StrictLogging {

  val mysqlHostname: String = sys.env("RAW_MYSQL_TEST_HOST")
  val mysqlDb: String = sys.env("RAW_MYSQL_TEST_DB")
  val mysqlUsername: String = sys.env("RAW_MYSQL_TEST_USER")
  val mysqlPassword: String = sys.env("RAW_MYSQL_TEST_PASSWORD")

  test("infer mysql table which is repeated in another database") { _ =>
    val mysqlCreds = MySqlCredential(mysqlHostname, None, mysqlDb, Some(mysqlUsername), Some(mysqlPassword))
    val inferrer = new JdbcInferrer()
    val client = new MySqlClient(mysqlCreds)
    val location = new MySqlTable(client, mysqlDb, "rd10439")
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
