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

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class RD8993Test extends SnapiTestContext {

  test("""apply(f: int -> bool) = f(1)
    |apply((x: int) -> true)""".stripMargin)(_ should evaluateTo("true"))

  test("""let apply(f: int -> bool) = f(1)
    |in apply((x: int) -> true)""".stripMargin)(_ should evaluateTo("true"))

  test("""apply(f: (int) -> double) = f(1)
    |apply(x: int -> 12.3)""".stripMargin)(_ should evaluateTo("12.3"))

  // test with function returning int when expecting double
  test("""apply(f: (int) -> double) = f(1)
    |apply((x: int) -> 12)""".stripMargin)(_ should evaluateTo("12.0"))

  test("""let apply(f: (int) -> double) = f(1)
    |in apply((x: int) -> 12.3)""".stripMargin)(_ should evaluateTo("12.3"))

  // That case worked because x < 0 is nullable/tryable, which matches the return type of the function.
  test("""apply(f: (int) -> bool) = f(1)
    |apply((x: int) -> x < 0)""".stripMargin)(_ should evaluateTo("false"))

  // Same with 'let'.
  test("""let apply(f: (int) -> bool) = f(1)
    |in apply((x: int) -> x < 0)""".stripMargin)(_ should evaluateTo("false"))

  // Original RD-8993 bug
  test("""ListJoinRec(
    |    left: list(record(a: int, b: string)),
    |    right: list(record(c: int, d: double)),
    |    predicate: (record(a: int, b: string), record(c: int, d: double)) -> bool
    |): list(record(a: int, b: string, c: int, d: double)) =
    |    let
    |        rec joinRec(
    |            l: list(record(a: int, b: string)),
    |            r: list(record(c: int, d: double)),
    |            acc: list(record(a: int, b: string, c: int, d: double))
    |        ): list(record(a: int, b: string, c: int, d: double)) =
    |            if List.Count(l) == 0 or List.Count(r) == 0 then
    |                acc
    |            else
    |                let
    |                    hdL = List.First(l),
    |                    hdR = List.First(r),
    |                    restL = List.Take(l, List.Count(l) - 1),
    |                    restR = List.Take(r, List.Count(r) - 1)
    |                in
    |                    if predicate(hdL, hdR) then
    |                        joinRec(
    |                            restL,
    |                            restR,
    |                            List.Union(acc, [Record.Concat(hdL, hdR)])
    |                        )
    |                    else
    |                        joinRec(restL, restR, acc)
    |    in
    |        joinRec(left, right, [])
    |
    |ListJoinRec(
    |    [{a: 1, b: "Hello"}, {a: 2, b: "World!"}],
    |    [{c: 1, d: 1.1f}, {c: 2, d: 1.2f}],
    |    (x: record(a: int, b: string), y: record(c: int, d: double)) -> true
    |)""".stripMargin)(_ should run)

  /**
   * **********************************************
   * A small recursive function that hits a bug in L0
   * *********************************************
   */

  // works
  test("""let rec apply(n: int): int = if n < 0 then n else apply(n - 1)
    |in apply(10)""".stripMargin)(_ should run)

  // doesn't work (cycle detected)
  test("""let rec apply(n: int): int = if n > 0 then apply(n - 1) else n
    |in apply(10)""".stripMargin)(_ should run)

  // with a type alias, doesn't work (cycle detected)
  test("""let rType = type int,
    |    rec apply(n: int): rType = if n > 0 then apply(n - 1) else n
    |in apply(10)""".stripMargin)(_ should run)

  // works
  test("""let rec apply(n: int): int = if n < 0 then n else n + apply(n - 1)
    |in apply(10)""".stripMargin)(_ should run)

  // works
  test("""let rec apply(n: int): int = if n > 0 then n + apply(n - 1) else n
    |in apply(10)""".stripMargin)(_ should run)

  /**
   * **********************************************
   * tests related to the error position not being reported.
   * *********************************************
   */

  // The actual function isn't matching the expected type. And the error is reported at the correct funabs position.
  test("""let f: (int) -> bool = x: int -> 12.3
    |in f(12)""".stripMargin)(_ should runErrorAs("expected (int) -> bool but got (int) -> double"))

  test("""let apply(f: (int) -> bool, n: int) = f(n)
    |in apply(x: int -> 12.3, 14)""".stripMargin)(_ should runErrorAs("expected (int) -> bool but got (int) -> double"))

  test("""let aFunction: (int) -> double = x: int -> 12.3,
    |    apply(f: (int) -> bool, n: int) = f(n)
    |in apply(aFunction, 14)""".stripMargin)(_ should runErrorAs("expected (int) -> bool but got (int) -> double"))

  test("""let apply(f: (int) -> bool, n: int) = f(n)
    |in apply((x: int) -> x > 12, true)""".stripMargin)(
    _ should runErrorAs("expected int but got bool")
  )

  test("""let apply(f: (int, int) -> bool, n: int) = f(n, n)
    |in apply((x: int, y: int) -> 12.3, 14)""".stripMargin)(
    _ should runErrorAs("expected (int, int) -> bool but got (int, int) -> double")
  )

}
