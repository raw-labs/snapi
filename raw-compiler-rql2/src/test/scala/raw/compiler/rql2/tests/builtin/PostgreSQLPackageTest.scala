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

package raw.compiler.rql2.tests.builtin

import raw.creds.RDBMSTestCreds
import raw.compiler.rql2.tests.CompilerTestContext

trait PostgreSQLPackageTest extends CompilerTestContext with RDBMSTestCreds {

  val pgSchema = "rdbmstest"
  val pgTable = "tbl1"

  rdbms(authorizedUser, "pgsql", pgsqlCreds)

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
    s"""PostgreSQL.InferAndRead("${pgsqlCreds.database}", "$pgSchema", "$pgTable",
      |   host = "${pgsqlCreds.host}", username = "${pgsqlCreds.username.get.toString}", password = "${pgsqlCreds.password.get.toString}")""".stripMargin
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
    s"""PostgreSQL.Read("${pgsqlCreds.database}", "$pgSchema", "$pgTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |   host = "${pgsqlCreds.host}", username = "${pgsqlCreds.username.get.toString}", password = "${pgsqlCreds.password.get.toString}" )""".stripMargin
  ) { it =>
    it should orderEvaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: Error.Build("failed to read value: column 'x': Bad value for type int : x1"), y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: Error.Build("failed to read value: column 'x': Bad value for type int : x2"), y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: Error.Build("failed to read value: column 'x': Bad value for type int : x3"), y: null}
        |]""".stripMargin
    )
  }

  test(s"""
    |let
    |   d = Location.Describe(Location.Build(
    |      "pgsql://${pgsqlCreds.database}/$pgSchema/$pgTable",
    |      db_host = "${pgsqlCreds.host}",
    |      db_username = "${pgsqlCreds.username.get.toString}",
    |      db_password = "${pgsqlCreds.password.get.toString}"
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
    s"""PostgreSQL.InferAndRead("${pgsqlCreds.database}", "$pgSchema", "$pgTable" )""".stripMargin
  )(it =>
    it should runErrorAs(s"""inference error: no credential found for postgresql: ${pgsqlCreds.database}""".stripMargin)
  )

  test(
    s"""PostgreSQL.Read("${pgsqlCreds.database}", "$pgSchema", "$pgTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string))
      |)""".stripMargin
  )(it => it should runErrorAs(s"""no credential found for postgresql: ${pgsqlCreds.database}""".stripMargin))

  // server does not exist
  test(
    s"""PostgreSQL.Read(
      |  "${pgsqlCreds.database}", "$pgSchema", "$pgTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${badMysqlCreds.host}", username = "${pgsqlCreds.username.get.toString}", password = "${pgsqlCreds.password.get.toString}"
      |)""".stripMargin
  )(it => it should runErrorAs(s"""unknown host: ${badMysqlCreds.host}""".stripMargin))

  // wrong port
  // When there is a wrong port supplied  the test takes a long time to run and we get  an connect time out error.
  ignore(
    s"""PostgreSQL.Read(
      |  "${pgsqlCreds.database}", "$pgSchema", "$pgTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${pgsqlCreds.host}", username = "${pgsqlCreds.username.get.toString}", password = "${pgsqlCreds.password.get.toString}", port = 1234
      |)""".stripMargin
  )(it => it should runErrorAs(s"""connect timed out: ${pgsqlCreds.host}""".stripMargin))

  // No password
  test(
    s"""PostgreSQL.Read(
      |  "${pgsqlCreds.database}", "$pgSchema", "$pgTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${pgsqlCreds.host}"
      |)""".stripMargin
  )(it => it should runErrorAs(s"""error connecting to database: ${pgsqlCreds.host}""".stripMargin))

  // wrong password
  test(
    s"""PostgreSQL.Read(
      |  "${pgsqlCreds.database}", "$pgSchema", "$pgTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${pgsqlCreds.host}", username = "${pgsqlCreds.username.get.toString}", password = "wrong!"
      |)""".stripMargin
  )(it => it should runErrorAs("""authentication failed""".stripMargin))

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
    s"""PostgreSQL.InferAndQuery("${pgsqlCreds.database}", "SELECT * FROM $pgSchema.$pgTable",
      |   host = "${pgsqlCreds.host}", username = "${pgsqlCreds.username.get.toString}", password = "${pgsqlCreds.password.get.toString}" )""".stripMargin
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
    s"""PostgreSQL.Query("${pgsqlCreds.database}", "SELECT * FROM $pgSchema.$pgTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: string, y: string)),
      |   host = "${pgsqlCreds.host}", username = "${pgsqlCreds.username.get.toString}", password = "${pgsqlCreds.password.get.toString}" )""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }
}
