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

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.compiler.api.{HoverResponse, Pos, TypeCompletion}
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class RD9409Test extends Rql2TruffleCompilerTestContext {

  test("""let
    |    a = 1,
    |    b = 2,
    |    c = 3
    |in a + b + c""".stripMargin) { it =>
    it should parse
    // now make sure the flexible parser works
    val code = it.q // this is the query source code
    // hover on 'a'
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(2, 5))
    name should be("a")
    tipe should be("int")
  }

  test("""// note the extra comma after c: int
    |let r: record(a: int, b: int, c: int,) = Record.Build(a=1, b=2, c=3)
    |in r.a + r.b + r.c""".stripMargin) { it =>
    it should parse
    // now make sure the flexible parser works
    val code = it.q // this is the query source code
    // hover on 'r'
    val HoverResponse(Some(TypeCompletion(name, tipe))) = hover(code, Pos(3, 4))
    name should be("r")
    tipe should be("record(a: int, b: int, c: int)")
  }

}
