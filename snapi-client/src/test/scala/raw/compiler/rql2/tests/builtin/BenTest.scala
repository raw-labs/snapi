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

trait BenTest extends CompilerTestContext {

  ignore(""" Byte.From(1)""")(it => it should evaluateTo("1b"))
  ignore(""" Math.Sin(1)""")(it => it should evaluateTo("Math.Sin(1)"))

  ignore("let x: any = 6.8 in Math.Sin(x)")(it => it should evaluateTo("Math.Sin(6.8)"))
  test("let x: record(number: double, size: int) = {number: 6.8, size: 12} in x.number")(it => it should evaluateTo("6.8"))
  test("let x: any = {number: 6.8, size: 12} in x.number")(it => it should evaluateTo("6.8"))
  test("let x: any = {number: 6.8, size: 12} in Math.Sin(x.number)")(it => it should evaluateTo("Math.Sin(6.8)"))

  ignore(
    """let d = Json.Read("file:/tmp/string.json", type any) in Type.CastAny(type int, Type.CastAny(type record(age: any), d).age) > 90"""
  )(_ should evaluateTo("false"))
  ignore("""let d = Json.Read("file:/tmp/string.json", type any) in d.alive""")(_ should evaluateTo("false"))
  ignore("""let d = Json.Read("file:/tmp/string.json", type any) in d.alive == true""")(_ should evaluateTo("false"))
  ignore("""let d = Json.Read("file:/tmp/string.json", type any) in 12 + d.age""")(_ should evaluateTo("false"))
  ignore("""let d = Json.Read("file:/tmp/string.json", type any) in true == d.alive""")(_ should evaluateTo("false"))
  test("""let d = Json.Read("file:/tmp/string.json", type any) in Math.Sin(d.size)""")(
    _ should evaluateTo("Math.Sin(6.8)")
  )
  test("""let d = Json.Read("file:/tmp/string.json", type any) in Math.Sin(d.age)""")(_ should evaluateTo("false"))
  test("""let d = Json.Read("file:/tmp/string.json", type any) in [Math.Sin(d.age), Math.Cos(d.size)]""")(
    _ should evaluateTo("false")
  )
}
