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
//import raw.utils.{RawTestSuite, SettingsTestContext}
//import raw.creds.api.TeradataCredential
//import raw.creds.jdbc.RDBMSTestCreds
//import raw.sources.jdbc.api.AuthenticationFailedException
//import raw.testing.tags.TeradataTests
//
//@TeradataTests
//class TeradataLocationTest extends RawTestSuite with SettingsTestContext with RDBMSTestCreds {
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
//  test("test connection")(_ => dbSystem.testAccess())
//
//  test("test failed connection") { _ =>
//    val invalid = TeradataCredential("test-teradata.raw-labs.com", None, Some("rawtest"), None)
//    val dbSystem = new TeradataClient(invalid)
//    intercept[AuthenticationFailedException] {
//      dbSystem.testAccess()
//    }
//  }
//
//  test("list tables") { _ =>
//    val tables = dbSystem.listTables("rdbmstest").toList
//    List("tbl1", "tbl2", "tbl3", "tbl4").foreach(t => assert(tables.contains(t)))
//  }
//
//  test("list schemas") { _ =>
//    val schemas = dbSystem.listSchemas.toList
//    assert(schemas.contains("rdbmstest"))
//    // as users are also schemas rawtest should also be here
//    assert(schemas.contains("rawtest"))
//  }
//
//}
