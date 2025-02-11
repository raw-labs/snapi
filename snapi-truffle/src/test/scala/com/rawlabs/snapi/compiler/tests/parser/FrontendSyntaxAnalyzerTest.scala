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

package com.rawlabs.snapi.compiler.tests.parser

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class FrontendSyntaxAnalyzerTest extends SnapiTestContext {

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
    |""".stripMargin)(it => it should parseErrorAs("the input does not form a valid statement or expression."))

  test("""let f = (v: int,) -> 1
    |in f(1)
    |""".stripMargin)(it => it should parseErrorAs("the input does not form a valid statement or expression."))

  test("""{ #: 1 }""".stripMargin)(it => it should parseErrorAs("token recognition error at: '#'"))

  test("""{ : 1 }""".stripMargin)(it =>
    it should parseErrorAs(
      "the input ':' is not valid here; expected elements are: 'type', 'bool', 'string', 'location', 'binary', 'byte', 'short', 'int', 'long', 'float', 'double', 'decimal', 'date', 'time', 'interval', 'timestamp', 'record', 'collection', 'list', 'let', 'undefined', 'if', 'null', BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, DECIMAL, '+', '-', 'not', 'true', 'false', STRING, '(', '{', '}', '[', BINARY_CONST, identifier."
    )
  )

  test("""
    |let
    |  hello = type recor(a: int)
    |in
    |  hello
    |""".stripMargin)(it => it should parseErrorAs("missing ','"))

}
