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

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class FunAbsTest extends Rql2TruffleCompilerTestContext {

  test("""
    |let f = (v: int) -> v + 1
    |in f(1)
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("2")
  }

  test("""
    |let f = (v: int): string -> v
    |in f(1)
    |""".stripMargin)(it => it should typeErrorAs("expected string but got int"))

  test("""
    |let f = (a: int, b: string, c: bool = true) -> "a"
    |in f
    |""".stripMargin)(it => it should typeAs("(int, string, c: bool) -> string"))

  test("""
    |let t = type int,
    |    f = (a: t, b: t, c: bool = true) -> "a"
    |in f
    |""".stripMargin)(it => it should typeAs("(int, int, c: bool) -> string"))

  test("""
    |let f = (a: int, b: bool = true) -> "a"
    |in f
    |""".stripMargin)(it => it should typeAs("(int, b: bool) -> string"))

  ignore("""
    |let f = (a: int, b: int = 2) -> a + b
    |in f(1)
    |""".stripMargin)(it => it should evaluateTo("3"))

  ignore("""
    |let f = (a: int, b: int = 2) -> a + b
    |in f(1, b = 1)
    |""".stripMargin)(it => it should evaluateTo("2"))

  test("""let f = (a: float) -> a * 2
    |in f(1l)
    |""".stripMargin) { it =>
    it should typeAs("float")
    it should evaluateTo("2f")
  }

  // case that is impossible to run without proper closures
  test("""
    |let mkGt(v: int) =
    |   let f(x: int) = [x, v]
    |   in f
    |in
    |   let
    |       f1 = mkGt(10)
    |   in f1(-10)""".stripMargin)(
    _ should evaluateTo("[-10, 10]")
  )

  // case that is impossible to run without proper closures
  test("""
    |let mkGt(v: int) =
    |   let f(x: int) = [x, v]
    |   in f
    |in
    |   let
    |       list = [-50, 0, 50, 100, 150],
    |       f1 = mkGt(0),
    |       f2 = mkGt(100)
    |   in [f1(-10), f1(10), f1(200), f2(-10), f2(10), f2(200) ]""".stripMargin)(
    _ should orderEvaluateTo("[ [-10, 0], [10, 0], [200, 0], [-10, 100], [10, 100], [200, 100] ]")
  )

  // case that is impossible to run without proper closures
  test("""
    |let mkGt(v: int) =
    |   let v2 = v*2,
    |       f(x: int) = [x, v2]
    |   in f
    |in
    |   let
    |       f1 = mkGt(10)
    |   in f1(-10)""".stripMargin)(
    _ should evaluateTo("[-10, 20]")
  )

  // case that is impossible to run without proper closures
  test("""
    |let mkGt(v: int) =
    |   let v2 = v*2,
    |       f(x: int) = [x, v2]
    |   in f
    |in
    |   let
    |       list = [-50, 0, 50, 100, 150],
    |       f1 = mkGt(0),
    |       f2 = mkGt(100)
    |   in [f1(-10), f1(10), f1(200), f2(-10), f2(10), f2(200) ]""".stripMargin)(
    _ should orderEvaluateTo("[ [-10, 0], [10, 0], [200, 0], [-10, 200], [10, 200], [200, 200] ]")
  )
}
