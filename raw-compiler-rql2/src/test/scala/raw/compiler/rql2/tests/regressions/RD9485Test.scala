package raw.compiler.rql2.tests.regressions

import raw.compiler.rql2.tests.CompilerTestContext

trait RD9485Test extends CompilerTestContext {

  property("raw.inferrer.local.xml.sample-size", "2")

  val xmlFile = tempFile("""<?xml version="1.0" encoding="UTF-8"?>
    |<top>
    |   <a>1</a>
    |   <a>1.1</a>
    |   <a>Hello</a>
    |   <a/>
    |</top>""".stripMargin)

  test(s"""Xml.InferAndRead("file://$xmlFile")""") { it =>
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

}
