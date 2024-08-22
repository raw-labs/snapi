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

package com.rawlabs.snapi.frontend.rql2.phases

import com.rawlabs.snapi.frontend.base.Phase
import com.rawlabs.snapi.frontend.rql2.source._
import com.rawlabs.snapi.frontend.rql2.builtin.{CollectionPackageBuilder, ListPackageBuilder}
import com.rawlabs.snapi.frontend.rql2.source._
import com.rawlabs.snapi.frontend.rql2.{PipelinedPhase, Tree}
import org.bitbucket.inkytonik.kiama.rewriting.Cloner.attempt
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._
import org.bitbucket.inkytonik.kiama.rewriting.Strategy

class ListProjDesugarerPhase(protected val parent: Phase[SourceProgram], protected val phaseName: String)(
    protected val baseProgramContext: com.rawlabs.snapi.frontend.base.ProgramContext
) extends PipelinedPhase {

  override protected def execute(program: SourceProgram): SourceProgram = {
    desugar(program)
  }

  private def desugar(program: SourceProgram): SourceProgram = {
    val tree = new Tree(program)
    lazy val analyzer = tree.analyzer

    // Using congruence here because of https://raw-labs.atlassian.net/browse/RD-5722
    lazy val s: Strategy = attempt(sometd(rulefs[Any] {
      case Proj(collection, i)
          if analyzer
            .tipe(collection)
            .isInstanceOf[Rql2IterableType] || analyzer.tipe(collection).isInstanceOf[Rql2ListType] =>
        congruence(s, id) <* rule[Any] {
          case Proj(nCollection, _) =>
            val arg = IdnDef()
            val projectFun = FunAbs(
              FunProto(Vector(FunParam(arg, None, None)), None, FunBody(Proj(IdnExp(arg), i)))
            )

            if (analyzer.tipe(collection).isInstanceOf[Rql2IterableType]) {
              CollectionPackageBuilder.Transform(nCollection, projectFun)
            } else {
              ListPackageBuilder.Transform(nCollection, projectFun)
            }
        }
    }))

    val r = rewrite(s)(tree.root)
    logger.trace("ListProjDesugarer:\n" + format(r))
    r
  }

}
