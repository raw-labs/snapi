package raw.compiler.rql2.tests.benchmark

import raw.compiler.rql2.tests.CompilerTestContext

trait StressTests extends CompilerTestContext {

  val shouldBeExecuted = false

  val numberOfRuns = 10000

  test("Testing code caches and memory leaks") { _ =>
    assume(shouldBeExecuted, "This test is disabled by default")

    fastExecute("""let a = "hello" in a """) // some random query
    fastExecute("""let a = 2 + 2 in a """) // some random query
    fastExecute("""let a = 2/2 in a """) // some random query

    for (i <- 0 to numberOfRuns) {
      fastExecute(
        s"Collection.Filter(Int.Range(0,10,step=1), x -> x > 5)"
      )
      fastExecute(
        s"Collection.Filter(Long.Range(0,10,step=2), x -> x == 5)"
      )
      logger.info("\n++++++++++ iteration: " + i)
    }

//    -XX: ReservedCodeCacheSize=15000k
//    -XX :+ UseCodeCacheFlushing
//    -XX :+ SegmentedCodeCache

  }
}
