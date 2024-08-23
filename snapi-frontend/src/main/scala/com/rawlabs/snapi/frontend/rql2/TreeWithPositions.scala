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

package com.rawlabs.snapi.frontend.rql2

import com.rawlabs.snapi.frontend.rql2.source._
import com.rawlabs.snapi.frontend.rql2.antlr4.{Antlr4SyntaxAnalyzer, ParseProgramResult}

class TreeWithPositions(originalSource: String, ensureTree: Boolean = true, frontend: Boolean = false)(
    implicit programContext: ProgramContext
) extends com.rawlabs.snapi.frontend.base.TreeWithPositions[SourceNode, SourceProgram, Exp](originalSource, ensureTree)
    with source.SourcePrettyPrinter
    with errors.ErrorsPrettyPrinter {

  override lazy val analyzer = new SemanticAnalyzer(sourceTree)

  override def doParse(): ParseProgramResult[SourceProgram] = {
    val parser = new Antlr4SyntaxAnalyzer(positions, frontend)
    parser.parse(originalSource)
  }

}
