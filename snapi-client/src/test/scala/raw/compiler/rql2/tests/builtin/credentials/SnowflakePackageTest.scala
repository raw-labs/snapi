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

class SnowflakePackageTest extends Rql2TruffleCompilerTestContext {

  import TestCredentials._

  val snowflakeSchema = "PUBLIC"
  val snowflakeMainTable = "TBL1"
  val snowflakeSideTable = "TBL4"

  rdbms("snowflake", snowflakeCreds)

  private val ttt = "\"\"\""
  // Trying all types. Not all expressions type as wanted so that
  // we force the type too.
  test(s"""Snowflake.Query("snowflake", $ttt
    |SELECT 120 AS "bytecol",
    |       CAST('1200' AS SMALLINT) AS "shortcol",
    |       CAST('12000' AS INTEGER) AS "intcol",
    |       CAST('12000000' AS BIGINT) AS "longcol",
    |       3.14::FLOAT AS "floatcol",
    |       CAST('3.14' AS FLOAT8) AS "doublecol",
    |       CAST('12000000' AS DECIMAL) AS "decimalcol",
    |       CAST('120' AS VARCHAR) AS "stringcol",
    |       CAST('12:23:34' AS TIME) AS "timecol",
    |       CAST('2020-01-01' AS DATE) AS "datecol",
    |       CAST('2020-01-01 12:23:34' AS DATETIME) AS "timestampcol",
    |       1 = 0 AS "boolcol",
    |       to_binary('tralala', 'utf-8') AS "binarycol" $ttt, type collection(
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
      |  timecol: Time.Build(12, 23, seconds=34),
      |  datecol: Date.Build(2020, 1, 1),
      |  timestampcol: Timestamp.Build(2020, 1, 1, 12, 23, seconds=34),
      |  boolcol: false,
      |  binarycol: String.Encode("tralala", "utf-8")
      |}]""".stripMargin)
  }

  property("com.rawlabs.utils.sources.rdbms.network-timeout", "10s")

  test(s"""Snowflake.InferAndRead("snowflake", "$snowflakeSchema", "$snowflakeMainTable")""") { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""Snowflake.Read("snowflake", "$snowflakeSchema", "$snowflakeMainTable",
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
    s"""Snowflake.Read("snowflake", "$snowflakeSchema", "$snowflakeMainTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: string, y: string)),
      |   options = [{"timezone", "UTC"}]
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
    s"""Snowflake.Read("snowflake", "$snowflakeSchema", "$snowflakeMainTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)))""".stripMargin
  ) { it =>
    it should orderEvaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: Error.Build("failed to read value: column 'x': Cannot convert value in the driver from type:12 to type:int, value=x1."), y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: Error.Build("failed to read value: column 'x': Cannot convert value in the driver from type:12 to type:int, value=x2."), y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: Error.Build("failed to read value: column 'x': Cannot convert value in the driver from type:12 to type:int, value=x3."), y: null}
        |]""".stripMargin
    )
  }

  test(s"""Snowflake.InferAndRead("snowflake", "$snowflakeSchema", "$snowflakeSideTable")""") { it =>
    it should evaluateTo(
      """[
        |  {
        |      d1: Date.Build(2001, 2, 3),
        |      d2: null,
        |      ts1: Timestamp.Build(2001, 2, 3, 1, 2, seconds=3, millis=40),
        |      ts2: Timestamp.Build(2003, 2, 1, 4, 3, seconds=2, millis=10),
        |      t1: Time.Build(20, 21, seconds=22),
        |      t2: Time.Build(1, 2, seconds=3),
        |      n: 1,
        |      x: "x1"
        |  },
        |  {
        |      d1: Date.Build(2006, 5, 4),
        |      d2: Date.Build(2004, 6, 4),
        |      ts1: Timestamp.Build(2006, 5, 4, 23, 22, seconds=21, millis=200),
        |      ts2: null,
        |      t1: Time.Build(4, 3, seconds=1),
        |      t2: Time.Build(1, 2, seconds=2),
        |      n: 2,
        |      x: "x2"
        |  },
        |  {
        |      d1: Date.Build(2008, 9, 10),
        |      d2: Date.Build(2010, 9, 8),
        |      ts1: Timestamp.Build(2010, 9, 8, 12, 11, seconds=10, millis=90),
        |      ts2: Timestamp.Build(2006, 5, 4, 23, 22, seconds=21, millis=200),
        |      t1: Time.Build(23, 22, seconds=21),
        |      t2: null,
        |      n: 3,
        |      x: "x3"
        |  }
        |]""".stripMargin
    )
  }

  test(s"""Snowflake.Read("snowflake", "$snowflakeSchema", "$snowflakeSideTable",
    |    type collection(record(
    |        d1: date,
    |        d2: date,
    |        ts1: timestamp,
    |        ts2: timestamp,
    |        t1: time,
    |        t2: time,
    |        n: decimal,
    |        x: string
    |   ))
    |)""".stripMargin) { it =>
    it should evaluateTo(
      """[
        |  {
        |      d1: Date.Build(2001, 2, 3),
        |      d2: null,
        |      ts1: Timestamp.Build(2001, 2, 3, 1, 2, seconds=3, millis=40),
        |      ts2: Timestamp.Build(2003, 2, 1, 4, 3, seconds=2, millis=10),
        |      t1: Time.Build(20, 21, seconds=22),
        |      t2: Time.Build(1, 2, seconds=3),
        |      n: 1,
        |      x: "x1"
        |  },
        |  {
        |      d1: Date.Build(2006, 5, 4),
        |      d2: Date.Build(2004, 6, 4),
        |      ts1: Timestamp.Build(2006, 5, 4, 23, 22, seconds=21, millis=200),
        |      ts2: null,
        |      t1: Time.Build(4, 3, seconds=1),
        |      t2: Time.Build(1, 2, seconds=2),
        |      n: 2,
        |      x: "x2"
        |  },
        |  {
        |      d1: Date.Build(2008, 9, 10),
        |      d2: Date.Build(2010, 9, 8),
        |      ts1: Timestamp.Build(2010, 9, 8, 12, 11, seconds=10, millis=90),
        |      ts2: Timestamp.Build(2006, 5, 4, 23, 22, seconds=21, millis=200),
        |      t1: Time.Build(23, 22, seconds=21),
        |      t2: null,
        |      n: 3,
        |      x: "x3"
        |  }
        |]""".stripMargin
    )
  }

  test(
    s"""Snowflake.InferAndRead("${snowflakeCreds.getDatabase}", "$snowflakeSchema", "$snowflakeMainTable",
      |   accountID = "${snowflakeCreds.getAccountIdentifier}", username = "${snowflakeCreds.getUser}", password = "${snowflakeCreds.getPassword}")""".stripMargin
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
    s"""Snowflake.Read("${snowflakeCreds.getDatabase}", "$snowflakeSchema", "$snowflakeMainTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |   accountID = "${snowflakeCreds.getAccountIdentifier}", username = "${snowflakeCreds.getUser}", password = "${snowflakeCreds.getPassword}" )""".stripMargin
  ) { it =>
    it should orderEvaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: Error.Build("failed to read value: column 'x': Cannot convert value in the driver from type:12 to type:int, value=x1."), y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: Error.Build("failed to read value: column 'x': Cannot convert value in the driver from type:12 to type:int, value=x2."), y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: Error.Build("failed to read value: column 'x': Cannot convert value in the driver from type:12 to type:int, value=x3."), y: null}
        |]""".stripMargin
    )
  }

  ignore(s"""
    |let
    |   d = Location.Describe(Snowflake.Build(
    |      "snowflake://${snowflakeCreds.getDatabase}/$snowflakeSchema/$snowflakeMainTable",
    |      accountID = "${snowflakeCreds.getAccountIdentifier}",
    |      username = "${snowflakeCreds.getUser}",
    |      password = "${snowflakeCreds.getPassword}"
    |   ))
    |in
    |  d.columns
    |""".stripMargin)(it => it should evaluateTo("""[
    |  {col_name: "a", col_type: "long", nullable: false},
    |  {col_name: "b", col_type: "long", nullable: true},
    |  {col_name: "c", col_type: "double", nullable: false},
    |  {col_name: "d", col_type: "double", nullable: true},
    |  {col_name: "x", col_type: "string", nullable: false},
    |  {col_name: "y", col_type: "string", nullable: true}
    |]""".stripMargin))

  // no credentials
  test(
    s"""Snowflake.InferAndRead("${snowflakeCreds.getDatabase}", "$snowflakeSchema", "$snowflakeMainTable" )""".stripMargin
  )(it =>
    it should runErrorAs(
      s"""unknown credential: ${snowflakeCreds.getDatabase}""".stripMargin
    )
  )

  test(
    s"""Snowflake.Read("${snowflakeCreds.getDatabase}", "$snowflakeSchema", "$snowflakeMainTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: int, y: string))
      |)""".stripMargin
  )(it => it should runErrorAs(s"""unknown credential: ${snowflakeCreds.getDatabase}""".stripMargin))

  // server does not exist
  test(
    s"""Snowflake.Read(
      |  "${snowflakeCreds.getDatabase}", "$snowflakeSchema", "$snowflakeMainTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  accountID = "does-not-exist", username = "${snowflakeCreds.getUser}", password = "${snowflakeCreds.getPassword}"
      |)""".stripMargin
  ) { it =>
    it should runErrorAs(
      """IO error connecting to does-not-exist: JDBC driver encountered communication error. Message: HTTP status=513.""".stripMargin
    )
  }

  // No username, no password
  test(
    s"""Snowflake.Read(
      |  "${snowflakeCreds.getDatabase}", "$snowflakeSchema", "$snowflakeMainTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  accountID = "${snowflakeCreds.getAccountIdentifier}"
      |)""".stripMargin
  )(it => it should runErrorAs(s"""username is required""".stripMargin))

  // wrong password
  test(
    s"""Snowflake.Read(
      |  "${snowflakeCreds.getDatabase}", "$snowflakeSchema", "$snowflakeMainTable",
      |  type collection(record(a: int, b: int, c: double, d: double, x: int, y: string)),
      |  accountID = "${snowflakeCreds.getAccountIdentifier}", username = "${snowflakeCreds.getUser}", password = "wrong!"
      |)""".stripMargin
  ) { it =>
    it should runErrorAs(
      s"""unable to establish connection to ${snowflakeCreds.getAccountIdentifier}: Incorrect username or password was specified.""".stripMargin
    )
  }

  test(s"""Snowflake.InferAndQuery("snowflake", "SELECT * FROM $snowflakeSchema.$snowflakeMainTable")""") { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(
    s"""Snowflake.InferAndQuery("${snowflakeCreds.getDatabase}", "SELECT * FROM public.$snowflakeMainTable",
      |   accountID = "${snowflakeCreds.getAccountIdentifier}", username = "${snowflakeCreds.getUser}", password = "${snowflakeCreds.getPassword}" )""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {a: 1, b: 1, c: 1.5, d: 1.5, x: "x1", y: "y1"},
        |  {a: 2, b: 2, c: 2.2, d: 2.2, x: "x2", y: "y2"},
        |  {a: 3, b: null, c: 3.3, d: null, x: "x3", y: null}
        |]""".stripMargin
    )
  }

  test(s"""Snowflake.Query("snowflake", "SELECT * FROM public.$snowflakeMainTable",
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
    s"""Snowflake.Query("${snowflakeCreds.getDatabase}", "SELECT * FROM public.$snowflakeMainTable",
      |   type collection(record(a: int, b: int, c: double, d: double, x: string, y: string)),
      |   accountID = "${snowflakeCreds.getAccountIdentifier}", username = "${snowflakeCreds.getUser}", password = "${snowflakeCreds.getPassword}" )""".stripMargin
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
    s"""Snowflake.InferAndRead("${snowflakeCreds.getDatabase}", "$snowflakeSchema", "$snowflakeSideTable",
      |   accountID = "${snowflakeCreds.getAccountIdentifier}",
      |   username = "${snowflakeCreds.getUser}",
      |   password = "${snowflakeCreds.getPassword}",
      |   options = [{"timezone", "America/Los_Angeles"}]
      |)""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {
        |      d1: Date.Build(2001, 2, 3),
        |      d2: null,
        |      ts1: Timestamp.Build(2001, 2, 3, 1, 2, seconds=3, millis=40),
        |      ts2: Timestamp.Build(2003, 2, 1, 4, 3, seconds=2, millis=10),
        |      t1: Time.Build(20, 21, seconds=22),
        |      t2: Time.Build(1, 2, seconds=3),
        |      n: 1,
        |      x: "x1"
        |  },
        |  {
        |      d1: Date.Build(2006, 5, 4),
        |      d2: Date.Build(2004, 6, 4),
        |      ts1: Timestamp.Build(2006, 5, 4, 23, 22, seconds=21, millis=200),
        |      ts2: null,
        |      t1: Time.Build(4, 3, seconds=1),
        |      t2: Time.Build(1, 2, seconds=2),
        |      n: 2,
        |      x: "x2"
        |  },
        |  {
        |      d1: Date.Build(2008, 9, 10),
        |      d2: Date.Build(2010, 9, 8),
        |      ts1: Timestamp.Build(2010, 9, 8, 12, 11, seconds=10, millis=90),
        |      ts2: Timestamp.Build(2006, 5, 4, 23, 22, seconds=21, millis=200),
        |      t1: Time.Build(23, 22, seconds=21),
        |      t2: null,
        |      n: 3,
        |      x: "x3"
        |  }
        |]""".stripMargin
    )
  }

  test(
    s"""Snowflake.InferAndQuery("${snowflakeCreds.getDatabase}", "SELECT * FROM $snowflakeSchema.$snowflakeSideTable",
      |   accountID = "${snowflakeCreds.getAccountIdentifier}",
      |   username = "${snowflakeCreds.getUser}",
      |   password = "${snowflakeCreds.getPassword}",
      |   options = [{"timezone", "UTC"}]
      |)""".stripMargin
  ) { it =>
    it should evaluateTo(
      """[
        |  {
        |      d1: Date.Build(2001, 2, 3),
        |      d2: null,
        |      ts1: Timestamp.Build(2001, 2, 3, 1, 2, seconds=3, millis=40),
        |      ts2: Timestamp.Build(2003, 2, 1, 4, 3, seconds=2, millis=10),
        |      t1: Time.Build(20, 21, seconds=22),
        |      t2: Time.Build(1, 2, seconds=3),
        |      n: 1,
        |      x: "x1"
        |  },
        |  {
        |      d1: Date.Build(2006, 5, 4),
        |      d2: Date.Build(2004, 6, 4),
        |      ts1: Timestamp.Build(2006, 5, 4, 23, 22, seconds=21, millis=200),
        |      ts2: null,
        |      t1: Time.Build(4, 3, seconds=1),
        |      t2: Time.Build(1, 2, seconds=2),
        |      n: 2,
        |      x: "x2"
        |  },
        |  {
        |      d1: Date.Build(2008, 9, 10),
        |      d2: Date.Build(2010, 9, 8),
        |      ts1: Timestamp.Build(2010, 9, 8, 12, 11, seconds=10, millis=90),
        |      ts2: Timestamp.Build(2006, 5, 4, 23, 22, seconds=21, millis=200),
        |      t1: Time.Build(23, 22, seconds=21),
        |      t2: null,
        |      n: 3,
        |      x: "x3"
        |  }
        |]""".stripMargin
    )
  }

  test(
    s"""List.Transform(["$snowflakeMainTable", "dont_exist"],
      |   table ->
      |     Collection.Count(
      |      Snowflake.Query("${snowflakeCreds.getDatabase}", "SELECT * FROM public." + table,
      |       type collection(record(a: int, b: int, c: double, d: double, x: string, y: string)),
      |       accountID = "${snowflakeCreds.getAccountIdentifier}", username = "${snowflakeCreds.getUser}",
      |       password = "${snowflakeCreds.getPassword}")))""".stripMargin
  ) { it =>
    val error =
      s"""failed to read from database snowflake:${snowflakeCreds.getDatabase}: SQL compilation error:\\nObject '${snowflakeCreds.getDatabase.toUpperCase}.PUBLIC.DONT_EXIST' does not exist or not authorized.""".stripMargin
    it should evaluateTo(s"""[3L, Error.Build("$error")]""")
  }

}
