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

import raw.compiler.common.source._
import raw.compiler.rql2.source.InternalSourcePrettyPrinter

class Tree(originalRoot: SourceProgram, ensureTree: Boolean = true)(
    implicit programContext: ProgramContext
) extends raw.compiler.base.Tree[SourceNode, SourceProgram, Exp](originalRoot, ensureTree)
    with source.SourcePrettyPrinter
    with errors.ErrorsPrettyPrinter {

  override lazy val analyzer = new SemanticAnalyzer(sourceTree)

  override def cloneWithPositions(): TreeWithPositions = {
    new TreeWithPositions(InternalSourcePrettyPrinter.format(originalRoot), ensureTree, frontend = false)
  }

  override def checkSemanticAnalyzer(): Boolean = {
    // Do not clone tree with positions since we hide type annotations.
    valid
  }

}
