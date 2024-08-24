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

package com.rawlabs.snapi.compiler.tests.parser

import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class OperatorPrecedenceTest extends SnapiTestContext {

  test("""1+2*3""") { it =>
    it should evaluateTo("(1+(2*3))")
    it should evaluateTo("7")
  }

  test("""false or true and true""") { it =>
    it should astParseAs(
      SnapiProgram(BinaryExp(Or(), BoolConst(false), BinaryExp(And(), BoolConst(true), BoolConst(true))))
    )
  }

  test("""false and true or true""") { it =>
    it should astParseAs(
      SnapiProgram(BinaryExp(Or(), BinaryExp(And(), BoolConst(false), BoolConst(true)), BoolConst(true)))
    )
  }

  test("""true or false and true or true""") { it =>
    it should astParseAs(
      SnapiProgram(
        BinaryExp(
          Or(),
          BinaryExp(Or(), BoolConst(true), BinaryExp(And(), BoolConst(false), BoolConst(true))),
          BoolConst(true)
        )
      )
    )
  }

  test("X.a.b.c")(it => it should astParseAs(SnapiProgram(Proj(Proj(Proj(IdnExp(IdnUse("X")), "a"), "b"), "c"))))

  test("x and y and z")(it =>
    it should astParseAs(
      SnapiProgram(BinaryExp(And(), BinaryExp(And(), IdnExp(IdnUse("x")), IdnExp(IdnUse("y"))), IdnExp(IdnUse("z"))))
    )
  )

  test("f(1)(2)")(it =>
    it should astParseAs(
      SnapiProgram(
        FunApp(
          FunApp(IdnExp(IdnUse("f")), Vector(FunAppArg(IntConst("1"), None))),
          Vector(FunAppArg(IntConst("2"), None))
        )
      )
    )
  )

  test("not not true")(it => it should astParseAs(SnapiProgram(UnaryExp(Not(), UnaryExp(Not(), BoolConst(true))))))

  test("100 / 10 * 10") { it =>
    it should evaluateTo("(100 / 10) * 10")
    it should astParseAs(
      SnapiProgram(BinaryExp(Mult(), BinaryExp(Div(), IntConst("100"), IntConst("10")), IntConst("10")))
    )
  }

  test("""Record.Build(a=1).a""") { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""Record.Build(a=Record.Build(b = 2)).a.b""") { it =>
    it should typeAs("int")
    it should evaluateTo("2")
  }

}
