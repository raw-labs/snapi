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

package com.rawlabs.snapi.compiler.tests.regressions.credentials

import com.rawlabs.snapi.compiler.tests.TestCredentials
import com.rawlabs.snapi.compiler.tests.Rql2TestContext

class RD3084Test extends Rql2TestContext {

  rdbms("mysql-test", TestCredentials.mysqlCreds)
  rdbms("postgres-test", TestCredentials.pgsqlCreds)
  rdbms("oracle-test", TestCredentials.oracleCreds)
  rdbms("mssql-test", TestCredentials.sqlServerCreds)

  test("""MySQL.InferAndQuery("mysql-test", "select * from test_types")""") {
    _ should run
  }

  // The field char1 is not the second in the table.
  // this would not work before (fields have be in the order)
  test("""MySQL.Query("mysql-test", "select * from test_types",
    |   type collection(record(integer1: int, char1: string)))""".stripMargin) {
    _ should evaluateTo("""[{integer1: 1, char1: "string"}]""")
  }

  // switching the order of the fields
  test("""MySQL.Query("mysql-test", "select * from test_types",
    |   type collection(record(char1: string, integer1: int)))""".stripMargin) {
    _ should evaluateTo("""[{char1: "string", integer1: 1}]""")
  }

  test("""PostgreSQL.InferAndQuery("postgres-test", "select * from rdbmstest.test_types")""") {
    _ should run
  }

  test("""PostgreSQL.Query("postgres-test", "select * from rdbmstest.test_types",
    |           type collection(record(integer1: int, char1: string)))""".stripMargin) {
    _ should run
  }

  test("""Oracle.InferAndQuery("oracle-test", "select * from rawtest.test_types")""") { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should run
  }

  test("""Oracle.Query("oracle-test", "select * from rawtest.test_types",
    |       type collection(record(integer1: int, char1: string)))""".stripMargin) { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should run
  }

  test("""SQLServer.InferAndQuery("mssql-test", "select * from rdbmstest.test_types")""") {
    _ should run
  }

  test("""SQLServer.Query("mssql-test", "select * from rdbmstest.test_types",
    |       type collection(record(integer1: int, char1: string)))""".stripMargin) {
    _ should run
  }

}
