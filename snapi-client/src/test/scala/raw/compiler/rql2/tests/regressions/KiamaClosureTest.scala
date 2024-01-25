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

trait KiamaClosureTest extends CompilerTestContext {

  test("""
    |abs(a: int) = if (a < 0) then -a else a
    |Collection.Sum(Collection.Transform(Int.Range(-10000, 10000), abs))""".stripMargin)(
    _ should evaluateTo("100000000")
  )

  test("""
    |abs(a: int) =
    |  let negative(z: int) = z < 0
    |  in if (negative(a)) then -a else a
    |Collection.Sum(Collection.Transform(Int.Range(-10000, 10000), abs))""".stripMargin)(
    _ should evaluateTo("100000000")
  )

  test("""
    |abs(a: int) =
    |  let negative = (z: int) -> z < 0
    |  in if (negative(a)) then -a else a
    |Collection.Sum(Collection.Transform(Int.Range(-10000, 10000), abs))""".stripMargin)(
    _ should evaluateTo("100000000")
  )

  test("""
    |let abs = (a: int) ->
    |  let negative = (z: int) -> z < 0
    |  in if (negative(a)) then -a else a
    |in Collection.Sum(Collection.Transform(Int.Range(-10000, 10000), abs))""".stripMargin)(
    _ should evaluateTo("100000000")
  )

  test("""min(a: int, b: int) = if (a <= b) then a else b
    |
    |min(14, 15)""".stripMargin)(_ should run)

}
