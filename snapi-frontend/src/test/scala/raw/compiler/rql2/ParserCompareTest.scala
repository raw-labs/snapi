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

import org.bitbucket.inkytonik.kiama.rewriting.Cloner.{everywhere, query}
import raw.compiler.common.source.{Exp, SourceNode}
import raw.compiler.rql2.antlr4.Antlr4SyntaxAnalyzer
import raw.compiler.rql2.source.TypeExp
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

  def compareTrees(s: String): Unit = {
    assert(parseWithAntlr4(s) == parseWithKiama(s))
  }

  def comparePositions(s: String): Unit = {
    val kiamaPositions = new org.bitbucket.inkytonik.kiama.util.Positions
    val kiamaParser = new FrontendSyntaxAnalyzer(kiamaPositions)
    val kiamaRoot = kiamaParser.parse(s)

    val antlr4Positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val antlr4Parser = new Antlr4SyntaxAnalyzer(antlr4Positions)
    val antlr4Root = antlr4Parser.parse(s)

    val kiamaNodes = scala.collection.mutable.ArrayBuffer[SourceNode]()
    val antlr4Nodes = scala.collection.mutable.ArrayBuffer[SourceNode]()

    everywhere(query[Any] { case n: Exp if !n.isInstanceOf[TypeExp] => kiamaNodes += n })(kiamaRoot)
    everywhere(query[Any] { case n: Exp if !n.isInstanceOf[TypeExp] => antlr4Nodes += n })(antlr4Root)

    assert(kiamaNodes.size == antlr4Nodes.size, s"Kiama nodes: ${kiamaNodes.size}, Antlr4 nodes: ${antlr4Nodes.size}")

    kiamaNodes.zip(antlr4Nodes).foreach {
      case (kiamaNode, antlr4Node) =>
        assert(antlr4Positions.getStart(antlr4Node) == kiamaPositions.getStart(kiamaNode))
        assert(antlr4Positions.getFinish(antlr4Node) == kiamaPositions.getFinish(kiamaNode))
    }

  }

  // =============== Hello world ==================
  test("""Hello world with string""") { _ =>
    val prog = """let hello = "hello" in hello"""
    compareTrees(prog)
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
    compareTrees(prog)
    comparePositions(prog)
  }

  // =============== Binary expressions ==================
  test("""Add binary expression""") { _ =>
    val prog = """1 + 1""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Subtract binary expression""") { _ =>
    val prog = """1 - 1""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Multiply binary expression""") { _ =>
    val prog = """1 * 1""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Divide binary expression""") { _ =>
    val prog = """1 / 1""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Mod binary expression""") { _ =>
    val prog = """1 % 1""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Priority of binary expressions""") { _ =>
    val prog = """1 + 1 * 1""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Priority of binary expressions with parenthesis""") { _ =>
    val prog = """(1 + 1) * 1""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Or binary expression""") { _ =>
    val prog = """true or false""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""And binary expression""") { _ =>
    val prog = """true and false""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Logical binary expressions (or and)""") { _ =>
    val prog = """true and false or true""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Logical binary expression (or and) with paren""") { _ =>
    val prog = """(true and false) or true""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Logical binary expression with ident""") { _ =>
    val prog = """(true and false) or a""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Comparison binary expression""") { _ =>
    val prog = """1 > 2""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Comparison binary expression with ident""") { _ =>
    val prog = """1 < a""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Geq binary expression""") { _ =>
    val prog = """1 >= 2""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Leq binary expression""") { _ =>
    val prog = """1 <= 2""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Eq binary expression""") { _ =>
    val prog = """1 == 2""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  // =============== Unary expressions ==================

  test("""Not unary expression """) { _ =>
    val prog = """not true""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Not unary expression with priority""") { _ =>
    val prog = """not true and false""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Not unary expression with priority and paren""") { _ =>
    val prog = """not (true and false)""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Minus unary expression""") { _ =>
    val prog = """-5""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Plus unary expression""") { _ =>
    val prog = """+5""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  // =============== Projections ==================
  test("""Projection with package test""") { _ =>
    val prog = """Collection.Build(1,2,3)""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Projection without params package test""") { _ =>
    val prog = """Collection.Build()""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Record projection test""") { _ =>
    val prog = """a.b""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  // =============== Functions ===================

  test("""Method (top level function) declaration""") { _ =>
    val prog = """a(x: int) = 1""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Normal function declaration""") { _ =>
    val prog = """let
      |  a(x: int) = 1
      |in
      |  a(1)""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Recursive function declaration""") { _ =>
    val prog = """let
      |  rec a(x: int) = 1
      |in
      |  a(1)""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  // dont fix the below "int=" it is a bug in kiama
  test("""Function with type declaration""") { _ =>
    val prog = """let
      |  a(x: int):int= 1
      |in
      |  a(1)""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Function with or type declaration""") { _ =>
    val prog = """let a(x:int):int or string=1 in a(1)""".stripMargin
    compareTrees(prog)
  }

  test("""Lambda test""") { _ =>
    val prog = """Collection.Transform(col,x->x+1)""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Lambda test with let""") { _ =>
    val prog = """let
      |  col = Collection.Build(1, 2, 3),
      |  col2 = Collection.Transform(col,x->x+1)
      |in
      |  col2""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Function with let and app""") { _ =>
    val prog = """let
      |    a = 1,
      |    f(v: int) = v * 2
      |in
      |    a + f(1) // Result is 3""".stripMargin
    compareTrees(prog)
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
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Function with params and types and arrows""") { _ =>
    val prog = """let
      |    say(name:string,cleaner:(string)->string)="Hi "+cleaner(name)+"!"
      |in
      |    say("John",s->String.Upper(s)) // Result is "Hi JOHN!"""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  // =============== Alternative builds =================

  test("""Record build""") { _ =>
    val prog = """{a,2,"str"}""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""List build""") { _ =>
    val prog = """[ 1, 2, 3 ]""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Empty list build""") { _ =>
    val prog = """[]""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Empty record build""") { _ =>
    val prog = """{}""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  // =============== Type aliases =================
  test("""Type expressions""") { _ =>
    val prog = """Json.Read("url", type collection(int))""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Type alias""") { _ =>
    val prog = """let
      |  my_type = type collection(record(name: string, age: int)),
      |  a = Collection.Read("url", my_type)
      |in a
      |""".stripMargin
    compareTrees(prog)
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
    compareTrees(prog)
    comparePositions(prog)
  }

  // =============== If-then-else =================

  test("""If then else""") { _ =>
    val prog = """let
      | res = if (true) then 5 else 56
      |in
      |  res""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  // =============== Identifiers =================

  test("""Escaped identifier""") { _ =>
    val prog = """let
      | `let` = 5
      |in
      |  `let`""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  // ================== Random code ======================
  test("""Random code""") { _ =>
    val prog = """let
      |  a(x:int)=1,
      |  col=Collection.Build(1,2,3),
      |  col2=Collection.Transform(col,x->x+1),
      |  z=3q,
      |  test=Json.Read("asdf",type collection(int)),
      |  f=true==true
      |in
      |  z""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""Complex code""") { _ =>
    val prog = """let
      |  data=Json.Read("http://example/some.json",
      |  type collection(record(age:int,height_in_cm:int))),
      |  adults=Collection.Filter(data,r->r.age>18)
      |  ,
      |  height_over_175_cm=Collection.Filter(data,r->r.height_in_cm>175)
      |  in {
      |    adults:adults
      |    , height_over_175_cm: height_over_175_cm
      |  }""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  // ================== String Escape  ======================
  test("""String escape test linebreak""") { _ =>
    val prog =
      """Csv.Parse("1;tralala\n12;ploum\n3;ploum;\n4;NULL", type collection(record(a: int, b: string, c: string, d: string, e: string)),
        |skip = 0, delimiter = ";", nulls=["NULL", "12"])""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""String escape test quotes""") { _ =>
    val ttt = "\"\"\""
    val prog = s"""Csv.InferAndParse(${ttt}1;2\n3;hello$ttt, delimiters=[";","\\n"])""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""String escape test quotes with broken rule""") { _ =>
    val prog = s"""
      |let f(x: int) = x + 1,
      |    g(x: int) = x + 2,
      |    r1 = {f1: f, f2: g},
      |    r2 = {f1: g, f2: f},
      |    min = Collection.Min(Collection.Build(r1, r2))
      |in min.f2(10) * min.f2(10)""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""String escape test quotes with list""") { _ =>
    val ttt = "\"\"\""
    val prog = s"""Csv.InferAndParse(${ttt}1;2\n3;hello;5;;;;;;;$ttt, delimiters=[";","\\n"])""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  // ================== Failed tests  ======================
  test("""Filed test 1""") { _ =>
    val ttt = "\"\"\""
    val prog =
      s"""let
         |  query = $ttt SELECT DISTINCT (?country as ?wikidata_country) ?countryLabel ?code
         |    WHERE
         |    {
         |        ?country wdt:P31 wd:Q3624078 .
         |        # part of the G20
         |        FILTER EXISTS {?country wdt:P463 wd:Q19771}
         |        #not a former country
         |        FILTER NOT EXISTS {?country wdt:P31 wd:Q3024240}
         |       OPTIONAL { ?country wdt:P901 ?code } .
         |
         |       SERVICE wikibase:label { bd:serviceParam wikibase:language "en" }
         |    }
         |  $ttt,
         |  data = Csv.Read(
         |    Http.Get(
         |        "https://query.wikidata.org/bigdata/namespace/wdq/sparql",
         |        args = [{"query", query}],
         |        headers = [{"Accept", "text/csv"}]
         |    ),
         |    type collection(record(wikidata_country: string, countryLabel: string, code: string))
         |  )
         |in
         |  Collection.Filter(data, x -> x.code == "UK")""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }
}
