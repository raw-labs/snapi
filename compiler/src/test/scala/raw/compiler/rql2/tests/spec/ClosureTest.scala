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

package raw.compiler.rql2.tests.spec

import raw.compiler.rql2.tests.CompilerTestContext

trait ClosureTest extends CompilerTestContext {

  test("""let functions =
    |   List.Transform([1,2,3],
    |   n -> let f(x: int) = x * n in f),
    |       f0 = List.Get(functions, 0),
    |       f1 = List.Get(functions, 1),
    |       f2 = List.Get(functions, 2)
    |in [f0(22), f1(10), f2(33)]""".stripMargin)(it => it should evaluateTo("""[22, 20, 99]"""))

  test("""let mkFun(n: int) = let f(x: int) = x * n in f,
    | f0 = mkFun(0),
    | f1 = mkFun(1),
    | f2 = mkFun(2)
    |in f0(22) + f1(10) + f2(33)""".stripMargin)(it => it should evaluateTo("""76"""))

}
