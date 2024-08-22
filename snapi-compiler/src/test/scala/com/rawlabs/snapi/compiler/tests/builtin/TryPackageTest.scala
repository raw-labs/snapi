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

class TryPackageTest extends Rql2TestContext {

  test("""Try.IsError("hi")""")(_ should evaluateTo("false"))
  test("""Try.IsError(1)""")(_ should evaluateTo("false"))
  test("""Try.IsError(null)""")(_ should evaluateTo("false"))
  test("""Try.IsError(Error.Build("argh"))""")(_ should evaluateTo("true"))

  test("""let x: string = "hi" in Try.IsError(x)""")(_ should evaluateTo("false"))
  test("""let x: int = 1 in Try.IsError(x)""")(_ should evaluateTo("false"))
  test("""let x: int = null in Try.IsError(x)""")(_ should evaluateTo("false"))
  test("""let x: int = Error.Build("argh") in Try.IsError(x)""")(_ should evaluateTo("true"))

  test("""Try.IsSuccess("hi")""")(_ should evaluateTo("true"))
  test("""Try.IsSuccess(1)""")(_ should evaluateTo("true"))
  test("""Try.IsSuccess(null)""")(_ should evaluateTo("true"))
  test("""Try.IsSuccess(Error.Build("argh"))""")(_ should evaluateTo("false"))

  test("""let x: string = "hi" in Try.IsSuccess(x)""")(_ should evaluateTo("true"))
  test("""let x: int = 1 in Try.IsSuccess(x)""")(_ should evaluateTo("true"))
  test("""let x: int = null in Try.IsSuccess(x)""")(_ should evaluateTo("true"))
  test("""let x: int = Error.Build("argh") in Try.IsSuccess(x)""")(_ should evaluateTo("false"))
}
