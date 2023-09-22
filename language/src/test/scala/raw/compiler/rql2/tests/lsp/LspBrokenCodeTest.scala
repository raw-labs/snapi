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
import raw.compiler.rql2.tests.CompilerTestContext
import raw.runtime.ProgramEnvironment

trait LspBrokenCodeTest extends CompilerTestContext {

  val queryEnvironment: ProgramEnvironment = ProgramEnvironment(Some("snapi"), Set.empty, Map.empty)

  test("broken code hover test") { _ =>
    val code = """let
      |a: float = 5
      |""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(2, 1)))
    response match {
      case HoverLSPResponse(hoverResponse: LSPHoverResponse, errors: List[ErrorMessage]) => hoverResponse match {
          case TypeHoverResponse(name, tipe) =>
            assertResult("a")(name)
            assertResult("float")(tipe)
            logger.debug(s"Errors: $errors")
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("very broken code hover test") { _ =>
    val code = """
      |let x = ,
      |    y = Record.Build(a=1),
      |    z = if (true) ,
      |    w = y.a
      |""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(5, 9)))
    response match {
      case HoverLSPResponse(hoverResponse: LSPHoverResponse, errors: List[ErrorMessage]) => hoverResponse match {
          case TypeHoverResponse(name, tipe) =>
            assertResult("y")(name)
            assertResult("record(a: int)")(tipe)
            logger.debug(s"Errors: $errors")
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("very broken code hover test 2") { _ =>
    val code = """
      |let x = ,
      |    f(v: int) = (v + ,
      |    y = Record.Build(a=1), ,
      |    z = if (true) ,
      |    w = y.a
      |""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(6, 9)))
    response match {
      case HoverLSPResponse(hoverResponse: LSPHoverResponse, errors: List[ErrorMessage]) => hoverResponse match {
          case TypeHoverResponse(name, tipe) =>
            assertResult("y")(name)
            assertResult("record(a: int)")(tipe)
            logger.debug(s"Errors: $errors")
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("very broken code hover test 3") { _ =>
    val code = """
      |let x = ,
      |    f(v: int) = (v + ,
      |    y = Record.Build(a=1), ,
      |    z = if (true) ,
      |    w = y.
      |""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(6, 9)))
    response match {
      case HoverLSPResponse(hoverResponse: LSPHoverResponse, errors: List[ErrorMessage]) => hoverResponse match {
          case TypeHoverResponse(name, tipe) =>
            assertResult("y")(name)
            assertResult("record(a: int)")(tipe)
            logger.debug(s"Errors: $errors")
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("very broken code hover test 4") { _ =>
    val code = """
      |let x = ,
      |    f(v: int) = (v + ,
      |    y = Record.Build(a=1), ,
      |    z = if (1) ,
      |    w = y.
      |""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(6, 9)))
    response match {
      case HoverLSPResponse(hoverResponse: LSPHoverResponse, errors: List[ErrorMessage]) => hoverResponse match {
          case TypeHoverResponse(name, tipe) =>
            assertResult("y")(name)
            assertResult("record(a: int)")(tipe)
            assert(errors.exists(err => err.message.contains("expected bool but got int")))
            logger.debug(s"Errors: $errors")
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("broken code with methods 1") { _ =>
    val code = """
      |x(v: int) =
      |  let y = Record.Build(a=1)
      |  in y.
      |""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(4, 6)))
    response match {
      case HoverLSPResponse(hoverResponse: LSPHoverResponse, errors: List[ErrorMessage]) => hoverResponse match {
          case TypeHoverResponse(name, tipe) =>
            assertResult("y")(name)
            assertResult("record(a: int)")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("broken code with methods and expression 1") { _ =>
    val code = """
      |x(v: int) =
      |  let y = Record.Build(a=1)
      |  in y.
      |
      |1 + 2
      |""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(4, 6)))
    response match {
      case HoverLSPResponse(hoverResponse: LSPHoverResponse, errors: List[ErrorMessage]) => hoverResponse match {
          case TypeHoverResponse(name, tipe) =>
            assertResult("y")(name)
            assertResult("record(a: int)")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }

  test("broken code with methods and expression 2") { _ =>
    val code = """
      |x(v: int) =
      |  let y = Record.Build(a=v)
      |  in y
      |
      |x(v) +
      |""".stripMargin
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(6, 1)))
    response match {
      case HoverLSPResponse(hoverResponse: LSPHoverResponse, errors: List[ErrorMessage]) => hoverResponse match {
          case TypeHoverResponse(name, tipe) =>
            assertResult("x")(name)
            assertResult("(int) -> record(a: int)")(tipe)
          case r => throw new AssertionError(s"Unexpected response: $r")
        }
      case r => throw new AssertionError(s"Unexpected response: $r")
    }
  }
}
