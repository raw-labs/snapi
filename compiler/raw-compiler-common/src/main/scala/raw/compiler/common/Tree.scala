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

package raw.compiler.common

import org.bitbucket.inkytonik.kiama.rewriting.Rewriter.{everywhere, everywherebu, query, rewrite, rule}
import raw.compiler.base.source.{RawBridgeImpl, Type}
import raw.compiler.base.{CompilerProvider, NormalizeMap, ProgramContext}
import raw.compiler.common.source.{ErrorType, Eval, Exp, IdnDef, IdnUse, SourceNode, SourceProgram}

abstract class Tree(originalRoot: SourceProgram, ensureTree: Boolean)(implicit programContext: ProgramContext)
    extends raw.compiler.base.Tree[SourceNode, SourceProgram, Exp](originalRoot, ensureTree)
    with source.SourcePrettyPrinter
    with errors.ErrorsPrettyPrinter {

  override def analyzer: SemanticAnalyzer

  override def normalize: SourceProgram = {
    // Recursively rewrite identifiers.
    val map = new NormalizeMap()
    val rewriteIdns = everywhere(
      rule[SourceNode] {
        case n: IdnDef if n.idn.contains("$") => IdnDef(map.newIdnForIdnNode(analyzer.entity(n), n))
        case n: IdnUse if n.idn.contains("$") => IdnUse(map.newIdnForIdnNode(analyzer.entity(n), n))
      }
    )
    val rootWithNewIdns = rewrite(rewriteIdns)(root)
    // Recursively rewrite inner languages of Eval.
    val rewriteInnerLanguages = everywherebu(
      rule[Any] {
        case Eval(RawBridgeImpl(lang, source)) => Eval(
            RawBridgeImpl(lang, CompilerProvider.normalize(lang, source))
          )
      }
    )
    rewrite(rewriteInnerLanguages)(rootWithNewIdns)
  }

  override def isTreeValid: Boolean = {
    val isValid = super.isTreeValid
    if (programContext.settings.onTrainingWheels && isValid) {
      // Check if any ErrorType "accidentally" left in the tree
      val collectBugs = collectNodes[SourceNode, Seq, Exp] {
        case e: Exp if containsErrorType(analyzer.tipe(e)) => e
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
  protected def containsErrorType(t: Type): Boolean = {
    val check = everywhere(query[Type] { case _: ErrorType => return true })
    check(t)
    false
  }
}
