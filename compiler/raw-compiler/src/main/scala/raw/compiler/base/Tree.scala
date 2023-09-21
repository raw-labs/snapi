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

package raw.compiler.base

import org.apache.commons.lang3.StringUtils
import raw.compiler.base.source._
import raw.utils._

abstract class Tree[N <: BaseNode: Manifest, P <: N: Manifest, E <: N: Manifest](
    protected val originalRoot: P,
    ensureTree: Boolean
)(implicit programContext: ProgramContext)
    extends BaseTree[N, P, E](ensureTree) {

  protected def cloneWithPositions(): TreeWithPositions[N, P, E]

  def checkSyntaxAnalyzer(): Boolean = {
    // Ensure it re-parses properly.
    val newTree = cloneWithPositions()

    // Ensure new tree looks like the current one
    val sameAST = newTree.root.toString == root.toString
    if (!sameAST) {
      val msg = s"""Original pretty printed: $pretty
        |Parsed pretty printed:   ${newTree.pretty}
        |Original AST: $root
        |Parsed AST:   ${newTree.root}
        |Difference:   ${StringUtils.difference(root.toString, newTree.root.toString)}""".stripMargin
      if (messageTooBig(msg)) {
        val p = saveToTemporaryFileNoDeleteOnExit(msg, "deepcheck-", ".log")
        throw new AssertionError(s"""Tree parsed differently!
          |Details in ${p.toAbsolutePath.toString}""".stripMargin)
      } else {
        throw new AssertionError(s"""Tree parsed differently!
          |$msg""".stripMargin)
      }
    }
    sameAST
  }

  def checkSemanticAnalyzer(): Boolean = {
    if (!valid) {
      // Rebuild tree with tree positions.
      val treeWithPositions = cloneWithPositions()

      // Do valid check again, which (should?) retrigger same error, but which will now include error positions in the code.
      val clonedTreeValid = treeWithPositions.valid
      assert(!clonedTreeValid, "Cloned tree is valid but original tree was not!")
      false
    } else {
      true
    }
  }

  def checkTree(): Boolean = {
    var r = true
    if (programContext.settings.checkSyntaxAnalyzers) {
      r &= checkSyntaxAnalyzer()
    }
    r &= checkSemanticAnalyzer()
    r
  }

}
