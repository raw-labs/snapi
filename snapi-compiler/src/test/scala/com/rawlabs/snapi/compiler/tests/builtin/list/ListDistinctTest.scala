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

package com.rawlabs.snapi.compiler.tests.builtin.list

import com.rawlabs.snapi.frontend.rql2.errors.ItemsNotComparable
import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class ListDistinctTest extends SnapiTestContext {

  test("""let numbers = [5, 2, 4, 2, 2, 4, 5]
    |in List.Distinct(numbers)""".stripMargin)(_ should evaluateTo("[2, 4, 5]"))

  test("""let numbers = []
    |in List.Distinct(numbers)""".stripMargin)(_ should evaluateTo("[]"))

  // Functions aren't comparable
  test("""let l = List.Build((x: int) -> x + 1, (x: int) -> x * 2)
    |in List.Distinct(l)""".stripMargin)(_ should runErrorAs(ItemsNotComparable.message))
}
