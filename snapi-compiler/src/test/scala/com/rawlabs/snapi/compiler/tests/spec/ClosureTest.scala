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

package com.rawlabs.snapi.compiler.tests.spec

import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class ClosureTest extends Rql2TruffleCompilerTestContext {

  test("""let functions =
    |   List.Transform([1,2,3],
    |   n -> let f(x: int) = x * n in f),
    |       f0 = List.Get(functions, 0),
    |       f1 = List.Get(functions, 1),
    |       f2 = List.Get(functions, 2)
    |in [f0(22), f1(10), f2(33)]""".stripMargin)(it => it should evaluateTo("""[22, 20, 99]"""))

  test("""let
    |    col = Collection.Build(1, 2),
    |    functions = Collection.Transform(col, (n) -> let f(x: int) = x * n in f),
    |    f0 = Collection.First(functions),
    |    f1 = Collection.Last(functions)
    |in
    |    [f0(22), f1(10)]""".stripMargin)(it => it should evaluateTo("""[22, 20]"""))

  test("""let mkFun(n: int) = let f(x: int) = x * n in f,
    | f0 = mkFun(0),
    | f1 = mkFun(1),
    | f2 = mkFun(2)
    |in f0(22) + f1(10) + f2(33)""".stripMargin)(it => it should evaluateTo("""76"""))

  test("""
    |f(v: int) = v * 10
    |
    |g(s: string) =
    |  let i = String.Length(s)
    |  in f(i)
    |
    |g("tralala")""".stripMargin)(_ should evaluateTo("70"))

  test("""hh(x: int) = x + 1
    |ii(x: int) = hh(x)
    |let f = (x: int) -> x + 1,
    |a = 1,
    |g(x: int) = x + a,
    |h(x: int, y: int = 12) = x + y
    |in List.Transform([f, g, hh, ii], func -> func(1))""".stripMargin)(_ should evaluateTo("[2, 2, 2, 2]"))

}
