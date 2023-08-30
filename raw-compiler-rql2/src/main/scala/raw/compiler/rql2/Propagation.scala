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
import raw.compiler.base.{Counter, Phase}
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

import scala.collection.immutable

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

    val customPackageExtensions = collection.mutable.Map.empty[(PackageEntryType, Vector[Type]), Rql2Method]
    def registerCustomPackageExtension(entry: PackageEntryType, argTypes: Vector[Type], f: FunProto): IdnExp = {
      val m = customPackageExtensions.get((entry, argTypes))
      m match {
        case Some(method) => IdnExp(method.i)
        case None =>
          val idn = IdnDef(Counter.next(entry.entName))
          customPackageExtensions((entry, argTypes)) = Rql2Method(f, idn)
          IdnExp(idn)
      }
    }

    val unaryExpMethods = collection.mutable.Map.empty[(Type, UnaryOp), Rql2Method]
    def registerUnaryProjectionMethod(
        uExp: UnaryExp,
        idn: IdnDef,
        output: Type,
        body: Exp
    ): IdnExp = {
      val eType = analyzer.tipe(uExp.exp)
      val m = unaryExpMethods.get((eType, uExp.unaryOp))
      m match {
        case Some(method) => IdnExp(method.i)
        case None =>
          val proto = FunProto(
            Vector(FunParam(idn, Some(eType), None)),
            Some(output),
            FunBody(body)
          )
          val method = Rql2Method(proto, IdnDef())
          unaryExpMethods((eType, uExp.unaryOp)) = method
          IdnExp(method.i)
      }
    }

    val binaryExpMethods = collection.mutable.Map.empty[(Type, Type, BinaryOp), Rql2Method]
    def registerBinaryProjectionMethod(
        binExp: BinaryExp,
        left: IdnDef,
        right: IdnDef,
        output: Type,
        body: Exp
    ): IdnExp = {
      val leftType = analyzer.tipe(binExp.left)
      val rightType = analyzer.tipe(binExp.right)
      val m = binaryExpMethods.get((leftType, rightType, binExp.binaryOp))
      m match {
        case Some(method) => IdnExp(method.i)
        case None =>
          val proto = FunProto(
            Vector(FunParam(left, Some(leftType), None), FunParam(right, Some(rightType), None)),
            Some(output),
            FunBody(body)
          )
          val method = Rql2Method(proto, IdnDef())
          binaryExpMethods((leftType, rightType, binExp.binaryOp)) = method
          IdnExp(method.i)
      }
    }

    val projectionMethods = collection.mutable.Map.empty[(Type, Type, String), Rql2Method]
    def registerProjectionMethod(proj: Proj, field: String): IdnExp = {
      val rType = analyzer.tipe(proj.e)
      val output = analyzer.tipe(proj)
      val m = projectionMethods.get((rType, output, field))
      m match {
        case Some(method) => IdnExp(method.i)
        case None =>
          val arg = IdnDef()
          val code = trivialFix(proj, Vector((proj.e, IdnExp(arg))), idns => Proj(idns(0), field), Set.empty)
          val proto = FunProto(Vector(FunParam(arg, Some(rType), None)), Some(output), FunBody(code))
          val f = IdnDef(Counter.next("proj"))
          val method = Rql2Method(proto, f)
          projectionMethods((rType, output, field)) = method
          IdnExp(f)
      }
    }

    lazy val s: Strategy = attempt(sometd(rulefs[Exp] {
      case binExp @ BinaryExp(op, e1, e2) => congruence(id, s, s) <* rule[Exp] {
          case BinaryExp(_, ne1, ne2) =>
            val expType = analyzer.tipe(binExp)
            op match {
              case _: BooleanOp =>
                val plainType = Rql2BoolType(Set(Rql2IsNullableTypeProperty()))
                val arguments = Vector((e1, ne1, IdnDef()), (e2, ne2, IdnDef()))
                val argExpProps = arguments.map {
                  case (e, ne, idn) =>
                    val t = analyzer.tipe(e)
                    val extraProps = getProps(t) - Rql2IsNullableTypeProperty()
                    ExpProps(IdnExp(idn), t, castNeeded = false, extraProps)
                }
                val body = coreFix(
                  plainType,
                  argExpProps,
                  (idns => BinaryExp(op, idns(0), idns(1))),
                  getProps(expType) - Rql2IsNullableTypeProperty()
                )
                val f = registerBinaryProjectionMethod(binExp, arguments(0)._3, arguments(1)._3, expType, body)
                FunApp(f, Vector(FunAppArg(arguments(0)._2, None), FunAppArg(arguments(1)._2, None)))
              case _ =>
                val operationProps: Set[Rql2TypeProperty] = op match {
                  case _: Div => Set(Rql2IsTryableTypeProperty())
                  case _ => Set.empty
                }
                // expType possibly got more flags because of the parameters. We remove
                // the plain operation's ones to figure out what is extra:
                val plainType = resetProps(expType, operationProps)
                val extraProps = getProps(expType) &~ operationProps
                val arguments = Vector((e1, ne1, IdnDef()), (e2, ne2, IdnDef()))
                val body = customFix(
                  plainType,
                  arguments.map(x => (x._1, IdnExp(x._3))),
                  (idns => BinaryExp(op, idns(0), idns(1))),
                  extraProps
                )
                val f = registerBinaryProjectionMethod(binExp, arguments(0)._3, arguments(1)._3, expType, body)
                FunApp(f, Vector(FunAppArg(arguments(0)._2, None), FunAppArg(arguments(1)._2, None)))
            }
        }
      case unaryExp @ UnaryExp(op, e) => congruence(id, s) <* rule[Exp] {
          case UnaryExp(_, ne) =>
            val idn = IdnDef()
            val body = trivialFix(unaryExp, Vector((e, IdnExp(idn))), idns => UnaryExp(op, idns(0)), Set.empty)
            val f = registerUnaryProjectionMethod(unaryExp, idn, analyzer.tipe(unaryExp), body)
            FunApp(f, Vector(FunAppArg(ne, None)))
        }
      case proj @ Proj(record, fieldName) if analyzer.tipe(record).isInstanceOf[Rql2RecordType] =>
        congruence(s, id) <* rule[Exp] {
          case nProj @ Proj(nRecord, _) =>
            if (getProps(analyzer.tipe(record)).isEmpty) nProj
            else {
              val f = registerProjectionMethod(proj, fieldName)
              FunApp(f, Vector(FunAppArg(nRecord, None)))
            }
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
                    // for package extensions that have the correct style, we'll forge a FunAbs that takes exactly the
                    // actual types as parameters and performs the various casts, flatMaps, etc., to eventually call the
                    // original package extension. If the function is reused (from query to query), it's like having a
                    // custom version of the package extension.

                    // goal is Sin$1($2: int) = Math.Sin(Double.From($2))
                    //         Sin$5($2: tryint) = FlatMap($2, $3 -> Success(Math.Sin(Double.From($3))))
                    // etc.

                    // That trick can be done even if a package extension has variable arguments, because the custom
                    // function is indexed by argument types (there will just be more or less)
                    // It can be done if arguments are ValueArg, because we'll inline those in the body, but can index the
                    // function by the other argument types and the inlined values

                    // The FunAbs has as many parameters as there are non-value-parameters.
                    val wrappingParameters = collection.mutable.ArrayBuffer.empty[FunParam]

                    // variables appear in place of original arguments + protection
                    val expArgs = args.zipWithIndex.map {
                      case (arg, idx) =>
                        // actual type of the argument
                        val aType = analyzer.tipe(arg.e)
                        val isType = aType.isInstanceOf[ExpType]
                        val actualArgType = aType match { case ExpType(inner) => inner; case t => t }
                        val r = for (
                          expectedArgType <- analyzer.getFunAppPackageEntryTypePartial(fa, pt, idx);
                          report <- analyzer.funParamTypeCompatibility(actualArgType, expectedArgType);
                          maybeValue = getTypeAndValue(entryArgs, args, idx);
                          expProperty =
                            if (maybeValue.nonEmpty) {
                              // when a ValueArg is found, its extra props (reminder: they are unexpected by the callee)
                              // have been cleared before passing them to the callee. No need to process
                              ExpProps(
                                valueToExp(maybeValue.get, expectedArgType),
                                actualArgType,
                                castNeeded = false,
                                Set.empty,
                                maybeValue
                              )
                            } else if (isType) {
                              // typeExp
                              ExpProps(
                                arg.e,
                                actualArgType,
                                castNeeded = false,
                                Set.empty,
                                None
                              )
                            } else {
                              // a regular exp-argument, let's create a parameter for the coming custom wrapping function.
                              val param = FunParam(IdnDef(), Some(actualArgType), None)
                              wrappingParameters += param
                              val bodyArg = IdnExp(param.i) // what appears in the body

                              val extraProps = report.extraProps
                              // rewritten expression:
                              // if castNeeded:
                              // - if no extraProps, just use `Cast` (it will cast nested values _in iterables_ without failure)
                              // - if extraProps, use ProtectCast,
                              // else (no castNeeded) use the expression untouched.
                              val nExp =
                                if (report.castNeeded) {
                                  if (extraProps.isEmpty) {
                                    TypePackageBuilder.Cast(report.t, bodyArg)
                                  } else if (sameModuloAttributes(report.t, actualArgType)) {
                                    bodyArg
                                  } else {
                                    TypePackageBuilder.ProtectCast(report.effective, report.t, bodyArg)
                                  }
                                } else bodyArg
                              val argType = if (extraProps.isEmpty) actualArgType else addProps(report.t, extraProps)
                              ExpProps(nExp, argType, report.castNeeded, extraProps, maybeValue)
                            }
                        ) yield expProperty
                        r.get
                    }

                    // arguments to process (that need cast or flatMap, or values that need to be replaced by a resolved tree)
                    val toProcess = expArgs.filter(arg => arg.castNeeded || arg.value.isDefined)
                    // skip protect/rewrite all arguments aren't values and had the expected properties
                    if (toProcess.isEmpty) nfa
                    else {
                      val returnType = entry
                        .returnTypeErrorList(fa, mandatoryArgs, optionalArgs, varArgs)
                        .right
                        .get
                      val toProtect = toProcess.filter(_.props.nonEmpty)

                      def safeApply(idns: Vector[IdnExp]) = {
                        val (remaining, finalArgs) = expArgs.zip(args).foldLeft((idns, Vector.empty[FunAppArg])) {
                          case ((idns, r), (expArg, arg)) =>
                            if (expArg.props.isEmpty) (idns, r :+ FunAppArg(expArg.ne, arg.idn))
                            else (idns.tail, r :+ FunAppArg(idns.head, arg.idn))
                        }
                        assert(remaining.isEmpty)
                        FunApp(f, finalArgs)
                      }

                      val customF = {
                        val body = coreFix(returnType, toProtect, safeApply, entryArgs.extraProps)
                        registerCustomPackageExtension(
                          pt,
                          expArgs.map(_.t),
                          FunProto(wrappingParameters.to, None, FunBody(body))
                        )
                      }
                      val finalArgs = expArgs
                        .zip(nArgs)
                        .zip(args)
                        .filterNot(x => x._1._1.value.isDefined || analyzer.tipe(x._2.e).isInstanceOf[ExpType])
                        .map {
                          case ((_, nArg), arg) =>
                            val finalArg = analyzer.tipe(arg.e) match {
                              case fType: FunType => TypePackageBuilder.Cast(fType, nArg.e)
                              case _ => nArg.e
                            }
                            FunAppArg(finalArg, None)
                        }
                      FunApp(customF, finalArgs)
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

    val mainCode = rewrite(s)(tree.root).asInstanceOf[Rql2Program]
    val protectionMethods: Vector[Rql2Method] =
      (projectionMethods.values ++ binaryExpMethods.values ++ unaryExpMethods.values ++ customPackageExtensions.values).toVector
    val finalCode =
      if (protectionMethods.isEmpty) mainCode
      else {
        // prepend the xform functions
        val newMethods = protectionMethods ++ mainCode.methods
        Rql2Program(newMethods, mainCode.me)
      }
    logger.debug("Propagation:\n" + format(finalCode))
    finalCode
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
