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
package raw.compiler.rql2.tests.regressions

import raw.compiler.rql2.tests.CompilerTestContext

trait RD9485Test extends CompilerTestContext {

  property("raw.inferrer.local.xml.sample-size", "2")

  val xmlList = tempFile("""<?xml version="1.0" encoding="UTF-8"?>
    |<top>
    |   <a>1</a>
    |   <a>1.1</a>
    |   <a>Hello</a>
    |   <a/>
    |</top>""".stripMargin)

  test(s"""Xml.InferAndRead("file://$xmlList")""") { it =>
    it should evaluateTo("""{
      |  a: [
      |      1.0,
      |      1.1,
      |      Error.Build("failed to parse XML (line 5 column 12): cannot cast 'Hello' to double"),
      |      null
      |  ]
      |}
      |""".stripMargin)
  }

  test(s"""Xml.InferAndRead("file://$xmlList", preferNulls=false)""") { it =>
    it should evaluateTo("""{
      |  a: [
      |      1.0,
      |      1.1,
      |      Error.Build("failed to parse XML (line 5 column 12): cannot cast 'Hello' to double"),
      |      Error.Build("failed to parse XML (line 6 column 4): cannot cast '' to double")
      |  ]
      |}
      |""".stripMargin)
  }

  val xmlRecords = tempFile("""<?xml version="1.0" encoding="UTF-8"?>
    |<top>
    |   <a arg1="1" arg2="2">
    |       <b>3</b>
    |       <c>Hello</c>
    |   </a>
    |   <a arg1="1.1" arg2="2.2">
    |       <b>3.3</b>
    |       <c>World!</c>
    |   </a>
    |   <a arg1="Hello" arg2="World!">
    |       <b>Hello</b>
    |       <c>Again!</c>
    |   </a>
    |   <a arg1="where is the other one?"></a>
    |</top>""".stripMargin)

  test(s"""Xml.InferAndRead("file://$xmlRecords")""") { it =>
    it should evaluateTo("""{
      |  a: [
      |     {
      |         `@arg1`: 1.0,
      |         `@arg2`: 2.0,
      |         b: 3.0,
      |         c: "Hello"
      |     },
      |     {
      |         `@arg1`: 1.1,
      |         `@arg2`: 2.2,
      |         b: 3.3,
      |         c: "World!"
      |     },
      |     {
      |         `@arg1`: Error.Build("failed to parse XML (line 11 column 4): cannot cast 'Hello' to double"),
      |         `@arg2`: Error.Build("failed to parse XML (line 11 column 4): cannot cast 'World!' to double"),
      |         b: Error.Build("failed to parse XML (line 12 column 16): cannot cast 'Hello' to double"),
      |         c: "Again!"
      |     },
      |     {
      |         `@arg1`: Error.Build("failed to parse XML (line 15 column 4): cannot cast 'where is the other one?' to double"),
      |         `@arg2`: null,
      |         b: null,
      |         c: null
      |     }
      |  ]
      |}
      |""".stripMargin)
  }

  test(s"""Xml.InferAndRead("file://$xmlRecords", preferNulls=false)""") { it =>
    it should runErrorAs("""failed to parse XML""".stripMargin)
  }

}
