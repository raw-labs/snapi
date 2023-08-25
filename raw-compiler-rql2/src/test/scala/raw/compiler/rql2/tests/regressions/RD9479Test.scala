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

import raw.compiler.SnapiInterpolator
import raw.compiler.rql2.tests.CompilerTestContext

trait RD9479Test extends CompilerTestContext {

  private val recordData = tempFile("""[{"a": 1, "b": 10, "c": 100}]""")

  test(snapi"""Collection.First(Json.InferAndRead("$recordData"))""")(_ should evaluateTo("{a: 1, b: 10, c: 100}"))
  test(snapi"""Collection.Last(Json.InferAndRead("$recordData"))""")(_ should evaluateTo("{a: 1, b: 10, c: 100}"))

}
