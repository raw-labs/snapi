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

import org.graalvm.polyglot.{Context, PolyglotAccess}
import raw.compiler.api.{
  AutoCompleteResponse,
  CompilationFailure,
  CompilationResponse,
  CompilationSuccess,
  CompilerService,
  CompilerServiceException,
  ExecutionResponse,
  ExecutionRuntimeFailure,
  ExecutionSuccess,
  ExecutionValidationFailure,
  FormatCodeResponse,
  GetProgramDescriptionFailure,
  GetProgramDescriptionResponse,
  GetProgramDescriptionSuccess,
  GetTypeFailure,
  GetTypeResponse,
  GetTypeSuccess,
  GoToDefinitionResponse,
  HoverResponse,
  ParseFailure,
  ParseResponse,
  ParseSuccess,
  ParseTypeFailure,
  ParseTypeResponse,
  ParseTypeSuccess,
  Pos,
  RenameResponse,
  ValidateResponse
}
import raw.compiler.common.{CommonCompilerProvider, Compiler}
import raw.runtime.{
  ParamBool,
  ParamByte,
  ParamDate,
  ParamDecimal,
  ParamDouble,
  ParamFloat,
  ParamInt,
  ParamInterval,
  ParamLong,
  ParamNull,
  ParamShort,
  ParamString,
  ParamTime,
  ParamTimestamp,
  ParamValue,
  ProgramEnvironment,
  RuntimeContext
}
import raw.runtime.truffle.RawLanguage
import org.graalvm.polyglot.Source
import raw.compiler.CompilerParserException
import raw.compiler.base.ProgramContext
import raw.compiler.base.source.BaseNode
import raw.compiler.scala2.Scala2CompilerContext
import raw.creds.api.CredentialsServiceProvider
import raw.inferrer.api.InferrerServiceProvider
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException
import raw.runtime.truffle.runtime.primitives.{DateObject, DecimalObject, IntervalObject, TimeObject, TimestampObject}
import raw.sources.api.SourceContext
import raw.utils.{AuthenticatedUser, RawConcurrentHashMap, RawException, RawSettings}

import java.io.OutputStream
import scala.util.control.NonFatal

class Rql2TruffleCompilerService(maybeClassLoader: Option[ClassLoader] = None)(implicit settings: RawSettings)
    extends CompilerService {

  private val credentials = CredentialsServiceProvider(maybeClassLoader)

  // Map of users to compilers.
  private val compilerCaches = new RawConcurrentHashMap[AuthenticatedUser, Compiler]

  private def getCompiler(user: AuthenticatedUser): Compiler = {
    compilerCaches.getOrElseUpdate(user, createCompiler(user, "rql2-truffle"))
  }

  private def createCompiler(user: AuthenticatedUser, language: String): Compiler = {
    // Initialize source context
    implicit val sourceContext = new SourceContext(user, credentials, settings)

    // Initialize inferrer
    val inferrer = InferrerServiceProvider(maybeClassLoader)

    // Initialize compiler context
    val compilerContext = new Scala2CompilerContext(language, user, sourceContext, inferrer, maybeClassLoader)
    try {
      // Initialize compiler. Default language, if not specified is 'rql2'.
      CommonCompilerProvider(language, maybeClassLoader)(compilerContext)
    } catch {
      case NonFatal(ex) =>
        // To not leave hanging inferrer services.
        // This would make tests fail in the afterAll when checking for running services
        inferrer.stop()
        throw ex
    }
  }

  private def getProgramContext(
      compiler: Compiler,
      code: String,
      environment: ProgramEnvironment
  ): ProgramContext = {
    val runtimeContext = getRuntimeContext(compiler, environment)
    compiler.getProgramContext(runtimeContext)
  }

  private def getRuntimeContext(
      compiler: Compiler,
      environment: ProgramEnvironment
  ): RuntimeContext = {
    val sourceContext = compiler.compilerContext.sourceContext
    new RuntimeContext(
      sourceContext,
      settings,
      environment
    )
  }

  override def prettyPrint(node: BaseNode, user: AuthenticatedUser): String = {
    val compiler = getCompiler(user)
    compiler.prettyPrint(node)
  }

  override def parseType(tipe: String, user: AuthenticatedUser): ParseTypeResponse = {
    val compiler = getCompiler(user)
    compiler.parseType(tipe) match {
      case Some(t) => ParseTypeSuccess(t)
      case None => ParseTypeFailure("could not parse type")
    }
  }

  override def parse(source: String, environment: ProgramEnvironment): ParseResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, environment)
    try {
      val r = compiler.parse(source)(programContext)
      ParseSuccess(r)
    } catch {
      case ex: CompilerParserException => ParseFailure(ex.getMessage, ex.position)
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def getType(
      source: String,
      environment: ProgramEnvironment
  ): GetTypeResponse = {
    withTruffleContext(
      environment,
      _ => {
        val compiler = getCompiler(environment.user)
        val programContext = getProgramContext(compiler, source, environment)
        try {
          compiler.getType(source)(programContext) match {
            case Left(errs) => GetTypeFailure(errs)
            case Right(t) => GetTypeSuccess(t)
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def getProgramDescription(
      source: String,
      environment: ProgramEnvironment
  ): GetProgramDescriptionResponse = {
    withTruffleContext(
      environment,
      _ => {
        val compiler = getCompiler(environment.user)
        val programContext = getProgramContext(compiler, source, environment)
        try {
          compiler.getProgramDescription(source)(programContext) match {
            case Left(errs) => GetProgramDescriptionFailure(errs)
            case Right(desc) => GetProgramDescriptionSuccess(desc)
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def compile(
      source: String,
      environment: ProgramEnvironment,
      ref: Any
  ): CompilationResponse = {
    // We do NOT create a TruffleContext here, because this method is only used from the RawLanguage,
    // where there is already a TruffleContext created.
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, environment)
    try {
      compiler.compile(source, ref)(programContext) match {
        case Left(errs) => CompilationFailure(errs)
        case Right(program) => CompilationSuccess(program)
      }
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream
  ): ExecutionResponse = {

    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, environment)

    try {
      val ctx = buildTruffleContext(environment, maybeOutputStream = Some(outputStream))

      ctx.initialize(RawLanguage.ID)
      ctx.enter()
      //
      //      try {
      //        ctx.parse(source)
      //      }
      //      this is ALSO wrong
      //
      //
      //      should be what?
      //
      //      ctx.
      //
      //        fix this!
      //
      //      should be ctx.parse
      //      get Value
      //        then execute Value
      //
      //      BTW, if this is WRONG, so it doEval!

      try {
        compiler
          .parseAndValidate(source, maybeDecl)(programContext)
          .right
          .flatMap { program =>
            compiler.compile(program)(programContext).right.map { entrypoint =>
              //ctx.p
              //              val v = entrypoint.asInstanceOf[TruffleEntrypoint].node;
              //              v.

              val src = Source.newBuilder("rql", source, "<stdin>").build
              ctx.eval(src)

              //              val target =
              //                Truffle.getRuntime.createDirectCallNode(entrypoint.asInstanceOf[TruffleEntrypoint].node.getCallTarget)
              //              target.call()
              outputStream.flush()
              ExecutionSuccess
            }
          }
          .fold(
            errs => ExecutionValidationFailure(errs),
            res => res
          )
      } finally {
        ctx.leave()
        ctx.close()
      }
    } catch {
      case ex: RawTruffleRuntimeException => ExecutionRuntimeFailure(ex.getMessage)
      case ex: RawException => ExecutionRuntimeFailure(ex.getMessage)
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def formatCode(
      source: String,
      environment: ProgramEnvironment,
      maybeIndent: Option[Int],
      maybeWidth: Option[Int]
  ): FormatCodeResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, environment)
    try {
      compiler.formatCode(source, environment, maybeIndent, maybeWidth)(programContext)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def dotAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): AutoCompleteResponse = {
    withTruffleContext(
      environment,
      _ => {
        val compiler = getCompiler(environment.user)
        val programContext = getProgramContext(compiler, source, environment)
        try {
          compiler.dotAutoComplete(source, environment, position)(programContext)
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse = {
    withTruffleContext(
      environment,
      _ => {
        val compiler = getCompiler(environment.user)
        val programContext = getProgramContext(compiler, source, environment)
        try {
          compiler.wordAutoComplete(source, environment, prefix, position)(programContext)
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    withTruffleContext(
      environment,
      _ => {
        val compiler = getCompiler(environment.user)
        val programContext = getProgramContext(compiler, source, environment)
        try {
          compiler.hover(source, environment, position)(programContext)
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse = {
    withTruffleContext(
      environment,
      _ => {
        val compiler = getCompiler(environment.user)
        val programContext = getProgramContext(compiler, source, environment)
        try {
          compiler.rename(source, environment, position)(programContext)
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def goToDefinition(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): GoToDefinitionResponse = {
    withTruffleContext(
      environment,
      _ => {

        val compiler = getCompiler(environment.user)
        val programContext = getProgramContext(compiler, source, environment)
        try {
          compiler.goToDefinition(source, environment, position)(programContext)
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def validate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    withTruffleContext(
      environment,
      _ => {
        val compiler = getCompiler(environment.user)
        val programContext = getProgramContext(compiler, source, environment)
        try {
          compiler.validate(source, environment)(programContext)
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    withTruffleContext(
      environment,
      _ => {
        val compiler = getCompiler(environment.user)
        val programContext = getProgramContext(compiler, source, environment)
        try {
          compiler.aiValidate(source, environment)(programContext)
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def doStop(): Unit = {
    compilerCaches.values.foreach(compiler => compiler.compilerContext.inferrer.stop())
    credentials.stop()
  }

  private def javaValueOf(value: ParamValue) = {
    value match {
      case ParamNull() => null
      case ParamByte(v) => v
      case ParamShort(v) => v
      case ParamInt(v) => v
      case ParamLong(v) => v
      case ParamFloat(v) => v
      case ParamDouble(v) => v
      case ParamBool(v) => v
      case ParamString(v) => v
      case ParamDecimal(v) => new DecimalObject(v)
      case ParamDate(v) => new DateObject(v)
      case ParamTime(v) => new TimeObject(v)
      case ParamTimestamp(v) => new TimestampObject(v)
      case ParamInterval(v) => IntervalObject.fromDuration(v)
    }
  }

  private def buildTruffleContext(
      environment: ProgramEnvironment,
      maybeOutputStream: Option[OutputStream] = None
  ): Context = {
    // Add environment settings as hardcoded environment variables.
    val ctxBuilder = Context
      .newBuilder(RawLanguage.ID)
      .environment("RAW_SETTINGS", settings.renderAsString)
      .environment("RAW_USER", environment.user.uid.toString)
      .environment("RAW_TRACE_ID", environment.user.uid.toString)
      .environment("RAW_SCOPES", environment.scopes.mkString(","))
      .allowExperimentalOptions(true)
      .allowPolyglotAccess(PolyglotAccess.ALL)
    // Set output format as a config setting.
    environment.options
      .get("output-format")
      .foreach(f => ctxBuilder.option(RawLanguage.ID + ".output-format", f))
    maybeOutputStream.foreach(os => ctxBuilder.out(os))
    // Add arguments as polyglot bindings.
    val ctx = ctxBuilder.build()
    environment.maybeArguments.foreach { args =>
      args.foreach(arg =>
        ctx.getPolyglotBindings.putMember(
          arg._1,
          javaValueOf(arg._2)
        )
      )
    }
    ctx
  }

  private def withTruffleContext[T](
      environment: ProgramEnvironment,
      f: Context => T
  ): T = {
    val ctx = buildTruffleContext(environment)
    ctx.initialize(RawLanguage.ID)
    ctx.enter()
    try {
      f(ctx)
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

}
