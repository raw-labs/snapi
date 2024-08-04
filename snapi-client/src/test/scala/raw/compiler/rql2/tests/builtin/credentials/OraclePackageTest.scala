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

@TruffleTests class OraclePackageTest extends Rql2TruffleCompilerTestContext {

  import TestCredentials._

  val oracleDb = "rawdb"
  val oracleSchema = "rawtest"
  val oracleTable = "tbl1"

  rdbms("oracle", oracleCreds)

  test(s"""Oracle.InferAndRead("oracle", "$oracleSchema", "$oracleTable")""") { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should evaluateTo(
      """[
        |  {A: 1, B: 1, C: Decimal.From(1.5), D: Decimal.From(1.5), X: "x1", Y: "y1"},
        |  {A: 2, B: 2, C: Decimal.From(2.2), D: Decimal.From(2.2), X: "x2", Y: "y2"},
        |  {A: 3, B: null, C: Decimal.From(3.3), D: null, X: "x3", Y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""Oracle.Read("oracle", "$oracleSchema", "$oracleTable",
      |   type collection(record(A: int, B: int, C: double, D: double, X: string, Y: string))
      |)""".stripMargin
  ) { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should evaluateTo(
      """[
        |  {A: 1, B: 1, C: 1.5, D: 1.5, X: "x1", Y: "y1"},
        |  {A: 2, B: 2, C: 2.2, D: 2.2, X: "x2", Y: "y2"},
        |  {A: 3, B: null, C: 3.3, D: null, X: "x3", Y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""Oracle.Read("oracle", "$oracleSchema", "$oracleTable",
      |   type collection(record(A: int, B: int, C: double, D: double, X: int, Y: string)))""".stripMargin
  ) { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should orderEvaluateTo(
      """[
        |  {A: 1, B: 1, C: 1.5, D: 1.5, X: Error.Build("failed to read value: column 'X': Fail to convert to internal representation"), Y: "y1"},
        |  {A: 2, B: 2, C: 2.2, D: 2.2, X: Error.Build("failed to read value: column 'X': Fail to convert to internal representation"), Y: "y2"},
        |  {A: 3, B: null, C: 3.3, D: null, X: Error.Build("failed to read value: column 'X': Fail to convert to internal representation"), Y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""Oracle.InferAndRead("$oracleDb", "$oracleSchema", "$oracleTable",
      |   host = "${oracleCreds.host}", username = "${oracleCreds.username}", password = "${oracleCreds.password}")""".stripMargin
  ) { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should evaluateTo(
      """[
        |  {A: 1, B: 1, C: Decimal.From(1.5), D: Decimal.From(1.5), X: "x1", Y: "y1"},
        |  {A: 2, B: 2, C: Decimal.From(2.2), D: Decimal.From(2.2), X: "x2", Y: "y2"},
        |  {A: 3, B: null, C: Decimal.From(3.3), D: null, X: "x3", Y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""Oracle.Read("$oracleDb", "$oracleSchema", "$oracleTable",
      |   type collection(record(A: int, B: int, C: double, D: double, X: int, Y: string)),
      |   host = "${oracleCreds.host}", username = "${oracleCreds.username}", password = "${oracleCreds.password}" )""".stripMargin
  ) { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should orderEvaluateTo(
      """[
        |  {A: 1, B: 1, C: 1.5, D: 1.5, X: Error.Build("failed to read value: column 'X': Fail to convert to internal representation"), Y: "y1"},
        |  {A: 2, B: 2, C: 2.2, D: 2.2, X: Error.Build("failed to read value: column 'X': Fail to convert to internal representation"), Y: "y2"},
        |  {A: 3, B: null, C: 3.3, D: null, X: Error.Build("failed to read value: column 'X': Fail to convert to internal representation"), Y: null}
        |]""".stripMargin
    )
  }

  test(s"""
    |let
    |   d = Location.Describe(Location.Build(
    |      "oracle://$oracleDb/$oracleSchema/$oracleTable",
    |      db_host = "${oracleCreds.host}",
    |      db_username = "${oracleCreds.username}",
    |      db_password = "${oracleCreds.password}"
    |   ))
    |in
    |  d.columns
    |""".stripMargin)(it => it should evaluateTo("""[
    |  {col_name: "A", col_type: "int", nullable: false},
    |  {col_name: "B", col_type: "int", nullable: true},
    |  {col_name: "C", col_type: "decimal", nullable: false},
    |  {col_name: "D", col_type: "decimal", nullable: true},
    |  {col_name: "X", col_type: "string", nullable: false},
    |  {col_name: "Y", col_type: "string", nullable: true}
    |]""".stripMargin))

  // no credentials
  test(
    s"""Oracle.InferAndRead("$oracleDb", "$oracleSchema", "$oracleTable" )""".stripMargin
  )(it => it should runErrorAs(s"""inference error: no credential found for oracle: $oracleDb""".stripMargin))

  test(
    s"""Oracle.Read("$oracleSchema", "rdbmstest", "$oracleTable",
      |   type collection(record(A: int, B: int, C: double, D: double, X: int, Y: string))
      |)""".stripMargin
  ) { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should runErrorAs(s"""o credential found for oracle: $oracleSchema""".stripMargin)
  }

  // server does not exist
  test(
    s"""Oracle.Read(
      |  "$oracleDb", "$oracleSchema", "$oracleTable",
      |  type collection(record(A: int, B: int, C: double, D: double, X: int, Y: string)),
      |  host = "oracle.localdomain", username = "${oracleCreds.username}", password = "${oracleCreds.password}"
      |)""".stripMargin
  ) { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should runErrorAs("""unknown host: oracle.localdomain""".stripMargin)
  }

  // network error
  test(
    s"""Oracle.Read(
      |  "$oracleDb", "$oracleSchema", "$oracleTable",
      |  type collection(record(A: int, B: int, C: double, D: double, X: int, Y: string)),
      |  host = "localhost", username = "${oracleCreds.username}", password = "${oracleCreds.password}"
      |)""".stripMargin
  ) { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should runErrorAs("error connecting to database: localhost")
  }

  // wrong port
  // When there is a wrong port supplied  the test takes a long time to run and we get  an connect time out error.
  ignore(
    s"""Oracle.Read(
      |  "$oracleDb", "$oracleSchema", "$oracleTable",
      |  type collection(record(A: int, B: int, C: double, D: double, X: int, Y: string)),
      |  host = "test-oracle.raw-labs.com", username = "${oracleCreds.username}", password = "${oracleCreds.password}", port = 1234
      |)""".stripMargin
  ) { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should runErrorAs("""connect timed out: test-oracle.raw-labs.com""".stripMargin)
  }

  // No password
  test(
    s"""Oracle.Read(
      |  "$oracleDb", "$oracleSchema", "$oracleTable",
      |  type collection(record(A: int, B: int, C: double, D: double, X: int, Y: string)),
      |  host = "test-oracle.raw-labs.com"
      |)""".stripMargin
  ) { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should runErrorAs("""authentication failed""".stripMargin)
  }

  // wrong password
  test(
    s"""Oracle.Read(
      |  "$oracleDb", "$oracleSchema", "$oracleTable",
      |  type collection(record(A: int, B: int, C: double, D: double, X: int, Y: string)),
      |  host = "test-oracle.raw-labs.com", username = "${oracleCreds.username}", password = "wrong!"
      |)""".stripMargin
  ) { it =>
    assume(!compilerService.language.contains("rql2-truffle"))
    it should runErrorAs("""authentication failed""".stripMargin)
  }

  test(s"""Oracle.InferAndQuery("oracle", "SELECT * FROM $oracleSchema.$oracleTable")""") { it =>
    it should evaluateTo(
      """[
        |  {A: 1, B: 1, C: Decimal.From(1.5), D: Decimal.From(1.5), X: "x1", Y: "y1"},
        |  {A: 2, B: 2, C: Decimal.From(2.2), D: Decimal.From(2.2), X: "x2", Y: "y2"},
        |  {A: 3, B: null, C: Decimal.From(3.3), D: null, X: "x3", Y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""Oracle.InferAndQuery("$oracleDb", "SELECT * FROM $oracleSchema.$oracleTable",
      |   host = "test-oracle.raw-labs.com", username = "${oracleCreds.username}", password = "${oracleCreds.password}" )""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {A: 1, B: 1, C: Decimal.From(1.5), D: Decimal.From(1.5), X: "x1", Y: "y1"},
        |  {A: 2, B: 2, C: Decimal.From(2.2), D: Decimal.From(2.2), X: "x2", Y: "y2"},
        |  {A: 3, B: null, C: Decimal.From(3.3), D: null, X: "x3", Y: null}
        |]""".stripMargin
    )
  }

  test(s"""Oracle.Query("oracle", "SELECT * FROM $oracleSchema.$oracleTable",
    |  type collection(record(A: int, B: int, C: double, D: double, X: string, Y: string))
    |)""".stripMargin) { it =>
    it should evaluateTo(
      """[
        |  {A: 1, B: 1, C: 1.5, D: 1.5, X: "x1", Y: "y1"},
        |  {A: 2, B: 2, C: 2.2, D: 2.2, X: "x2", Y: "y2"},
        |  {A: 3, B: null, C: 3.3, D: null, X: "x3", Y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""Oracle.Query("$oracleDb", "SELECT * FROM $oracleSchema.$oracleTable",
      |   type collection(record(A: int, B: int, C: double, D: double, X: string, Y: string)),
      |   host = "${oracleCreds.host}", username = "${oracleCreds.username}", password = "${oracleCreds.password}" )""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {A: 1, B: 1, C: 1.5, D: 1.5, X: "x1", Y: "y1"},
        |  {A: 2, B: 2, C: 2.2, D: 2.2, X: "x2", Y: "y2"},
        |  {A: 3, B: null, C: 3.3, D: null, X: "x3", Y: null}
        |]""".stripMargin
    )
  }
}
