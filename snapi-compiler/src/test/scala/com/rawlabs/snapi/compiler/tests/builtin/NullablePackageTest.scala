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

import com.rawlabs.snapi.compiler.tests.Rql2TestContext

class NullablePackageTest extends Rql2TestContext {

  test("""Nullable.IsNull(null)""")(_ should evaluateTo("true"))
  test("""Nullable.IsNull(1)""")(_ should evaluateTo("false"))
  test("""Nullable.IsNull(Error.Build("argh!"))""")(_ should runErrorAs("argh!"))

  test("""let x: int = null in Nullable.IsNull(x)""")(_ should evaluateTo("true"))
  test("""let x: int = 1 in Nullable.IsNull(x)""")(_ should evaluateTo("false"))
  test("""let x: int = Error.Build("argh!") in Nullable.IsNull(x)""")(_ should runErrorAs("argh!"))
}
