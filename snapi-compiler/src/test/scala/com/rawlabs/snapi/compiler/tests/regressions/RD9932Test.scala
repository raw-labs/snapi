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

import com.rawlabs.snapi.compiler.tests.Rql2TestContext

class RD9932Test extends Rql2TestContext {

  val ttt = "\"\"\""
  test(s"""Json.Parse(
    |  $ttt [
    |  {"a": 1, "b": "#1", "c": 1.1},
    |  {"a": 2, "b": "#2", "c": 2.2},
    |  #############################$ttt, 
    |  type collection(record(a: int, b: string, c: double))
    |)""".stripMargin)(it =>
    it should runErrorAs(
      "failed to read JSON (line 4 column 4): Unexpected character ('#' (code 35)): expected a valid value"
    )
  )
}
