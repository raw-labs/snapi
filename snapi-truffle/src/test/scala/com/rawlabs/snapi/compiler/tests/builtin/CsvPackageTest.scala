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

package com.rawlabs.snapi.compiler.tests.builtin

import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class CsvPackageTest extends SnapiTestContext {

  val ttt = "\"\"\""

  private val data = tempFile("""a|b|c
    |1|10|100
    |2|20|200
    |3|30|300""".stripMargin)

  private val dataWithEscaped = tempFile("""a|b|c
    |1|10|\N""".stripMargin)

  private val dataWithQuoted = tempFile("""a|b|c
    |1|10|"N"""".stripMargin)

  private val headerLessData = tempFile("""1|10|100
    |2|20|200
    |3|30|300""".stripMargin)

  private val directory = headerLessData.getParent

  test(
    """Csv.Parse("1;tralala\n12;ploum\n3;ploum;\n4;NULL", type collection(record(a: int, b: string, c: string, d: string, e: string)),
      |skip = 0, delimiter = ";", nulls=["NULL", "12"])""".stripMargin
  )(it =>
    it should runErrorAs(
      "failed to read CSV (line 1 column 10): not enough columns found"
    )
  )

  private val badData = tempFile("""1|10|100|3.13
    |2|20|200
    |3|30|300|3.14""".stripMargin)

  test(snapi"""Csv.Read("$badData", type collection(record(a: int, b: int, c: int, d: double)), delimiter="|")""")(it =>
    it should runErrorAs(
      snapi"failed to read CSV (line 2 column 9) (location: $badData): not enough columns found"
    )
  )

  test(snapi"""Csv.Read("$badData", type collection(record(a: int, b: int, c: int, d: double)), delimiter=";")""")(it =>
    it should runErrorAs(
      snapi"failed to read CSV (line 1 column 14) (location: $badData): not enough columns found"
    )
  )

  // Failure when opening a directory
  test(snapi"""Csv.InferAndRead("$directory")""".stripMargin) { it =>
    // The location is built successfully, and CSV inference fails
    it should runErrorAs(s"inference error: file system error: path is not a file: $directory")
  }

  test(
    snapi"""Csv.Read("$directory", type collection(record(a:int, b:int, c:int)), skip = 1, delimiter = "|")""".stripMargin
  ) { it =>
    // The location is built successfully, and the CSV reader fails when finding the directory
    it should runErrorAs(s"file system error: path is not a file: $directory")
  }

  // Each line has 11 bytes so it will fail at line 10 more or less.
  private val junkAfter10Items = tempFile(
    """1, #1, 1.1
      |2, #2, 2.2
      |3, #3, 3.3
      |4, #4, 4.4
      |5, #5, 5.5
      |6, #6, 6.6
      |7, #7, 7.7
      |8, #8, 8.8
      |9, #9, 9.9
      |10, #10, 10.10
      |#############################""".stripMargin
  )

  test("""Csv.Parse("a", type int)""".stripMargin)(it => it should typeErrorAs("unsupported type"))

  test("""Csv.Parse("a", type collection(record(a: collection(int))))""".stripMargin)(it =>
    it should typeErrorAs("unsupported type")
  )

  test("""Csv.Parse("a", type collection(record(a: list(int))))""".stripMargin)(it =>
    it should typeErrorAs("unsupported type")
  )

  test("""Csv.Parse("a", type collection(record(a: record(a: int))))""".stripMargin)(it =>
    it should typeErrorAs("unsupported type")
  )

  test("""Csv.Parse("a", type collection(record(a: binary)))""".stripMargin)(it =>
    it should typeErrorAs("unsupported type")
  )

  test("""Csv.Parse("a", type collection(record(
    |  a: int,
    |  c: record(a: int),
    |  d: location
    |)))""".stripMargin)(it => it should typeErrorAs("unsupported type"))

  test(snapi"""Csv.Read("$data", type collection(int))""")(it => it should typeErrorAs("unsupported type"))

  test(snapi"""Csv.Read("$data", type collection(record(a: list(int))))""")(it =>
    it should typeErrorAs("unsupported type")
  )

  test(snapi"""Csv.Read("$data", type collection(record(a: binary)))""")(it =>
    it should typeErrorAs("unsupported type")
  )

  test(snapi"""Csv.Read("$data", type collection(record(
    |  a: int,
    |  c: record(a: int),
    |  d: location
    |)))""".stripMargin)(it => it should typeErrorAs("unsupported type"))

  test(snapi"""Csv.InferAndRead("$headerLessData")""".stripMargin)(it => it should evaluateTo("""[
    |{1, 10, 100},
    |{2, 20, 200},
    |{3, 30, 300}
    |]""".stripMargin))

  test(snapi"""
    |let data = Csv.InferAndRead("$data")
    |in
    |    Collection.Count(data)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("3")
  }

  test(snapi"""
    |let data = Csv.InferAndRead("$data"),
    |    filter = Collection.Filter(data, r -> r.a > 1)
    |in
    |    Collection.Count(filter)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("2")
  }

  test(snapi"""
    |let data = Csv.Read("$data", type collection(record(a:int, b:int, c:int)), skip = 1, delimiter = "|"),
    |    filter = Collection.Filter(data, r -> r.a > 1)
    |in
    |    Collection.Count(filter)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("2")
  }

  test("""Csv.Parse("a,b,c\nd,e,f", type collection(record(_1: string, _2: string, _3: string)), delimiter = ",")""") {
    it =>
      it should evaluateTo("""
        |Collection.Build(
        |  Record.Build(_1 = "a", _2 = "b", _3 = "c"),
        |  Record.Build(_1 = "d", _2 = "e", _3 = "f")
        |)""".stripMargin)
  }

  test("""
    |let t = type collection(record(_1: string, _2: string, _3: string))
    |in
    |  Csv.Parse("a,b,c\nd,e,f", type t, delimiter = ",")
    |""".stripMargin)(it => it should typeAs("collection(record(_1: string, _2: string, _3: string))"))

  // Errors

  test(snapi"""let d = Csv.Read("$data", type collection(record(a: int, b: int, c: int)))
    |in Try.IsError(d)""".stripMargin)(_ should typeErrorAs("cannot be applied to a collection"))

  test(snapi"""let d = Csv.Read("file:/not/found", type collection(record(a: int, b: int, c: int)))
    |in Try.IsError(d)""".stripMargin)(_ should typeErrorAs("cannot be applied to a collection"))

  test(snapi"""let d = Csv.Read("file:/not/found", type collection(record(a: int, b: int, c: int))),
    |c = Collection.Count(d)
    |in Try.IsError(c)""".stripMargin)(_ should evaluateTo("true"))

  test(snapi"""Csv.InferAndRead("file:/not/found")""".stripMargin)(it => it should runErrorAs("path not found"))

  test(snapi"""Csv.Read("file:/not/found", type collection(record(a: int, b: int, c: int)))""".stripMargin)(it =>
    it should runErrorAs("path not found")
  )

  test(snapi"""let urls = List.Build("file:/not/found", "$data"),
    |    contents = List.Transform(urls, u -> Csv.Read(u, type collection(record(a: int, b: int, c: int)),
    |                                                  delimiter="|", skip=1)),
    |    counts = List.Transform(contents, c -> Collection.Count(c))
    |in counts""".stripMargin)(
    _ should evaluateTo("""List.Build(Error.Build("file system error: path not found: /not/found"), 3L)""")
  )

  test(snapi"""List.Build(
    |    Collection.Count(Csv.InferAndRead("file:/not/found")),
    |    Collection.Count(Csv.InferAndRead("$data"))
    |)""".stripMargin)(
    _ should runErrorAs("path not found")
  )

  //Generating a file where the last row they are all nulls.
  private val csvWithNulls =
    tempFile("a, b, c\n" + (1 to 1000).map(n => s"$n, #$n, $n.2").mkString("\n") + "\nNA,NA,NA", "csv")

  test(snapi"""Csv.InferAndRead("$csvWithNulls", sampleSize = 100, nulls = ["NA"])""")(it => it should run)

  test(snapi"""Csv.InferAndRead("$csvWithNulls", sampleSize = 100, preferNulls = true, nulls = ["NA"])""")(it =>
    it should run
  )

  test(snapi"""Csv.InferAndRead("$csvWithNulls", sampleSize = -1, preferNulls = false, nulls = ["NA"])""")(it =>
    it should run
  )

  // Because file was sampled but with preferNulls as false, the last line has errors instead of nulls.
  test(snapi"""let
    |  data = Csv.InferAndRead("$csvWithNulls", sampleSize = 100, preferNulls = false)
    |in
    |  Collection.Filter(data, row -> Try.IsError(row.a))""".stripMargin)(it =>
    it should evaluateTo(
      snapi"""[{
        |  a : Error.Build("failed to parse CSV (location: $csvWithNulls: line 1002, col 1), cannot parse 'NA' as an int"),
        |  b : "NA", // it's not parsed as a null in that test since we didn't pass it in the nulls list
        |  c : Error.Build("failed to parse CSV (location: $csvWithNulls: line 1002, col 7), cannot parse 'NA' as a double")
        |}] """.stripMargin
    )
  )

  // Generating a file where the last row changes types (ints, doubles to strings)
  private val csvWithTypeChange =
    tempFile("a, b, c\n" + (1 to 1000).map(n => s"$n, #$n, $n.2").mkString("\n") + "\nhello,,world", "csv")

  test(snapi"""Csv.InferAndRead("$csvWithTypeChange", sampleSize = 100)""")(it => it should run)

  test(snapi"""let
    |  data = Csv.InferAndRead("$csvWithTypeChange", sampleSize = 100)
    |in
    |  Collection.Filter(data, row -> Try.IsError(row.a))""".stripMargin)(it =>
    it should evaluateTo(
      snapi"""[{
        |  a : Error.Build("failed to parse CSV (location: $csvWithTypeChange: line 1002, col 1), cannot parse 'hello' as an int"),
        |  b : null,
        |  c : Error.Build("failed to parse CSV (location: $csvWithTypeChange: line 1002, col 8), cannot parse 'world' as a double")
        |}] """.stripMargin
    )
  )

  test(snapi"""Csv.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double)))""") { it =>
    // ideally it would be line 11, column 30 (end of line 11)
    it should runErrorAs(
      snapi"failed to read CSV (line 11 column 173) (location: $junkAfter10Items): not enough columns found"
    )
  }

  test(
    snapi"""Collection.Take(Csv.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))), 9)"""
  )(
    _ should evaluateTo(s"""[
      | {a: 1, b: "#1", c: 1.1},
      | {a: 2, b: "#2", c: 2.2},
      | {a: 3, b: "#3", c: 3.3},
      | {a: 4, b: "#4", c: 4.4},
      | {a: 5, b: "#5", c: 5.5},
      | {a: 6, b: "#6", c: 6.6},
      | {a: 7, b: "#7", c: 7.7},
      | {a: 8, b: "#8", c: 8.8},
      | {a: 9, b: "#9", c: 9.9}
      |]""".stripMargin)
  )

  test(
    snapi"""Collection.Take(Csv.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))), 11)"""
  ) { it =>
    // ideally it would be line 11, column 30 (end of line 11)
    it should runErrorAs(
      snapi"failed to read CSV (line 11 column 173) (location: $junkAfter10Items): not enough columns found"
    )
  }

  test(
    snapi"""Collection.Count(Csv.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))))""".stripMargin
  ) { it =>
    // ideally it would be line 11, column 30 (end of line 11)
    it should runErrorAs(
      snapi"failed to read CSV (line 11 column 173) (location: $junkAfter10Items): not enough columns found"
    )
  }

  test(
    snapi"""Try.IsError(Collection.Count(Csv.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double)))) ) """
  ) {
    _ should evaluateTo("true")
  }

  test(
    snapi"""Try.IsError( List.From(Csv.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double)))) ) """
  ) {
    _ should evaluateTo("true")
  }

  test(
    snapi""" List.From( Collection.Take(Csv.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))) , 9 )) """
  ) {
    _ should evaluateTo(s"""[
      | {a: 1, b: "#1", c: 1.1},
      | {a: 2, b: "#2", c: 2.2},
      | {a: 3, b: "#3", c: 3.3},
      | {a: 4, b: "#4", c: 4.4},
      | {a: 5, b: "#5", c: 5.5},
      | {a: 6, b: "#6", c: 6.6},
      | {a: 7, b: "#7", c: 7.7},
      | {a: 8, b: "#8", c: 8.8},
      | {a: 9, b: "#9", c: 9.9}
      |]""".stripMargin)
  }

  test(snapi"""Try.IsError(
    |  List.From(Collection.Take(Csv.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))), 9))
    |)""".stripMargin) {
    _ should evaluateTo("false")
  }

  private val csvWithAllTypes =
    tempFile("""byteCol;shortCol;intCol;longCol;floatCol;doubleCol;decimalCol;boolCol;dateCol;timeCol;timestampCol
      |1;10;100;1000;3.14;6.28;9.42;true;2023-12-25;01:02:03;2023-12-25T01:02:03
      |120;2500;25000;250000;30.14;60.28;90.42;false;2023-02-05;11:12:13;2023-02-05T11:12:13""".stripMargin)

  test(snapi"""Csv.InferAndRead("$csvWithAllTypes")""") { it =>
    it should evaluateTo("""[
      |{byteCol: Int.From("1"), shortCol:Int.From("10"), intCol: Int.From("100"), longCol: Int.From("1000"),
      | floatCol: Double.From("3.14"), doubleCol: Double.From("6.28"), decimalCol: Double.From("9.42"), boolCol: true,
      | dateCol: Date.Parse("12/25/2023", "M/d/yyyy"), timeCol: Time.Parse("01:02:03", "H:m:s"),
      | timestampCol: Timestamp.Parse("12/25/2023 01:02:03", "M/d/yyyy H:m:s")},
      |{byteCol: Int.From("120"), shortCol:Int.From("2500"), intCol: Int.From("25000"), longCol: Int.From("250000"),
      | floatCol: Double.From("30.14"), doubleCol: Double.From("60.28"), decimalCol: Double.From("90.42"), boolCol: false,
      | dateCol: Date.Parse("2/5/2023", "M/d/yyyy"), timeCol: Time.Parse("11:12:13", "H:m:s"),
      | timestampCol: Timestamp.Parse("2/5/2023 11:12:13", "M/d/yyyy H:m:s")}
      |]""".stripMargin)
  }

  test(snapi"""Csv.Read("$csvWithAllTypes", type collection(
    |    record(
    |        byteCol: byte,
    |        shortCol: short,
    |        intCol: int,
    |        longCol: long,
    |        floatCol: float,
    |        doubleCol: double,
    |        decimalCol: decimal,
    |        boolCol: bool,
    |        dateCol: date,
    |        timeCol: time,
    |        timestampCol: timestamp
    |    )
    |), delimiter = ";", skip = 1)""".stripMargin)(it =>
    it should
      evaluateTo("""[
        |{byteCol: Byte.From(1), shortCol:Short.From(10), intCol: Int.From(100), longCol: Long.From(1000),
        | floatCol: Float.From(3.14), doubleCol: Double.From(6.28), decimalCol: Decimal.From("9.42"), boolCol: true,
        | dateCol: Date.Parse("12/25/2023", "M/d/yyyy"), timeCol: Time.Parse("01:02:03", "H:m:s"),
        | timestampCol: Timestamp.Parse("12/25/2023 01:02:03", "M/d/yyyy H:m:s")},
        |{byteCol: Byte.From(120), shortCol:Short.From(2500), intCol: Int.From(25000), longCol: Long.From(250000),
        | floatCol: Float.From(30.14), doubleCol: Double.From(60.28), decimalCol: Decimal.From("90.42"), boolCol: false,
        | dateCol: Date.Parse("2/5/2023", "M/d/yyyy"), timeCol: Time.Parse("11:12:13", "H:m:s"),
        | timestampCol: Timestamp.Parse("2/5/2023 11:12:13", "M/d/yyyy H:m:s")}
        |]""".stripMargin)
  )

  test(snapi"""Csv.Read("$csvWithAllTypes", type collection(
    |    record(
    |        byteCol: byte,
    |        shortCol: short,
    |        intCol: int,
    |        longCol: long,
    |        floatCol: float,
    |        doubleCol: double,
    |        decimalCol: decimal,
    |        boolCol: bool,
    |        dateCol: date,
    |        timeCol: time,
    |        timestampCol: timestamp
    |    )
    |), delimiter = ";", skip = 0)""".stripMargin)(it => it should evaluateTo(snapi"""[
    |{byteCol: Error.Build("failed to parse CSV (location: $csvWithAllTypes: line 1, col 1), cannot parse 'byteCol' as a byte"),
    | shortCol:Error.Build("failed to parse CSV (location: $csvWithAllTypes: line 1, col 9), cannot parse 'shortCol' as a short"),
    | intCol: Error.Build("failed to parse CSV (location: $csvWithAllTypes: line 1, col 18), cannot parse 'intCol' as an int"),
    | longCol: Error.Build("failed to parse CSV (location: $csvWithAllTypes: line 1, col 25), cannot parse 'longCol' as a long"),
    | floatCol: Error.Build("failed to parse CSV (location: $csvWithAllTypes: line 1, col 33), cannot parse 'floatCol' as a float"),
    | doubleCol: Error.Build("failed to parse CSV (location: $csvWithAllTypes: line 1, col 42), cannot parse 'doubleCol' as a double"),
    | decimalCol: Error.Build("failed to parse CSV (location: $csvWithAllTypes: line 1, col 52), cannot parse 'decimalCol' as a decimal"),
    | boolCol: Error.Build("failed to parse CSV (location: $csvWithAllTypes: line 1, col 63), cannot parse 'boolCol' as a bool"),
    | dateCol: Error.Build("failed to parse CSV (location: $csvWithAllTypes: line 1, col 71), string 'dateCol' does not match date template 'yyyy-M-d'"),
    | timeCol: Error.Build("failed to parse CSV (location: $csvWithAllTypes: line 1, col 79), string 'timeCol' does not match time template 'HH:mm[:ss[.SSS]]'"),
    | timestampCol: Error.Build("failed to parse CSV (location: $csvWithAllTypes: line 1, col 87), string 'timestampCol' does not match timestamp template 'HH:mm[:ss[.SSS]]'")},
    |{byteCol: Byte.From(1), shortCol:Short.From(10), intCol: Int.From(100), longCol: Long.From(1000),
    | floatCol: Float.From(3.14), doubleCol: Double.From(6.28), decimalCol: Decimal.From("9.42"), boolCol: true,
    | dateCol: Date.Parse("12/25/2023", "M/d/yyyy"), timeCol: Time.Parse("01:02:03", "H:m:s"),
    | timestampCol: Timestamp.Parse("12/25/2023 01:02:03", "M/d/yyyy H:m:s")},
    |{byteCol: Byte.From(120), shortCol:Short.From(2500), intCol: Int.From(25000), longCol: Long.From(250000),
    | floatCol: Float.From(30.14), doubleCol: Double.From(60.28), decimalCol: Decimal.From("90.42"), boolCol: false,
    | dateCol: Date.Parse("2/5/2023", "M/d/yyyy"), timeCol: Time.Parse("11:12:13", "H:m:s"),
    | timestampCol: Timestamp.Parse("2/5/2023 11:12:13", "M/d/yyyy H:m:s")}
    |]""".stripMargin))

  test(
    """Csv.Parse("1;tralala\n12;ploum\n3;ploum;\n4;NULL", type collection(record(a: int, b: string)),
      |skip = 0, delimiter = ";", nulls=["NULL", "12"])""".stripMargin
  )(it =>
    it should evaluateTo("""[{a: 1, b: "tralala"}, {a: null, b: "ploum"}, {a: 3, b: "ploum"}, {a: 4, b: null}]""")
  )

  // Infer and Parse

  test(
    s"""Csv.InferAndParse("1,2,3")"""
  )(_ should evaluateTo("""[{_1: 1, _2: 2, _3: 3}]"""))

  test(
    s"""Csv.InferAndParse("1, 2, hello")"""
  )(_ should evaluateTo("""[{_1: 1, _2: 2, _3: "hello"}]"""))

  test(
    s"""Csv.InferAndParse("1, 2,")"""
  )(_ should evaluateTo("""[{_1: 1, _2: 2, _3: null}]"""))

  test(
    s"""Csv.InferAndParse("1;2;")"""
  )(_ should evaluateTo("""[{_1: 1, _2: 2, _3: null}]"""))

  test(
    s"""Csv.InferAndParse(${ttt}1;2\n3;hello$ttt, delimiters=[";","\\n"])""".stripMargin
  )(_ should evaluateTo("""[{_1: 1, _2: "2"}, {_1: 3, _2: "hello"}]"""))

  test(
    s"""Csv.InferAndParse(${ttt}1;2\n3;hello;5;;;;;;;$ttt, delimiters=[";","\\n"])""".stripMargin
  )(_ should evaluateTo("""[]"""))

  test(
    snapi"""Csv.Read("$data", type collection(record(_1: int, _2: int, _3: int)), delimiter="|", escape="\\", quote="\"")"""
  )(it => it should run)

  test(
    snapi"""Csv.Read("$data", type collection(record(_1: int, _2: int, _3: int)), delimiter="|", escape=null, quote=null)"""
  )(it => it should run)

  test(snapi"""Csv.InferAndRead("$data", escape="\\", quotes=["\""])""")(it => it should run)

  test(snapi"""Csv.Parse("1,2,3", type collection(record(_1: int, _2: int, _3: int)), escape="\\", quote="\"")""")(it =>
    it should run
  )

  test(snapi"""Csv.Parse("1,2,3", type collection(record(_1: int, _2: int, _3: int)), escape=null, quote=null)""")(it =>
    it should run
  )

  test(snapi"""Csv.InferAndParse("1,2,3", escape="\\", quotes=["\""])""")(it => it should run)

  test(snapi"""Csv.InferAndRead("$dataWithEscaped")""".stripMargin)(it => it should evaluateTo("""[
    |{a: 1, b: 10, c: "N"}
    |]""".stripMargin))

  test(
    snapi"""Csv.Read("$dataWithEscaped", type collection(record(a:int,b:int,c:string)), delimiter="|", skip=1)""".stripMargin
  )(it => it should evaluateTo("""[
    |{a: 1, b: 10, c: "N"}
    |]""".stripMargin))

  test(
    snapi"""Csv.Read("$dataWithEscaped", type collection(record(a:int,b:int,c:string)), delimiter="|", skip=1, escape=null)""".stripMargin
  )(it => it should evaluateTo("""[
    |{a: 1, b: 10, c: "\\N"}
    |]""".stripMargin))

  test(
    snapi"""Csv.Read("$dataWithEscaped", type collection(record(a:int,b:int,c:string)), delimiter="|", skip=1, escape="\\")""".stripMargin
  )(it => it should evaluateTo("""[
    |{a: 1, b: 10, c: "N"}
    |]""".stripMargin))

  test(
    snapi"""Csv.Read("$dataWithQuoted", type collection(record(a:int,b:int,c:string)), delimiter="|", skip=1, quote=null)""".stripMargin
  )(it => it should evaluateTo("""[
    |{a: 1, b: 10, c: "\"N\""}
    |]""".stripMargin))

  test(
    snapi"""Csv.Read("$dataWithQuoted", type collection(record(a:int,b:int,c:string)), delimiter="|", skip=1)""".stripMargin
  )(it => it should evaluateTo("""[
    |{a: 1, b: 10, c: "N"}
    |]""".stripMargin))

  test(
    snapi"""Csv.Read("$dataWithQuoted", type collection(record(a:int,b:int,c:string)), delimiter="|", skip=1 , quote="\"")""".stripMargin
  )(it => it should evaluateTo("""[
    |{a: 1, b: 10, c: "N"}
    |]""".stripMargin))

}
