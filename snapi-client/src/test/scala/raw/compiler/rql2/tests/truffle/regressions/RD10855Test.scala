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
import raw.testing.tags.TruffleTests

@TruffleTests
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

  // As we are getting items by index, we can rename the columns, as long as the types are correct
  test(
    s"""PostgreSQL.Read(
      |  "${pgsqlCreds.database}",
      |  "rdbmstest",
      |  "tbl1",
      |   type collection(record(e: int, f: int, g: double, h: double, xx: string, yy: string)),
      |   host = "${pgsqlCreds.host}",
      |   username = "${pgsqlCreds.username.get}",
      |   password = "${pgsqlCreds.password.get}"
      |)""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {e: 1, f: 1, g: 1.5, h: 1.5, xx: "x1", yy: "y1"},
        |  {e: 2, f: 2, g: 2.2, h: 2.2, xx: "x2", yy: "y2"},
        |  {e: 3, f: null, g: 3.3, h: null, xx: "x3", yy: null}
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

  val ttt = "\"\"\""
  // Retrieving columns with repeated names
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

  // Interval type is not supported, so its skipped
  test(s"""PostgreSQL.InferAndQuery(
    |  "${pgsqlCreds.database}",
    |  "SELECT * FROM rdbmstest.test_types",
    |   host = "${pgsqlCreds.host}",
    |   username = "${pgsqlCreds.username.get}",
    |   password = "${pgsqlCreds.password.get}"
    |)""".stripMargin) { it =>
    it should evaluateTo(
      """[
        |  {
        |    smallint1: 1,
        |    integer1: 2,
        |    bigint1: 3,
        |    decimal1: Decimal.From(4.4),
        |    real1: 5.5f,
        |    double1: 6.6,
        |    money1: 7.7,
        |    varchar1: "string",
        |    char1: "string    ",
        |    text1: "string",
        |    boolean1: true,
        |    timestamp1: Timestamp.Build(1975, 6, 23, 1, 2, seconds=3),
        |    date1: Date.Build(1975, 6, 23),
        |    time1: Time.Build(1, 2, seconds=3),
        |    interval1: "skipping column of type interval",
        |    bytea1: "eyJhIjogMTIzNCwgImIiOiBbImhlbGxvIiwgIndvcmxkIl19",
        |    bit1: null,
        |    integer_array1: null,
        |    smallserial1: 0,
        |    serial1: 0,
        |    bigserial1: 0
        |  }
        |]""".stripMargin
    )
  }
}
