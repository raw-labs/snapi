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

trait MySQLPackageTest extends CompilerTestContext with RDBMSTestCreds {

  val mysqlRegDb = "registered-db"
  val mysqlTable = "tbl1"

  rdbms(authorizedUser, mysqlRegDb, mysqlCreds)

  test(s"""MySQL.InferAndRead("$mysqlRegDb", "$mysqlTable")""") { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""MySQL.Read("$mysqlRegDb", "$mysqlTable",
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
    s"""MySQL.Read("$mysqlRegDb", "$mysqlTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)))""".stripMargin
  ) { it =>
    it should orderEvaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: Error.Build("failed to read value: column 'x': Cannot determine value type from string 'x1'"), y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: Error.Build("failed to read value: column 'x': Cannot determine value type from string 'x2'"), y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: Error.Build("failed to read value: column 'x': Cannot determine value type from string 'x3'"), y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""MySQL.InferAndRead("${mysqlCreds.database}", "$mysqlTable",
      |   host = "${mysqlCreds.host}", username = "${mysqlCreds.username.get.toString}", password = "${mysqlCreds.password.get.toString}")""".stripMargin
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
    s"""MySQL.Read("${mysqlCreds.database}", "$mysqlTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |   host = "${mysqlCreds.host}", username = "${mysqlCreds.username.get.toString}", password = "${mysqlCreds.password.get.toString}")""".stripMargin
  ) { it =>
    it should orderEvaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: Error.Build("failed to read value: column 'x': Cannot determine value type from string 'x1'"), y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: Error.Build("failed to read value: column 'x': Cannot determine value type from string 'x2'"), y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: Error.Build("failed to read value: column 'x': Cannot determine value type from string 'x3'"), y: null}
        |]""".stripMargin
    )
  }

  test(s"""
    |let
    |   d = Location.Describe(Location.Build(
    |      "mysql://${mysqlCreds.database}/$mysqlTable",
    |      db_host = "${mysqlCreds.host}",
    |      db_username = "${mysqlCreds.username.get.toString}",
    |      db_password = "${mysqlCreds.password.get.toString}"
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
    s"""MySQL.InferAndRead("${mysqlCreds.database}", "$mysqlTable" )""".stripMargin
  )(it =>
    it should runErrorAs(s"""inference error: no credential found for mysql: ${mysqlCreds.database}""".stripMargin)
  )

  test(
    s"""MySQL.Read("${mysqlCreds.database}", "$mysqlTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string))
      |)""".stripMargin
  )(it => it should runErrorAs(s"""no credential found for mysql: ${mysqlCreds.database}""".stripMargin))

  // server does not exist
  test(
    s"""MySQL.Read(
      |  "${badMysqlCreds.database}", "$mysqlTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${badMysqlCreds.host}", username = "${mysqlCreds.username.get.toString}", password = "${mysqlCreds.password.get.toString}"
      |)""".stripMargin
  )(it => it should runErrorAs(s"""unknown host: ${badMysqlCreds.host}""".stripMargin))

  // wrong port
  // When there is a wrong port supplied  the test takes a long time to run and we get  an connect time out error.
  ignore(
    s"""MySQL.Read(
      |  "${mysqlCreds.database}", "$mysqlTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${mysqlCreds.host}", username = "${mysqlCreds.username.get.toString}", password = "${mysqlCreds.password.get.toString}", port = 1234
      |)""".stripMargin
  )(it => it should runErrorAs(s"""connect timed out: ${mysqlCreds.database}""".stripMargin))

  // No password
  test(
    s"""MySQL.Read(
      |  "${mysqlCreds.database}", "$mysqlTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${mysqlCreds.host}"
      |)""".stripMargin
  )(it => it should runErrorAs("""authentication failed""".stripMargin))

  // wrong password
  test(
    s"""MySQL.Read(
      |  "${mysqlCreds.database}", "$mysqlTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${mysqlCreds.host}", username = "${mysqlCreds.username.get.toString}", password = "wrong!"
      |)""".stripMargin
  )(it => it should runErrorAs("""authentication failed""".stripMargin))

  test(s"""MySQL.InferAndQuery("$mysqlRegDb", "SELECT * FROM ${mysqlCreds.database}.$mysqlTable")""") { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""MySQL.InferAndQuery("${mysqlCreds.database}", "SELECT * FROM $mysqlTable",
      |   host = "${mysqlCreds.host}", username = "${mysqlCreds.username.get.toString}", password = "${mysqlCreds.password.get.toString}" )""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(s"""MySQL.Query("$mysqlRegDb", "SELECT * FROM $mysqlTable",
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
    s"""MySQL.Query("${mysqlCreds.database}", "SELECT * FROM $mysqlTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: string, y: string)),
      |   host = "${mysqlCreds.host}", username = "${mysqlCreds.username.get.toString}", password = "${mysqlCreds.password.get.toString}" )""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(s"""MySQL.InferAndRead("$mysqlRegDb", "higher_case_columns")""") { it =>
    it should evaluateTo(
      """[
        | {ID: 1, Name: "john", Surname: "doe", Address: "123 memory lane"},
        | {ID: 2, Name: "jane", Surname: "doe", Address: "321 the crater road"},
        | {ID: 3, Name: "bob", Surname: "smith", Address: "456 sesame street"}
        |] """.stripMargin
    )
  }

  test(s"""MySQL.InferAndQuery("$mysqlRegDb", "SELECT * FROM higher_case_columns")""") { it =>
    it should evaluateTo(
      """[
        | {ID: 1, Name: "john", Surname: "doe", Address: "123 memory lane"},
        | {ID: 2, Name: "jane", Surname: "doe", Address: "321 the crater road"},
        | {ID: 3, Name: "bob", Surname: "smith", Address: "456 sesame street"}
        |] """.stripMargin
    )
  }

  test(s"""MySQL.Read("$mysqlRegDb", "higher_case_columns",
    |   type collection(record(
    |       ID: int,
    |       Name: string,
    |       Surname: string,
    |       Address: string))
    |)""".stripMargin) { it =>
    it should evaluateTo(
      """[
        | {ID: 1, Name: "john", Surname: "doe", Address: "123 memory lane"},
        | {ID: 2, Name: "jane", Surname: "doe", Address: "321 the crater road"},
        | {ID: 3, Name: "bob", Surname: "smith", Address: "456 sesame street"}
        |] """.stripMargin
    )
  }
}
