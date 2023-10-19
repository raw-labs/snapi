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

import raw.compiler.rql2.source.{
  Rql2FloatType,
  Rql2IntType,
  Rql2IsNullableTypeProperty,
  Rql2IsTryableTypeProperty,
  Rql2OrType,
  Rql2StringType,
  Rql2TypeProperty
}
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
    |""".stripMargin)(it => it should parseErrorAs("expected declaration"))

  test("""
    |let f(v: int) = v + 1
    |in f(#)
    |""".stripMargin)(it => it should parseErrorAs("')' expected but '#' found"))

  // Extra commas in type and in let
  test("""let
    |  prType = type record(
    |    title: string,
    |    body: string,
    |    url: string,
    |    // ...
    |  ),
    |in 1
    |""".stripMargin)(it => it should parse)

  test("""{a = 1}""")(it => it should parseErrorAs("use ':' instead of '='"))

  test("""let f = (v: int) => 1
    |in f(1)
    |""".stripMargin)(it => it should parseErrorAs("use '->' instead of '=>'"))

  test("""let f = (v: int,) -> 1
    |in f(1)
    |""".stripMargin)(it => it should parseErrorAs("identifier expected but ')' found"))

  test("""{ #: 1 }""".stripMargin)(it => it should parseErrorAs("'}' expected but '#' found"))

  test("""{ : 1 }""".stripMargin)(it => it should parseErrorAs("'}' expected but ':' found"))

  test("""
    |let
    |  hello = type recor(a: int)
    |in
    |  hello
    |""".stripMargin)(it => it should parseErrorAs("illegal type use"))

}
