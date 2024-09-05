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

package com.rawlabs.snapi.compiler.tests.lineage

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class LineageTest extends SnapiTestContext {

  test(
    s"""let data = Json.InferAndRead("https://jsonplaceholder.typicode.com/users"), res = Collection.Transform(data, x -> x.id) in res"""
  )(it => it should run)
}
