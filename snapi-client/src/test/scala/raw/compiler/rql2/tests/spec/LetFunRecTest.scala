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

import raw.compiler.rql2.errors.OutputTypeRequiredForRecursiveFunction
import raw.compiler.rql2.tests.CompilerTestContext

trait LetFunRecTest extends CompilerTestContext {

  test("""let rec fact(v: int): int = if (v > 1) then v * fact(v - 1) else v
    |in fact(4)
    |""".stripMargin)(it => it should evaluateTo("4*3*2*1"))

  test("""let rec fact(v: int) = if (v > 0) then v * fact(v - 1) else v
    |in fact(4)
    |""".stripMargin)(it => it should typeErrorAs(OutputTypeRequiredForRecursiveFunction.message))

  test("""let fact(v: int): int = if (v > 0) then v * fact(v - 1) else v
    |in fact(4)
    |""".stripMargin)(it => it should typeErrorAs("fact is not declared"))

  test("""
    |let f(v: int) =
    |  let rec g(v: int): int = if (v > 0) then v * g(v-1) else 1
    |  in g(v)
    |in f(4)
    |""".stripMargin)(it => it should evaluateTo("24"))

  test("""let rec fibo(maxN: int, n1: int, n2: int): list(int) =
    |    if maxN < n1
    |    then List.Empty(type int)
    |    else List.Union([n1], fibo(maxN, n2, n2 + n1))
    |in fibo(50, 1, 2)""".stripMargin)(_ should orderEvaluateTo("[1,2,3,5,8,13,21,34]"))

  // RD-5691
  test("""let primes(max: int) =
    |     let process(numbers: list(int)) = List.OrderBy(numbers, n -> n, "ASC"),
    |         rec recurse(n: int, found: list(int)): list(int) =
    |         if (n > max) then found
    |         else
    |             let divisors = List.Filter(found, p -> n % p == 0)
    |             in if List.Count(divisors) == 0
    |                then recurse(n + 1, List.Union(found, [n]))
    |                else recurse(n + 1, found)
    |     in process(recurse(2, []))
    |in primes(30)""".stripMargin)(_ should orderEvaluateTo("[2,3,5,7,11,13,17,19,23,29]"))

}
