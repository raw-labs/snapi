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

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class RD5920Test extends Rql2TruffleCompilerTestContext {

  test("""let f(x: int) = x * 10
    |in f(10)""".stripMargin)(_ should evaluateTo("100"))

  test("""let f(x: int) = x * 10,
    |    g(x: int) = x / 10,
    |    funcs = [f, g]
    |in List.Transform(funcs, f -> f(10))""".stripMargin)(_ should evaluateTo("[100, 1]"))

  test("""let f(x: int) = x * 10,
    |    g(x: int) = x / 10,
    |    funcs = [f, g],
    |    h = List.Get(funcs, 0)
    |in h(10)""".stripMargin)(_ should evaluateTo("100"))

  test("""let f(x: int) = x * 10,
    |    g(x: int) = x / 10,
    |    funcs = [f, g],
    |    h = List.Get(funcs, 1)
    |in h(10)""".stripMargin)(_ should evaluateTo("1"))

  test("""let f(x: int) = x * 10,
    |    g(x: int) = x / 10,
    |    funcs = [f, g],
    |    h = List.Get(funcs, 2)
    |in h(10)""".stripMargin)(_ should runErrorAs("index out of bounds"))

  test("""let f(x: int) = x * 10,
    |    g: (int) -> int = f
    |in g(10)""".stripMargin)(_ should evaluateTo("100"))

  test("""let f(x: int) = x * 10,
    |    g: (int) -> int = null
    |in g(10)""".stripMargin)(_ should evaluateTo("null"))

  test("""let f(x: int) = x * 10,
    |    g: (int) -> int = Error.Build("bug")
    |in g(10)""".stripMargin)(_ should runErrorAs("bug"))

}
