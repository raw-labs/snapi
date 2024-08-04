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

import raw.compiler.rql2.tests.TestCredentials
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class SqlServerPackageTest extends Rql2TruffleCompilerTestContext {

  import TestCredentials._

  val sqlServDb = "rawtest"
  val sqlServRegDb = "registered-db"
  val sqlServSchema = "rdbmstest"
  val sqlServTable = "tbl1"

  rdbms(sqlServRegDb, sqlServerCreds)

  private val ttt = "\"\"\""
  test(s"""SQLServer.InferAndQuery("$sqlServRegDb", $ttt
    |SELECT CAST('120' AS TINYINT) AS bytecol,
    |       CAST('1200' AS SMALLINT) AS shortcol,
    |       CAST('12000' AS INTEGER) AS intcol,
    |       CAST('12000000' AS BIGINT) AS longcol,
    |       CAST('3.14' AS REAL) AS floatcol,
    |       CAST('3.14' AS DOUBLE PRECISION) AS doublecol,
    |       CAST('12000000' AS DECIMAL) AS decimalcol,
    |       CAST('120' AS VARCHAR) AS stringcol,
    |       CAST('12:23:34' AS TIME) AS timecol,
    |       CAST('2020-01-01' AS DATE) AS datecol,
    |       CAST('2020-01-01 12:23:34' AS DATETIME) AS timestampcol,
    |       CAST('Olala!' AS VARBINARY(MAX)) AS binarycol $ttt)""".stripMargin) { it =>
    it should typeAs("""collection(
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
      |        binarycol: binary
      |    )
      |)""".stripMargin)
    it should evaluateTo("""[{
      |  bytecol: Byte.From(120),
      |  shortcol: Short.From(1200),
      |  intcol: 12000,
      |  longcol: 12000000L,
      |  floatcol: 3.14f,
      |  doublecol: 3.14,
      |  decimalcol: Decimal.From(12000000),
      |  stringcol: "120",
      |  timecol: Time.Build(12, 23, seconds=34),
      |  datecol: Date.Build(2020, 1, 1),
      |  timestampcol: Timestamp.Build(2020, 1, 1, 12, 23, seconds=34),
      |  binarycol: Binary.FromString("Olala!")
      |}]""".stripMargin)
  }

  test(s"""SQLServer.InferAndRead("$sqlServRegDb", "$sqlServSchema", "$sqlServTable")""") { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""SQLServer.Read("$sqlServRegDb", "$sqlServSchema", "$sqlServTable",
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
    s"""SQLServer.Read("$sqlServRegDb", "$sqlServSchema", "$sqlServTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)))""".stripMargin
  ) { it =>
    it should orderEvaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: Error.Build("failed to read value: column 'x': An error occurred while converting the varchar value to JDBC data type INTEGER."), y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: Error.Build("failed to read value: column 'x': An error occurred while converting the varchar value to JDBC data type INTEGER."), y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: Error.Build("failed to read value: column 'x': An error occurred while converting the varchar value to JDBC data type INTEGER."), y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""SQLServer.InferAndRead("$sqlServDb", "$sqlServSchema", "$sqlServTable",
      |   host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username}", password = "${sqlServerCreds.password}")""".stripMargin
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
    s"""SQLServer.Read("$sqlServDb", "$sqlServSchema", "$sqlServTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |   host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username}", password = "${sqlServerCreds.password}" )""".stripMargin
  ) { it =>
    it should orderEvaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: Error.Build("failed to read value: column 'x': An error occurred while converting the varchar value to JDBC data type INTEGER."), y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: Error.Build("failed to read value: column 'x': An error occurred while converting the varchar value to JDBC data type INTEGER."), y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: Error.Build("failed to read value: column 'x': An error occurred while converting the varchar value to JDBC data type INTEGER."), y: null}
        |]""".stripMargin
    )
  }

  test(s"""
    |let
    |   d = Location.Describe(Location.Build(
    |      "sqlserver://$sqlServDb/$sqlServSchema/$sqlServTable",
    |      db_host = "${sqlServerCreds.host}",
    |      db_username = "${sqlServerCreds.username}",
    |      db_password = "${sqlServerCreds.password}"
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
    s"""SQLServer.InferAndRead("$sqlServDb", "$sqlServSchema", "$sqlServTable" )""".stripMargin
  )(it => it should runErrorAs(s"""inference error: no credential found for sqlserver: rawtest""".stripMargin))

  test(
    s"""SQLServer.Read("$sqlServDb", "$sqlServSchema", "$sqlServTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string))
      |)""".stripMargin
  )(it => it should runErrorAs(s"""no credential found for sqlserver: $sqlServDb""".stripMargin))

  // server does not exist
  test(
    s"""SQLServer.Read(
      |  "$sqlServDb", "$sqlServSchema", "$sqlServTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "does-not-exist", username = "${sqlServerCreds.username}", password = "${sqlServerCreds.password}"
      |)""".stripMargin
  )(it => it should runErrorAs("""error connecting to database: does-not-exist""".stripMargin))

  // wrong port
  // When there is a wrong port supplied  the test takes a long time to run and we get  an connect time out error.
  ignore(
    s"""SQLServer.Read(
      |  "$sqlServDb", "$sqlServSchema", "$sqlServTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username}", password = "${sqlServerCreds.password}", port = 1234
      |)""".stripMargin
  )(it => it should runErrorAs(s"""connect timed out: ${sqlServerCreds.host}""".stripMargin))

  // No password
  test(
    s"""SQLServer.Read(
      |  "$sqlServDb", "$sqlServSchema", "$sqlServTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${sqlServerCreds.host}"
      |)""".stripMargin
  )(it => it should runErrorAs("""Login failed for user ''""".stripMargin))

  // wrong password
  test(
    s"""SQLServer.Read(
      |  "$sqlServDb", "$sqlServSchema", "$sqlServTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username}", password = "wrong!"
      |)""".stripMargin
  )(it => it should runErrorAs(s"""Login failed for user '${sqlServerCreds.username}'""".stripMargin))

  test(s"""SQLServer.InferAndQuery("$sqlServRegDb", "SELECT * FROM $sqlServSchema.$sqlServTable")""") { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""SQLServer.InferAndQuery("$sqlServDb", "SELECT * FROM $sqlServSchema.$sqlServTable",
      |   host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username}", password = "${sqlServerCreds.password}" )""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(s"""SQLServer.Query("$sqlServRegDb", "SELECT * FROM $sqlServSchema.$sqlServTable",
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
    s"""SQLServer.Query("$sqlServDb", "SELECT * FROM $sqlServSchema.$sqlServTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: string, y: string)),
      |   host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username}", password = "${sqlServerCreds.password}" )""".stripMargin
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
    s"""List.Transform(["$sqlServTable", "dont_exist"],
      |   table ->
      |     Collection.Count(
      |      SQLServer.Query("$sqlServDb", "SELECT * FROM $sqlServSchema." + table,
      |      type collection(record(a: int, b: int, c: double, d: double, x: string, y: string)),
      |      host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username}",
      |      password = "${sqlServerCreds.password}")
      |     ))""".stripMargin
  ) { it =>
    val error =
      s"""failed to read from database sqlserver:$sqlServDb: Invalid object name '$sqlServSchema.dont_exist'."""
    it should evaluateTo(s"""[3L, Error.Build("$error")]""")
  }

}
