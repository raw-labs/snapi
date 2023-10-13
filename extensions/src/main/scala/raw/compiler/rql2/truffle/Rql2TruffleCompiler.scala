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

package raw.compiler.rql2.truffle

import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.interop.InteropLibrary
import org.graalvm.polyglot.Context
import raw.compiler.base.source.{BaseNode, Type}
import raw.compiler.base.{BaseTree, CompilerContext}
import raw.compiler.common.source._
import raw.compiler.rql2.Rql2TypeUtils.removeProp
import raw.compiler.rql2._
import raw.compiler.rql2.builtin._
import raw.compiler.rql2.source._
import raw.compiler.snapi.truffle.compiler.{TruffleEmit, TruffleEntrypoint}
import raw.compiler.truffle.TruffleCompiler
import raw.compiler.{base, CompilerException, ErrorMessage}
import raw.runtime._
import raw.runtime.interpreter._
import raw.runtime.truffle._
import raw.runtime.truffle.runtime.generator.GeneratorLibrary
import raw.runtime.truffle.runtime.iterable.IterableLibrary
import raw.runtime.truffle.runtime.list.ListLibrary
import raw.runtime.truffle.runtime.option.OptionLibrary
import raw.runtime.truffle.runtime.or.OrObject
import raw.runtime.truffle.runtime.primitives._
import raw.runtime.truffle.runtime.tryable.TryableLibrary

import scala.collection.mutable

class Rql2TruffleCompiler(implicit compilerContext: CompilerContext)
    extends Compiler
    with TruffleCompiler[SourceNode, SourceProgram, Exp] {

  override def supportsCaching: Boolean = false

  override protected def doEval(
      tree: BaseTree[SourceNode, SourceProgram, Exp]
  )(implicit programContext: raw.compiler.base.ProgramContext): Value = {
    // FIXME (msb): This should pretty print the tree, then call Context.eval.
    //              We shouldn't have access to doCompile and hence not do be rawLanguageAsNull as null, which is broken!
    val context = Context
      .newBuilder(RawLanguage.ID)
      .environment("RAW_USER", programContext.runtimeContext.environment.user.uid.toString)
      .environment("RAW_TRACE_ID", programContext.runtimeContext.environment.user.uid.toString)
      .environment("RAW_SCOPES", programContext.runtimeContext.environment.scopes.mkString(","))
      .build()
    context.initialize(RawLanguage.ID)
    context.enter()
    try {
      val entrypoint = doCompile(tree, null).asInstanceOf[TruffleEntrypoint]
      val target = Truffle.getRuntime.createDirectCallNode(entrypoint.target().getCallTarget)
      val res = target.call()
      convertAnyToValue(res, tree.rootType.get)
    } finally {
      // We explicitly created and then entered the context during code emission.
      // Now we explicitly leave and close the context.
      context.leave()
      context.close()
    }
  }

  override def parseAndValidate(source: String, maybeDecl: Option[String])(
      implicit programContext: base.ProgramContext
  ): Either[List[ErrorMessage], SourceProgram] = {
    buildInputTree(source).right.flatMap { tree =>
      val Rql2Program(methods, me) = tree.root

      maybeDecl match {
        case None =>
          if (programContext.runtimeContext.maybeArguments.nonEmpty) {
            // arguments are given while no method to execute is
            // provided.
            throw new CompilerException("arguments found")
          }

          if (me.isEmpty) {
            // no method, but also no bottom expression. This isn't
            // executable.
            throw new CompilerException("no expression found")
          }

          val e = me.get
          Right(Rql2Program(methods, Some(e)))
        case Some(decl) => tree.description.expDecls.get(decl) match {
            case Some(descriptions) =>
              if (descriptions.size > 1) throw new CompilerException("multiple declarations found")
              val description = descriptions.head
              val params = description.params.get
              val mandatory = params.collect { case p if p.required => p.idn }.toSet
              val args = programContext.runtimeContext.maybeArguments.get
              val programArgs = args.map {
                case (k, v) =>
                  val e = v match {
                    case _: ParamNull => NullConst()
                    case _: ParamByte => EnvironmentPackageBuilder.Parameter(Rql2ByteType(), StringConst(k))
                    case _: ParamShort => EnvironmentPackageBuilder.Parameter(Rql2ShortType(), StringConst(k))
                    case _: ParamInt => EnvironmentPackageBuilder.Parameter(Rql2IntType(), StringConst(k))
                    case _: ParamLong => EnvironmentPackageBuilder.Parameter(Rql2LongType(), StringConst(k))
                    case _: ParamFloat => EnvironmentPackageBuilder.Parameter(Rql2FloatType(), StringConst(k))
                    case _: ParamDouble => EnvironmentPackageBuilder.Parameter(Rql2DoubleType(), StringConst(k))
                    case _: ParamDecimal => EnvironmentPackageBuilder.Parameter(Rql2DecimalType(), StringConst(k))
                    case _: ParamBool => EnvironmentPackageBuilder.Parameter(Rql2BoolType(), StringConst(k))
                    case _: ParamString => EnvironmentPackageBuilder.Parameter(Rql2StringType(), StringConst(k))
                    case _: ParamDate => EnvironmentPackageBuilder.Parameter(Rql2DateType(), StringConst(k))
                    case _: ParamTime => EnvironmentPackageBuilder.Parameter(Rql2TimeType(), StringConst(k))
                    case _: ParamTimestamp => EnvironmentPackageBuilder.Parameter(Rql2TimestampType(), StringConst(k))
                    case _: ParamInterval => EnvironmentPackageBuilder.Parameter(Rql2IntervalType(), StringConst(k))
                  }
                  // Build the argument node.
                  // If argument is mandatory, then according to RQL2's language definition, the argument is not named.
                  // However, if the argument os optional, the name is added.
                  FunAppArg(e, if (mandatory.contains(k)) None else Some(k))
              }
              Right(Rql2Program(methods, Some(FunApp(IdnExp(IdnUse(decl)), programArgs.toVector))))
            case None => throw new CompilerException("declaration not found")
          }
      }
    }
  }

  private def convertAnyToValue(v: Any, t: Type): Value = t match {
    case t: Rql2TypeWithProperties if t.props.contains(Rql2IsTryableTypeProperty()) =>
      val triables = TryableLibrary.getFactory.getUncached(v)
      if (triables.isSuccess(v)) {
        TryValue(Right(convertAnyToValue(triables.success(v), removeProp(t, Rql2IsTryableTypeProperty()))))
      } else {
        TryValue(Left(triables.failure(v)))
      }
    case t: Rql2TypeWithProperties if t.props.contains(Rql2IsNullableTypeProperty()) =>
      val options = OptionLibrary.getFactory.getUncached(v)
      if (options.isDefined(v)) {
        OptionValue(Some(convertAnyToValue(options.get(v), removeProp(t, Rql2IsNullableTypeProperty()))))
      } else {
        OptionValue(None)
      }
    case _: Rql2BoolType => BoolValue(v.asInstanceOf[java.lang.Boolean])
    case _: Rql2StringType => StringValue(v.asInstanceOf[java.lang.String])
    case _: Rql2BinaryType =>
      val v1 = v.asInstanceOf[BinaryObject]
      val vs = mutable.ArrayBuffer[Byte]()
      for (i <- 0 until java.lang.reflect.Array.getLength(v1.getBytes)) {
        vs.append(java.lang.reflect.Array.getByte(v1, i))
      }
      BinaryValue(vs.toArray)
    case _: Rql2ByteType => ByteValue(v.asInstanceOf[java.lang.Byte])
    case _: Rql2ShortType => ShortValue(v.asInstanceOf[java.lang.Short])
    case _: Rql2IntType => IntValue(v.asInstanceOf[java.lang.Integer])
    case _: Rql2LongType => LongValue(v.asInstanceOf[java.lang.Long])
    case _: Rql2FloatType => FloatValue(v.asInstanceOf[java.lang.Float])
    case _: Rql2DoubleType => DoubleValue(v.asInstanceOf[java.lang.Double])
    case _: Rql2DecimalType => DecimalValue(v.asInstanceOf[DecimalObject].getBigDecimal)
    case _: Rql2LocationType => LocationValue(v.asInstanceOf[LocationObject].getLocationDescription)
    case _: Rql2DateType => DateValue(v.asInstanceOf[DateObject].getDate)
    case _: Rql2TimeType => TimeValue(v.asInstanceOf[TimeObject].getTime)
    case _: Rql2TimestampType => TimestampValue(v.asInstanceOf[TimestampObject].getTimestamp)
    case _: Rql2IntervalType =>
      val v1 = v.asInstanceOf[IntervalObject]
      IntervalValue(
        v1.getYears,
        v1.getMonths,
        v1.getWeeks,
        v1.getDays,
        v1.getHours,
        v1.getMinutes,
        v1.getSeconds,
        v1.getMillis
      )
    //      case _: Rql2RegexType => RegexValue(v.asInstanceOf[scala.util.matching.Regex]) // a.z I think we don't have it in RQL2, yes?
    case _: Rql2UndefinedType => NothingValue()
    case Rql2RecordType(atts, _) =>
      val records = InteropLibrary.getFactory.getUncached(v)
      val keys = records.getMembers(v)
      val keysLib = InteropLibrary.getFactory.getUncached(keys)
      RecordValue(atts.zipWithIndex.map {
        case (att, idx) => convertAnyToValue(
            records.readMember(v, keysLib.readArrayElement(keys, idx).asInstanceOf[String]),
            att.tipe
          )
      })
    case Rql2ListType(t1, _) =>
      val lists = ListLibrary.getFactory.getUncached(v)
      val values = for (i <- 0 until lists.size(v).asInstanceOf[Int]) yield lists.get(v, i)
      ListValue(values.map(v1 => convertAnyToValue(v1, t1)))
    case Rql2IterableType(t1, _) =>
      val iterables = IterableLibrary.getFactory.getUncached(v)
      val generator = iterables.getGenerator(v)
      val generators = GeneratorLibrary.getFactory.getUncached(generator)
      generators.init(generator)
      val vs = mutable.ArrayBuffer[Any]()
      while (generators.hasNext(generator)) {
        vs.append(generators.next(generator))
      }
      generators.close(generator)
      IterableValue(vs.map(v1 => convertAnyToValue(v1, t1)))
    case Rql2OrType(ts, _) =>
      val orObj = v.asInstanceOf[OrObject]
      OrValue(Seq(convertAnyToValue(orObj.getValue, ts(orObj.getIndex))))
  }

  // FIXME (msb): Overridding this here shows me inheritance isn't right. parent Compiler implementing "too much"!!!
  override def prettyPrintOutput(node: BaseNode): String = {
    SourcePrettyPrinter.format(node)
  }

  override def doEmit(signature: String, program: SourceProgram, rawLanguageAsAny: Any)(
      implicit programContext: raw.compiler.base.ProgramContext
  ): Entrypoint = {

    val rawLanguage = rawLanguageAsAny.asInstanceOf[RawLanguage]

    logger.debug(s"Output final program is:\n${prettyPrintOutput(program)}")

    TruffleEmit.doEmit(program, rawLanguage, programContext.asInstanceOf[ProgramContext])

  }
}
