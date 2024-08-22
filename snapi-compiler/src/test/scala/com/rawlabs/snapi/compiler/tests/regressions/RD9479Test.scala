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

import com.rawlabs.snapi.frontend.utils._
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class RD9479Test extends Rql2TruffleCompilerTestContext {

  private val recordData = tempFile("""[{"a": 1, "b": 10, "c": 100}]""")

  test(snapi"""Collection.First(Json.InferAndRead("$recordData"))""")(_ should evaluateTo("{a: 1, b: 10, c: 100}"))
  test(snapi"""Collection.Last(Json.InferAndRead("$recordData"))""")(_ should evaluateTo("{a: 1, b: 10, c: 100}"))

}
