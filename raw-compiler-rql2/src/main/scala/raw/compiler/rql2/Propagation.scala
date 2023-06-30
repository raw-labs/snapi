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
import raw.compiler.common.source.{Exp, IdnDef, IdnExp, SourceProgram}
import raw.compiler.rql2.builtin._
import raw.compiler.rql2.source._
import raw.runtime.interpreter.{
  BoolValue,
  ByteValue,
  DateValue,
  DoubleValue,
  FloatValue,
  IntValue,
  IntervalValue,
  ListValue,
  LongValue,
  OptionValue,
  RecordValue,
  ShortValue,
  StringValue,
  TimeValue,
  TimestampValue,
  Value
}

/**
 * Rewrites the tree in order to propagate nullables and errors.
 */
class Propagation(protected val parent: Phase[SourceProgram], protected val phaseName: String)(
    protected val baseProgramContext: raw.compiler.base.ProgramContext
) extends PipelinedPhase
    with Rql2TypeUtils {

  override protected def execute(program: SourceProgram): SourceProgram = {
    propagate(program)
  }

  private def propagate(program: SourceProgram): SourceProgram = {
    val tree = new Tree(program)
    lazy val analyzer = tree.analyzer

    case class TypeAndValue(t: Type, value: Option[Value])
    case class ExpProps(
        ne: Exp,
        t: Type,
        castNeeded: Boolean,
        props: Set[Rql2TypeProperty],
        value: Option[Value] = None
    )

    // Returns the type of argument at index `idx`. If the index points to a ValueArg,
    // add the computed value
    def getTypeAndValue(entryArguments: FunAppPackageEntryArguments, args: Seq[FunAppArg], idx: Int): Option[Value] = {
      val arg: Arg = args(idx).idn match {
        case Some(i) => entryArguments.optionalArgs.collectFirst { case a if a._1 == i => a._2 }.get
        case None =>
          if (idx < entryArguments.mandatoryArgs.size) entryArguments.mandatoryArgs(idx)
          else entryArguments.varArgs(idx - (entryArguments.mandatoryArgs.size + entryArguments.optionalArgs.size))
      }
      arg match {
        case ValueArg(v, _) => Some(v)
        case _ => None
      }
    }

    lazy val s: Strategy = attempt(sometd(rulefs[Exp] {
      case binExp @ BinaryExp(op, e1, e2) => congruence(id, s, s) <* rule[Exp] {
          case BinaryExp(_, ne1, ne2) =>
            val expType = analyzer.tipe(binExp)
            op match {
              case _: BooleanOp =>
                val plainType = Rql2BoolType(Set(Rql2IsNullableTypeProperty()))
                val arguments = Vector((e1, ne1), (e2, ne2))
                val argExpProps = arguments.map {
                  case (e, ne) =>
                    val t = analyzer.tipe(e)
                    val extraProps = getProps(t) - Rql2IsNullableTypeProperty()
                    ExpProps(ne, t, castNeeded = false, extraProps)
                }
                coreFix(
                  plainType,
                  argExpProps,
                  (idns => BinaryExp(op, idns(0), idns(1))),
                  getProps(expType) - Rql2IsNullableTypeProperty()
                )
              case _ =>
                val operationProps: Set[Rql2TypeProperty] = op match {
                  case _: Div => Set(Rql2IsTryableTypeProperty())
                  case _ => Set.empty
                }
                // expType possibly got more flags because of the parameters. We remove
                // the plain operation's ones to figure out what is extra:
                val plainType = resetProps(expType, operationProps)
                val extraProps = getProps(expType) &~ operationProps
                customFix(
                  plainType,
                  Vector((e1, ne1), (e2, ne2)),
                  (idns => BinaryExp(op, idns(0), idns(1))),
                  extraProps
                )
            }
        }
      case unaryExp @ UnaryExp(op, e) => congruence(id, s) <* rule[Exp] {
          case UnaryExp(_, ne) => trivialFix(unaryExp, Vector((e, ne)), idns => UnaryExp(op, idns(0)), Set.empty)
        }
      case proj @ Proj(record, fieldName) if analyzer.tipe(record).isInstanceOf[Rql2RecordType] =>
        congruence(s, id) <* rule[Exp] {
          case Proj(nRecord, _) =>
            trivialFix(proj, Vector((record, nRecord)), idns => Proj(idns(0), fieldName), Set.empty)
        }
      case ifThenElse @ IfThenElse(e1, e2, e3) => congruence(s, s, s) <* rule[Exp] {
          case IfThenElse(ne1, ne2, ne3) =>
            val expType = analyzer.tipe(ifThenElse)
            val e2e3Props = getProps(analyzer.tipe(e2)) ++ getProps(analyzer.tipe(e3))
            // ignore e1's properties to compute the plain type (that is the exp type with e2e3 properties)
            val plainType = resetProps(expType, e2e3Props)
            // extra props are only due to e1
            val extraProps = getProps(analyzer.tipe(e1)) &~ e2e3Props
            customFix(plainType, Vector((e1, ne1)), idns => IfThenElse(idns(0), ne2, ne3), extraProps)
        }
      case fa @ FunApp(f, args) => congruence(s, s) <* rule[Exp] {
          case nfa @ FunApp(nf, nArgs) => analyzer.tipe(f) match {
              case fType: FunType =>
                // FunType means we're processing a user or library function. Its parameters are fully flagged
                // with null/try: No need to process arguments and wrap them with flatmap. The only case left
                // is if the `FunType` itself is null/try.
                if (fType.props.isEmpty) {
                  // No properties, we leave the resulting call untouched.
                  nfa
                } else {
                  // The FunType is nullable/tryable. It possibly has extra properties, to be added to the FunApp:
                  val extraProps = fType.props &~ getProps(fType.r)
                  coreFix(
                    fType.r,
                    Vector(ExpProps(nf, fType, false, fType.props)),
                    idns => FunApp(idns(0), nArgs), // once the function is safe, apply it
                    extraProps
                  )
                }
              case pt: PackageEntryType => programContext.getPackage(pt.pkgName).map(_.getEntry(pt.entName)) match {
                  case Some(entry) =>
                    val entryArgs @ FunAppPackageEntryArguments(mandatoryArgs, optionalArgs, varArgs, _) =
                      analyzer.getArgumentsForFunAppPackageEntry(fa, entry).right.get.get
                    val expArgs = args.zip(nArgs).zipWithIndex.map {
                      case ((arg, nArg), idx) =>
                        val t = analyzer.tipe(arg.e) match { case ExpType(inner) => inner; case t => t }
                        val r = for (
                          eType <- analyzer.getFunAppPackageEntryTypePartial(fa, pt, idx);
                          report <- analyzer.funParamTypeCompatibility(t, eType);
                          value = getTypeAndValue(entryArgs, args, idx);
                          expProperty =
                            if (value.nonEmpty) {
                              // when a ValueArg is found, its extra props (reminder: they are unexpected by the callee)
                              // have been cleared before passing them to the callee.
                              ExpProps(nArg.e, t, castNeeded = false, Set.empty, value)
                            } else {
                              val extraProps = report.extraProps
                              // rewritten expression:
                              // if castNeeded:
                              // - if no extraProps, just use `Cast` (it will cast nested values _in iterables_ without failure)
                              // - if extraProps, use ProtectCast,
                              // else (no castNeeded) use the expression untouched.
                              val nExp =
                                if (report.castNeeded) {
                                  if (extraProps.isEmpty) {
                                    TypePackageBuilder.Cast(report.t, nArg.e)
                                  } else if (sameModuloAttributes(report.t, t)) {
                                    nArg.e
                                  } else {
                                    TypePackageBuilder.ProtectCast(report.effective, report.t, nArg.e)
                                  }
                                } else nArg.e
                              val argType = if (extraProps.isEmpty) t else addProps(report.t, extraProps)
                              ExpProps(nExp, argType, report.castNeeded, extraProps, value)
                            }
                        ) yield expProperty
                        r.get
                    }

                    val toProcess = expArgs.filter(arg => arg.castNeeded || arg.value.isDefined)
                    // skip protect/rewrite all arguments aren't values and had the expected properties
                    if (toProcess.isEmpty) nfa
                    else {
                      val realType = entry
                        .returnTypeErrorList(fa, mandatoryArgs, optionalArgs, varArgs)
                        .right
                        .get
                      val toProtect = toProcess.filter(_.props.nonEmpty)

                      def safeApply(idns: Vector[IdnExp]) = {
                        // computes the new FunApp(f to be wrapped with protection:
                        // * only protected arguments are replaced by an IdnExp (parameter of the protecting map/flatmap)
                        // * value arguments are replaced by the computed value
                        // * non-protected arguments are left as they were
                        val (remaining, finalArgs) = expArgs.zip(nArgs).foldLeft((idns, Vector.empty[FunAppArg])) {
                          case ((idns, r), (expArg, arg)) => expArg.value match {
                              case Some(value) => (idns, r :+ FunAppArg(valueToExp(value, expArg.t), arg.idn))
                              case None =>
                                if (expArg.props.isEmpty) (idns, r :+ FunAppArg(expArg.ne, arg.idn))
                                else (idns.tail, r :+ FunAppArg(idns.head, arg.idn))
                            }
                        }
                        assert(remaining.isEmpty)
                        FunApp(nf, finalArgs)
                      }

                      coreFix(realType, toProtect, safeApply, entryArgs.extraProps)
                    }
                }
            }
        }
    }))

    def trivialFix(
        originalExp: Exp,
        arguments: Vector[(Exp, Exp)],
        apply: Vector[IdnExp] => Exp,
        effectiveOutputProps: Set[Rql2TypeProperty]
    ): Exp = {
      val argExpProps = arguments.map {
        case (e, ne) =>
          val t = analyzer.tipe(e)
          val props = getProps(t)
          ExpProps(ne, t, castNeeded = false, props)
      }
      coreFix(removeProps(analyzer.tipe(originalExp), effectiveOutputProps), argExpProps, apply, effectiveOutputProps)
    }

    def customFix(
        plainType: Type,
        arguments: Vector[(Exp, Exp)],
        apply: Vector[IdnExp] => Exp,
        extraProps: Set[Rql2TypeProperty]
    ): Exp = {
      val argExpProps = arguments.map {
        case (e, ne) =>
          val t = analyzer.tipe(e)
          val props = getProps(t)
          ExpProps(ne, t, castNeeded = false, props)
      }
      coreFix(plainType, argExpProps, apply, extraProps)
    }

    def coreFix(
        rootType: Type,
        argExpProps: Vector[ExpProps],
        apply: Vector[IdnExp] => Exp,
        extraProps: Set[Rql2TypeProperty]
    ): Exp = {
      val idns = argExpProps.map(_ => IdnDef())
      val boxedBody = {
        val root = apply(idns.map(idnDef => IdnExp(idnDef)))
        val outProps = getProps(rootType)
        val effectiveOutputProps = outProps ++ extraProps
        if (outProps == effectiveOutputProps) {
          // The operation happens to match the expected type, no transformation is needed after it's computed
          root
        } else {
          if (outProps.isEmpty) {
            // the operation isn't nullable/tryable. Wrap it with the needed nodes to match.
            val withNullWrapping =
              if (effectiveOutputProps.contains(Rql2IsNullableTypeProperty())) NullablePackageBuilder.Build(root)
              else root
            val withTryWrapping =
              if (effectiveOutputProps.contains(Rql2IsTryableTypeProperty()))
                SuccessPackageBuilder.Build(withNullWrapping)
              else withNullWrapping
            withTryWrapping
          } else if (outProps == Set(Rql2IsNullableTypeProperty())) {
            // e.g. nullable and/or called with errors
            assert(
              effectiveOutputProps.contains(Rql2IsTryableTypeProperty())
            ) // otherwise it should have been equal above
            SuccessPackageBuilder.Build(root)
          } else if (outProps == Set(Rql2IsTryableTypeProperty())) {
            // e.g. division.
            if (outProps.contains(Rql2IsNullableTypeProperty())) {
              // special case: the operation is a plain tryable one, but needs to be computed as a nullable too.
              // We cannot box it in a nullable.build since it would be a null(try) instead of try(null). We
              // compute the operation (as a try) and box its results as a nullable with Transform.
              val innerValue = IdnDef()
              TryPackageBuilder.Transform(
                root,
                FunAbs(
                  FunProto(
                    Vector(FunParam(innerValue, None, None)),
                    None,
                    FunBody(NullablePackageBuilder.Build(IdnExp(innerValue)))
                  )
                )
              )
            } else root
          } else ???
        }
      }
      val outputType = addProps(rootType, extraProps)
      argExpProps.zip(idns).foldRight(boxedBody: Exp) {
        case ((p, idn), body) =>
          if (p.props.nonEmpty) {
            // the map/flatmap function applies to an object of type p.t with the
            // extra flags removed (they are handled by map/flatmap).
            val innerType = removeProps(p.t, p.props)
            val funParam = FunParam(idn, Some(innerType), None)
            // if the map/flatmap is applied to a 'undefined', the function won't be executed. We replace the
            // body by an unspecified zero of the body type.
            val nextBody = if (innerType == Rql2UndefinedType()) TypePackageBuilder.Empty(outputType) else body
            val fun = FunAbs(
              FunProto(
                Vector(funParam),
                Some(outputType),
                FunBody(nextBody)
              )
            )
            NullableTryablePackageBuilder.FlatMap(p.ne, fun)
          } else {
            assert(p.value.isEmpty)
            Let(Vector(LetBind(p.ne, idn, Some(p.t))), body)
          }
      }

    }

    val r = rewrite(s)(tree.root)
    logger.debug("Propagation:\n" + format(r))
    r
  }

  private def valueToExp(value: Value, t: Type): Exp = value match {
    case ByteValue(v) => ByteConst(v.toString)
    case ShortValue(v) => ShortConst(v.toString)
    case IntValue(v) => IntConst(v.toString)
    case LongValue(v) => LongConst(v.toString)
    case FloatValue(v) => FloatConst(v.toString)
    case DoubleValue(v) => DoubleConst(v.toString)
    case StringValue(v) => StringConst(v)
    case BoolValue(v) => BoolConst(v)
    case OptionValue(option) =>
      val innerType = removeProp(t, Rql2IsNullableTypeProperty())
      option
        .map(v => valueToExp(v, innerType))
        .map(NullablePackageBuilder.Build(_))
        .getOrElse(NullablePackageBuilder.Empty(innerType))
    case RecordValue(r) =>
      val Rql2RecordType(atts, _) = t
      val fields = r.zip(atts).map { case (v, att) => att.idn -> valueToExp(v, att.tipe) }
      RecordPackageBuilder.Build(fields.toVector)
    case ListValue(v) =>
      val Rql2ListType(innerType, _) = t
      ListPackageBuilder.Build(v.map(x => valueToExp(x, innerType)): _*)
    case DateValue(v) => DatePackageBuilder.FromLocalDate(v)
    case TimeValue(v) => TimePackageBuilder.FromLocalTime(v)
    case TimestampValue(v) => TimestampPackageBuilder.FromLocalDateTime(v)
    case IntervalValue(
          years,
          month,
          weeks,
          days,
          hours,
          minutes,
          seconds,
          millis
        ) => IntervalPackageBuilder.FromRawInterval(years, month, weeks, days, hours, minutes, seconds, millis)
  }

  private def sameModuloAttributes(t1: Type, t2: Type) = {
    val plainType1 = resetProps(t1, Set.empty)
    val plainType2 = resetProps(t2, Set.empty)
    // root types are the same
    val sameRootType = plainType1 == plainType2
    // nothing but extra properties (no need to cast with new properties)
    val extraPropsAreSubset = getProps(t1).subsetOf(getProps(t2))
    sameRootType && extraPropsAreSubset
  }

}
