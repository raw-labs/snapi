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

package raw.compiler.rql2.tests.spec

import raw.compiler.rql2.tests.Rql2CompilerTestContext

trait ProjTest extends Rql2CompilerTestContext {

  test("""let a = {x: 1}
    |in a.x
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""let a = {x: 1, x: 2, y: "Hello"}
    |in a.y
    |""".stripMargin) { it =>
    it should typeAs("string")
    it should evaluateTo(""" "Hello" """)
  }

  test("""let a = Record.Build(x=1, x=2, y="Hello")
    |in a.x
    |""".stripMargin)(it => it should typeErrorAs("record has more than one field with the same name: x"))

  test("""let a = {x: 1, x: 2, y: "Hello"}
    |in a.x
    |""".stripMargin)(it => it should typeErrorAs("record has more than one field with the same name: x"))

}
