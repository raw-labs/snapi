/*
 * Copyright 2024 RAW Labs S.A.
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

@TruffleTests class RD10220Test extends Rql2TruffleCompilerTestContext {

  test("""Csv.InferAndParse("stringData")""")(
    _ should (typeAs("collection(record(stringData: undefined))") and evaluateTo("[]"))
  )

  test("""Csv.Parse("stringData", type collection(record(stringData: undefined)), skip=1)""")(
    _ should (typeAs("collection(record(stringData: undefined))") and evaluateTo("[]"))
  )

  test("""Csv.Parse("stringData\nna\nna", type collection(record(stringData: undefined)), nulls=["na"], skip=1)""")(
    _ should (typeAs("collection(record(stringData: undefined))") and evaluateTo(
      "[{stringData: null}, {stringData: null}]"
    ))
  )

  test("""Csv.Parse("stringData\n12\nna", type collection(record(stringData: undefined)), nulls=["na"], skip=1)""")(
    _ should (typeAs("collection(record(stringData: undefined))") and evaluateTo(
      """[{stringData:Error.Build("failed to parse CSV (null: line 2, col 1), unexpected value found, token '12'")}, {stringData:null}]"""
    ))
  )
}
