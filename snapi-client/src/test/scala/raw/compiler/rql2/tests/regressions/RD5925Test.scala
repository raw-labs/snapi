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

import raw.compiler.rql2.tests.Rql2CompilerTestContext

trait RD5925Test extends Rql2CompilerTestContext {

  test("""let c = Collection.Build(1, 2, 3, 4)
    |in Collection.Filter(c, s -> null)""".stripMargin) { it =>
    it should typeAs("collection(int)")
    it should evaluateTo("Collection.Build()")
  }

  test("""let c = List.Build(1, 2, 3, 4)
    |in List.Filter(c, s -> null)""".stripMargin) { it =>
    it should typeAs("list(int)")
    it should evaluateTo("List.Build()")
  }

  test("""let c = Collection.Build(1, 2, 3, 4)
    |in Collection.Exists(c, s -> null)""".stripMargin) { it =>
    it should typeAs("bool")
    it should evaluateTo("false")
  }

  test("""let c = List.Build(1, 2, 3, 4)
    |in List.Exists(c, s -> null)""".stripMargin) { it =>
    it should typeAs("bool")
    it should evaluateTo("false")
  }

  test("""let c1 = Collection.Build(1, 2, 3, 4), c2 = Collection.Build(1, 2, 3, 4)
    |in Collection.Join(c1, c2, i -> null)""".stripMargin) { it =>
    it should typeAs("collection(record(_1: int, _2: int))")
    it should evaluateTo("Collection.Build()")
  }

  test("""let c1 = List.Build(1, 2, 3, 4), c2 = List.Build(1, 2, 3, 4)
    |in List.Join(c1, c2, i -> null)""".stripMargin) { it =>
    it should typeAs("list(record(_1: int, _2: int))")
    it should evaluateTo("List.Build()")
  }
}
