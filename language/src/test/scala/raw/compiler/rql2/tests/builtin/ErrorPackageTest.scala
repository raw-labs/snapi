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

package raw.compiler.rql2.tests.builtin

import raw.compiler.rql2.tests.CompilerTestContext

trait ErrorPackageTest extends CompilerTestContext {
  test(""" Error.Build("an error")""")(it => it should runErrorAs("an error"))
  test(""" Error.Build("an error") == Error.Build("an error")""")(it => it should runErrorAs("an error"))
  test(""" Error.Build("an error") == Error.Build("another error")""")(it => it should runErrorAs("an error"))
  test("""List.Distinct([
    |   Error.Build("an error"),
    |   Error.Build("another error"),
    |   Error.Build("an error")
    |])""".stripMargin)(it => it should evaluateTo("""[Error.Build("an error"), Error.Build("another error")]"""))

  test("""List.OrderBy([
    |   1, 2,
    |   Error.Build("a X error"),
    |   Error.Build("a Y error"),
    |   Error.Build("a X error")
    |], x -> x, "ASC")""".stripMargin)(it =>
    it should orderEvaluateTo(
      """[Error.Build("a X error"), Error.Build("a X error"), Error.Build("a Y error"), 1, 2]"""
    )
  )

  test("""List.OrderBy([
    |   1, 2,
    |   Error.Build("a X error"),
    |   Error.Build("a Y error"),
    |   Error.Build("a X error")
    |], x -> x, "DESC")""".stripMargin)(it =>
    it should orderEvaluateTo(
      """[2, 1, Error.Build("a Y error"), Error.Build("a X error"), Error.Build("a X error")]"""
    )
  )

  test("""Error.Get("an error")""")(it => it should runErrorAs("not a failure"))

}
