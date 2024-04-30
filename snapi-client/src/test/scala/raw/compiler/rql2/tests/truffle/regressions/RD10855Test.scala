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

  val pgSchema = "rdbmstest"
  val pgTable = "tbl1"

  test(
    s"""PostgreSQL.InferAndRead("${pgsqlCreds.database}", "$pgSchema", "$pgTable",
      |   host = "${pgsqlCreds.host}", username = "${pgsqlCreds.username.get}", password = "${pgsqlCreds.password.get}")""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""PostgreSQL.Read("${pgsqlCreds.database}", "$pgSchema", "$pgTable", type collection(record(a: int, b: int, c: double, d: double, x: string, y: string)),
       |   host = "${pgsqlCreds.host}", username = "${pgsqlCreds.username.get}", password = "${pgsqlCreds.password.get}")""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

// table skippable_types has unsupported types
//  Column |         Type
//  --------+-----------------------+-
//    a      | integer               |
//    b      | character varying(10) |
//    c      | smallint              |
//    d      | bigint                |
//    e      | numeric(20,4)         |
//    f      | macaddr               |
//    g      | cidr                  |
  test(
    s"""PostgreSQL.Read(
       |  "${pgsqlCreds.database}",
       |  "$pgSchema",
       |  "skippable_types",
       |  type collection(record(
       |      a: int,
       |      b: string,
       |      c: int,
       |      d: long,
       |      e: double,
       |      f: undefined,
       |      g: undefined)),
       |   host = "${pgsqlCreds.host}",
       |   username = "${pgsqlCreds.username.get}",
       |   password = "${pgsqlCreds.password.get}"
       |)""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: "hello", c: 2, d: 3, e: 4.4, f: null, g: null}
        |]""".stripMargin
    )
  }

  test(
    s"""PostgreSQL.InferAndRead(
       |  "${pgsqlCreds.database}",
       |  "$pgSchema",
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
}
