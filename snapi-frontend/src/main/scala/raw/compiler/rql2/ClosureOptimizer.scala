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

import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._
import org.bitbucket.inkytonik.kiama.rewriting.Strategy
import raw.compiler.base.Phase
import raw.compiler.common.source._
import raw.compiler.rql2.source._

import scala.collection.mutable

/**
 * Rewrites the tree in order to propagate nullables and errors.
 */
class ClosureOptimizer(protected val parent: Phase[SourceProgram], protected val phaseName: String)(
    protected val baseProgramContext: raw.compiler.base.ProgramContext
) extends PipelinedPhase {

  override protected def execute(program: SourceProgram): SourceProgram = {
    optimize(program)
  }

  private def optimize(program: SourceProgram): SourceProgram = {
    val tree = new Tree(program)
    lazy val analyzer = tree.analyzer

    val newMethods = mutable.ListBuffer.empty[Rql2Method]
    lazy val s1: Strategy = everywhere(rule[Exp] {
      case fa: FunAbs if analyzer.freeVars(fa).isEmpty =>
        val m = IdnDef()
        // make a new method
        val method = Rql2Method(fa.p, m)
        newMethods += method
        // replace the function by its matching IdnExp
        IdnExp(IdnUse(m.idn))
    })

    val methodsRemoved = rewrite(s1)(tree.root).asInstanceOf[Rql2Program]
    val newProgram = Rql2Program(newMethods.toVector ++ methodsRemoved.methods, methodsRemoved.me)
    logger.trace("ClosureOptimizer:\n" + format(newProgram))
    newProgram
  }

}
