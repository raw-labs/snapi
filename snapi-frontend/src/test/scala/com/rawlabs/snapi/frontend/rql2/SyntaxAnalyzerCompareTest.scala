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

import com.rawlabs.snapi.frontend.rql2.source.{Exp, SourceNode}
import com.rawlabs.snapi.frontend.rql2.antlr4.Antlr4SyntaxAnalyzer
import com.rawlabs.snapi.frontend.rql2.source.TypeExp
import com.rawlabs.utils.core.RawTestSuite
import org.bitbucket.inkytonik.kiama.rewriting.Cloner.{everywhere, query}

class SyntaxAnalyzerCompareTest extends RawTestSuite {
  val triple = "\"\"\""

  private def parseWithAntlr4(s: String) = {
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new Antlr4SyntaxAnalyzer(positions, false)
    parser.parse(s)
  }

  private def parseWithKiama(s: String) = {
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new SyntaxAnalyzer(positions)
    parser.parse(s)
  }

  def compareTrees(s: String): Unit = {
    val antlr4Result = parseWithAntlr4(s)
    val kiamaResult = parseWithKiama(s)

    if (antlr4Result.hasErrors && kiamaResult.isLeft) {
      assert(true)
      // todo Compare errors
    } else if (antlr4Result.isSuccess && kiamaResult.isRight) {
      assert(antlr4Result.tree == kiamaResult.right.get)
    } else {
      assert(
        false,
        s"""Antlr4: succeeded: ${antlr4Result.isSuccess}, Kiama succeeded: ${kiamaResult.isRight}"""
      )
    }
  }

  def comparePositions(s: String): Unit = {
    val kiamaPositions = new org.bitbucket.inkytonik.kiama.util.Positions
    val kiamaParser = new SyntaxAnalyzer(kiamaPositions)
    val kiamaRoot = kiamaParser.parse(s)

    val antlr4Positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val antlr4Parser = new Antlr4SyntaxAnalyzer(antlr4Positions, false)
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
    val prog = """let
      |  hello = Json.Read("a", type collection(int) @null @try)
      |in
      |  hello""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""FE test 1""") { _ =>
    val prog = s"""$$package("Collection")""".stripMargin
    compareTrees(prog)
    comparePositions(prog)
  }

  test("""FE test 2""") { _ =>
    val prog = s"""(int @try @null) -> list(int @try @null) @try @null (@try @null)""".stripMargin
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new Antlr4SyntaxAnalyzer(positions, false)
    parser.parseType(prog)
    assert(true)
  }

  test("""FE test 3""") { _ =>
    val prog = s"""let a = 0x617c627c630a317c327c330a347c357c36 in a""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(result.isSuccess)
  }

  test("""FE test 4""") { _ =>
    val prog = s"""let
      |  hello = Json.InferAndRead("https://jsonplaceholder.typicode.com/users")
      |  in
      |  hello""".stripMargin
    val result = parseWithAntlr4(prog)
    assert(result.isSuccess)
  }

}
