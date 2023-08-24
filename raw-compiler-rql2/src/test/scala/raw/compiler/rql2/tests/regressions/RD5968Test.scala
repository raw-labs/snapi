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

import raw.compiler.SnapiInterpolator
import raw.compiler.rql2.tests.CompilerTestContext

trait RD5968Test extends CompilerTestContext {

  private val cdataFile = tempFile("""
    |<person place="world" >
    |   <name>john</name>
    |   Text here!
    |   <age>34</age>
    |   <![CDATA[<a>Hello!</a>]]>
    |</person>""".stripMargin)

  private val entityReferenceFile = tempFile("""
    |<person place="world" >
    |   <name>john</name>
    |   Text here!
    |   <age>34</age>
    |   &lt;a&gt;Hello!&lt;/a&gt;
    |</person>""".stripMargin)

  private val textInts = tempFile("""
    |<person place="world" >
    |   <name>john</name>
    |   1
    |   <age>34</age>
    |   2
    |</person>""".stripMargin)

  test(snapi"""let
    |    data = Xml.InferAndRead("$cdataFile")
    |in
    |    Collection.Transform(data.`#text`, x -> String.Trim(x))""".stripMargin)(
    _ should evaluateTo("""["Text here!", "<a>Hello!</a>"]""".stripMargin)
  )

  test(snapi"""let
    |    data = Xml.InferAndRead("$entityReferenceFile")
    |in
    |    Collection.Transform(data.`#text`, x -> String.Trim(x))""".stripMargin)(
    _ should evaluateTo("""["Text here!", "<a>Hello!</a>"]""".stripMargin)
  )

  test(snapi"""let
    |   data = Xml.Read("$textInts", type record(name: string, age: int, `#text`: list(int)))
    |in
    |    data.`#text`""".stripMargin)(
    _ should evaluateTo("""[1, 2]""".stripMargin)
  )
}
