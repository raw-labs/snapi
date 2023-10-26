package raw.compiler.rql2

import antlr4_parser.Antlr4SyntaxAnalyzer
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

  // Hello world
  test("""Hello world with string""") { _ =>
    val prog = """let hello = "hello" in hello"""
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
  }

  // Projections
  test("""Projection test""") { _ =>
    val prog = """Collection.Build(1,2,3)""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
  }

  // Functions

  test("""Normal function declaration""") { _ =>
    val prog = """let
      |  a(x: int) = 1
      |in
      |  a(1)""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
  }

  test("""Random code""") { _ =>
    val prog = """let
      |  a(x: int) = 1,
      |  col = Collection.Build(1,2,3)
      |in
      |  col""".stripMargin
    assert(parseWithAntlr4(prog) == parseWithKiama(prog))
  }

}
