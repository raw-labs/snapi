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

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class LetTypeTest extends SnapiTestContext {

  test("""
    |let t = type int,
    |    x: t = 1
    |in x
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""
    |let t = type int,
    |    t = type float
    |in 1
    |""".stripMargin)(it => it should typeErrorAs("t is declared more than once"))

  test("""
    |let x: t = 1
    |in x
    |""".stripMargin)(it => it should typeErrorAs("t is not declared"))

  test("""
    |let t = type int,
    |    x = let t = type string, x: t = "hello" in x
    |in x
    |""".stripMargin) { it =>
    it should typeAs("string")
    it should evaluateTo(""""hello"""")
  }

  test("""
    |let t: type int = type int,
    |    x: t = 1
    |in x
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""
    |let t: type float = type int,
    |    x: t = 1
    |in x
    |""".stripMargin)(it => it should typeAs("float"))

}
