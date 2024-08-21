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

import com.rawlabs.utils.core.RawUtils
import org.apache.commons.lang3.StringUtils
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter.{everywhere, query}
import raw.compiler.base.source._
import raw.compiler.common.source.ErrorType
import com.rawlabs.utils.core._

abstract class Tree[N <: BaseNode: Manifest, P <: N: Manifest, E <: N: Manifest](
    protected val originalRoot: P,
    ensureTree: Boolean
)(implicit programContext: ProgramContext)
    extends BaseTree[N, P, E](ensureTree) {

  private val checkSyntaxAnalyzers =
    programContext.settings.getBooleanOpt("raw.check-syntax-analyzers").getOrElse(false)

  protected def cloneWithPositions(): TreeWithPositions[N, P, E]

  def checkSyntaxAnalyzer(): Boolean = {
    // Ensure it re-parses properly.
    val newTree = cloneWithPositions()

    // Ensure new tree looks like the current one
    val sameAST = newTree.root == root
    if (!sameAST) {
      val msg = s"""Original pretty printed: $pretty
        |Parsed pretty printed:   ${newTree.pretty}
        |Original AST: $root
        |Parsed AST:   ${newTree.root}
        |Difference:   ${StringUtils.difference(root.toString, newTree.root.toString)}""".stripMargin
      if (messageTooBig(msg)) {
        val p = RawUtils.saveToTemporaryFileNoDeleteOnExit(msg, "deepcheck-", ".log")
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
    if (checkSyntaxAnalyzers) {
      r &= checkSyntaxAnalyzer()
    }
    r &= checkSemanticAnalyzer()
    r
  }

  override def isTreeValid: Boolean = {
    val isValid = super.isTreeValid
    if (programContext.settings.onTrainingWheels && isValid) {
      // Check if any ErrorType "accidentally" left in the tree
      val collectBugs = collectNodes[N, Seq, E] {
        case e: E if containsErrorType(analyzer.tipe(e)) => e
      }
      val bugs = collectBugs(root)

      if (bugs.nonEmpty) {
        val msg = bugs
          .map { e =>
            val t = analyzer.tipe(e)
            s"""Expression: $e
              |Type: $t""".stripMargin
          }
          .mkString("\n")
        throw new AssertionError(s"""Error types found on tree that has no errors reported!
          |This is the full (broken) tree:
          |$pretty
          |This is the expression(s) that typed with an error type:
          |(just above is a pretty-printed version of the full (broken) tree):
          |$msg""".stripMargin)
        // msb: The following lines are commented but can be useful to uncomment during debugging:
        //      I found that AssertionError was sometimes hard to debug: so I "let it go" by commenting out the assert
        //      and then the plan would fail at some later point (by "chance") and we'd have the pretty printed errors
        //      to help us out. Those pretty printed errors are not available via this assertion mechanism.
        //        logger.error(
        //          s"""Error types found on tree that has no errors reported!
        //             |$msg""".stripMargin)

      }
    }
    isValid
  }

  private def containsErrorType(t: Type): Boolean = {
    val check = everywhere(query[Type] { case _: ErrorType => return true })
    check(t)
    false
  }

}
