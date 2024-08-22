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

class RD5644Test extends Rql2TruffleCompilerTestContext {

  // Basic reference behavior. The function parameter isn't type and inherits the list item type. Everything matches.
  test("""
    |let numbers = [0,1,2,3,4,5,6,7,8,9]
    |in List.Transform(numbers, x -> x + 10)""".stripMargin)(_ should evaluateTo("[10,11,12,13,14,15,16,17,18,19]"))

  // This time the parameter is typed, and it is internally both tryable/nullable. That doesn't match exactly.
  test("""
    |let numbers = [0,1,2,3,4,5,6,7,8,9]
    |in List.Transform(numbers, (x: int) -> x + 10)""".stripMargin)(
    _ should evaluateTo("[10,11,12,13,14,15,16,17,18,19]")
  )

  // Same with a function declared separately.
  test("""
    |let numbers = [0,1,2,3,4,5,6,7,8,9],
    |  f(x: int) = x + 10
    |in List.Transform(numbers, f)""".stripMargin)(_ should evaluateTo("[10,11,12,13,14,15,16,17,18,19]"))

  // If the parameter is typed as float, the code doesn't type. A function taking 'float' can in principle take the
  // place of a function taking 'int', but we don't accept it in that context.
  test("""
    |let numbers = [0,1,2,3,4,5,6,7,8,9]
    |in List.Transform(numbers, (x: float) -> x + 10)""".stripMargin)(
    _ should runErrorAs("expected (int) -> anything but got (float) -> float")
  )

  // Same operation with a function declared separately.
  test("""
    |let numbers = [0,1,2,3,4,5,6,7,8,9],
    |  f(x: float) = x * 10
    |in List.Transform(numbers, f)""".stripMargin)(
    _ should runErrorAs("expected (int) -> anything but got (float) -> float")
  )

  // The reverse case also doesn't typecheck. Here it's more normal since anyway, a function on 'int' cannot just put where
  // a function taking 'float' is expected.
  test("""
    |let numbers = [0.14f,1.14f,2.14f,3.14f,4.14f,5.14f,6.14f,7.14f,8.14f,9.14f],
    |  f(x: int) = x * 10
    |in List.Transform(numbers, f)""".stripMargin)(
    _ should runErrorAs("expected (float) -> anything but got (int) -> int")
  )

  // Functions that have 'the same type' can be merged in a list. This is the same rule as when they're passed as a parameter
  // to a user function.
  test("""let fs = [(x: int) -> x + 2, (x: int) -> x * 10]
    |in List.Transform(fs, f -> f(10))""".stripMargin)(_ should evaluateTo("[12, 100]"))

  // Functions that don't have 'the same type' can be merged in a list if their return type merge.
  test("""let fs = [(x: int) -> x + 2, (x: int) -> x * 10.4]
    |in List.Transform(fs, f -> f(10))""".stripMargin)(
    _ should typeAs("list(double)")
  )

  // Typechecking is also strict with user-defined functions taking a function as parameter. In that case, a user
  // has declared the expected type, and typechecking accepts an argument only if it matches.
  test("""
    |let myTransform(l: list(int), f: int -> bool) = List.Transform(l, f)
    |in myTransform([1,2,3,4], (x: int) -> x % 2 == 0)
    |
    |""".stripMargin)(_ should evaluateTo("[false, true, false, true]"))

  // Checking it fails normally when the actual type is totally wrong.
  test("""
    |let myTransform(l: list(int), f: int -> bool) = List.Transform(l, f)
    |in myTransform([1,2,3,4], (x: int) -> x + 10)
    |
    |""".stripMargin)(_ should runErrorAs("expected (int) -> bool but got (int) -> int"))

  // This is accepted although the output type doesn't match, because output types can be merged.
  test("""
    |let myTransform(l: list(int), f: (int) -> float) = List.Transform(l, f)
    |in myTransform([1,2,3,4], (x: int) -> x + 10)
    |
    |""".stripMargin)(_ should typeAs("list(float)"))

  // This doesn't type because the argument type doesn't match.
  test("""
    |let myTransform(l: list(int), f: int -> float) = List.Transform(l, f)
    |in myTransform([1,2,3,4], (x: float) -> x + 10)
    |
    |""".stripMargin)(_ should runErrorAs("expected (int) -> float but got (float) -> float"))

  // Functions that don't have 'the same type' cannot be merged in a list. This is the same rule as when they're passed as a parameter
  // to a user function.
  test("""let fs = [(x: int) -> x + 2, (x: float) -> x * 10]
    |in List.Transform(fs, f -> f(10))""".stripMargin)(
    _ should runErrorAs("expected compatible with (int) -> int but got (float) -> float")
  )

  // Same here.
  test("""let fs = [(x: int) -> x + 2, (x: float) -> 10]
    |in List.Transform(fs, f -> f(10))""".stripMargin)(
    _ should runErrorAs("expected compatible with (int) -> int but got (float) -> int")
  )

  // Same here.
  test("""let fs = [(x: int) -> x + 2, null, Error.Build("bug")]
    |in List.Transform(fs, f -> f(10))""".stripMargin)(
    _ should evaluateTo("""[12, null, Error.Build("bug")]""")
  )

}
