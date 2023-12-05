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

package raw.compiler.rql2.tests.parser

import raw.compiler.rql2.tests.CompilerTestContext

trait FrontendSyntaxAnalyzerTest extends CompilerTestContext {

  // Internal node, not visible to the user parser.
  test(s"""$$package("Collection")""".stripMargin)(it => it shouldNot parse)

  test("""
    |let $$package(v: int) = 1
    |in $$package(1)
    |""".stripMargin)(it => it shouldNot parse)

  test("""
    |let # = 1
    |in x
    |""".stripMargin)(it => it should parseErrorAs("token recognition error at: '#'"))

  test("""
    |let f(v: int) = v + 1
    |in f(#)
    |""".stripMargin)(it => it should parseErrorAs("token recognition error at: '#'"))

  test("""{a = 1}""")(it =>
    it should parseErrorAs(
      "mismatched input '=' expecting {'==', '!=', '<=', '<', '>=', '>', '+', '-', '*', '/', '%', 'and', 'or', '(', ',', '.', '}'}"
    )
  )

  test("""let f = (v: int) => 1
    |in f(1)
    |""".stripMargin)(it => it should parseErrorAs("no viable alternative at input 'f=(v:int)='"))

  test("""let f = (v: int,) -> 1
    |in f(1)
    |""".stripMargin)(it => it should parseErrorAs("no viable alternative at input 'f=(v:int,)'"))

  test("""{ #: 1 }""".stripMargin)(it => it should parseErrorAs("token recognition error at: '#'"))

  test("""{ : 1 }""".stripMargin)(it =>
    it should parseErrorAs(
      "extraneous input ':' expecting {'type', 'bool', 'string', 'location', 'binary', 'byte', 'short', 'int', 'long', 'float', 'double', 'decimal', 'date', 'time', 'interval', 'timestamp', 'record', 'collection', 'list', 'let', 'undefined', 'if', 'null', BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, DECIMAL, '+', '-', 'not', 'true', 'false', STRING, START_TRIPLE_QUOTE, NON_ESC_IDENTIFIER, ESC_IDENTIFIER, '(', '{', '}', '[', BINARY_CONST, '$'}"
    )
  )

  test("""
    |let
    |  hello = type recor(a: int)
    |in
    |  hello
    |""".stripMargin)(it => it should parseErrorAs("Missing ','"))

}
