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

package com.rawlabs.snapi.compiler.tests.builtin

import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class IntPackageTest extends Rql2TruffleCompilerTestContext {

  test(""" Int.From(1)""")(it => it should evaluateTo("1"))

  test(""" Int.From("1")""")(it => it should evaluateTo("1"))

  test(""" Int.From(1.5)""")(it => it should evaluateTo("1"))

  test(""" Int.From(1.5f)""")(it => it should evaluateTo("1"))

  test(""" Int.From(List.Build(1, 2, 3))""")(it =>
    it should typeErrorAs("expected either number or string but got list(int)")
  )

  test(""" Int.From("abc")""")(it => it should runErrorAs("cannot cast 'abc' to int"))

  // Errors don't propagate through
  test(""" [Int.From("abc") + 12]""")(it => it should evaluateTo("""[Error.Build("cannot cast 'abc' to int")]"""))

  // Nullability is handled
  test(""" [Int.From("abc" + null) + 12]""")(it => it should evaluateTo("""[null]"""))
  test(""" [Int.From(1b + null) + 12b]""")(it => it should evaluateTo("""[null]"""))

  // Range tests
  test("""Long.Range(0,2,step=1)""")(it => it should evaluateTo("""Collection.Build(0L,1L)"""))
}
