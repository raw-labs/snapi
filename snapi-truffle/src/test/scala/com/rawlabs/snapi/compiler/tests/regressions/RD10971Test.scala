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

class RD10971Test extends SnapiTestContext {

  test("Json.Parse(\" \", type int)")(
    // Jackson returns an internal error.
    _ should runErrorAs("""Internal error: _parseNumericValue called when parser instance closed
      | at [Source: (String)" "; line: 1, column: 2]""".stripMargin)
  )

  test("[Json.Parse(\" \", type int)]")(
    _ should run // it doesn't fail because it's in a list.
  )

  test("Json.Parse(\" \", type record(a: int, b: string))")(
    // unexpected token.
    _ should runErrorAs("expected { but token null found")
  )

  test("[Json.Parse(\" \", type record(a: int, b: string))]")(
    // Same as above, the tryable should be left in the list. The query doesn't fail.
    _ should run // it doesn't fail because it's in a list.
  )

  test("Json.Parse(\" \", type list(record(a: int, b: string))) // RD-10971")(
    // unexpected token.
    _ should runErrorAs("expected [ but token null found")
  )

  test("[Json.Parse(\" \", type list(record(a: int, b: string)))] // RD-10971")(
    _ should run // it doesn't fail because it's in a list.
  )

}
