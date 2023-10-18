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
import raw.client.api.CompilerException
import raw.compiler.base.Phase
import raw.compiler.base.errors.BaseError
import raw.compiler.base.source.Type
import raw.compiler.common.source.SourceProgram
import raw.compiler.rql2.api.SugarEntryExtension
import raw.compiler.rql2.source._

class SugarExtensionDesugarer(protected val parent: Phase[SourceProgram], protected val phaseName: String)(
    protected val baseProgramContext: raw.compiler.base.ProgramContext
) extends PipelinedPhase {

  override protected def execute(program: SourceProgram): SourceProgram = {
    desugar(program)
  }

  private def getSugarEntryExtension(p: Type): Option[SugarEntryExtension] = {
    p match {
      case PackageEntryType(pkgName, entName) => programContext.getPackage(pkgName) match {
          case Some(pkg) => pkg.getEntry(entName) match {
              case s: SugarEntryExtension => Some(s)
              case _ => None
            }
          case _ => None
        }
      case _ => None
    }
  }

  private def desugar(program: SourceProgram): SourceProgram = {
    val tree = new Tree(program)
    lazy val analyzer = tree.analyzer

    lazy val s: Strategy = attempt(sometd(rulefs[Any] {
      case f @ FunApp(p @ Proj(e, i), _) if getSugarEntryExtension(analyzer.tipe(p)).isDefined =>
        congruence(s, s) <* rule[Any] {
          case FunApp(np, nargs) =>
            val PackageType(pkgName) = analyzer.tipe(e)
            val ent = getSugarEntryExtension(analyzer.tipe(p)).get
            val FunAppPackageEntryArguments(mandatoryArgs, optionalArgs, varArgs, _) =
              analyzer.getArgumentsForFunAppPackageEntry(f, ent) match {
                case Right(x) => x.get
                case Left(error: BaseError) =>
                  // The code call to this package extension isn't typing (wrong parameters?)
                  // Report the error. That can happen when developing of a sugar package extension.
                  // Wrong desugaring isn't caught otherwise.
                  throw new CompilerException(s"SugarExtensionDesugarer failed while desugaring $pkgName.$i: $error")
              }

            ent.desugar(analyzer.tipe(f), nargs, mandatoryArgs, optionalArgs, varArgs)
        }
    }))

    val r = rewrite(s)(tree.root)
    logger.trace("SugarExtensionDesugarer:\n" + format(r))
    r
  }

}
