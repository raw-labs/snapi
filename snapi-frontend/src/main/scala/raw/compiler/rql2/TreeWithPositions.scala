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

import raw.client.api.{CompilerParserException, ErrorPosition}
import raw.compiler.common.source._
import raw.compiler.rql2.antlr4.Antlr4SyntaxAnalyzer

class TreeWithPositions(originalSource: String, ensureTree: Boolean = true, frontend: Boolean = false)(
    implicit programContext: ProgramContext
) extends raw.compiler.base.TreeWithPositions[SourceNode, SourceProgram, Exp](originalSource, ensureTree)
    with source.SourcePrettyPrinter
    with errors.ErrorsPrettyPrinter {

  override lazy val analyzer = new SemanticAnalyzer(sourceTree)

  @throws[CompilerParserException]
  override def doParse(): SourceProgram = {

    if (frontend) {
      ParserCompare.compareTrees(originalSource, true)
      ParserCompare.comparePositions(originalSource, true)
    }

    if (!frontend) {
      ParserCompare.compareTrees(originalSource, false)
      ParserCompare.comparePositions(originalSource, false)
    }

    // We have both a frontend parser and an internal parser, which gives access to internal nodes.
    if (frontend) {
      val parser = new Antlr4SyntaxAnalyzer(positions, frontend)
//        val parser = new FrontendSyntaxAnalyzer(positions)
      parser.parse(originalSource) match {
        case Right(program) => program.asInstanceOf[SourceProgram]
        case Left((err, pos)) => throw new CompilerParserException(err, ErrorPosition(pos.line, pos.column))
      }
    } else {
      val parser = new Antlr4SyntaxAnalyzer(positions, frontend)
      parser.parse(originalSource) match {
        case Right(program) => program.asInstanceOf[SourceProgram]
        case Left((err, pos)) => throw new CompilerParserException(err, ErrorPosition(pos.line, pos.column))
      }
    }
  }

}
