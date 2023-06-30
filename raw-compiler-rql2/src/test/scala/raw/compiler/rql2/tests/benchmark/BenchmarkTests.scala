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

package raw.compiler.rql2.tests.benchmark

import raw.compiler.rql2.tests.CompilerTestContext

trait BenchmarkTests extends CompilerTestContext {

  val shouldBeExecuted = false

  val numberOfRuns = 5

  // A.Z. this one I use for the _experimental package test just to see how it works
  //  test("""Math.Power(1,1)""")(it => it should run)
  //  test("""{a: {a: {a: {a: {a: {a: {a: {a: {a: {a: {a: {a: 1, b: 2}, b: 2}, b: 2}, b: 2}, b: 2}}}}}}}}""")(it => it should run)

  test("Json Writer range test") { _ =>
    assume(shouldBeExecuted, "This test is disabled by default")

    fastExecute("""let a = "hello" in a """) // some random query
    fastExecute("""let a = 2 + 2 in a """) // some random query
    fastExecute("""let a = 2/2 in a """) // some random query

    //Warmup
    fastExecute(
      """Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))} , {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}, {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}, {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}, {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}))"""
    )

    val started = System.currentTimeMillis()
    for (i <- 0 to numberOfRuns) {
      fastExecute(
        """Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))} , {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}, {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}, {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}, {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}))"""
      )
    }
    val elapsed = System.currentTimeMillis()

    logger.info("++++++++++ Average execution time time: " + ((elapsed - started) / numberOfRuns))
  }

  test("Range Count test") { _ =>
    assume(shouldBeExecuted, "This test is disabled by default")

    fastExecute("""let a = "hello" in a """) // some random query
    fastExecute("""let a = 2 + 2 in a """) // some random query
    fastExecute("""let a = 2/2 in a """) // some random query

    //Warmup
    fastExecute(s"Collection.Count(Int.Range(0,${Int.MaxValue},step=1))")
    val started = System.currentTimeMillis()
    for (i <- 0 to numberOfRuns) {
      fastExecute(s"Collection.Count(Int.Range(0,${Int.MaxValue},step=1))")
    }
    val elapsed = System.currentTimeMillis()

    logger.info("++++++++++ Average execution time time: " + ((elapsed - started) / numberOfRuns))
  }

  test("Filter Test") { _ =>
    assume(shouldBeExecuted, "This test is disabled by default")

    fastExecute("""let a = "hello" in a """) // some random query
    fastExecute("""let a = 2 + 2 in a """) // some random query
    fastExecute("""let a = 2/2 in a """) // some random query

    //Warmup
    fastExecute(s"Collection.Filter(Int.Range(0,${Int.MaxValue},step=1), x -> x > ${Int.MaxValue - 2})")
    val started = System.currentTimeMillis()
    for (i <- 0 to numberOfRuns) {
      fastExecute(s"Collection.Filter(Int.Range(0,${Int.MaxValue},step=1), x -> x > ${Int.MaxValue - 2})")
    }
    val elapsed = System.currentTimeMillis()

    logger.info("++++++++++ Average execution time time: " + ((elapsed - started) / numberOfRuns))
  }

}
