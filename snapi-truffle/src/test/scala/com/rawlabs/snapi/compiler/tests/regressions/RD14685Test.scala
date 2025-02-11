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

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class RD14685Test extends SnapiTestContext {

  test("Json.Parse(\"12\", type collection(record(a: int, b: string)))")(
    _ should runErrorAs("expected [ but token VALUE_NUMBER_INT found")
  )

  test("Json.Parse(\" \", type collection(record(a: int, b: string)))")(
    // Same error as above. The collection is OK but when rendering to JSON we get a failure.
    _ should runErrorAs("expected [ but token null found")
  )

  ignore("[Json.Parse(\"12\", type collection(record(a: int, b: string)))]")(
    // the collection is consumed when writing JSON, it fails in the middle.
    _ should run // TODO if it runs, check the error message in Error.Build
  )

  ignore("[Json.Parse(\" \", type collection(record(a: int, b: string)))]")(
    // the collection is consumed when writing JSON, it fails in the middle.
    _ should run // TODO if it runs, check the error message in Error.Build
  )

  ignore("[Collection.Count(Json.Parse(\" \", type collection(record(a: int, b: string))))]")(
    // the collection is consumed when running Count, it leads to a failed int in the list.
    _ should run // TODO if it runs, check the error message in Error.Build
  )

  ignore("[Collection.Count(Json.Parse(\"12\", type collection(record(a: int, b: string))))]")(
    // the collection is consumed when running Count, it leads to a failed int in the list.
    _ should run // TODO if it runs, check the error message in Error.Build
  )
}
