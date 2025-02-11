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

package com.rawlabs.snapi.compiler.tests.benchmark

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class StressTests extends SnapiTestContext {

  val shouldBeExecuted = false

  val numberOfRuns = 10000

  test("Testing code caches and memory leaks") { _ =>
    assume(shouldBeExecuted, "This test is disabled by default")

    doExecute("""let a = "hello" in a """) // some random query
    doExecute("""let a = 2 + 2 in a """) // some random query
    doExecute("""let a = 2/2 in a """) // some random query

    for (i <- 0 to numberOfRuns) {
      doExecute(
        s"Collection.Filter(Int.Range(0,10,step=1), x -> x > 5)"
      )
      doExecute(
        s"Collection.Filter(Long.Range(0,10,step=2), x -> x == 5)"
      )
      logger.info("\n++++++++++ iteration: " + i)
    }

//    -XX: ReservedCodeCacheSize=15000k
//    -XX :+ UseCodeCacheFlushing
//    -XX :+ SegmentedCodeCache

  }
}
