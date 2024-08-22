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

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class RD4529Test extends Rql2TruffleCompilerTestContext {

  test("""
    |let a: float = 1.0f
    |in let b: int = 1
    |   in
    |   if (a > b)
    |   then a - b
    |   else b - a
    |""".stripMargin)(it => it should run)

  test("""
    |let a: float = 1.0f
    |in let b: int = 1
    |   in
    |   (if (a == 1) then b else a) + (if (a >= 1) then a else b)""".stripMargin)(it => it should run)

  test("""
    |let a: float = 1.0f
    |in let b: int = 1
    |   in
    |   Math.Cos(if (a == 1) then b else 12) + (if (a >= 1) then 14 else b)""".stripMargin)(it => it should run)

  test("""
    |let f(a: float, b: int): float =
    |   Math.Sign(if (a == 1) then b else 12) + (if (a >= 1) then 14 else b)
    |in f(1.0f, 14)
    |""".stripMargin)(it => it should run)

}
