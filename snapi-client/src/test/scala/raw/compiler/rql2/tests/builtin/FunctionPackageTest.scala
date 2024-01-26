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

package raw.compiler.rql2.tests.builtin

import raw.utils.TestData
import raw.compiler.rql2.tests.CompilerTestContext
import raw.sources.bytestream.api.HttpLocationsTestContext

trait FunctionPackageTest extends CompilerTestContext with HttpLocationsTestContext {

  test(
    """test() = 1 + 1
      |Function.InvokeAfter(() -> test(), 2)""".stripMargin)(_ should evaluateTo("2"))

  test("invoke function after 5 seconds") { _ =>
    val start = System.nanoTime()
    val delay = 5000
    val query = TestData(s"""Function.InvokeAfter(()-> 1+1, $delay)""".stripMargin)
    query should evaluateTo("2")

    val end = System.nanoTime()
    assert(end - start > delay * 1000, "function run in less time than the invocation time")
  }

  test("""let
    |   f() = 1+1
    |in
    |   Function.InvokeAfter(f, 10)
    |""".stripMargin)(_ should evaluateTo("2"))

  test("""let
    |   data = Function.InvokeAfter(
    |       () -> Json.InferAndRead("http://test-data.raw-labs.com/public/authors.json"),
    |       10
    |   )
    |in
    |   Collection.Filter(data,  x -> x.year == 1972) """.stripMargin)(
    _ should evaluateTo(""" [
      | {name: "Stricker, D.A.", title: "PhD", year:  1972},
      | {name: "Ishibashi, K.", title: "engineer", year:  1972},
      | {name: "Tickle, R.", title: "professor", year:  1972}
      |]  """.stripMargin)
  )

  test("""let
    |   f() = Json.InferAndRead("http://test-data.raw-labs.com/public/authors.json"),
    |   data = Function.InvokeAfter(f, 10)
    |in
    |   Collection.Filter(data, x -> x.year == 1972) """.stripMargin)(
    _ should evaluateTo(""" [
      | {name: "Stricker, D.A.", title: "PhD", year:  1972},
      | {name: "Ishibashi, K.", title: "engineer", year:  1972},
      | {name: "Tickle, R.", title: "professor", year:  1972}
      |]  """.stripMargin)
  )

  test("""let
    |   a = 1
    |in
    |   Function.InvokeAfter(() -> a + 1 , 10)
    |""".stripMargin)(
    _ should evaluateTo(" 2 ")
  )

  test("""let
    |   f(a: int) = a + 1
    |in
    |   Function.InvokeAfter(() -> f(1) + 1 , 10)
    |""".stripMargin)(
    _ should evaluateTo(" 3 ")
  )

  // Checking the propagation of nulls and errors.
  test("""let
    |   functions = [()-> 1+1 , null, Error.Build("something went wrong")]
    |in
    |   List.Transform(functions, x -> Function.InvokeAfter(x, 10))
    |""".stripMargin)(
    _ should orderEvaluateTo("""[2, null, Error.Build("something went wrong")]""")
  )

  test("""let
    |   f(a: int) = a +1
    |in
    |   Function.InvokeAfter(f , 10)
    |""".stripMargin)(
    _ should typeErrorAs("expected () -> anything but got (int) -> int")
  )

  test("""Function.InvokeAfter((a: int) -> a +1 , 100)""".stripMargin)(
    _ should typeErrorAs("expected () -> anything but got (int) -> int")
  )

}
