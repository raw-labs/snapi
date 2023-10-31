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

import org.bitbucket.inkytonik.kiama.util.Positions
import raw.client.api.{CompilerParserException, ErrorPosition}
import raw.compiler.common.source.SourceProgram
import raw.compiler.rql2.antlr4.Antlr4SyntaxAnalyzer

class TreeWithPositions(originalSource: String, ensureTree: Boolean = true, frontend: Boolean = false)(
    implicit programContext: ProgramContext
) extends raw.compiler.common.TreeWithPositions(originalSource, ensureTree)
    with source.SourcePrettyPrinter
    with errors.ErrorsPrettyPrinter {

  override lazy val analyzer = new SemanticAnalyzer(sourceTree)

  @throws[CompilerParserException]
  override def doParse(): SourceProgram = {

    if (frontend) {
      val test = new Antlr4SyntaxAnalyzer(new Positions)
    }

    val parser =
      // We have both a frontend parser and an internal parser, which gives access to internal nodes.
      if (frontend) {
        new FrontendSyntaxAnalyzer(positions)
      } else {
        new SyntaxAnalyzer(positions)
      }
    parser.parse(originalSource) match {
      case Right(program) => program.asInstanceOf[SourceProgram]
      case Left((err, pos)) => throw new CompilerParserException(err, ErrorPosition(pos.line, pos.column))
    }
  }

}
