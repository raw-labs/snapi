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

package com.rawlabs.snapi.compiler.tests.builtin

import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class DoublePackageTest extends Rql2TruffleCompilerTestContext {

  test(""" Double.From(1)""")(it => it should evaluateTo("1.0"))

  test(""" Double.From("1.5")""")(it => it should evaluateTo("1.5"))

  test(""" Double.From(1.5)""")(it => it should evaluateTo("1.5"))

  test(""" Double.From(1.5D)""")(it => it should evaluateTo("1.5"))

  test(""" Double.From(List.Build(1, 2, 3))""")(it =>
    it should typeErrorAs("expected either number or string but got list(int)")
  )

  test(""" Double.From("abc")""")(it => it should runErrorAs("cannot cast 'abc' to double"))

  // NaN
  test("""Double.From("NaN")""")(_ should run)

  test("""
    |let NaN = Double.From("NaN")
    |in NaN == NaN""".stripMargin)(_ should evaluateTo("true"))

  test("""
    |let NaN = Double.From("NaN")
    |in NaN < NaN""".stripMargin)(_ should evaluateTo("false"))

  test("""
    |let NaN = Double.From("NaN")
    |in NaN <= NaN""".stripMargin)(_ should evaluateTo("true"))

  test("""
    |let NaN = Double.From("NaN")
    |in NaN <= 1""".stripMargin)(_ should evaluateTo("false"))

  test("""
    |let NaN = Double.From("NaN")
    |in NaN < 1""".stripMargin)(_ should evaluateTo("false"))

  // Errors don't propagate through
  test(""" [Double.From("abc") + 12]""")(it => it should evaluateTo("""[Error.Build("cannot cast 'abc' to double")]"""))

  // Nullability is handled
  test(""" [Double.From("abc" + null) + 12]""")(it => it should evaluateTo("""[null]"""))
  test(""" [Double.From(1 + null) + 12]""")(it => it should evaluateTo("""[null]"""))

}
