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

package raw.compiler.rql2

import raw.compiler.rql2.antlr4.Antlr4SyntaxAnalyzer
import raw.utils.RawTestSuite

class Antlr4LSPTests extends RawTestSuite {
  val triple = "\"\"\""

  private def parseWithAntlr4(s: String) = {
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new Antlr4SyntaxAnalyzer(positions, true)
    parser.parse(s)
  }

  test("""Missing comma lsp test""") { _ =>
    val prog = s"""let a = 5 b = 3 in b""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(result.hasErrors)
    assert(result.errors.size == 1)
    assert(result.errors.head.positions.head.begin.line == 1)
    assert(result.errors.head.positions.head.begin.column == 10)
    assert(result.errors.head.positions.head.end.line == 1)
    assert(result.errors.head.positions.head.end.column == 11)
  }

  test("""Missing binding lsp test""") { _ =>
    val prog = s"""let x = ,
      |    y = 3
      |in y
      |""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(result.hasErrors)
    assert(result.errors.size == 1)
    assert(result.errors.head.positions.head.begin.line == 1)
    assert(result.errors.head.positions.head.begin.column == 8)
    assert(result.errors.head.positions.head.end.line == 1)
    assert(result.errors.head.positions.head.end.column == 9)
  }

  test("""Very broken code lsp test""") { _ =>
    val prog = """let x = ,
      |    f(v: int) = (v + ,
      |    y = Record.Build(a=1), ,
      |    z = if (true) ,
      |    w = y.a
      |""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(result.hasErrors)
  }

  test("""Very broken code lsp without projection test""") { _ =>
    val prog = """let x = ,
      |    f(v: int) = (v + ,
      |    y = Record.Build(a=1), ,
      |    z = if (true) ,
      |    w = y.
      |""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(result.hasErrors)
  }

  test("""Broken method code lsp test""") { _ =>
    val prog = """
      |x(v: int) =
      |  let y = Record.Build(a=1)
      |  in y.
      |""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(result.hasErrors)
  }

}
