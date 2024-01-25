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

    def noFreeVars(node: SourceNode) = {
      val fvs = analyzer.freeVars(node).filterNot(_.isInstanceOf[PackageEntity])
      fvs.isEmpty
    }

    lazy val s1: Strategy = everywheretd(rule[Exp] {
      case f: FunAbs if noFreeVars(f) =>
        val m = IdnDef()
        // make a new method
        val arguments = f.p.ps.map(p => FunParam(p.i, p.t.orElse(Some(analyzer.idnType(p.i))), p.e))
        val proto = FunProto(arguments, f.p.r, f.p.b)
        val method = Rql2Method(proto, m)
        newMethods += method
        // replace the function by its matching IdnExp
        IdnExp(IdnUse(m.idn))
    })

    lazy val s2: Strategy = everywherebu(rule[Exp] {
      case f: FunAbs if noFreeVars(f) =>
        val m = IdnDef()
        // make a new method
        val arguments = f.p.ps.map(p => FunParam(p.i, p.t.orElse(Some(analyzer.idnType(p.i))), p.e))
        val proto = FunProto(arguments, f.p.r, f.p.b)
        val method = Rql2Method(proto, m)
        newMethods += method
        // replace the function by its matching IdnExp
        IdnExp(IdnUse(m.idn))
    })

    lazy val s3: Strategy = sometd(rulefs[Exp] {
      case f: FunAbs if noFreeVars(f) =>
        attempt(congruence(s3)) <* rule[Exp] {
          case f2: FunAbs =>
            val newBody = f2.p.b
            val m = IdnDef()
            // make a new method
            val arguments = f.p.ps.map(p => FunParam(p.i, p.t.orElse(Some(analyzer.idnType(p.i))), p.e))
            val proto = FunProto(arguments, f.p.r, newBody)
            val method = Rql2Method(proto, m)
            newMethods += method
            // replace the function by its matching IdnExp
            IdnExp(IdnUse(m.idn))
        }
    })

    lazy val s4: Strategy = sometd(rulefs[SourceNode] {
      case f: FunAbs if noFreeVars(f) =>
        attempt(congruence(s3)) <* rule[Exp] {
          case f2: FunAbs =>
            val newBody = f2.p.b
            val m = IdnDef()
            // make a new method
            val arguments = f.p.ps.map(p => FunParam(p.i, p.t.orElse(Some(analyzer.idnType(p.i))), p.e))
            val proto = FunProto(arguments, f.p.r, newBody)
            val method = Rql2Method(proto, m)
            newMethods += method
            // replace the function by its matching IdnExp
            IdnExp(IdnUse(m.idn))
        }
      case let: LetFun if noFreeVars(let) =>
        attempt(congruence(s4, id)) <* rule[LetDecl] {
          case let2: LetFun =>
            val newBody = let2.p.b
            val m = IdnDef()
            // make a new method
            val arguments = let.p.ps.map(p => FunParam(p.i, p.t.orElse(Some(analyzer.idnType(p.i))), p.e))
            val proto = FunProto(arguments, let.p.r, newBody)
            val method = Rql2Method(proto, m)
            newMethods += method
            // replace the function by its matching IdnExp
            LetBind(IdnExp(IdnUse(m.idn)), let.i, None)
        }

    })

    val methodsRemoved = rewrite(s4)(tree.root).asInstanceOf[Rql2Program]
    val newProgram = Rql2Program(newMethods.toVector ++ methodsRemoved.methods, methodsRemoved.me)
    logger.trace("ClosureOptimizer:\n" + format(newProgram))
    newProgram
  }

}
