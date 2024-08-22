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

class FloatPackageTest extends Rql2TruffleCompilerTestContext {

  test(""" Float.From(1)""")(it => it should evaluateTo("1.0f"))

  test(""" Float.From("1.5")""")(it => it should evaluateTo("1.5f"))

  test(""" Float.From(1.5)""")(it => it should evaluateTo("1.5f"))

  test(""" Float.From(1.5D)""")(it => it should evaluateTo("1.5f"))

  test(""" Float.From(List.Build(1, 2, 3))""")(it =>
    it should typeErrorAs("expected either number or string but got list(int)")
  )

  test(""" Float.From("abc")""")(it => it should runErrorAs("cannot cast 'abc' to float"))

  // NaN
  test("""Float.From("NaN")""")(_ should run)

  test("""
    |let NaN = Float.From("NaN")
    |in NaN == NaN""".stripMargin)(_ should evaluateTo("true"))

  test("""
    |let NaN = Float.From("NaN")
    |in NaN < NaN""".stripMargin)(_ should evaluateTo("false"))

  test("""
    |let NaN = Float.From("NaN")
    |in NaN <= NaN""".stripMargin)(_ should evaluateTo("true"))

  test("""
    |let NaN = Float.From("NaN")
    |in NaN <= 1""".stripMargin)(_ should evaluateTo("false"))

  test("""
    |let NaN = Float.From("NaN")
    |in NaN < 1""".stripMargin)(_ should evaluateTo("false"))

  // Errors don't propagate through
  test(""" [Float.From("abc") + 12]""")(it => it should evaluateTo("""[Error.Build("cannot cast 'abc' to float")]"""))

  // Nullability is handled
  test(""" [Float.From("abc" + null) + 12]""")(it => it should evaluateTo("""[null]"""))
  test(""" [Float.From(1b + null) + 12b]""")(it => it should evaluateTo("""[null]"""))

}
