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

trait RD5786Test extends CompilerTestContext {

  test("""true == false""") {
    _ should evaluateTo(""" false """)
  }

  test("""true == true""") {
    _ should evaluateTo(""" true """)
  }

  test("""false == false""") {
    _ should evaluateTo(""" true """)
  }

  test("""let
    | a = false,
    | b = false
    |in
    | a == b""".stripMargin) {
    _ should evaluateTo(""" true """)
  }

  test("""let
    | a = false
    |in
    | a == true""".stripMargin) {
    _ should evaluateTo(""" false """)
  }

  test("""let
    | a = false
    |in a == false""".stripMargin) {
    _ should evaluateTo(""" true """)
  }

  test("""let
    | a = false
    |in
    | false == a""".stripMargin) {
    _ should evaluateTo(""" true """)
  }

  test("""let
    | l = [true, false, true]
    |in
    | List.Transform(l, x -> x == false)""".stripMargin) {
    _ should evaluateTo(""" [false, true, false] """)
  }
}
