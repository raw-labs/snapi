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

///*
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
//package raw.sources.jdbc.teradata
//
//import org.scalatest.BeforeAndAfterAll
//import raw.utils.RawTestSuite
//import raw.creds.jdbc.RDBMSTestCreds
//import raw.sources.api.SourcesTestContext
//import raw.sources.jdbc.api.{JdbcColumnType, TableColumn, TableMetadata}
//
//import java.sql.Types._
//import raw.testing.tags.TeradataTests
//
//@TeradataTests
//class TeradataTableTest extends RawTestSuite with SourcesTestContext with RDBMSTestCreds with BeforeAndAfterAll {
//
//  val dbSystem = new TeradataClient(teradataCreds)
//
//  override def beforeAll(): Unit = {
//    super.beforeAll()
//    // (CTM) while we sort out our snowflake installation
//    // this will fail immediately instead of polluting tier2 tests
//    dbSystem.listSchemas
//  }
//
//  test("get type") { _ =>
//    val t = new TeradataTable(dbSystem, "db", "rdbmstest", "tbl1")
//    assert(
//      t.getType() == TableMetadata(
//        List(
//          TableColumn("a", JdbcColumnType(INTEGER, 0)),
//          TableColumn("b", JdbcColumnType(INTEGER, 1)),
//          TableColumn("c", JdbcColumnType(FLOAT, 0)),
//          TableColumn("d", JdbcColumnType(FLOAT, 1)),
//          TableColumn("x", JdbcColumnType(VARCHAR, 0)),
//          TableColumn("y", JdbcColumnType(VARCHAR, 1))
//        ),
//        None
//      )
//    )
//  }
//
//  test("url") { _ =>
//    val t = new TeradataTable(dbSystem, "db", "rdbmstest", "tbl1")
//    assert(t.rawUri == "teradata:db/rdbmstest/tbl1")
//  }
//
//  test("test access") { _ =>
//    val t = new TeradataTable(dbSystem, "db", "rdbmstest", "tbl1")
//    t.testAccess()
//  }
//}
