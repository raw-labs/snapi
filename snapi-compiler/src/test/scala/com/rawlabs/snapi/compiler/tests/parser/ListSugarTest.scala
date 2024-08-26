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

package com.rawlabs.snapi.compiler.tests.parser

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class ListSugarTest extends SnapiTestContext {

  test("""[1,2,3]""") { it =>
    it should typeAs("list(int)")
    it should evaluateTo("""List.Build(1,2,3)""")
  }

  test("""[]""") { it =>
    it should typeAs("list(undefined)")
    it should evaluateTo("""List.Build()""")
  }

}
