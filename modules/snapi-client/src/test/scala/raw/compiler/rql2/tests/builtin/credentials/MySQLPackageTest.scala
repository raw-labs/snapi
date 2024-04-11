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

package raw.compiler.rql2.tests.builtin.credentials

import raw.compiler.rql2.tests.CompilerTestContext
import raw.creds.api.CredentialsTestContext
import raw.creds.jdbc.RDBMSTestCreds

trait MySQLPackageTest extends CompilerTestContext with CredentialsTestContext with RDBMSTestCreds {

  val mysqlRegDb = "registered-db"
  val mysqlTable = "tbl1"

  rdbms(authorizedUser, mysqlRegDb, mysqlCreds)

  private val ttt = "\"\"\""
  // Trying all types. Not all expressions type as wanted so that
  // we force the type too.
  test(s"""MySQL.Query("$mysqlRegDb", $ttt
    |SELECT 120 AS bytecol,
    |       1200 AS shortcol,
    |       CAST(12000 AS SIGNED INTEGER) AS intcol,
    |       12000000 AS longcol,
    |       CAST(3.14 AS FLOAT) AS floatcol,
    |       CAST(3.14 AS DOUBLE) AS doublecol,
    |       CAST(1200000000 AS DECIMAL) AS decimalcol,
    |       '120' AS stringcol,
    |       CAST('12:23:34' AS TIME) AS timecol,
    |       CAST('2020-01-01' AS DATE) AS datecol,
    |       CAST('2020-01-01 12:23:34' AS DATETIME) AS timestampcol,
    |       1 = 0 AS boolcol,
    |       convert('Olala!' using utf8) AS binarycol$ttt, type collection(
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
      |  decimalcol: Decimal.From(1200000000),
      |  stringcol: "120",
      |  timecol: Time.Build(12, 23, seconds=34),
      |  datecol: Date.Build(2020, 1, 1),
      |  timestampcol: Timestamp.Build(2020, 1, 1, 12, 23, seconds=34),
      |  boolcol: false,
      |  binarycol: Binary.FromString("Olala!")
      |}]""".stripMargin)
  }

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

  test(
    s"""List.Transform(["$mysqlTable", "dont_exist"],
      |   table ->
      |     Collection.Count(
      |      MySQL.Query("${mysqlCreds.database}", "SELECT * FROM " + table,
      |      type collection(record(a: int, b: int, c: double, d: double, x: string, y: string)),
      |      host = "${mysqlCreds.host}", username = "${mysqlCreds.username.get}",
      |      password = "${mysqlCreds.password.get}")
      |     ))""".stripMargin
  ) { it =>
    val error =
      s"""failed to read from database mysql:${mysqlCreds.database}: Table '${mysqlCreds.database}.dont_exist' doesn't exist"""
    it should evaluateTo(s"""[3L, Error.Build("$error")]""")
  }

  test(s"""MySQL.InferAndRead("$mysqlRegDb", "rd10439")""") { it =>
    it should evaluateTo("""[
      |  {id: 1, name: "john", salary: 23.5},
      |  {id: 2, name: "jane", salary: 30.4},
      |  {id: 3, name: "bob", salary: 17.8}
      |]""".stripMargin)
  }
}
