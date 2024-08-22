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

import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.compiler.tests.Rql2TestContext
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext

class RD5238Test extends Rql2TestContext with LocalLocationsTestContext {

  test(snapi"""
    |let region1 = Csv.InferAndRead("$tpchRegionCsvLocal"),
    |    region2 = Csv.InferAndRead("$tpchRegionCsvLocal")
    |in [Collection.Count(region1), Collection.Count(region2)]""".stripMargin)(_ should orderEvaluateTo("[6L, 6L]"))

  test(snapi"""
    |let region = Csv.InferAndRead("$tpchRegionCsvLocal")
    |in [Collection.Count(region), Collection.Count(region)]""".stripMargin)(
    _ should orderEvaluateTo("[6L, 6L]")
  )

}
