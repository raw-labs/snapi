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

import raw.compiler.utils._
import raw.compiler.rql2.tests.CompilerTestContext

trait JsonPackageTest extends CompilerTestContext {

  private val junkAfter10Items = tempFile("""[
    |  {"a": 1, "b": "#1", "c": 1.1},
    |  {"a": 2, "b": "#2", "c": 2.2},
    |  {"a": 3, "b": "#3", "c": 3.3},
    |  {"a": 4, "b": "#4", "c": 4.4},
    |  {"a": 5, "b": "#5", "c": 5.5},
    |  {"a": 6, "b": "#6", "c": 6.6},
    |  {"a": 7, "b": "#7", "c": 7.7},
    |  {"a": 8, "b": "#8", "c": 8.8},
    |  {"a": 9, "b": "#9", "c": 9.9},
    |  {"a": 10, "b": "#10", "c": 10.10}###################################""".stripMargin)

  private val data = tempFile("""
    |[
    |  {"a": 1, "b": 10, "c": 100},
    |  {"a": 2, "b": 20, "c": 200},
    |  {"a": 3, "b": 30, "c": 300}
    |]""".stripMargin)

  private val tryNothing1 = tempFile("""
    |[
    |  null,
    |  null,
    |  null,
    |  null,
    |  null,
    |  {"a": 3, "b": 30, "c": 300}
    |]""".stripMargin)

  private val tryNothing2 = tempFile("""
    |[
    |  {"a": 3, "b": 30, "c": null},
    |  {"a": 3, "b": 30, "c": null},
    |  {"a": 3, "b": 30, "c": null},
    |  {"a": 3, "b": 30, "c": null},
    |  {"a": 3, "b": 30, "c": null},
    |  {"a": 3, "b": 30, "c": 300}
    |]""".stripMargin)

  private val tryNothing3 = tempFile("""
    |[
    |  {"a": 3, "b": 30, "c": []},
    |  {"a": 3, "b": 30, "c": []},
    |  {"a": 3, "b": 30, "c": []},
    |  {"a": 3, "b": 30, "c": []},
    |  {"a": 3, "b": 30, "c": []},
    |  {"a": 3, "b": 30, "c": [300, null]}
    |]""".stripMargin)

  private val recordData = tempFile("""{"a": 1, "b": 10, "c": 100}""")

  test(snapi"""Json.Read("$data", type collection(undefined))""")(it => it should evaluateTo("""[
    |  Error.Build("expected null but got non-null"),
    |  Error.Build("expected null but got non-null"),
    |  Error.Build("expected null but got non-null")
    |] """.stripMargin))

  test(snapi"""Json.Read("$tryNothing1", type collection(undefined))""")(it =>
    it should evaluateTo(""" [null, null, null, null, null, Error.Build("expected null but got non-null")] """)
  )

  test(snapi"""Json.Read("$tryNothing2", type collection(record(a: int, b: int, c: undefined)))""")(it =>
    it should evaluateTo("""
      |[
      |  {a: 3, b: 30, c: null},
      |  {a: 3, b: 30, c: null},
      |  {a: 3, b: 30, c: null},
      |  {a: 3, b: 30, c: null},
      |  {a: 3, b: 30, c: null},
      |  {a: 3, b: 30, c: Error.Build("expected null but got non-null")}
      |]""".stripMargin)
  )

  test(snapi"""Json.Read("$tryNothing3", type collection(record(a: int, b: int, c: list(undefined))))""")(it =>
    it should evaluateTo("""
      |[
      |  {a: 3, b: 30, c: []},
      |  {a: 3, b: 30, c: []},
      |  {a: 3, b: 30, c: []},
      |  {a: 3, b: 30, c: []},
      |  {a: 3, b: 30, c: []},
      |  {a: 3, b: 30, c: [Error.Build("expected null but got non-null"), null]}
      |]""".stripMargin)
  )

  test(snapi"""Json.InferAndRead("$tryNothing1", sampleSize = 4)""")(it =>
    it should evaluateTo("""[null, null, null, null, null, Error.Build("expected null but got non-null")]""")
  )

  test(snapi"""Json.InferAndRead("$tryNothing2", sampleSize = 4)""")(it => it should evaluateTo("""
    |[
    |  {a: 3, b: 30, c: null},
    |  {a: 3, b: 30, c: null},
    |  {a: 3, b: 30, c: null},
    |  {a: 3, b: 30, c: null},
    |  {a: 3, b: 30, c: null},
    |  {a: 3, b: 30, c: Error.Build("expected null but got non-null")}
    |]""".stripMargin))

  test(snapi"""Json.InferAndRead("$tryNothing3", sampleSize = 4)""")(it => it should evaluateTo("""
    |[
    |  {a: 3, b: 30, c: []},
    |  {a: 3, b: 30, c: []},
    |  {a: 3, b: 30, c: []},
    |  {a: 3, b: 30, c: []},
    |  {a: 3, b: 30, c: []},
    |  {a: 3, b: 30, c: [Error.Build("expected null but got non-null"), null]}
    |]""".stripMargin))

  //With preferNulls = false the empty collection will be inferred as nothing not nullable so the null will also be a error
  test(snapi"""Json.InferAndRead("$tryNothing3", sampleSize = 4, preferNulls = false)""")(it => it should evaluateTo("""
    |[
    |  {a: 3, b: 30, c: []},
    |  {a: 3, b: 30, c: []},
    |  {a: 3, b: 30, c: []},
    |  {a: 3, b: 30, c: []},
    |  {a: 3, b: 30, c: []},
    |  {a: 3, b: 30, c: [Error.Build("unexpected value found, token 'VALUE_NUMBER_INT'"), Error.Build("unexpected value found, token 'VALUE_NULL'")]}
    |]""".stripMargin))

  test(
    """Json.Parse("[1,2,3]", type list(int))"""
  )(it => it should evaluateTo("""List.Build(1, 2, 3)"""))

  test("""Json.Parse("a", type collection(record(a: collection(int))))""".stripMargin)(it =>
    it should runErrorAs("failed to read JSON")
  )

  test("""Json.Parse("a", type collection(record(a: list(int))))""".stripMargin)(it =>
    it should runErrorAs("failed to read JSON")
  )

  test(
    """Json.Parse("a", type list(location))"""
  )(it => it should typeErrorAs("unsupported type"))

  test(
    snapi"""Json.Read("$data", type collection(location))"""
  )(it => it should typeErrorAs("unsupported type"))

  test(
    """Json.Print(Location.Build("http://something"))"""
  )(it => it should typeErrorAs("unsupported type"))

  test(snapi"""Json.InferAndRead("$data")""".stripMargin)(it => it should run)

  test(snapi"""
    |let data = Json.InferAndRead("$data")
    |in
    |    Collection.Count(data)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("3")
  }

  test(snapi"""
    |let data = Json.InferAndRead("$data"),
    |    filter = Collection.Filter(data, r -> r.a > 1)
    |in
    |    Collection.Count(filter)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("2")
  }

  test(snapi"""
    |let data = Json.Read("$data", type collection(record(a:int, b:int, c:int))),
    |    filter = Collection.Filter(data, r -> r.a > 1)
    |in
    |    Collection.Count(filter)""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("2")
  }

  test(
    """Json.Parse("[1,2,3]", type collection(int))"""
  )(it => it should evaluateTo("""Collection.Build(1, 2, 3)"""))

  test("""
    |let t = type collection(int)
    |in
    |  Json.Parse("[1,2,3]", type t)
    |""".stripMargin)(it => it should typeAs("collection(int)"))

  test("""Json.InferAndParse("[1,2,3]")""".stripMargin)(it => it should typeAs("collection(int)"))

  test("""
    |let t = type int
    |in
    |  Json.Parse("42", type t)
    |""".stripMargin)(it => it should evaluateTo("42"))

  test(snapi"""
    |let data = Json.InferAndRead("$recordData")
    |in data.a + data.b + data.c""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("111")
  }

  private val fileWithAKeywordField = tempFile("""
    |[
    |  {"name": "Penne a la Siciliana", "type": "food", "price": 13},
    |  {"name": "Back to the Future", "type": "DVD", "price": 9.99},
    |  {"name": "Microphone", "type": "device", "price": 99}
    |]""".stripMargin)

  test(snapi"""Collection.Filter(
    |  Json.InferAndRead("$fileWithAKeywordField"),
    |  i -> i.price < 10
    |)""".stripMargin)(
    _ should evaluateTo("""Collection.Build(Record.Build(name="Back to the Future", `type`="DVD", price=9.99))""")
  )

  // Errors

  test(snapi"""let d = Json.Read("$data", type collection(record(a: int, b: int, c: int)))
    |in Try.IsError(d)""".stripMargin)(_ should typeErrorAs("cannot be applied to a collection"))

  test(snapi"""let url: string = "http://not-found",
    |d = Json.Read(url, type collection(record(a: int, b: int, c: int)))
    |in Try.IsError(d)""".stripMargin)(_ should typeErrorAs("cannot be applied to a collection"))

  test(snapi"""let d = Json.Read("file:/not/found", type collection(record(a: int, b: int, c: int)))
    |in Try.IsError(d)""".stripMargin)(_ should typeErrorAs("cannot be applied to a collection"))

  test(snapi"""let d = Json.Read("file:/not/found", type collection(record(a: int, b: int, c: int))),
    |c = Collection.Count(d)
    |in Try.IsError(c)""".stripMargin)(_ should evaluateTo("true"))

  test(snapi"""let d = Json.Read("file:/not/found", type record(a: int, b: int, c: list(int))),
    |c = List.Count(d.c)
    |in Try.IsError(c)""".stripMargin)(_ should evaluateTo("true"))

  test(snapi"""Json.InferAndRead("file:/not/found")""".stripMargin)(it => it should runErrorAs("path not found"))

  test(snapi"""Json.Read("file:/not/found", type collection(int))""".stripMargin)(it =>
    it should runErrorAs("path not found")
  )

  test(snapi"""let urls = List.Build("file:/not/found", "$data"),
    |    contents = List.Transform(urls, u -> Json.Read(u, type collection(record(a: int, b: int, c: int)))),
    |    counts = List.Transform(contents, c -> Collection.Count(c))
    |in counts""".stripMargin)(
    _ should evaluateTo("""List.Build(Error.Build("file system error: path not found: /not/found"), 3L)""")
  )

  test(snapi"""List.Build(
    |    Collection.Count(Json.InferAndRead("file:/not/found")),
    |    Collection.Count(Json.InferAndRead("$data"))
    |)""".stripMargin)(
    _ should runErrorAs("path not found")
  )

  private val jsonWithNulls = tempFile(
    """[
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": null, "b": null, "c": null}
      |]""".stripMargin,
    "json"
  )

  // Inferrer makes all fields triable and nullable (preferNulls is true by default), last line does not fail.
  test(snapi"""Json.InferAndRead("$jsonWithNulls", sampleSize = 5)""")(it => it should evaluateTo("""[
    | {a: 1, b: "1", c: [1, 2, 3]},
    | {a: 1, b: "1", c: [1, 2, 3]},
    | {a: 1, b: "1", c: [1, 2, 3]},
    | {a: 1, b: "1", c: [1, 2, 3]},
    | {a: 1, b: "1", c: [1, 2, 3]},
    | {a: 1, b: "1", c: [1, 2, 3]},
    | {a: 1, b: "1", c: [1, 2, 3]},
    | {a: 1, b: "1", c: [1, 2, 3]},
    | {a: 1, b: "1", c: [1, 2, 3]},
    | {a: null, b: null, c: null}
    |]""".stripMargin))

  // With preferNulls = false then all fields are triable but not nullable, so we have errors in the last line.
  test(snapi"""Json.InferAndRead("$jsonWithNulls", sampleSize = 5, preferNulls = false)""")(it =>
    it should orderEvaluateTo("""[
      | {a: 1, b: "1", c: [1, 2, 3]},
      | {a: 1, b: "1", c: [1, 2, 3]},
      | {a: 1, b: "1", c: [1, 2, 3]},
      | {a: 1, b: "1", c: [1, 2, 3]},
      | {a: 1, b: "1", c: [1, 2, 3]},
      | {a: 1, b: "1", c: [1, 2, 3]},
      | {a: 1, b: "1", c: [1, 2, 3]},
      | {a: 1, b: "1", c: [1, 2, 3]},
      | {a: 1, b: "1", c: [1, 2, 3]},
      | {a: Error.Build("null value found"), b: Error.Build("null value found"), c: Error.Build("null value found")}
      |]""".stripMargin)
  )

  test(snapi"""Json.Read("$jsonWithNulls", type collection(record(a: int, b: string, c: list(int))))""")(it =>
    it should
      orderEvaluateTo("""[
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: null, b: null, c: null}
        |]""".stripMargin)
  )

  private val changeTypes = tempFile(
    """[
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": 1, "b": "1", "c": [1, 2, 3]},
      | {"a": "Hello!", "b": 1, "c": "World"}
      |]""".stripMargin,
    "json"
  )
  val triple = "\"\"\""

  test(snapi"""Json.InferAndRead("$changeTypes", sampleSize = 5)""".stripMargin)(it =>
    it should orderEvaluateTo(
      s"""[
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {a: 1, b: "1", c: [1, 2, 3]},
        | {
        |   a: Error.Build(${triple}Current token (VALUE_STRING) not numeric, can not use numeric value accessors
        | at [Source: (InputStreamReader); line: 11, column: 9]$triple),
        |   b: "1",
        |   c: Error.Build("expected [ but token VALUE_STRING found")
        | }
        |]""".stripMargin
    )
  )

  private val recordInTheMiddle = tempFile("""[
    |  1,
    |  2,
    |  {"a": 1, "b": 2},
    |  3
    |]""".stripMargin)

  // (az) this failes because of BufferedReader usage in truffle, ask which one should we use
  test(snapi"""Json.Read("$recordInTheMiddle", type list(int))""".stripMargin) {
    _ should orderEvaluateTo(
      s"""[
        |  1,
        |  2,
        |  Error.Build(${triple}Current token (START_OBJECT) not numeric, can not use numeric value accessors
        | at [Source: (InputStreamReader); line: 4, column: 4]$triple),
        |  3
        |]""".stripMargin
    )
  }

  private val listInTheMiddle = tempFile("""[
    |  1,
    |  2,
    |  [{"a": 1, "b": 2}, {"a": 3, "b": 4}],
    |  3
    |]""".stripMargin)

  test(snapi"""Json.Read("$listInTheMiddle", type list(int))""".stripMargin) {
    _ should orderEvaluateTo(
      s"""[
        |  1,
        |  2,
        |  Error.Build(${triple}Current token (START_ARRAY) not numeric, can not use numeric value accessors
        | at [Source: (InputStreamReader); line: 4, column: 4]$triple),
        |  3
        |]""".stripMargin
    )
  }

  test(snapi"""Json.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double)))""") { it =>
    if (isTruffle) {
      it should runErrorAs(snapi"failed to read JSON (url: $junkAfter10Items): Unexpected character ('#' (code 35))")
    } else {
      it should runErrorAs(
        snapi"""failed to read JSON (line 11 column 37) (url: $junkAfter10Items): Unexpected character ('#' (code 35)): was expecting comma to separate Array entries
          | at [Source: (InputStreamReader); line: 11, column: 37]""".stripMargin
      )
    }
  }

  test(
    snapi"""Collection.Take(Json.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))), 9)"""
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
    snapi"""Collection.Take(Json.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))), 11)"""
  ) { it =>
    if (isTruffle) {
      it should runErrorAs(snapi"failed to read JSON (url: $junkAfter10Items): Unexpected character ('#' (code 35))")
    } else {
      it should runErrorAs(
        snapi"""failed to read JSON (line 11 column 37) (url: $junkAfter10Items): Unexpected character ('#' (code 35)): was expecting comma to separate Array entries
          | at [Source: (InputStreamReader); line: 11, column: 37]""".stripMargin
      )
    }
  }

  test(
    snapi"""Collection.Count(Json.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))))""".stripMargin
  ) { it =>
    if (isTruffle) {
      it should runErrorAs(snapi"failed to read JSON (url: $junkAfter10Items): Unexpected character ('#' (code 35))")
    } else {
      it should runErrorAs(
        snapi"""failed to read JSON (line 11 column 37) (url: $junkAfter10Items): Unexpected character ('#' (code 35)): was expecting comma to separate Array entries
          | at [Source: (InputStreamReader); line: 11, column: 37]""".stripMargin
      )
    }
  }

  test(
    snapi"""Try.IsError(Collection.Count(Json.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double)))) ) """
  ) {
    _ should evaluateTo("true")
  }

  test(
    snapi"""Try.IsError( List.From(Json.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double)))) ) """
  ) {
    _ should evaluateTo("true")
  }

  test(
    snapi""" List.From( Collection.Take(Json.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))) , 9 )) """
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
    |  List.From(Collection.Take(Json.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))), 9))
    |)""".stripMargin) {
    _ should evaluateTo("false")
  }

  test("""Json.Print(42 - 30)""")(_ should evaluateTo(""" "12" """))
  test("""Json.Print(1 == 2)""")(_ should evaluateTo(""" "false" """))
  test("""Json.Print({number: 32, name: "Magic Johnson", titles: [1980, 1982, 1985, 1987, 1988]})""")(
    _ should evaluateTo(""" "{\"number\":32,\"name\":\"Magic Johnson\",\"titles\":[1980,1982,1985,1987,1988]}" """)
  )

  // Errors found in former ExceptionTest ScalaTest suite

  private val wrong_int = tempFile("""[
    |{"name": "Benjamin", "birthYear": 1978},
    |{"name": "X", "birthYear": "not_an_int"}
    |]
    """.stripMargin)

  test(
    snapi"""Json.InferAndRead("$wrong_int", preferNulls = false, sampleSize = 1, encoding = "iso-8859-1")"""
  ) { it =>
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978}, {name: "X", birthYear: Error.Build("Current token (VALUE_STRING) not numeric, can not use numeric value accessors\n at [Source: (InputStreamReader); line: 3, column: 29]")}]""".stripMargin
    )
  }

  test(
    snapi"""Json.Read("$wrong_int", type collection(record(name: string, birthYear: int)), encoding = "iso-8859-1")"""
  ) { it =>
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978}, {name: "X", birthYear: Error.Build("Current token (VALUE_STRING) not numeric, can not use numeric value accessors\n at [Source: (InputStreamReader); line: 3, column: 29]")}]""".stripMargin
    )
  }

  private val wrong_null_field = tempFile("""[
    |{"name": "Benjamin", "birthYear": 1978},
    |{"name": "X", "birthYear": null}
    |]
      """.stripMargin)

  test(
    snapi"""Json.InferAndRead("$wrong_null_field", sampleSize = 1, preferNulls = false)"""
  ) { it =>
    //      r.left.value should include("failed to read JSON")
    //      r.left.value should include("birthYear")
    //      r.left.value should include(wrong_field_json.toAbsolutePath.toString)
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978}, {name: "X", birthYear: Error.Build("null value found")}]"""
    )
  }

  private val wrong_missing_field = tempFile("""[
    |{"name": "Benjamin", "birthYear": 1978},
    |{"name": "X"}
    |]
  """.stripMargin)

  test(
    snapi"""Json.InferAndRead("$wrong_missing_field", sampleSize = 1, preferNulls = false)"""
  ) { it =>
    // r.left.value should include("failed to read JSON")
    //    r.left.value should include("size")
    //    r.left.value should include(wrong_field_json.toAbsolutePath.toString)
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978}, Error.Build("'birthYear': not found")]"""
    )
  }

  private val json_3382 = tempFile("""[{"name": "Michael Jordan", "team": "Chicago Bulls", "started": 1984},
    |{"name": "X", "team": null, "started": "never"}]""".stripMargin)

  test(
    snapi"""Json.InferAndRead("$json_3382", sampleSize = 1, preferNulls = false)"""
  ) { it =>
    //      r.left.value should include("failed to read HJSON")
    //      r.left.value should not include "name"
    //      r.left.value should include("team")
    //      r.left.value should include(hjson_3382.toAbsolutePath.toString)
    it should evaluateTo(
      """[{name: "Michael Jordan", team: "Chicago Bulls", started: 1984},
        |{name: "X", team: Error.Build("null value found"), started: Error.Build("Current token (VALUE_STRING) not numeric, can not use numeric value accessors\n at [Source: (InputStreamReader); line: 2, column: 41]")}]""".stripMargin
    )
  }

  private val xmlFile = tempFile("""<?xml version="1.0" encoding="utf-8"?>
    |<people>
    | <person><name>Benjamin</name><birthYear>1978</birthYear></person>
    | <person><name>X</name><birthYear>0</birthYear></person>
    |</people>""".stripMargin)

  test(
    snapi"""Json.InferAndRead("$xmlFile", sampleSize = 1, preferNulls = false)"""
  ) { it =>
    //      r.left.value should include("failed to read HJSON")
    //      r.left.value should not include "name"
    //      r.left.value should include("team")
    //      r.left.value should include(hjson_3382.toAbsolutePath.toString)
    it should runErrorAs(
      "inference error: invalid JSON: Unexpected character ('<' (code 60)): expected a valid value (JSON String, Number (or 'NaN'/'INF'/'+INF'), Array, Object or token 'null', 'true' or 'false')"
    )
  }

  test(
    snapi"""Json.Read("$xmlFile", type collection(record(name: string, birthYear: int)))"""
  ) { it =>
    it should runErrorAs(
      s"failed to read JSON (line 1 column 2) (url: file:${xmlFile.toAbsolutePath}): Unexpected character ('<' (code 60)): expected a valid value (JSON String, Number (or 'NaN'/'INF'/'+INF'), Array, Object or token 'null', 'true' or 'false')\n at [Source: (InputStreamReader); line: 1, column: 2]"
    )
  }

  private val beatles = tempFile("""[
    |{"name": "John", "birthYear": 1940, "instrument": "guitar"},
    |{"name": "Paul", "birthYear": 1942, "instrument": "bass"},
    |{"birthYear": 1943, "name": "George", "instrument": "guitar"},
    |{"name": "Ringo", "birthYear": "1940"}
    |]""".stripMargin)

  // The record was inferred as tryable. Fields are also tryable (not nullable). The absence of a record field is seen as an error of the record.
  test(snapi"""Json.InferAndRead("$beatles", sampleSize = 1, preferNulls = false)""") { it =>
    it should evaluateTo(
      """[{name: "John", birthYear: 1940, instrument: "guitar"},
        |{name: "Paul", birthYear: 1942, instrument: "bass"},
        |{name: "George", birthYear: 1943, instrument: "guitar"},
        |Error.Build("'instrument': not found")]""".stripMargin
    )
  }

  // The record was inferred as tryable, fields are nullable (and tryable). The absence of a record field is interpreted as a null value
  test(snapi"""Json.InferAndRead("$beatles", sampleSize = 1, preferNulls = true)""") { it =>
    it should evaluateTo(
      """[{name: "John", birthYear: 1940, instrument: "guitar"},
        |{name: "Paul", birthYear: 1942, instrument: "bass"},
        |{name: "George", birthYear: 1943, instrument: "guitar"},
        |{name: "Ringo", birthYear: Error.Build("Current token (VALUE_STRING) not numeric, can not use numeric value accessors\n at [Source: (InputStreamReader); line: 5, column: 33]"), instrument: null}]""".stripMargin
    )
  }

  // The specified record is correct, just misses the 'birthYear' field. But that's fine, it's ignored.
  test(snapi"""Json.Read("$beatles", type collection(record(name: string, instrument: string)))""") { it =>
    it should evaluateTo(
      """[{name: "John", instrument: "guitar"},
        |{name: "Paul", instrument: "bass"},
        |{name: "George", instrument: "guitar"},
        |{name: "Ringo", instrument: null}]""".stripMargin
    )
  }

  // full inference, instrument is null (not tryable). Field birthYear is inferred as string because of the last line.
  test(snapi"""Json.InferAndRead("$beatles", sampleSize = 10)""") { it =>
    it should evaluateTo(
      """[{name: "John", birthYear: "1940", instrument: "guitar"},
        |{name: "Paul", birthYear: "1942", instrument: "bass"},
        |{name: "George", birthYear: "1943", instrument: "guitar"},
        |{name: "Ringo", birthYear: "1940", instrument: null}]""".stripMargin
    )
  }

  private val orType = tempFile("""[
    |  {"host": "server-01", "disks": ["/dev/sda1", "/dev/sda2"]},
    |  {"host": "server-02", "disks": "/dev/sda1"},
    |  {"host": "server-02", "disks": {"partitions": ["/dev/sda1", "/dev/sda2"]}}
    |]""".stripMargin)

  test(snapi"""Json.InferAndRead("$orType")""")(_ should run)
  test(snapi"""Json.Read("$orType",
    |  type collection(
    |    record(host: string,
    |           disks: collection(string) or string or record(partitions: collection(string))
    |          )
    |      )
    |)""".stripMargin)(_ should run)

  // making sure fields of all kinds (lists, records, etc.) are skipped properly when ignored.
  private val ttt = "\"\"\""
  test(
    s"""Json.Parse($ttt[{"a": [1,2,3], "b": {"name": "one", "value": 1}, "c": 14, "d": 12}]$ttt, type collection(record(d: int)))"""
  )(_ should evaluateTo("[{d: 12}]"))

  test(
    s"""Json.Parse($ttt[{"a": [1,2,3], "c": null}]$ttt, type collection(record(a: list(int), b: undefined, c: undefined)))"""
  )

  // Infer and Parse
  test(
    s"""Json.InferAndParse("[1, 2, 3]")"""
  )(_ should evaluateTo("[1, 2, 3]"))

  test(
    s"""let a = Json.InferAndParse(Json.Print({ a:1, b : { c : 2, d : 3 } })) in a.b.c"""
  )(_ should evaluateTo("2"))

  test(
    s"""Json.InferAndParse("1, 2, 3]")"""
  )(
    _ should runErrorAs(
      "Unexpected character (',' (code 44)): Expected space separating root-level values\n at [Source: (InputStreamReader); line: 1, column: 3]"
    )
  )

  // RD-5986
  test("""Json.Parse("[10, 9, 8]", type string)""")(_ should runErrorAs("unexpected token: START_ARRAY"))
  test("""Json.Parse("{\"a\": 12}", type string)""")(_ should runErrorAs("unexpected token: START_OBJECT"))

  private def isTruffle = compilerService.language.contains("rql2-truffle")

}
