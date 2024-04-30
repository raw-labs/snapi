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

package raw.compiler.rql2.tests.truffle.regressions

import raw.compiler.rql2.truffle.TruffleCompilerTestContext
import raw.creds.jdbc.RDBMSTestCreds

class RD10855Test extends TruffleCompilerTestContext with RDBMSTestCreds {

  // The column y is of type varchar, but we are setting to undefined
  // when the value is not null it shows the error "skipping column of type ..."
  test(
    s"""PostgreSQL.Read(
      |  "${pgsqlCreds.database}",
      |  "rdbmstest",
      |  "tbl1",
      |   type collection(record(a: int, b: int, c: double, d: double, x: string, y: undefined)),
      |   host = "${pgsqlCreds.host}",
      |   username = "${pgsqlCreds.username.get}",
      |   password = "${pgsqlCreds.password.get}"
      |)""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "skipping column of type varchar"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "skipping column of type varchar"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  // table skippable_types has unsupported types, field f: macaddr, field g: cidr
  // but are null so no errors will be shown
  test(
    s"""PostgreSQL.InferAndRead(
      |  "${pgsqlCreds.database}",
      |  "rdbmstest",
      |  "skippable_types",
      |   host = "${pgsqlCreds.host}",
      |   username = "${pgsqlCreds.username.get}",
      |   password = "${pgsqlCreds.password.get}"
      |)""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: "hello", c: 2, d: 3, e: Decimal.From(4.4), f: null, g: null}
        |]""".stripMargin
    )
  }

  // Retrieving columns with repeated names
  val ttt = "\"\"\""
  test(s"""PostgreSQL.InferAndQuery(
    |  "${pgsqlCreds.database}",
    |  ${ttt}SELECT t1.a, t2.a, t1.b, t2.x as b
    |    FROM rdbmstest.tbl1 t1, rdbmstest.tbl1 t2
    |    WHERE t1.a = t2.a $ttt,
    |   host = "${pgsqlCreds.host}",
    |   username = "${pgsqlCreds.username.get}",
    |   password = "${pgsqlCreds.password.get}"
    |)""".stripMargin) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, a: 1, b: 1, b: "x1"},
        |  {a: 2, a: 2, b: 2, b: "x2"},
        |  {a: 3, a: 3, b: null, b: "x3"}
        |]""".stripMargin
    )
  }
}
