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

package raw.compiler.rql2.tests.lsp

import raw.compiler._
import raw.compiler.api._
import raw.compiler.rql2.tests.CompilerTestContext

trait LspBrokenCodeTest extends CompilerTestContext {

  test("broken code hover test") { _ =>
    val code = """let
      |a: float = 5
      |""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe)), errors) = hover(code, Pos(2, 1))
    assertResult("a")(name)
    assertResult("float")(tipe)
    logger.debug(s"Errors: $errors")

  }

  test("very broken code hover test") { _ =>
    val code = """
      |let x = ,
      |    y = Record.Build(a=1),
      |    z = if (true) ,
      |    w = y.a
      |""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe)), errors) = hover(code, Pos(5, 9))
    assertResult("y")(name)
    assertResult("record(a: int)")(tipe)
    logger.debug(s"Errors: $errors")
  }

  test("very broken code hover test 2") { _ =>
    val code = """
      |let x = ,
      |    f(v: int) = (v + ,
      |    y = Record.Build(a=1), ,
      |    z = if (true) ,
      |    w = y.a
      |""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe)), errors) = hover(code, Pos(6, 9))
    assertResult("y")(name)
    assertResult("record(a: int)")(tipe)
    logger.debug(s"Errors: $errors")
  }

  test("very broken code hover test 3") { _ =>
    val code = """
      |let x = ,
      |    f(v: int) = (v + ,
      |    y = Record.Build(a=1), ,
      |    z = if (true) ,
      |    w = y.
      |""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe)), errors) = hover(code, Pos(6, 9))
    assertResult("y")(name)
    assertResult("record(a: int)")(tipe)
    logger.debug(s"Errors: $errors")
  }

  test("very broken code hover test 4") { _ =>
    val code = """
      |let x = ,
      |    f(v: int) = (v + ,
      |    y = Record.Build(a=1), ,
      |    z = if (1) ,
      |    w = y.
      |""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe)), errors) = hover(code, Pos(6, 9))
    assertResult("y")(name)
    assertResult("record(a: int)")(tipe)
    assert(errors.exists(err => err.message.contains("expected bool but got int")))
    logger.debug(s"Errors: $errors")
  }

  test("broken code with methods 1") { _ =>
    val code = """
      |x(v: int) =
      |  let y = Record.Build(a=1)
      |  in y.
      |""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe)), errors) = hover(code, Pos(4, 6))
    assertResult("y")(name)
    assertResult("record(a: int)")(tipe)
  }

  test("broken code with methods and expression 1") { _ =>
    val code = """
      |x(v: int) =
      |  let y = Record.Build(a=1)
      |  in y.
      |
      |1 + 2
      |""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe)), errors) = hover(code, Pos(4, 6))
    assertResult("y")(name)
    assertResult("record(a: int)")(tipe)
  }

  test("broken code with methods and expression 2") { _ =>
    val code = """
      |x(v: int) =
      |  let y = Record.Build(a=v)
      |  in y
      |
      |x(v) +
      |""".stripMargin
    val HoverResponse(Some(TypeCompletion(name, tipe)), errors) = hover(code, Pos(6, 1))
    assertResult("x")(name)
    assertResult("(int) -> record(a: int)")(tipe)
  }
}
