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

package com.rawlabs.snapi.compiler.tests.builtin.credentials

import com.rawlabs.snapi.compiler.tests.TestCredentials
import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class PostgreSQLPackageTest extends SnapiTestContext {

  import TestCredentials._

  val pgSchema = "rdbmstest"
  val pgTable = "tbl1"

  rdbms("pgsql", pgsqlCreds)

  private val ttt = "\"\"\""
  // Trying all types. Not all expressions type as wanted so that
  // we force the type too.
  test(s"""PostgreSQL.Query("pgsql", $ttt
    |SELECT 120 AS bytecol,
    |       CAST('1200' AS SMALLINT) AS shortcol,
    |       CAST('12000' AS INTEGER) AS intcol,
    |       CAST('12000000' AS BIGINT) AS longcol,
    |       CAST('3.14' AS REAL) AS floatcol,
    |       CAST('3.14' AS DOUBLE PRECISION) AS doublecol,
    |       CAST('12000000' AS DECIMAL) AS decimalcol,
    |       CAST('120' AS VARCHAR) AS stringcol,
    |       CAST('12:23:34.123' AS TIME) AS timecol,
    |       CAST('2020-01-01' AS DATE) AS datecol,
    |       CAST('2020-01-01 12:23:34.123' AS TIMESTAMP) AS timestampcol,
    |       CAST('false' AS BOOL) AS boolcol,
    |       decode('T2xhbGEh', 'base64') as binarycol$ttt, type collection(
    |    record(
    |        bytecol: byte,
    |        shortcol: short,
    |        intcol: int,
    |        longcol: long,
    |        floatcol: float,
    |        doublecol: double,
    |        decimalcol: decimal,
    |        stringcol: string,
    |        timecol: time,
    |        datecol: date,
    |        timestampcol: timestamp,
    |        boolcol: bool,
    |        binarycol: binary
    |    )
    |))""".stripMargin) { it =>
    it should evaluateTo("""[{
      |  bytecol: Byte.From(120),
      |  shortcol: Short.From(1200),
      |  intcol: 12000,
      |  longcol: 12000000L,
      |  floatcol: 3.14f,
      |  doublecol: 3.14,
      |  decimalcol: Decimal.From(12000000),
      |  stringcol: "120",
      |  timecol: Time.Build(12, 23, seconds=34, millis=123),
      |  datecol: Date.Build(2020, 1, 1),
      |  timestampcol: Timestamp.Build(2020, 1, 1, 12, 23, seconds=34, millis=123),
      |  boolcol: false,
      |  binarycol: Binary.FromString("Olala!")
      |}]""".stripMargin)
  }

  test(s"""PostgreSQL.InferAndRead("pgsql", "$pgSchema", "$pgTable")""") { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""PostgreSQL.Read("pgsql", "$pgSchema", "$pgTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: string, y: string))
      |)""".stripMargin
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
    s"""PostgreSQL.Read("pgsql", "$pgSchema", "$pgTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)))""".stripMargin
  ) { it =>
    it should orderEvaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: Error.Build("failed to read value: column 'x': Bad value for type int : x1"), y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: Error.Build("failed to read value: column 'x': Bad value for type int : x2"), y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: Error.Build("failed to read value: column 'x': Bad value for type int : x3"), y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""PostgreSQL.InferAndRead("${pgsqlCreds.getDatabase}", "$pgSchema", "$pgTable",
      |   host = "${pgsqlCreds.getHost}", username = "${pgsqlCreds.getUser}", password = "${pgsqlCreds.getPassword}")""".stripMargin
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
    s"""PostgreSQL.Read("${pgsqlCreds.getDatabase}", "$pgSchema", "$pgTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |   host = "${pgsqlCreds.getHost}", username = "${pgsqlCreds.getUser}", password = "${pgsqlCreds.getPassword}" )""".stripMargin
  ) { it =>
    it should orderEvaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: Error.Build("failed to read value: column 'x': Bad value for type int : x1"), y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: Error.Build("failed to read value: column 'x': Bad value for type int : x2"), y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: Error.Build("failed to read value: column 'x': Bad value for type int : x3"), y: null}
        |]""".stripMargin
    )
  }

  ignore(s"""
    |let
    |   d = Location.Describe(PostgreSQL.Build(
    |      "pgsql://${pgsqlCreds.getDatabase}/$pgSchema/$pgTable",
    |      host = "${pgsqlCreds.getHost}",
    |      username = "${pgsqlCreds.getUser}",
    |      password = "${pgsqlCreds.getPassword}"
    |   ))
    |in
    |  d.columns
    |""".stripMargin)(it => it should evaluateTo("""[
    |  {col_name: "a", col_type: "int", nullable: false},
    |  {col_name: "b", col_type: "int", nullable: true},
    |  {col_name: "c", col_type: "double", nullable: false},
    |  {col_name: "d", col_type: "double", nullable: true},
    |  {col_name: "x", col_type: "string", nullable: false},
    |  {col_name: "y", col_type: "string", nullable: true}
    |]""".stripMargin))

  // no credentials
  test(
    s"""PostgreSQL.InferAndRead("${pgsqlCreds.getDatabase}", "$pgSchema", "$pgTable" )""".stripMargin
  )(it => it should runErrorAs(s"""unknown credential: ${pgsqlCreds.getDatabase}""".stripMargin))

  test(
    s"""PostgreSQL.Read("${pgsqlCreds.getDatabase}", "$pgSchema", "$pgTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string))
      |)""".stripMargin
  )(it => it should runErrorAs(s"""unknown credential: ${pgsqlCreds.getDatabase}""".stripMargin))

  // server does not exist
  test(
    s"""PostgreSQL.Read(
      |  "${pgsqlCreds.getDatabase}", "$pgSchema", "$pgTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${badMysqlCreds.getHost}", username = "${pgsqlCreds.getUser}", password = "${pgsqlCreds.getPassword}"
      |)""".stripMargin
  )(it => it should runErrorAs(s"""unknown host: ${badMysqlCreds.getHost}""".stripMargin))

  // wrong port
  // When there is a wrong port supplied  the test takes a long time to run and we get  an connect time out error.
  ignore(
    s"""PostgreSQL.Read(
      |  "${pgsqlCreds.getDatabase}", "$pgSchema", "$pgTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${pgsqlCreds.getHost}", username = "${pgsqlCreds.getUser}", password = "${pgsqlCreds.getPassword}", port = 1234
      |)""".stripMargin
  )(it => it should runErrorAs(s"""connect timed out: ${pgsqlCreds.getHost}""".stripMargin))

  // No password
  test(
    s"""PostgreSQL.Read(
      |  "${pgsqlCreds.getDatabase}", "$pgSchema", "$pgTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${pgsqlCreds.getHost}"
      |)""".stripMargin
  )(it => it should runErrorAs("username is required"))

  // wrong password
  test(
    s"""PostgreSQL.Read(
      |  "${pgsqlCreds.getDatabase}", "$pgSchema", "$pgTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${pgsqlCreds.getHost}", username = "${pgsqlCreds.getUser}", password = "wrong!"
      |)""".stripMargin
  )(it => it should runErrorAs("authentication failed"))

  test(s"""PostgreSQL.InferAndQuery("pgsql", "SELECT * FROM $pgSchema.$pgTable")""") { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""PostgreSQL.InferAndQuery("${pgsqlCreds.getDatabase}", "SELECT * FROM $pgSchema.$pgTable",
      |   host = "${pgsqlCreds.getHost}", username = "${pgsqlCreds.getUser}", password = "${pgsqlCreds.getPassword}" )""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(s"""PostgreSQL.Query("pgsql", "SELECT * FROM $pgSchema.$pgTable",
    |  type collection(record(a: int, b: int, c: double, d: double, x: string, y: string))
    |)""".stripMargin) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""PostgreSQL.Query("${pgsqlCreds.getDatabase}", "SELECT * FROM $pgSchema.$pgTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: string, y: string)),
      |   host = "${pgsqlCreds.getHost}", username = "${pgsqlCreds.getUser}", password = "${pgsqlCreds.getPassword}" )""".stripMargin
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
    s"""List.Transform(["$pgTable", "dont_exist"],
      |   table ->
      |     Collection.Count(
      |      PostgreSQL.Query("${pgsqlCreds.getDatabase}", "SELECT * FROM $pgSchema." + table,
      |      type collection(record(a: int, b: int, c: double, d: double, x: string, y: string)),
      |      host = "${pgsqlCreds.getHost}", username = "${pgsqlCreds.getUser}",
      |      password = "${pgsqlCreds.getPassword}")
      |     ))""".stripMargin
  ) { it =>
    val error =
      s"""failed to read from database pgsql:${pgsqlCreds.getDatabase}: ERROR: relation \\"$pgSchema.dont_exist\\" does not exist\\n  Position: 15"""
    it should evaluateTo(s"""[3L, Error.Build("$error")]""")
  }

}
