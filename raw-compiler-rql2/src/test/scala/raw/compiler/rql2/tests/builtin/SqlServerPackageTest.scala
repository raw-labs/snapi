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

trait SqlServerPackageTest extends CompilerTestContext with RDBMSTestCreds {

  val sqlServDb = "rawtest"
  val sqlServRegDb = "registered-db"
  val sqlServSchema = "rdbmstest"
  val sqlServTable = "tbl1"

  rdbms(authorizedUser, sqlServRegDb, sqlServerCreds)

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
      |   host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username.get.toString}", password = "${sqlServerCreds.password.get.toString}")""".stripMargin
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
      |   host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username.get.toString}", password = "${sqlServerCreds.password.get.toString}" )""".stripMargin
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
    |      db_username = "${sqlServerCreds.username.get.toString}",
    |      db_password = "${sqlServerCreds.password.get.toString}"
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
      |  host = "does-not-exist", username = "${sqlServerCreds.username.get.toString}", password = "${sqlServerCreds.password.get.toString}"
      |)""".stripMargin
  )(it => it should runErrorAs("""error connecting to database: does-not-exist""".stripMargin))

  // wrong port
  // When there is a wrong port supplied  the test takes a long time to run and we get  an connect time out error.
  ignore(
    s"""SQLServer.Read(
      |  "$sqlServDb", "$sqlServSchema", "$sqlServTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username.get.toString}", password = "${sqlServerCreds.password.get.toString}", port = 1234
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
      |  host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username.get.toString}", password = "wrong!"
      |)""".stripMargin
  )(it => it should runErrorAs(s"""Login failed for user '${sqlServerCreds.username.get.toString}'""".stripMargin))

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
      |   host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username.get.toString}", password = "${sqlServerCreds.password.get.toString}" )""".stripMargin
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
      |   host = "${sqlServerCreds.host}", username = "${sqlServerCreds.username.get.toString}", password = "${sqlServerCreds.password.get.toString}" )""".stripMargin
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
