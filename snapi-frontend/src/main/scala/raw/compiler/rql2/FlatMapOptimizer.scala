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
import raw.compiler.base.source.Type
import raw.compiler.common.source._
import raw.compiler.rql2.source._

import scala.collection.mutable

/**
 * Rewrites the tree in order to propagate nullables and errors.
 */
class FlatMapOptimizer(protected val parent: Phase[SourceProgram], protected val phaseName: String)(
    protected val baseProgramContext: raw.compiler.base.ProgramContext
) extends PipelinedPhase {

  private val tryable = Rql2IsTryableTypeProperty();
  private val nullable = Rql2IsNullableTypeProperty();

  override protected def execute(program: SourceProgram): SourceProgram = {
    optimize(program)
  }

  private def isSuccess(exp: Exp): FunApp =
    FunApp(Proj(PackageIdnExp("Try"), "IsSuccess"), Vector(FunAppArg(exp, None)))
  private def isNull(exp: Exp): FunApp = FunApp(Proj(PackageIdnExp("Nullable"), "IsNull"), Vector(FunAppArg(exp, None)))
  private def isNull(idn: IdnDef): FunApp = isNull(IdnExp(IdnUse(idn.idn)))
  private def tryableGet(exp: Exp): FunApp =
    FunApp(Proj(PackageIdnExp("Try"), "UnsafeGet"), Vector(FunAppArg(exp, None)))
  private def nullableGet(exp: Exp): FunApp =
    FunApp(Proj(PackageIdnExp("Nullable"), "UnsafeGet"), Vector(FunAppArg(exp, None)))
  private def nullableGet(idn: IdnDef): FunApp = nullableGet(IdnExp(IdnUse(idn.idn)))
  private def successNull(t: Type): FunApp = FunApp(
    Proj(PackageIdnExp("Success"), "Build"),
    Vector(FunAppArg(FunApp(Proj(PackageIdnExp("Nullable"), "Empty"), Vector(FunAppArg(TypeExp(t), None))), None))
  )
  private def errorBuild(value: Exp): FunApp =
    FunApp(Proj(PackageIdnExp("Error"), "Build"), Vector(FunAppArg(value, None)))
  private def errorGet(exp: Exp): FunApp = FunApp(Proj(PackageIdnExp("Error"), "Get"), Vector(FunAppArg(exp, None)))
  private def nullableTryable(e: Exp): FunApp = FunApp(
    Proj(PackageIdnExp("Success"), "Build"),
    Vector(FunAppArg(FunApp(Proj(PackageIdnExp("Nullable"), "Build"), Vector(FunAppArg(e, None))), None))
  )

  private def optimize(program: SourceProgram): SourceProgram = {
    val tree = new Tree(program)
    lazy val analyzer = tree.analyzer

    lazy val s: Strategy = sometd(rulefs[Exp] {
      case FunApp(
            Proj(PackageIdnExp("NullableTryable"), "FlatMap"),
            Vector(FunAppArg(obj1, None), FunAppArg(FunAbs(FunProto(Vector(param1), _, body1)), None))
          ) => attempt(congruence(s, s)) <* rule[Exp] {
          case FunApp(
                _,
                Vector(FunAppArg(obj2, None), FunAppArg(FunAbs(FunProto(Vector(param2), _, body2)), None))
              ) =>
            val eType = analyzer.tipe(obj1).asInstanceOf[Rql2TypeWithProperties]
            val inType = analyzer.idnType(param1.i).asInstanceOf[Rql2TypeWithProperties]
            val outType = analyzer.tipe(body1.e).asInstanceOf[Rql2TypeWithProperties]
            if (
              eType.props.contains(nullable)
              && eType.props.contains(tryable)
              && inType.props.isEmpty
              && outType.props.contains(nullable)
              && outType.props.contains(tryable)
            ) {
              val tGet = IdnDef()
              IfThenElse(
                isSuccess(obj2),
                Let(
                  Vector(LetBind(tryableGet(obj2), tGet, None)),
                  IfThenElse(
                    isNull(tGet),
                    successNull(resetProps(outType, Set.empty)),
                    Let(Vector(LetBind(nullableGet(tGet), param2.i, None)), body2.e)
                  )
                ),
                errorBuild(errorGet(obj2))
              )
            } else {
              ???
            }
        }
    })

    val optimized = rewrite(s)(tree.root).asInstanceOf[Rql2Program]
    logger.trace("FlatMapOptimizer:\n" + format(optimized))
    optimized
  }

}
