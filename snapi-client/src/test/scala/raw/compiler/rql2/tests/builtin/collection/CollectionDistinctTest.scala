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

package raw.compiler.rql2.tests.builtin.collection

import raw.compiler.rql2.errors.ItemsNotComparable
import raw.compiler.rql2.tests.Rql2CompilerTestContext

trait CollectionDistinctTest extends Rql2CompilerTestContext {

  test("""let numbers = Collection.From([5, 2, 4, 2, 2, 4, 5])
    |in Collection.Distinct(numbers)""".stripMargin)(_ should evaluateTo("[2, 4, 5]"))

  test("""let numbers = Collection.From([])
    |in Collection.Distinct(numbers)""".stripMargin)(_ should evaluateTo("[]"))

  // Functions aren't comparable
  test("""let l = Collection.Build((x: int) -> x + 1, (x: int) -> x * 2)
    |in Collection.Distinct(l)""".stripMargin)(_ should runErrorAs(ItemsNotComparable.message))
}
