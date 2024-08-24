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

package com.rawlabs.snapi.frontend.snapi.phases

import com.rawlabs.snapi.frontend.base.Phase
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi.extensions.builtin._
import com.rawlabs.snapi.frontend.snapi.{PipelinedPhase, SnapiTypeUtils, Tree}
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._
import org.bitbucket.inkytonik.kiama.rewriting.Strategy

/**
 * Applies implicit casts to core nodes of the language.
 */
class ImplicitCastsPhase(protected val parent: Phase[SourceProgram], protected val phaseName: String)(
    protected val baseProgramContext: com.rawlabs.snapi.frontend.base.ProgramContext
) extends PipelinedPhase
    with SnapiTypeUtils {

  override protected def execute(program: SourceProgram): SourceProgram = {
    implicitCast(program)
  }

  private def implicitCast(program: SourceProgram): SourceProgram = {
    val tree = new Tree(program)
    lazy val analyzer = tree.analyzer

    // Cast "e" from "actual" type to "expected".
    def cast(e: Exp, actual: Type, expected: Type): Option[Exp] = {
      if (actual == expected) None
      else if (actual == SnapiUndefinedType()) Some(TypePackageBuilder.Empty(expected))
      else expected match {
        case r: SnapiTypeWithProperties =>
          val expectedProps = r.props
          val actualProps = getProps(actual)
          if (expectedProps.contains(SnapiIsTryableTypeProperty())) {
            if (actualProps.contains(SnapiIsTryableTypeProperty())) {
              // both are tryables, cast in TryPackage.Transform
              val arg = IdnDef()
              val a = removeProp(actual, SnapiIsTryableTypeProperty())
              val t = removeProp(expected, SnapiIsTryableTypeProperty())
              cast(IdnExp(arg.idn), a, t).map { xCode =>
                val mapFun = FunAbs(
                  FunProto(Vector(FunParam(arg, Some(a), None)), Some(t), FunBody(xCode))
                )
                TryPackageBuilder.Transform(e, mapFun)
              }
            } else {
              // expected is tryable, not actual. This case is casting, say, Int to Success(Float)
              cast(e, actual, removeProp(expected, SnapiIsTryableTypeProperty())) match {
                case Some(ne) => Some(SuccessPackageBuilder.Build(ne))
                case None => Some(SuccessPackageBuilder.Build(e))
              }
            }
          } else if (actualProps.contains(SnapiIsTryableTypeProperty())) {
            // expected isn't tryable, but actual is. We use a .get
            val inner = cast(e, actual, addProp(expected, SnapiIsTryableTypeProperty()))
            val inner2 = inner.getOrElse(e)
            Some(TryPackageBuilder.UnsafeGet(inner2))
          } else {
            // expected isn't tryable
            if (expectedProps.contains(SnapiIsNullableTypeProperty())) {
              if (actualProps.contains(SnapiIsNullableTypeProperty())) {
                // both are nullables, use Null.Transform
                val arg = IdnDef()
                val a = removeProp(actual, SnapiIsNullableTypeProperty())
                val t = removeProp(expected, SnapiIsNullableTypeProperty())
                cast(IdnExp(arg.idn), a, t).map { xCode =>
                  val mapFun = FunAbs(
                    FunProto(Vector(FunParam(arg, Some(a), None)), Some(t), FunBody(xCode))
                  )
                  NullablePackageBuilder.Transform(e, mapFun)
                }
              } else {
                // expected is nullable, not actual
                // This case is casting, say, Int to Option(Float)
                cast(e, actual, removeProp(expected, SnapiIsNullableTypeProperty())) match {
                  case Some(ne) => Some(NullablePackageBuilder.Build(ne))
                  case None => Some(NullablePackageBuilder.Build(e))
                }
              }
            } else {
              // expected isn't nullable
              if (actualProps.contains(SnapiIsNullableTypeProperty())) {
                // expected isn't nullable, but actual is. We stack .get on actual
                val inner = cast(e, actual, addProp(expected, SnapiIsNullableTypeProperty()))
                val inner2 = inner.getOrElse(e)
                Some(NullablePackageBuilder.UnsafeGet(inner2))
              } else {
                // regular cast
                expected match {
                  case _: SnapiShortType => actual match {
                      case _: SnapiByteType => Some(ShortPackageBuilder.From(e))
                      case _ => None
                    }
                  case _: SnapiIntType => actual match {
                      case _: SnapiByteType => Some(IntPackageBuilder.From(e))
                      case _: SnapiShortType => Some(IntPackageBuilder.From(e))
                      case _ => None
                    }
                  case _: SnapiLongType => actual match {
                      case _: SnapiByteType => Some(LongPackageBuilder.From(e))
                      case _: SnapiShortType => Some(LongPackageBuilder.From(e))
                      case _: SnapiIntType => Some(LongPackageBuilder.From(e))
                      case _ => None
                    }
                  case _: SnapiFloatType => actual match {
                      case _: SnapiByteType => Some(FloatPackageBuilder.From(e))
                      case _: SnapiShortType => Some(FloatPackageBuilder.From(e))
                      case _: SnapiIntType => Some(FloatPackageBuilder.From(e))
                      case _: SnapiLongType => Some(FloatPackageBuilder.From(e))
                      case _ => None
                    }
                  case _: SnapiDoubleType => actual match {
                      case _: SnapiByteType => Some(DoublePackageBuilder.From(e))
                      case _: SnapiShortType => Some(DoublePackageBuilder.From(e))
                      case _: SnapiIntType => Some(DoublePackageBuilder.From(e))
                      case _: SnapiLongType => Some(DoublePackageBuilder.From(e))
                      case _: SnapiFloatType => Some(DoublePackageBuilder.From(e))
                      case _ => None
                    }
                  case _: SnapiDecimalType => actual match {
                      case _: SnapiByteType => Some(DecimalPackageBuilder.From(e))
                      case _: SnapiShortType => Some(DecimalPackageBuilder.From(e))
                      case _: SnapiIntType => Some(DecimalPackageBuilder.From(e))
                      case _: SnapiLongType => Some(DecimalPackageBuilder.From(e))
                      case _: SnapiFloatType => Some(DecimalPackageBuilder.From(e))
                      case _: SnapiDoubleType => Some(DecimalPackageBuilder.From(e))
                      case _ => None
                    }
                  case _: SnapiTimestampType => actual match {
                      case _: SnapiDateType => Some(TimestampPackageBuilder.FromDate(e))
                      case _ => None
                    }
                  case _: SnapiLocationType => actual match {
                      case _: SnapiStringType => Some(LocationPackageBuilder.FromString(e))
                      case _ => None
                    }
                  case SnapiIterableType(t, _) => actual match {
                      case SnapiIterableType(SnapiUndefinedType(props), _) if props.isEmpty =>
                        Some(CollectionPackageBuilder.Empty(t))
                      case SnapiIterableType(a, _) =>
                        val arg = IdnDef()
                        cast(IdnExp(arg.idn), a, t).map { xCode =>
                          val mapFun = FunAbs(
                            FunProto(Vector(FunParam(arg, Some(a), None)), Some(t), FunBody(xCode))
                          )
                          CollectionPackageBuilder.Transform(e, mapFun)
                        }
                    }
                  case SnapiListType(t, _) => actual match {
                      case SnapiListType(SnapiUndefinedType(props), _) if props.isEmpty =>
                        Some(ListPackageBuilder.Empty(t))
                      case SnapiListType(a, _) =>
                        val arg = IdnDef()
                        cast(IdnExp(arg.idn), a, t).map { xCode =>
                          val mapFun = FunAbs(
                            FunProto(Vector(FunParam(arg, Some(a), None)), Some(t), FunBody(xCode))
                          )
                          ListPackageBuilder.Transform(e, mapFun)
                        }
                    }
                  case SnapiRecordType(expectedFields, _) => actual match {
                      case SnapiRecordType(actualFields, _) =>
                        assert(expectedFields.size == actualFields.size)
                        // We'll copy the record in that variable so that its value
                        // isn't duplicated (RD-5502)
                        val src = IdnDef()
                        val fieldCasts = expectedFields.zip(actualFields).zipWithIndex.map {
                          case ((expectedField, actualField), idx) => cast(
                              RecordPackageBuilder.GetFieldByIndex(IdnExp(src), IntConst((idx + 1).toString)),
                              actualField.tipe,
                              expectedField.tipe
                            )
                        }
                        // if not field needed a cast, don't cast the record at all
                        if (fieldCasts.forall(_.isEmpty)) None
                        else {
                          // for each field that needs to be cast, use the formerly computed cast expression,
                          // otherwise use the original field.
                          val params = fieldCasts.zip(expectedFields).zipWithIndex.map {
                            case ((Some(nField), expectedField), _) => FunAppArg(nField, Some(expectedField.idn))
                            case ((None, expectedField), idx) => FunAppArg(
                                RecordPackageBuilder.GetFieldByIndex(IdnExp(src), IntConst((idx + 1).toString)),
                                Some(expectedField.idn)
                              )
                          }
                          // and build a new record
                          Some(
                            Let(Vector(LetBind(e, src, None)), FunApp(Proj(PackageIdnExp("Record"), "Build"), params))
                          )
                        }
                    }
                  case FunType(eParamTypes, Vector(), eRType, _) =>
                    val FunType(aParamTypes, _, aRType, _) = actual
                    // fix the return type of f_actual(x, y) = ... x .. y ..
                    // replace it with: f_final($1, $2) = cast(f_actual($1, $2), actual, expected)
                    // if f_actual has optional parameters, since they aren't in the expected type (Vector.empty), we ignore them
                    // Part of casting the actual function to its expected type involve fixing parameters. This goes by
                    // casting _expected parameter types to actual_ (yes, expected to actual) before applying the `actual` function:
                    // Say the f_actual takes a nullable int, while the expected function takes a plain int. f_actual has to be
                    // replaced by a function taking a plain int, that applies the real actual function internally:
                    // f_final($1: int) = f_actual(cast($1 to nullable int)) <= we cast expected (int) to actual (nullable int)
                    val newArgs = eParamTypes.map(t => (IdnDef(), t))
                    val applyF = FunApp(
                      e,
                      newArgs.zip(aParamTypes).map {
                        case ((p, ex), ac) => FunAppArg(cast(IdnExp(p), ex, ac).getOrElse(IdnExp(p)), None)
                      }
                    )
                    val body = FunBody(cast(applyF, aRType, eRType).getOrElse(applyF))
                    val msParams = newArgs.map { case (idn, ex) => FunParam(idn, Some(ex), None) }
                    Some(FunAbs(FunProto(msParams, Some(eRType), body)))
                  case _ => None
                }
              }
            }
          }
        case _ => None // types that don't have properties aren't cast (e.g. ExpType)
      }
    }

    // FunProto factorized logic
    def handleProto(proto: FunProto, returnType: Type) = build(proto) <* congruence(s, id, s) <* rule[Any] {
      case FunProto(rps, _, rb) =>
        // if expected/return type of body is different than the actual body type.
        val retypedBody = for (b <- cast(rb.e, analyzer.tipe(proto.b.e), returnType)) yield FunBody(b)
        val nb = retypedBody.getOrElse(rb)
        val nps = rps.zip(proto.ps).map {
          case (rp, p) => FunParam(
              rp.i,
              rp.t,
              rp.e match {
                case Some(re) => cast(re, analyzer.tipe(p.e.get), analyzer.idnType(p.i))
                case None => None
              }
            )
        }
        FunProto(nps, Some(returnType), nb)
    }
    // Check if the argument type matches that of the parameter type.
    // If not, does the cast.
    def argNeedsCast(fa: FunApp, argIdx: Int, e: Exp): Option[FunAppArg] = {
      val FunApp(f, args) = fa
      val arg = args(argIdx)
      // unwrap ExpType
      val actual = analyzer.tipe(arg.e) match { case ExpType(x) => x; case x => x }
      analyzer.tipe(f) match {
        case FunType(ms, os, _, _) =>
          // If it's a function type, must find the parameter type.
          val paramType =
            if (argIdx < ms.length) {
              // It is a mandatory parameter.
              ms(argIdx)
            } else {
              // It is an optional parameter. Has to be find either by name (if the user specified it), or by its
              // relative position.
              arg.idn match {
                case Some(idn) =>
                  // By name.
                  os.collectFirst { case o if o.i == idn => o.t }.get
                case None =>
                  // By relative position.
                  os(argIdx - ms.length).t
              }
            }
          cast(e, actual, paramType) match {
            case Some(ne) => Some(FunAppArg(ne, arg.idn))
            case None => None
          }
        case pt: PackageEntryType =>
          // The default casting rule is: cast the argument to the type that satisfies the expected type.
          // However, a couple of EntryExtension need a custom cast of their arguments.
          // Arguments of both List.Build and Collection.Build have to be cast to the collection type (the
          // merge of all item types.
          val paramType = (pt.pkgName, pt.entName) match {
            case ("Type", "Cast") if argIdx == 1 =>
              val ExpType(t) = analyzer.tipe(args(0).e)
              t
            case ("Type", "ProtectCast") if argIdx == 2 =>
              val ExpType(t) = analyzer.tipe(args(1).e)
              t
            case ("List", "Build") =>
              val SnapiListType(itemType, props) = analyzer.tipe(fa)
              assert(props.isEmpty)
              itemType
            case ("Collection", "Build") =>
              val SnapiIterableType(itemType, props) = analyzer.tipe(fa)
              assert(props.isEmpty)
              itemType
            case ("Collection", "Union") => analyzer.tipe(fa)
            case ("Type", "Match") if argIdx >= 1 =>
              // argIdx >= 1, we're typing a handler function.
              // Its outputType has to be a merge of all handlers,
              // That's to say, the type of Type.Match
              val outputType = analyzer.tipe(fa)
              // That's the actual type (we'll keep the args and props)
              val FunType(args, _, _, props) = actual
              // We can merge the actual function type with the same function type
              // changed to return the expected output type.
              val merged = for (
                expected <- analyzer.getFunAppPackageEntryTypePartial(fa, pt, argIdx);
                r <- analyzer.funParamTypeCompatibility(FunType(args, Vector.empty, outputType, props), expected)
              ) yield r.t
              merged.get
            case _ => analyzer
                .getFunAppPackageEntryTypePartial(fa, pt, argIdx)
                .flatMap(expected =>
                  // The method getFunAppPackageEntryTypePartial gets us the expected parameter. This could be e.g. OneOfType.
                  // So we need to merge it with the actual type to get the target type we should be casting to.
                  // So for instance, if getFunAppPackageEntryTypePartial returns OneOfType(StringType, FloatType())
                  // but actual is IntType(), then our target type to cast to must be FloatType(), which is the merge
                  // of IntType() with FloatType().
                  analyzer.funParamTypeCompatibility(actual, expected)
                )
                .get
                .t
          }
          for (ne <- cast(e, actual, paramType); if !e.isInstanceOf[TypeExp]) yield FunAppArg(ne, arg.idn)
      }
    }

    lazy val s: Strategy = attempt(sometd(rulefs[Any] {
      // LetBind
      case LetBind(e, idnDef, Some(_)) => congruence(s, id, id) <* rule[Any] {
          case LetBind(e2, _, mt) => analyzer.idnType(idnDef) match {
              case _: ExpType => LetBind(e2, idnDef, mt)
              case t =>
                val ne = cast(e2, analyzer.tipe(e), t).getOrElse(e2)
                LetBind(ne, idnDef, mt)
            }
        }
      // FunApp
      case fa: FunApp => congruence(s, s) <* rulefs[Any] {
          case FunApp(nf, nArgs) =>
            val castArgs =
              nArgs.zipWithIndex.map { case (arg, argIdx) => argNeedsCast(fa, argIdx, arg.e).getOrElse(arg) }
            build(
              FunApp(
                nf,
                castArgs
              )
            )
        }
      case f @ FunAbs(p) =>
        val FunType(_, _, rType, _) = analyzer.tipe(f)
        handleProto(p, rType) <* rule[Any] { case nProto: FunProto => FunAbs(nProto) }
      case LetFun(p, idn) =>
        val FunType(_, _, rType, _) = analyzer.idnType(idn)
        handleProto(p, rType) <* rule[Any] { case nProto: FunProto => LetFun(nProto, idn) }
      case LetFunRec(idn, p) =>
        val FunType(_, _, rType, _) = analyzer.idnType(idn)
        handleProto(p, rType) <* rule[Any] { case nProto: FunProto => LetFunRec(idn, nProto) }
      case SnapiMethod(p, idn) =>
        val FunType(_, _, rType, _) = analyzer.idnType(idn)
        handleProto(p, rType) <* rule[Any] { case nProto: FunProto => SnapiMethod(nProto, idn) }
      case unaryExp @ UnaryExp(op, e) => congruence(id, s) <* rule[Any] {
          case UnaryExp(_, re) =>
            val t = analyzer.tipe(unaryExp)
            val ne = cast(re, analyzer.tipe(e), t).getOrElse(re)
            UnaryExp(op, ne)
        }
      // BinaryExp
      case BinaryExp(op: BooleanOp, e1, e2) => congruence(id, s, s) <* rule[Any] {
          case BinaryExp(_, re1, re2) =>
            val te1 = analyzer.tipe(e1)
            val te2 = analyzer.tipe(e2)
            val tm = SnapiBoolType(Set(SnapiIsNullableTypeProperty()))
            val ne1 = cast(re1, te1, tm).getOrElse(re1)
            val ne2 = cast(e2, te2, tm).getOrElse(re2)
            BinaryExp(op, ne1, ne2)
        }
      case b @ BinaryExp(op, e1, e2) if analyzer.tipe(e1) != analyzer.tipe(e2) =>
        op match {
          case _: Plus | _: Sub | _: Mult | _: Div | _: Mod => congruence(id, s, s) <* rule[Any] {
              case BinaryExp(_, re1, re2) =>
                val t1 = analyzer.tipe(e1)
                val t2 = analyzer.tipe(e2)
                val t = analyzer.mergeType(t1, t2).get
                val ne1 = cast(re1, t1, t).getOrElse(re1)
                val ne2 = cast(re2, t2, t).getOrElse(re2)
                BinaryExp(op, ne1, ne2)
            }
          case _: Ge | _: Gt | _: Le | _: Lt | _: Eq | _: Neq => congruence(id, s, s) <* rule[Any] {
              case BinaryExp(_, re1, re2) =>
                val te1 = analyzer.tipe(e1)
                val te2 = analyzer.tipe(e2)
                val tm = analyzer.mergeType(te1, te2).get
                val ne1 = cast(re1, te1, tm).getOrElse(re1)
                val ne2 = cast(e2, te2, tm).getOrElse(re2)
                BinaryExp(op, ne1, ne2)
            }
        }
      case IfThenElse(e1, e2, e3) =>
        // IfThenElse: Else's type is different from Then's type.
        val te1 = analyzer.tipe(e1)
        val te2 = analyzer.tipe(e2)
        val te3 = analyzer.tipe(e3)
        congruence(s, s, s) <* rule[Any] {
          case r @ IfThenElse(re1, re2, re3) => analyzer.mergeType(te2, te3) match {
              case Some(tm) =>
                val ne1 = cast(re1, te1, SnapiBoolType()).getOrElse(re1)
                val ne2 = cast(re2, te2, tm).getOrElse(re2)
                val ne3 = cast(re3, te3, tm).getOrElse(re3)
                IfThenElse(ne1, ne2, ne3)
              case None => r
            }
        }
      case _ => fail
    }))

    val r = rewrite(s)(tree.root)
    logger.trace("ImplicitCasts:\n" + format(r))
    r
  }

}
