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

import raw.compiler.RQLInterpolator
import raw.compiler.rql2.tests.{CompilerTestContext, FailAfterNServer}

trait XmlPackageTest extends CompilerTestContext with FailAfterNServer {

  override def failServices: Seq[FailAfter] = Seq(
    FailAfter(
      "/fail-after-10",
      450,
      """<item> <a>1</a> <b>#1</b> <c>1.1</c> </item>
        |<item> <a>2</a> <b>#2</b> <c>2.2</c> </item>
        |<item> <a>3</a> <b>#3</b> <c>3.3</c> </item>
        |<item> <a>4</a> <b>#4</b> <c>4.4</c> </item>
        |<item> <a>5</a> <b>#5</b> <c>5.5</c> </item>
        |<item> <a>6</a> <b>#6</b> <c>6.6</c> </item>
        |<item> <a>7</a> <b>#7</b> <c>7.7</c> </item>
        |<item> <a>8</a> <b>#8</b> <c>8.8</c> </item>
        |<item> <a>9</a> <b>#9</b> <c>9.9</c> </item>
        |<item> <a>10</a> <b>#10</b> <c>10.10</c> </item>
        |<item> <a>11</a> <b>#11</b> <c>11.11</c> </item>
        |<item> <a>12</a> <b>#12</b> <c>12.12</c> </item>
        |""".stripMargin
    )
  )

  private val data = tempFile("""
    |<person place="world" >
    |   <name>john</name>
    |   <age>34</age>
    |   <ints>1</ints>
    |   <ints>2</ints>
    |   <ints>3</ints>
    |</person>""".stripMargin)

  test(
    """Xml.Parse("<person><name>john</name><age>34</age></person>", type record(name: string, age: int))"""
  )(it => it should evaluateTo("""{name: "john", age: 34}"""))

  test("""Xml.Parse("a", type list(record(a: list(int))))""".stripMargin)(it =>
    it should runErrorAs("failed to read XML")
  )

  test("""Xml.Parse("a", type record(a: location))""".stripMargin)(it => it should runErrorAs("unsupported type"))

  test(
    rql"""Xml.Read("$data", type record(`@place`: string, name: string, age: int, ints: list(int)))""".stripMargin
  )(it => it should evaluateTo("""{
    |  `@place`: "world",
    |  name: "john",
    |  age: 34,
    |  ints: [1, 2, 3]
    |}
    |""".stripMargin))

  test(rql"""Xml.InferAndRead("$data")""".stripMargin)(it => it should evaluateTo("""{
    |  `@place`: "world",
    |  name: "john",
    |  age: 34,
    |  ints: [1, 2, 3]
    |}
    |""".stripMargin))

  test(
    """Xml.Parse("<n>1</n><n>2</n><n>3</n>", type list(int))"""
  )(it => it should evaluateTo("""[1, 2, 3]"""))

  test("""
    |let t = type collection(int)
    |in
    |  Xml.Parse("<n>1</n><n>2</n><n>3</n>", type t)
    |""".stripMargin)(it => it should evaluateTo("""[1, 2, 3]"""))

  test("""
    |let t = type int
    |in
    |  Xml.Parse("<n>42</n>", type t)
    |""".stripMargin)(it => it should evaluateTo("42"))

  private val personList = tempFile("""
    |<person place="world"> <name>john</name> <age>34</age> </person>
    |<person place="venus"> <name>jane</name> <age>32</age> </person>
    |<person place="moon"> <name>bob</name> <age>36</age> </person>
    |""".stripMargin)

  test(rql"""Xml.InferAndRead("$personList")""".stripMargin) { it =>
    it should evaluateTo("""[
      |  {`@place`: "world", name: "john", age: 34},
      |  {`@place`: "venus", name: "jane", age: 32},
      |  {`@place`: "moon", name: "bob", age: 36}
      |]""".stripMargin)
  }

  private val fileWithAKeywordField = tempFile("""
    |<item><name>Penne a la Siciliana</name> <type>food</type> <price>13</price></item>
    |<item><name>Back to the Future</name> <type>DVD</type> <price>9.99</price></item>
    |<item><name>Microphone</name> <type>device</type> <price>99</price></item>
    |""".stripMargin)

  test(rql"""Collection.Filter(
    |  Xml.InferAndRead("$fileWithAKeywordField"),
    |  i -> i.price < 10
    |)""".stripMargin)(
    _ should evaluateTo("""Collection.Build(Record.Build(name="Back to the Future", `type`="DVD", price=9.99))""")
  )

  private val data2 = tempFile("""
    |<item><a>1</a> <b>10</b> <c>100</c></item>
    |<item><a>2</a> <b>20</b> <c>200</c></item>
    |<item><a>3</a> <b>30</b> <c>300</c></item>""".stripMargin)

  test("""Xml.Read(
    |  "file:/something/else",
    |  type collection(record(a: int, b: int, c: regex(0)))
    |)""".stripMargin)(
    _ should typeErrorAs("unsupported type")
  )

  test("""Xml.Read(
    |  "file:/something/else",
    |  type collection(record(a: int, b: int, c: location))
    |)""".stripMargin)(
    _ should typeErrorAs("unsupported type")
  )

  test(rql"""Xml.InferAndRead("$data2")""") { it =>
    it should evaluateTo("""[
      | {a: 1, b: 10, c: 100},
      | {a: 2, b: 20, c: 200},
      | {a: 3, b: 30, c: 300}
      |]""".stripMargin)
  }
// Error handling

  test(rql"""let d = Xml.Read("$data2", type collection(record(a: int, b: int, c: int)))
    |in Try.IsError(d)""".stripMargin)(_ should typeErrorAs("cannot be applied to a collection"))

  test(rql"""let d = Xml.Read("file:/not/found", type collection(record(a: int, b: int, c: int)))
    |in Try.IsError(d)""".stripMargin)(_ should typeErrorAs("cannot be applied to a collection"))

  test(rql"""let d = Xml.Read("file:/not/found", type collection(record(a: int, b: int, c: int))),
    |c = Collection.Count(d)
    |in Try.IsError(c)""".stripMargin)(_ should evaluateTo("true"))

  test(rql"""let d = Xml.Read("file:/not/found", type record(a: int, b: int, c: list(int))),
    |c = List.Count(d.c)
    |in Try.IsError(c)""".stripMargin)(_ should evaluateTo("true"))

  test(rql"""Xml.InferAndRead("file:/not/found")""".stripMargin)(it => it should runErrorAs("path not found"))

  test(rql"""Xml.Read("file:/not/found", type collection(int))""".stripMargin)(it =>
    it should runErrorAs("path not found")
  )

  test(rql"""let urls = List.Build("file:/not/found", "$data2"),
    |    contents = List.Transform(urls, u -> Xml.Read(u, type collection(record(a: int, b: int, c: int)))),
    |    counts = List.Transform(contents, c -> Collection.Count(c))
    |in counts""".stripMargin)(
    _ should evaluateTo("""List.Build(Error.Build("file system error: path not found: /not/found"), 3L)""")
  )

  test(rql"""List.Build(
    |    Collection.Count(Xml.InferAndRead("file:/not/found")),
    |    Collection.Count(Xml.InferAndRead("$data2"))
    |)""".stripMargin)(
    _ should runErrorAs("path not found")
  )

  private val strings = tempFile("""<top>
    |  <n>1</n>
    |  <n>2</n>
    |  <n>Hello</n>
    |  <n>3</n>
    |</top>""".stripMargin)

  test(rql"""Xml.Read("$strings", type record(n: list(int)))""".stripMargin)(
    _ should orderEvaluateTo(
      """{ n: [
        |  1,
        |  2,
        |  Error.Build("failed to parse XML (line 4 column 11): cannot cast 'Hello' to int"),
        |  3
        |]}""".stripMargin
    )
  )

  private val recordInTheMiddle = tempFile("""<top>
    |  <n>1</n>
    |  <n>2</n>
    |  <n><a>1</a><b>hello</b></n>
    |  <n>3</n>
    |</top>""".stripMargin)

  test(rql"""Xml.Read("$recordInTheMiddle", type record(n: list(int))) """.stripMargin)(
    _ should orderEvaluateTo("""{ n: [
      |  1,
      |  2,
      |  Error.Build("failed to parse XML (line 4 column 6): expected end-element </n> but got start-element <a>"),
      |  3
      |]}""".stripMargin)
  )

  // here we are ignoring the unexpected tags
  private val unexpectedTags = tempFile("""<top>
    |  <n>1</n>
    |  <n>2</n>
    |  <n a="1" b="2">3</n>
    |  <n>4</n>
    |</top>""".stripMargin)

  test(rql"""Xml.Read("$unexpectedTags", type record(n: list(int))) """.stripMargin)(
    _ should orderEvaluateTo(
      """{n: [1, 2, 3, 4]}"""
    )
  )

  private val tagErrorsMiddle = tempFile("""<top>
    |<person age="34" place="world"> <name>john</name> <job>marketing</job> </person>
    |<person age="xxx" place="venus"> <name>jane</name> <job>engineering</job> </person>
    |<person age="36" place="moon"> <name>bob</name> <job>accounting</job> </person>
    |</top>""".stripMargin)

  test(rql"""let
    |  t = type record(
    |    person: list(
    |      record(`@age`: int, `@place`: string, name: string, job: string)
    |    )
    |  )
    |in
    |  List.Filter(
    |    Xml.Read("$tagErrorsMiddle", t).person,
    |    x -> Try.IsError(x.`@age`)
    |  )""".stripMargin)(
    _ should orderEvaluateTo(
      """[
        |   {
        |       `@age`: Error.Build("failed to parse XML (line 3 column 1): cannot cast 'xxx' to int"),
        |       `@place`: "venus",
        |       name: "jane",
        |       job: "engineering"
        |    }
        |]""".stripMargin
    )
  )

  test(s"""Xml.Read("$testServerUrl/fail-after-10", type collection(record(a: int, b: string, c: double)))""")(
    _ should runErrorAs(s"failed to read XML (url: $testServerUrl/fail-after-10): closed")
  )

  test(
    s"""Collection.Take(Xml.Read("$testServerUrl/fail-after-10", type collection(record(a: int, b: string, c: double))), 9)"""
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
    s"""Collection.Take(Xml.Read("$testServerUrl/fail-after-10", type collection(record(a: int, b: string, c: double))), 11)"""
  )(
    _ should runErrorAs(s"failed to read XML (url: $testServerUrl/fail-after-10): closed")
  )

  test(
    s"""Collection.Count(Xml.Read("$testServerUrl/fail-after-10", type collection(record(a: int, b: string, c: double))))""".stripMargin
  )(
    _ should runErrorAs(s"failed to read XML (url: $testServerUrl/fail-after-10): closed")
  )

  test(
    s"""Try.IsError(Collection.Count(Xml.Read("$testServerUrl/fail-after-10", type collection(record(a: int, b: string, c: double)))) ) """
  ) {
    _ should evaluateTo("true")
  }

  test(
    s"""Try.IsError( List.From(Xml.Read("$testServerUrl/fail-after-10", type collection(record(a: int, b: string, c: double)))) ) """
  ) {
    _ should evaluateTo("true")
  }

  test(
    s""" List.From( Collection.Take(Xml.Read("$testServerUrl/fail-after-10", type collection(record(a: int, b: string, c: double))) , 9 )) """
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

  test(s"""Try.IsError(
    |  List.From(Collection.Take(Xml.Read("$testServerUrl/fail-after-10", type collection(record(a: int, b: string, c: double))), 9))
    |)""".stripMargin) {
    _ should evaluateTo("false")
  }

  private val orRecords = tempFile("""<top>
    |   <list>
    |       <c>tralala</c>
    |       <d>true</d>
    |   </list>
    |   <list>
    |       <a>12</a>
    |       <b>14</b>
    |   </list>
    |</top>""".stripMargin)

  test(rql"""Xml.Read("$orRecords",
    |type record(list: collection(record(a: int, b: int) or record(c: string, d: bool)))
    |)""".stripMargin)(
    _ should run
  )

  test(rql"""Xml.InferAndRead("$orRecords")""".stripMargin)(
    _ should run
  )

  //Tags are defined as collections, but that does not make so much sense.
  // Should we disable this option, throw a validation error?
  test(
    """let
      | s = "<r att=\"1\"><tag>2</tag>3</r>"
      |in
      | Xml.Parse(s, type record(tag: collection(byte), notag: collection(byte), `#text`: collection(byte), `@att`: collection(byte)))
      |""".stripMargin
  )(it => it should evaluateTo("{tag: [Byte.From(2)], notag: [], `#text`: [Byte.From(3)], `@att`: [Byte.From(1)]}"))

  test(
    """let
      | s = "<r att=\"1\"><tag>2</tag>3</r>"
      |in
      | Xml.Parse(s, type record(tag: list(byte), notag: list(byte), `#text`: list(byte), `@att`: list(byte)))
      |""".stripMargin
  )(it => it should evaluateTo("{tag: [Byte.From(2)], notag: [], `#text`: [Byte.From(3)], `@att`: [Byte.From(1)]}"))
}
