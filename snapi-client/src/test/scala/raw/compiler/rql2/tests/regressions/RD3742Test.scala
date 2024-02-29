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

trait RD3742Test extends CompilerTestContext {

  val triple = "\"\"\""

  test(s"""let
    |   a = ${triple}Hello World!$triple
    |in
    |   a""".stripMargin)(_ should evaluateTo(s""" "Hello World!" """.stripMargin))

  // the second string should be parsed correctly also
  test(s"""let
    |   a = $triple"$triple,
    |   b = ${triple}Hello
    |world!$triple
    |in
    |   [a, b]""".stripMargin)(_ should evaluateTo(""" ["\"", "Hello\nworld!"] """.stripMargin))

  test(s"""let
    |   a = $triple""$triple,
    |   b = ${triple}Hello
    |world!$triple
    |in
    |   [a, b]""".stripMargin)(_ should evaluateTo(""" ["\"\"", "Hello\nworld!"] """.stripMargin))

  test(s"""let
    |   a = $triple$triple$triple,
    |   b = ${triple}Hello world!$triple
    |in
    |   [a, b]""".stripMargin)(_ should parseErrorAs("missing 'in' at '\",\\n   b = \"'"))

}
