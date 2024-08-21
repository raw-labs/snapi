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

class RD5884Test extends Rql2TruffleCompilerTestContext {

  test("""let f(x: int, y: int = 12) = 3.14 + x + y
    |in f(1)""".stripMargin)(_ should run)

  test("""let f(x: int, y: int = 12) = 3.14 + x + y
    |in f(1, 2)""".stripMargin)(_ should run)

  test("""let f(x: int, y: int = 12) = 3.14 + x + y
    |in f(1, 2, 3)""".stripMargin)(_ should runErrorAs("too many arguments found"))

  test("""let f(x: int, y: int = 12) = 3.14 + x + y
    |in f(1, 2, 3, 4)""".stripMargin)(_ should runErrorAs("too many arguments found"))

  test("""let f() = 3.14
    |in f()""".stripMargin)(_ should run)

  test("""let f() = 3.14
    |in f(1)""".stripMargin)(_ should runErrorAs("too many arguments found"))

  test("""let f() = 3.14
    |in f(1, 2)""".stripMargin)(_ should runErrorAs("too many arguments found"))
}
