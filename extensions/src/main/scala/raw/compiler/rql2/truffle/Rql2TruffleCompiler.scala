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

import com.oracle.truffle.api.frame.{FrameDescriptor, FrameSlotKind}
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

    // should just call the compiler service NEW method to do doEval

    ???
//    // FIXME (msb): This should pretty print the tree, then call Context.eval.
//    //              We shouldn't have access to doCompile and hence not do be rawLanguageAsNull as null, which is broken!
//    val context = Context
//      .newBuilder(RawLanguage.ID)
//      .environment("RAW_USER", programContext.runtimeContext.environment.user.uid.toString)
//      .environment("RAW_TRACE_ID", programContext.runtimeContext.environment.user.uid.toString)
//      .environment("RAW_SCOPES", programContext.runtimeContext.environment.scopes.mkString(","))
//      .build()
//    context.initialize(RawLanguage.ID)
//    context.enter()
//    try {
//      val TruffleEntrypoint(_, node, _) = doCompile(tree, null).asInstanceOf[TruffleEntrypoint]
//      val target = Truffle.getRuntime.createDirectCallNode(node.getCallTarget)
//      val res = target.call()
//      convertAnyToValue(res, tree.rootType.get)
//    } finally {
//      // We explicitly created and then entered the context during code emission.
//      // Now we explicitly leave and close the context.
//      context.leave()
//      context.close()
//    }
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
