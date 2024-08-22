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

package com.rawlabs.snapi.compiler.tests.spec

import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class ProjTest extends Rql2TruffleCompilerTestContext {

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
