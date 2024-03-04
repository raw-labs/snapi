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

  test(""" Byte.From(1)""")(it => it should evaluateTo("1b"))
  test(""" Math.Sin(1)""")(it => it should evaluateTo("Math.Sin(1)"))
  test("""let r = Json.Parse("{\"a\": 12, \"b\": 14}", type any) in 12 < r.a""")(_ should evaluateTo("false"))
  test("""let r = Json.Parse("{\"a\": 12, \"b\": 14}", type any) in 12 < r.b""")(_ should evaluateTo("true"))
  test("""let r = Json.Parse("{\"a\": 12, \"b\": 3.14}", type any) in Math.Sin(r.b)""")(
    _ should evaluateTo("Math.Sin(3.14)")
  )
  test("""let r = Json.Parse("{\"a\": 12, \"b\": 3.14}", type any) in Math.Sin(r.a)""")(
    _ should runErrorAs("Type cast error")
  )
  test(
    """let r = Json.Parse("{\"a\": 12, \"b\": 3.14}", type any) in Math.Sin(Type.CastAny(type double, Type.CastAny(type record(a: any), r).a))"""
  )(
    _ should evaluateTo("Math.Sin(12)")
  )
  test("""let r = Json.Parse("[1,2,3.14, {\"a\": 12, \"b\": 3.14}]", type collection(any)) in Collection.Count(r)""")(
    _ should evaluateTo("4")
  )
  test("""let r = Json.Parse("[1,2,3.14, {\"a\": 12, \"b\": 3.14}]", type any) in Collection.Count(r)""")(
    _ should evaluateTo("4")
  )

}
