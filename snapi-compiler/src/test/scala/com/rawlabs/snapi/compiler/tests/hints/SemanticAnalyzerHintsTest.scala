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

package com.rawlabs.snapi.compiler.tests.hints

import com.rawlabs.snapi.compiler.tests.Rql2TestContext

class SemanticAnalyzerHintsTest extends Rql2TestContext {

  test("""Rekord.Build(a=1)""")(it => it should typeErrorAs("did you mean Record.Build?"))

  test("""Record.Vuild(a=1)""")(it => it should typeErrorAs("did you mean Record.Build?"))

  test("""Rekord.Vuild(a=1)""")(it => it should typeErrorAs("did you mean Record.Build?"))

  // If more than 2 characters wrong we don't suggest any fix.
  test("""Rekord.Vuixt(a=1)""")(it => it should typeErrorAs("Rekord is not declared"))

  test("""
    |let thisIsAVar = 1,
    |    thisIsAnotherVar = 2
    |in thisIsAVar + thisIsAnotherVal
    |""".stripMargin)(it => it should typeErrorAs("did you mean thisIsAnotherVar?"))

  test("""let r = {aaa: 1, bbb: 2}
    |in r.aab""".stripMargin)(it => it should typeErrorAs("did you mean aaa or bbb"))

  test("""let r = {aaaa: 1, bbbb: 2}
    |in r.aaab""".stripMargin)(it => it should typeErrorAs("did you mean aaaa"))

  // Test collection projection sugar.
  test("""let r = Collection.Build({aaaa: 1, bbbb: 2})
    |in r.aaab""".stripMargin)(it => it should typeErrorAs("did you mean aaaa"))

  // Test list projection sugar.
  test("""let r = [{aaaa: 1, bbbb: 2}]
    |in r.aaab""".stripMargin)(it => it should typeErrorAs("did you mean aaaa"))

  test("""let a = Collection.Build(1,2,3)
    |in List.Filter(a, x -> x > 2)""".stripMargin)(it => it should typeErrorAs("""did you mean Collection.Filter?"""))

  test("""let a = [1,2,3]
    |in Collection.Filter(a, x -> x > 2)""".stripMargin)(it => it should typeErrorAs("""did you mean List.Filter?"""))

  test("""Json.Read("http//example.com")""")(it =>
    it should typeErrorAs("missing mandatory arguments", "* type: The type of the data in the JSON.")
  )

  test("""String.Replace("a")""")(it =>
    it should typeErrorAs(
      "missing mandatory arguments",
      "* pattern: The substring to match.",
      "* newSubString: The new substring to replace matches with."
    )
  )

  test("""String.IsNull(null)""")(it => it should typeErrorAs("did you mean Nullable.IsNull?"))

  test("""Text.Trim("aa")""")(it => it should typeErrorAs("did you mean String.Trim?"))

  test("""Text.From("aa")""")(it => it shouldNot typeErrorAs("did you mean"))

}
