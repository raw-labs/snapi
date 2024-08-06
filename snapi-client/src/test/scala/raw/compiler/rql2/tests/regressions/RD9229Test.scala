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

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class RD9229Test extends Rql2TruffleCompilerTestContext {

  test(s"""[{a: "binary", b: [1,2,3,4]}]""") { it =>
    option("output-format", "binary")
    it should runErrorAs("unsupported type")
  }

  test(s"""[{a: "text", b: [1,2,3,4]}]""") { it =>
    option("output-format", "text")
    it should runErrorAs("unsupported type")
  }

  test(s"""[{a: "csv", b: [1,2,3,4]}]""") { it =>
    option("output-format", "csv")
    it should runErrorAs("unsupported type")
  }

  test(s"""[{a: (x: int) -> x + 1, b: [1,2,3,4]}]""") { it =>
    option("output-format", "json")
    it should runErrorAs("unsupported type")
  }

  test("\"tralala\"") { it =>
    option("output-format", "text")
    it should run
  }

  test("\"tralala2\"") { it =>
    option("output-format", "binary")
    it should runErrorAs("unsupported type")
  }
}
