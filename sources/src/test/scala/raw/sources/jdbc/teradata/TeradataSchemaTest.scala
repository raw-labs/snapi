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
//import raw.utils.RawTestSuite
//import raw.creds.jdbc.RDBMSTestCreds
//import raw.sources.api.SourcesTestContext
//import raw.testing.tags.TeradataTests
//
//@TeradataTests
//class TeradataSchemaTest extends RawTestSuite with SourcesTestContext with RDBMSTestCreds {
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
//  test("list tables") { _ =>
//    val s = new TeradataSchema(dbSystem, "db", "rdbmstest")
//    val tables = s.listTables().toList
//
//    List("tbl1", "tbl2", "tbl3", "tbl4").foreach(t => assert(tables.contains(s"teradata:db/rdbmstest/$t")))
//  }
//
//  test("url") { _ =>
//    val s = new TeradataSchema(dbSystem, "db", "rdbmstest")
//    assert(s.rawUri == "teradata:db/rdbmstest")
//  }
//
//  test("test schema") { _ =>
//    val s = new TeradataSchema(dbSystem, "db", "rdbmstest")
//    s.testAccess()
//  }
//}
