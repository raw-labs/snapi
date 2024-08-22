/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.frontend.rql2

import com.rawlabs.snapi.frontend.rql2.source.{ErrorType, IdnDef, IdnExp, IdnUse}
import com.rawlabs.snapi.frontend.rql2.antlr4.Antlr4SyntaxAnalyzer
import com.rawlabs.snapi.frontend.rql2.source._
import com.rawlabs.utils.core.RawTestSuite

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

  test("""Missing type let bind""") { _ =>
    val prog = """let b = type int, a :  = 5 in a""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(result.hasErrors)
    assert(
      result.tree == Rql2Program(
        Vector(),
        Some(
          Let(
            Vector(
              LetBind(
                TypeExp(Rql2IntType(Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()))),
                IdnDef("b"),
                None
              ),
              LetBind(IntConst("5"), IdnDef("a"), None)
            ),
            IdnExp(IdnUse("a"))
          )
        )
      )
    )
  }

  test("""Missing attr type""") { _ =>
    val prog = """let b = type int, f(v:  ) = v * 2 in f(1)""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(result.hasErrors)
    assert(
      result.tree == Rql2Program(
        Vector(),
        Some(
          Let(
            Vector(
              LetBind(
                TypeExp(Rql2IntType(Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()))),
                IdnDef("b"),
                None
              ),
              LetFun(
                FunProto(
                  Vector(FunParam(IdnDef("v"), Some(ErrorType()), None)),
                  None,
                  FunBody(BinaryExp(Mult(), IdnExp(IdnUse("v")), IntConst("2")))
                ),
                IdnDef("f")
              )
            ),
            FunApp(IdnExp(IdnUse("f")), Vector(FunAppArg(IntConst("1"), None)))
          )
        )
      )
    )
  }

  test("""Escaped identifier with colon""") { _ =>
    val prog = """let
      |    myVar = 123,
      |    `my:` = 3
      |in
      |m""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(!result.hasErrors)
  }

  test("""record type completion""") { _ =>
    val prog = """let b = type int, c = Json.Read("url", type record()) in c""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(!result.hasErrors)
  }

  test("""function type completion""") { _ =>
    val prog = """let b = type int, f(v: int):  = v * 2 in f(1)""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(result.hasErrors)
  }

  test("""list type completion""") { _ =>
    val prog = """let b = type int, c = Json.Read("url", type list()) in c""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(result.hasErrors)
  }

  test("""error output test 1""") { _ =>
    val prog = """let
      |    res = if true then
      |        Record.Build(a = 4)
      |    else
      |        Record.Build(a = 4, ),
      |    a = type int,
      |in
      |    res.a""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(
      result.errors.head.message == "the input 'in' is not valid here; expected elements are: 'bool', 'string', 'location', 'binary', 'byte', 'short', 'int', 'long', 'float', 'double', 'decimal', 'date', 'time', 'interval', 'timestamp', 'record', 'collection', 'list', 'rec', 'undefined', identifier."
    )
  }

  test("""error output test 2""") { _ =>
    val prog = """f(v: int) =
      |    let
      |    xx = 1,
      |    b = xxadjgfjhasguyfgeaeuwygwyuegguyagwuefgayuwgfuwagygfayuwegfuewyafuagiwuygfwuywgfuyewgauygfuewygfauyiwegfauyiwegfuawgufygfauiwyegfuawgef
      |    hello = "Hello world!"
      |in
      |    hello""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(
      result.errors.head.message == "the input does not form a valid statement or expression."
    )
  }

  test("""error output test 3""") { _ =>
    val prog = """let
      |    res = if true then
      |        Record.Build(a = 4)
      |    else
      |        Record.Build(a = 4),
      |    a = type int,
      |    x: i
      |in
      |    res.a""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(
      result.errors(1).message == "the input 'i' is not valid here; expected elements is: 'in'.'"
    )
  }

  test("RD11199") { _ =>
    val prog = "Collection.Transform(Int.Range(1, 100000), -="
    val result = parseWithAntlr4(prog)
    assert(
      result.errors(0).message == "the input does not form a valid statement or expression."
    )
  }

}
