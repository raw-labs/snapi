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

object ParserCompare {

  private def parseWithAntlr4(s: String) = {
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new Antlr4SyntaxAnalyzer(positions)
    parser.parse(s)
  }

  private def parseWithKiama(s: String) = {
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new FrontendSyntaxAnalyzer(positions)
    parser.parse(s)
  }

  def compareTrees(s: String): Unit = {
    val antlr4Result = parseWithAntlr4(s)
    val kiamaResult = parseWithKiama(s)

    if (antlr4Result.isLeft && kiamaResult.isLeft) {
      assert(true)
      // todo Compare errors
    } else if (antlr4Result.isRight && kiamaResult.isRight) {
      assert(antlr4Result == kiamaResult)
    } else {
      assert(
        false,
        s"""Antlr4: succeeded: ${antlr4Result.isRight}, Kiama succeeded: ${kiamaResult.isRight}"""
      )
    }
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

    if (kiamaNodes.size != antlr4Nodes.size) {
      throw new AssertionError(
        s"=+=+= Different number of nodes Kiama: ${kiamaNodes.size}, Antlr4: ${antlr4Nodes.size}"
      )
    }

    kiamaNodes.zip(antlr4Nodes).foreach {
      case (kiamaNode, antlr4Node) =>
        if (kiamaPositions.getStart(kiamaNode) != antlr4Positions.getStart(antlr4Node))
          throw new AssertionError(s"=+=+= Different start position Kiama node: $kiamaNode, Antlr4 node: $antlr4Node")

        if (kiamaPositions.getFinish(kiamaNode) != antlr4Positions.getFinish(antlr4Node))
          throw new AssertionError(s"=+=+= Different end position Kiama node: $kiamaNode, Antlr4 node: $antlr4Node")
    }

  }

}