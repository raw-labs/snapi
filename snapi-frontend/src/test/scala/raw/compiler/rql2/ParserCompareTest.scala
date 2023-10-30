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

import antlr4_parser.Antlr4SyntaxAnalyzer
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._
import raw.compiler.common.source.SourceNode
import raw.utils.RawTestSuite

class ParserCompareTest extends RawTestSuite {

  private def parseWithAntlr4(s: String) = {
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new Antlr4SyntaxAnalyzer(positions)
    parser.parse(s).right.get
  }

  private def parseWithKiama(s: String) = {
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new FrontendSyntaxAnalyzer(positions)
    parser.parse(s).right.get
  }

  private def comparePositions(s: String): Unit = {
    val kiamaPositions = new org.bitbucket.inkytonik.kiama.util.Positions
    val kiamaParser = new FrontendSyntaxAnalyzer(kiamaPositions)
    val kiamaRoot = kiamaParser.parse(s)

    val antlr4Positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val antlr4Parser = new Antlr4SyntaxAnalyzer(antlr4Positions)
    val antlr4Root = antlr4Parser.parse(s)

    val kiamaNodes = scala.collection.mutable.ArrayBuffer[SourceNode]()
    val antlr4Nodes = scala.collection.mutable.ArrayBuffer[SourceNode]()

    everywhere(query[Any] { case n: SourceNode => kiamaNodes += n })(kiamaRoot)

    everywhere(query[Any] { case n: SourceNode => antlr4Nodes += n })(antlr4Root)

    assert(kiamaNodes.size == antlr4Nodes.size, s"Kiama nodes: ${kiamaNodes.size}, Antlr4 nodes: ${antlr4Nodes.size}")

    kiamaNodes.zip(antlr4Nodes).foreach {
      case (kiamaNode, antlr4Node) =>
        assert(kiamaPositions.getStart(kiamaNode) == antlr4Positions.getStart(antlr4Node))
        assert(kiamaPositions.getFinish(kiamaNode) == antlr4Positions.getFinish(antlr4Node))
    }

  }

  // =============== Hello world ==================
  test("""Hello world with string""") { _ =>
    val prog = """let hello = "hello" in hello"""
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  // =============== Constants ==================
  test("""Constants""") { _ =>
    val prog = """let
      |a = 1b,
      |b = 1.0q,
      |c = 1,
      |d = 1.0f,
      |g = 1.0d,
      |f = "hello",
      |g = true,
      |h = false,
      |i = 1l,
      |j = 1s,
      |k = 1.0
      |in 1""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  // =============== Binary expressions ==================
  test("""Add binary expression""") { _ =>
    val prog = """1 + 1""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Subtract binary expression""") { _ =>
    val prog = """1 - 1""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Multiply binary expression""") { _ =>
    val prog = """1 * 1""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Divide binary expression""") { _ =>
    val prog = """1 / 1""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Mod binary expression""") { _ =>
    val prog = """1 % 1""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Priority of binary expressions""") { _ =>
    val prog = """1 + 1 * 1""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Priority of binary expressions with parenthesis""") { _ =>
    val prog = """(1 + 1) * 1""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Or binary expression""") { _ =>
    val prog = """true or false""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""And binary expression""") { _ =>
    val prog = """true and false""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Logical binary expressions (or and)""") { _ =>
    val prog = """true and false or true""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Logical binary expression (or and) with paren""") { _ =>
    val prog = """(true and false) or true""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Logical binary expression with ident""") { _ =>
    val prog = """(true and false) or a""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Comparison binary expression""") { _ =>
    val prog = """1 > 2""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Comparison binary expression with ident""") { _ =>
    val prog = """1 < a""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Geq binary expression""") { _ =>
    val prog = """1 >= 2""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
  }

  test("""Leq binary expression""") { _ =>
    val prog = """1 <= 2""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Eq binary expression""") { _ =>
    val prog = """1 == 2""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  // =============== Unary expressions ==================

  test("""Not unary expression """) { _ =>
    val prog = """not true""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Not unary expression with priority""") { _ =>
    val prog = """not true and false""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Not unary expression with priority and paren""") { _ =>
    val prog = """not (true and false)""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Minus unary expression""") { _ =>
    val prog = """-5""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Plus unary expression""") { _ =>
    val prog = """+5""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  // =============== Projections ==================
  test("""Projection with package test""") { _ =>
    val prog = """Collection.Build(1,2,3)""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Projection without params package test""") { _ =>
    val prog = """Collection.Build()""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Record projection test""") { _ =>
    val prog = """a.b""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  // =============== Functions ===================

  test("""Method (top level function) declaration""") { _ =>
    val prog = """a(x: int) = 1""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Normal function declaration""") { _ =>
    val prog = """let
      |  a(x: int) = 1
      |in
      |  a(1)""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Recursive function declaration""") { _ =>
    val prog = """let
      |  rec a(x: int) = 1
      |in
      |  a(1)""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Function with type declaration""") { _ =>
    val prog = """let
      |  a(x: int):int = 1
      |in
      |  a(1)""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Function with or type declaration""") { _ =>
    val prog = """let
      |  a(x: int): int or string = 1
      |in
      |  a(1)""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Lambda test""") { _ =>
    val prog = """Collection.Transform(col, x -> x + 1)""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Lambda test with let""") { _ =>
    val prog = """let
      |  col = Collection.Build(1, 2, 3),
      |  col2 = Collection.Transform(col, x -> x + 1)
      |in
      |  col2""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Function with let and app""") { _ =>
    val prog = """let
      |    a = 1,
      |    f(v: int) = v * 2
      |in
      |    a + f(1) // Result is 3""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Test with main""") { _ =>
    val prog = """main(name: string) =
      |    let
      |  capitalized_name = String.Capitalize(name)
      |  ,
      |  prefix = "Hello"
      |  in
      |  prefix + capitalized_name + "!"
      |
      |  main("jane")""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Function with params and types and arrows""") { _ =>
    val prog = """let
      |    say(name: string, cleaner: (string) -> string) = "Hi " + cleaner(name) + "!"
      |in
      |    say("John", s -> String.Upper(s)) // Result is "Hi JOHN!"""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  // =============== Alternative builds =================

  test("""Record build""") { _ =>
    val prog = """{ a , 2 , "str" }""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""List build""") { _ =>
    val prog = """[ 1, 2, 3 ]""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  // =============== Type aliases =================
  test("""Type expressions""") { _ =>
    val prog = """Json.Read("url", type collection(int))""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Type alias""") { _ =>
    val prog = """let
      |  my_type = type collection(record(name: string, age: int)),
      |  a = Collection.Read("url", my_type)
      |in a
      |""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  // =============== Comments =================
  test("""Comments""") { _ =>
    val prog = """let
      |  a = 5,
      |  b = 5
      |  // c = 5
      |  in a
      |""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  // =============== If-then-else =================

  test("""If then else""") { _ =>
    val prog = """let
      | res = if (true) then 5 else 56
      |in
      |  res""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  // =============== Identifiers =================

  test("""Escaped identifier""") { _ =>
    val prog = """let
      | `let` = 5
      |in
      |  `let`""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  // ================== Random code ======================
  test("""Random code""") { _ =>
    val prog = """let
      |  a(x: int) = 1,
      |  col = Collection.Build(1,2,3),
      |  col2 = Collection.Transform(col, x -> x + 1),
      |  z = 3q,
      |  test = Json.Read("asdf", type collection(int)),
      |  f = true == true
      |in
      |  z""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

  test("""Complex code""") { _ =>
    val prog = """let
      |  data = Json.Read("http://example/some.json",
      |  type collection
      |  (record(age: int, height_in_cm: int))
      |  ),
      |  adults = Collection.Filter(data, r -> r.age > 18)
      |  ,
      |  height_over_175_cm = Collection.Filter(data, r -> r.height_in_cm > 175)
      |  in {
      |    adults: adults
      |    , height_over_175_cm: height_over_175_cm
      |  }""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
    comparePositions(prog)
  }

}
