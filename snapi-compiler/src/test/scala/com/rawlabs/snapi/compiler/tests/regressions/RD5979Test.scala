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

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class RD5979Test extends SnapiTestContext {

  test("""[{a: 12, b: 14}, {c: 23, d: 54}]""")(_ should runErrorAs("expected compatible with"))
  test("""[{a: 12, b: 14}, {a: 23, d: 54}]""")(_ should runErrorAs("expected compatible with"))
  test("""[{a: 12, b: 14}, {a: 23, b: 54, c: 12}]""")(_ should runErrorAs("expected compatible with"))
  test("""[{a: 12, b: 14}, {c: 14, a: 23, b: 54}]""")(_ should runErrorAs("expected compatible with"))

}
