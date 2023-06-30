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

trait NullableTryablePackageTest extends CompilerTestContext {
  test("""1 / null""")(_ should evaluateTo("null"))
  test(""" 1.0 / (if true then null else 2)""")(_ should evaluateTo("null"))
  test(""" 1.0 / (if false then null else 2)""")(_ should evaluateTo("0.5"))

  test("""// Case #1
    |let x: int = if Math.Pi() > 3 then 2 else null
    |in Nullable.Transform(x, v -> v * 10)""".stripMargin)(_ should evaluateTo("20"))

  test("""// Case #1
    |let x: int = if Math.Pi() < 3 then 2 else null
    |in Nullable.Transform(x, v -> v * 10)""".stripMargin)(_ should evaluateTo("null"))

  test("""// Case #1
    |let x: int = Error.Build("argh!")
    |in Nullable.Transform(x, v -> v * 10)""".stripMargin)(_ should runErrorAs("argh!"))

  test("""// Case #5
    |let x: int = if Math.Pi() > 3 then 2 else null
    |in Nullable.IsNull(x)""".stripMargin)(_ should evaluateTo("false"))

  test("""// Case #5
    |let x: int = if Math.Pi() < 3 then 2 else null
    |in Nullable.IsNull(x)""".stripMargin)(_ should evaluateTo("true"))

  test("""// Case #5
    |let x: int = Error.Build("argh!")
    |in Nullable.IsNull(x)""".stripMargin)(_ should runErrorAs("argh!"))

  test("""// Case #2 + Case #5
    |let x: int = if Math.Pi() > 3 then 2 else null
    |in Nullable.IsNull(10 / x)""".stripMargin)(_ should evaluateTo("false"))

  test("""// Case #2 + Case #5
    |let x: int = if Math.Pi() < 3 then 2 else null
    |in Nullable.IsNull(10 / x)""".stripMargin)(_ should evaluateTo("true"))

  test("""// Case #2 + Case #5
    |let x: int = if Math.Pi() > 3 then 0 else null
    |in Nullable.IsNull(10 / x)""".stripMargin)(_ should runErrorAs("/ by zero"))

  test("""// Case #4
    |let x = if Math.Pi() > 3 then 2 else null
    |in 10 / x""".stripMargin)(_ should evaluateTo("5"))

  test("""// Case #4
    |let x = if Math.Pi() < 3 then 2 else null
    |in 10 / x""".stripMargin)(_ should evaluateTo("null"))

  test("""// Case #4
    |let x = if Math.Pi() > 3 then 0 else null
    |in 10 / x""".stripMargin)(_ should runErrorAs("/ by zero"))

  test("""// Case #4 + Case #5
    |let x = if Math.Pi() > 3 then 2 else null
    |in Nullable.IsNull(10 / x)""".stripMargin)(_ should evaluateTo("false"))

  test("""// Case #4 + Case #5
    |let x = if Math.Pi() < 3 then 2 else null
    |in Nullable.IsNull(10 / x)""".stripMargin)(_ should evaluateTo("true"))

  test("""// Case #4 + Case #5
    |let x = if Math.Pi() > 3 then 0 else null
    |in Nullable.IsNull(10 / x)""".stripMargin)(_ should runErrorAs("/ by zero"))

  test("""// Case #6
    |let x = if Math.Pi() > 3 then 3 else null
    |in x + 10""".stripMargin)(_ should evaluateTo("13"))

  test("""// Case #6
    |let x = if Math.Pi() < 3 then 3 else null
    |in x + 10""".stripMargin)(_ should evaluateTo("null"))

  test("""// Case #6
    |let x = if Math.Pi() > 3 then Math.Pi() else null
    |in Math.Cos(x)""".stripMargin)(_ should evaluateTo("-1.0"))

  test("""// Case #6
    |let x = if Math.Pi() < 3 then Math.Pi() else null
    |in Math.Cos(x)""".stripMargin)(_ should evaluateTo("null"))

}
