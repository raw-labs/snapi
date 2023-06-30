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

package raw.compiler.rql2.tests.regressions

import raw.compiler.rql2.tests.CompilerTestContext

trait RD5685Test extends CompilerTestContext {

  test("""let itemType = type int,
    |    listType = type list(itemType),
    |    f(l: listType) = List.Count(l)
    |in f([1,2,3,4,5])""".stripMargin)(_ should evaluateTo("5"))

  test("""let itemType = type int,
    |    listType = type list(itemType),
    |    f(l: listType): itemType = List.First(l)
    |in f([1,2,3,4,5])""".stripMargin)(_ should evaluateTo("1"))

  test("""let itemType = type int,
    |    listType = type list(itemType),
    |    f(l: listType): itemType = List.First(l),
    |    myList: listType = [1,2,3,4,5]
    |in f(myList)""".stripMargin)(_ should evaluateTo("1"))

}
