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
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class XmlPackageTest extends Rql2TruffleCompilerTestContext {

  private val allTypes = tempFile("""<items>
    | <byte>120</byte>
    | <short>32760</short>
    | <int>2147483640</int>
    | <long>922337203685477580</long>
    | <float>1.2345678</float>
    | <double>1.23456789012345678</double>
    | <decimal>123456789012345678</decimal>
    | <boolean>true</boolean>
    | <string>hello</string>
    | <date>2019-01-01</date>
    | <timestamp>2019-01-01T00:00:00</timestamp>
    | <time>03:01:02</time>
    |</items>""".stripMargin)

  test(snapi"""Xml.Read("$allTypes", type record(
    |  byte: byte,
    |  short: short,
    |  int: int,
    |  long: long,
    |  float: float,
    |  double: double,
    |  decimal: decimal,
    |  boolean: bool,
    |  string: string,
    |  date: date,
    |  timestamp: timestamp,
    |  time: time
    |))""".stripMargin)(it => it should evaluateTo("""{
    |  byte: Byte.From("120"),
    |  short: Short.From("32760"),
    |  int: Int.From("2147483640"),
    |  long: Long.From("922337203685477580"),
    |  float: Float.From("1.2345678"),
    |  double: Double.From("1.23456789012345678"),
    |  decimal: Decimal.From("123456789012345678"),
    |  boolean: true,
    |  string: "hello",
    |  date: Date.Parse("2019-01-01", "yyyy-MM-dd"),
    |  timestamp: Timestamp.Parse("2019-01-01T00:00:00", "yyyy-M-d'T'H:m:s"),
    |  time: Time.Parse("03:01:02", "H:m:s")
    |}""".stripMargin))

  private val junkAfter10Items = tempFile("""<item> <a>1</a> <b>#1</b> <c>1.1</c> </item>
    |<item> <a>2</a> <b>#2</b> <c>2.2</c> </item>
    |<item> <a>3</a> <b>#3</b> <c>3.3</c> </item>
    |<item> <a>4</a> <b>#4</b> <c>4.4</c> </item>
    |<item> <a>5</a> <b>#5</b> <c>5.5</c> </item>
    |<item> <a>6</a> <b>#6</b> <c>6.6</c> </item>
    |<item> <a>7</a> <b>#7</b> <c>7.7</c> </item>
    |<item> <a>8</a> <b>#8</b> <c>8.8</c> </item>
    |<item> <a>9</a> <b>#9</b> <c>9.9</c> </item>
    |<item> <a>10</a> <b>#10</b> <c>10.10</c> </item>########################
    |""".stripMargin)

  private val data = tempFile("""
    |<person place="world" >
    |   <name>john</name>
    |   <age>34</age>
    |   <ints>1</ints>
    |   <ints>2</ints>
    |   <ints>3</ints>
    |</person>""".stripMargin)

  private val qqq = "\"\"\""

  private val simple = tempFile("""
    |<person>
    |   <name>john</name>
    |   <age>34</age>
    |</person>""".stripMargin)

  private val simpleWithNestedRecord = tempFile("""
    |<person>
    |   <name>hulk</name>
    |   <dimensions><width>1600</width><height>900</height></dimensions>
    |</person>""".stripMargin)

  private val simpleWithList = tempFile("""
    |<person>
    |   <name>jane</name>
    |   <age>1</age>
    |   <age>2</age>
    |   <age>3</age>
    |</person>""".stripMargin)

  private val simpleWithAttributes = tempFile("""
    |<person name="jane" age="32">
    |   <name>jane</name>
    |   <last>doe</last>
    |</person>""".stripMargin)

  private val simpleWithText = tempFile("""
    |<person>
    |   Is this the real life?
    |   <last>Mercury</last>
    |   <![CDATA[Is this just fantasy?]]>
    |   <first>Freddie</first>
    |</person>""".stripMargin)

  private val simpleWithNumericText = tempFile("""
    |<person>
    |   123
    |   <last>Mercury</last>
    |   456
    |   <first>Freddie</first>
    |</person>""".stripMargin)

  private val twoPeopleStr = """
    |<person>
    |   <name>john</name>
    |   <age>34</age>
    |</person>
    |<person>
    |   <name>jane</name>
    |   <age>34</age>
    |</person>""".stripMargin
  private val twoPeople = tempFile(twoPeopleStr)

  private val nullableField = tempFile("""
    |<person>
    |   <name>john</name>
    |   <age>34</age>
    |</person>
    |<person>
    |   <name>jane</name>
    |   <age/>
    |</person>""".stripMargin)

  private val nullableRecord = tempFile("""
    |<person>
    |   <name>john</name>
    |   <age>34</age>
    |</person>
    |<person/>""".stripMargin)

  test(snapi"""Xml.InferAndRead("$simple")""") { it =>
    it should typeAs("record(name: string, age: int)")
    it should evaluateTo("{name: \"john\", age: 34}")
  }

  test(snapi"""Xml.Read("$simple", type record(name: string, age: int))""") { it =>
    it should typeAs("record(name: string, age: int)")
    it should evaluateTo("{name: \"john\", age: 34}")
  }

  test(snapi"""Xml.Read("$simple", type record(name: string, age: bool or int))""") { it =>
    it should typeAs("record(name: string, age: bool or int)")
    it should evaluateTo("{name: \"john\", age: 34}")
  }

  test(snapi"""Xml.Read("$simple", type record(name: int or string, age: int))""") { it =>
    it should typeAs("record(name: int or string, age: int)")
    it should evaluateTo("{name: \"john\", age: 34}")
  }

  test(snapi"""Xml.Read("$simple", type record(name: int or string, age: bool or int))""") { it =>
    it should typeAs("record(name: int or string, age: bool or int)")
    it should evaluateTo("{name: \"john\", age: 34}")
  }

  test(snapi"""Xml.InferAndRead("$simpleWithNestedRecord")""") { it =>
    it should typeAs("record(name: string, dimensions: record(width: int, height: int))")
    it should evaluateTo("{name: \"hulk\", dimensions: {width: 1600, height: 900}}")
  }

  test(
    snapi"""Xml.Read("$simpleWithNestedRecord", type record(name: string, dimensions: record(width: int, height: int)))"""
  ) { it =>
    it should typeAs("record(name: string, dimensions: record(width: int, height: int))")
    it should evaluateTo("{name: \"hulk\", dimensions: {width: 1600, height: 900}}")
  }

  test(snapi"""Xml.InferAndRead("$simpleWithList")""") { it =>
    it should typeAs("record(name: string, age: collection(int))")
    it should evaluateTo("{name: \"jane\", age: [1, 2, 3]}")
  }

  test(snapi"""Xml.Read("$simpleWithList", type record(name: string, age: collection(int)))""") { it =>
    it should typeAs("record(name: string, age: collection(int))")
    it should evaluateTo("{name: \"jane\", age: [1, 2, 3]}")
  }

  test(snapi"""Xml.Read("$simpleWithList", type record(name: string, age: list(int)))""") { it =>
    it should typeAs("record(name: string, age: list(int))")
    it should evaluateTo("{name: \"jane\", age: [1, 2, 3]}")
  }

  test(snapi"""Xml.InferAndRead("$simpleWithAttributes")""") { it =>
    it should typeAs("record(`@name`: string, `@age`: int, name: string, last: string)")
    it should evaluateTo("{`@name`: \"jane\", `@age`: 32, name: \"jane\", last: \"doe\"}")
  }

  test(
    snapi"""Xml.Read("$simpleWithAttributes", type record(`@name`: string, `@age`: int, name: string, last: string))"""
  ) { it =>
    it should typeAs("record(`@name`: string, `@age`: int, name: string, last: string)")
    it should evaluateTo("{`@name`: \"jane\", `@age`: 32, name: \"jane\", last: \"doe\"}")
  }

  test(snapi"""Xml.InferAndRead("$simpleWithNumericText")""") { it =>
    it should typeAs("record(last: string, `#text`: collection(string), first: string)")
    it should evaluateTo(
      "{last: \"Mercury\", `#text`: [\"\\n   123\\n   \", \"\\n   456\\n   \"], first: \"Freddie\"}"
    )
  }

  test(
    snapi"""Xml.Read("$simpleWithNumericText", type record(last: string, `#text`: collection(string), first: string))"""
  ) { it =>
    it should typeAs("record(last: string, `#text`: collection(string), first: string)")
    it should evaluateTo(
      "{last: \"Mercury\", `#text`: [\"\\n   123\\n   \", \"\\n   456\\n   \"], first: \"Freddie\"}"
    )
  }

  test(
    snapi"""Xml.Read("$simpleWithNumericText", type record(last: string, `#text`: collection(int), first: string))"""
  ) { it =>
    it should typeAs("record(last: string, `#text`: collection(int), first: string)")
    it should evaluateTo(
      "{last: \"Mercury\", `#text`: [123,456], first: \"Freddie\"}"
    )
  }

  test(snapi"""Xml.InferAndRead("$simpleWithText")""") { it =>
    it should typeAs("record(last: string, `#text`: collection(string), first: string)")
    it should evaluateTo(
      "{last: \"Mercury\", `#text`: [\"\\n   Is this the real life?\\n   \", \"Is this just fantasy?\"], first: \"Freddie\"}"
    )
  }

  test(s"""Xml.Parse($qqq$twoPeopleStr$qqq, type collection(record(name: string, age: int)))""") { it =>
    it should typeAs("collection(record(name: string, age: int))")
    it should evaluateTo(
      """[{name: "john", age: 34}, {name: "jane", age: 34}]"""
    )
  }

  test(s"""Xml.Parse($qqq$twoPeopleStr$qqq, type list(record(name: string, age: int)))""") { it =>
    it should typeAs("list(record(name: string, age: int))")
    it should evaluateTo(
      """[{name: "john", age: 34}, {name: "jane", age: 34}]"""
    )
  }

  test(snapi"""Xml.InferAndRead("$twoPeople")""") { it =>
    it should typeAs("collection(record(name: string, age: int))")
    it should evaluateTo(
      """[{name: "john", age: 34}, {name: "jane", age: 34}]"""
    )
  }

  test(snapi"""Xml.Read("$twoPeople", type collection(record(name: string, age: int)))""") { it =>
    it should typeAs("collection(record(name: string, age: int))")
    it should evaluateTo(
      """[{name: "john", age: 34}, {name: "jane", age: 34}]"""
    )
  }

  test(snapi"""Xml.Read("$twoPeople", type list(record(name: string, age: int)))""") { it =>
    it should typeAs("list(record(name: string, age: int))")
    it should evaluateTo(
      """[{name: "john", age: 34}, {name: "jane", age: 34}]"""
    )
  }

  test(snapi"""Xml.InferAndRead("$nullableField")""") { it =>
    it should typeAs("collection(record(name: string, age: int))")
    it should evaluateTo(
      """[{name: "john", age: 34}, {name: "jane", age: null}]"""
    )
  }

  test(snapi"""Xml.Read("$nullableField", type collection(record(name: string, age: int)))""") { it =>
    it should typeAs("collection(record(name: string, age: int))")
    it should evaluateTo(
      """[{name: "john", age: 34}, {name: "jane", age: null}]"""
    )
  }

  test(snapi"""Xml.InferAndRead("$nullableRecord")""") { it =>
    it should typeAs("collection(record(name: string, age: int))")
    it should evaluateTo(
      """[{name: "john", age: 34}, {name: null, age: null}]"""
    )
  }

  test(snapi"""Xml.Read("$nullableRecord", type collection(record(name: string, age: int)))""") { it =>
    it should typeAs("collection(record(name: string, age: int))")
    it should evaluateTo(
      """[{name: "john", age: 34}, {name: null, age: null}]"""
    )
  }

  test(snapi"""Xml.Read("$simple", type record(name: string))""") { it =>
    it should typeAs("record(name: string)")
    it should evaluateTo("{name: \"john\"}")
  }

  test(snapi"""Xml.Read("$simple", type record(age: int))""") { it =>
    it should typeAs("record(age: int)")
    it should evaluateTo("{age: 34}")
  }

  test(
    """Xml.Parse("<person><name>john</name><age>34</age></person>", type record(name: string, age: int))"""
  )(it => it should evaluateTo("""{name: "john", age: 34}"""))

  test("""Xml.Parse("a", type list(record(a: list(int))))""".stripMargin)(it =>
    it should runErrorAs("failed to read XML")
  )

  test("""Xml.Parse("a", type record(a: location))""".stripMargin)(it => it should runErrorAs("unsupported type"))

  test(
    snapi"""Xml.Read("$data", type record(`@place`: string, name: string, age: int, ints: list(int)))""".stripMargin
  )(it => it should evaluateTo("""{
    |  `@place`: "world",
    |  name: "john",
    |  age: 34,
    |  ints: [1, 2, 3]
    |}
    |""".stripMargin))

  test(snapi"""Xml.InferAndRead("$data")""".stripMargin)(it => it should evaluateTo("""{
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

  test(snapi"""Xml.InferAndRead("$personList")""".stripMargin) { it =>
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

  test(snapi"""Collection.Filter(
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
    |  type collection(record(a: int, b: int, c: location))
    |)""".stripMargin)(
    _ should typeErrorAs("unsupported type")
  )

  test(snapi"""Xml.InferAndRead("$data2")""") { it =>
    it should evaluateTo("""[
      | {a: 1, b: 10, c: 100},
      | {a: 2, b: 20, c: 200},
      | {a: 3, b: 30, c: 300}
      |]""".stripMargin)
  }
// Error handling

  test(snapi"""let d = Xml.Read("$data2", type collection(record(a: int, b: int, c: int)))
    |in Try.IsError(d)""".stripMargin)(_ should typeErrorAs("cannot be applied to a collection"))

  test(snapi"""let d = Xml.Read("file:/not/found", type collection(record(a: int, b: int, c: int)))
    |in Try.IsError(d)""".stripMargin)(_ should typeErrorAs("cannot be applied to a collection"))

  test(snapi"""let d = Xml.Read("file:/not/found", type collection(record(a: int, b: int, c: int))),
    |c = Collection.Count(d)
    |in Try.IsError(c)""".stripMargin)(_ should evaluateTo("true"))

  test(snapi"""let d = Xml.Read("file:/not/found", type record(a: int, b: int, c: list(int))),
    |c = List.Count(d.c)
    |in Try.IsError(c)""".stripMargin)(_ should evaluateTo("true"))

  test(snapi"""Xml.InferAndRead("file:/not/found")""".stripMargin)(it => it should runErrorAs("path not found"))

  test(snapi"""Xml.Read("file:/not/found", type collection(int))""".stripMargin)(it =>
    it should runErrorAs("path not found")
  )

  test(snapi"""let urls = List.Build("file:/not/found", "$data2"),
    |    contents = List.Transform(urls, u -> Xml.Read(u, type collection(record(a: int, b: int, c: int)))),
    |    counts = List.Transform(contents, c -> Collection.Count(c))
    |in counts""".stripMargin)(
    _ should evaluateTo("""List.Build(Error.Build("file system error: path not found: /not/found"), 3L)""")
  )

  test(snapi"""List.Build(
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

  test(snapi"""Xml.Read("$strings", type record(n: list(int)))""".stripMargin)(
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

  test(snapi"""Xml.Read("$recordInTheMiddle", type record(n: list(int))) """.stripMargin)(
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

  test(snapi"""Xml.Read("$unexpectedTags", type record(n: list(int))) """.stripMargin)(
    _ should orderEvaluateTo(
      """{n: [1, 2, 3, 4]}"""
    )
  )

  private val tagErrorsMiddle = tempFile("""<top>
    |<person age="34" place="world"> <name>john</name> <job>marketing</job> </person>
    |<person age="xxx" place="venus"> <name>jane</name> <job>engineering</job> </person>
    |<person age="36" place="moon"> <name>bob</name> <job>accounting</job> </person>
    |</top>""".stripMargin)

  test(snapi"""let
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

  test(snapi"""Xml.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double)))""")(
    _ should runErrorAs(
      snapi"failed to read XML (line 10 column 49) (url: $junkAfter10Items): Unexpected character '#' (code 35) in epilog"
    )
  )

  test(
    snapi"""Collection.Take(Xml.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))), 9)"""
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
    snapi"""Collection.Take(Xml.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))), 11)"""
  )(
    _ should runErrorAs(
      snapi"failed to read XML (line 10 column 49) (url: $junkAfter10Items): Unexpected character '#' (code 35) in epilog"
    )
  )

  test(
    snapi"""Collection.Count(Xml.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))))""".stripMargin
  )(
    _ should runErrorAs(
      snapi"failed to read XML (line 10 column 49) (url: $junkAfter10Items): Unexpected character '#' (code 35) in epilog"
    )
  )

  test(
    snapi"""Try.IsError(Collection.Count(Xml.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double)))) ) """
  ) {
    _ should evaluateTo("true")
  }

  test(
    snapi"""Try.IsError( List.From(Xml.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double)))) ) """
  ) {
    _ should evaluateTo("true")
  }

  test(
    snapi""" List.From( Collection.Take(Xml.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))) , 9 )) """
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
    |  List.From(Collection.Take(Xml.Read("$junkAfter10Items", type collection(record(a: int, b: string, c: double))), 9))
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

  test(snapi"""Xml.Read("$orRecords",
    |type record(list: collection(record(a: int, b: int) or record(c: string, d: bool)))
    |)""".stripMargin)(
    _ should run
  )

  test(snapi"""Xml.InferAndRead("$orRecords")""".stripMargin)(
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

  // stripping unwanted fields.
  test(snapi"""Xml.Read("$data", type record(`@place`: string, name: string))""") { it =>
    it should typeAs("record(`@place`: string, name: string)")
    it should evaluateTo("{`@place`: \"world\", name: \"john\"}")
  }

  // lists are accepted when parsing an attribute.
  test(snapi"""Xml.Read("$data", type record(`@place`: list(string), name: string))""") { it =>
    it should typeAs("record(`@place`: list(string), name: string)")
    it should evaluateTo("{`@place`: [\"world\"], name: \"john\"}")
  }

  // collections are accepted when parsing an attribute.
  test(snapi"""Xml.Read("$data", type record(`@place`: collection(string), name: string))""") { it =>
    it should typeAs("record(`@place`: collection(string), name: string)")
    it should evaluateTo("{`@place`: [\"world\"], name: \"john\"}")
  }

  // other types than primitives, lists and collections are not accepted when parsing an attribute (RD-5910).
  test(snapi"""Xml.Read("$data", type record(`@place`: record(a: string), name: string))""") { it =>
    it should typeErrorAs("unsupported type")
  }

}
