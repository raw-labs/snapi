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

package raw.compiler.common

import raw.compiler.api._
import raw.compiler._
import raw.compiler.base.ProgramContext
import raw.compiler.base.source.BaseNode
import raw.compiler.scala2.Scala2CompilerContext
import raw.creds.api.CredentialsServiceProvider
import raw.inferrer.api.InferrerServiceProvider
import raw.runtime._
import raw.sources.api.SourceContext
import raw.utils._

import java.io.OutputStream
import scala.util.control.NonFatal

abstract class CommonCompilerService(language: String)(implicit settings: RawSettings) extends CompilerService {

  private val credentials = CredentialsServiceProvider()

  // Map of users to compilers.
  private val compilerCaches = new RawConcurrentHashMap[(AuthenticatedUser, String), Compiler]

  protected def getCompiler(user: AuthenticatedUser): Compiler = {
    compilerCaches.getOrElseUpdate((user, language), createCompiler(user, language))
  }

  private def createCompiler(user: AuthenticatedUser, language: String): Compiler = {
    // Initialize source context
    implicit val sourceContext = new SourceContext(user, credentials, settings)

    // Initialize inferrer
    val inferrer = InferrerServiceProvider()

    // Initialize compiler context
    val compilerContext = new Scala2CompilerContext(language, user, sourceContext, inferrer)
    try {
      // Initialize compiler. Default language, if not specified is 'rql2'.
      CommonCompilerProvider(language)(compilerContext)
    } catch {
      case NonFatal(ex) =>
        // To not leave hanging inferrer services.
        // This would make tests fail in the afterAll when checking for running services
        inferrer.stop()
        throw ex
    }
  }

  protected def getProgramContext(
      compiler: Compiler,
      code: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): ProgramContext = {
    val runtimeContext = getRuntimeContext(compiler, maybeArguments, environment)
    compiler.getProgramContext(runtimeContext)
  }

  private def getRuntimeContext(
      compiler: Compiler,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): RuntimeContext = {
    val sourceContext = compiler.compilerContext.sourceContext
    new RuntimeContext(
      sourceContext,
      settings,
      maybeArguments,
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
    val programContext = getProgramContext(compiler, source, None, environment)
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
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): GetTypeResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, maybeArguments, environment)
    try {
      compiler.getType(source)(programContext) match {
        case Left(errs) => GetTypeFailure(errs)
        case Right(t) => GetTypeSuccess(t)
      }
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def getProgramDescription(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): GetProgramDescriptionResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, maybeArguments, environment)
    try {
      compiler.getProgramDescription(source)(programContext) match {
        case Left(errs) => GetProgramDescriptionFailure(errs)
        case Right(desc) => GetProgramDescriptionSuccess(desc)
      }
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def compile(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment,
      ref: Any
  ): CompilationResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, maybeArguments, environment)
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
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream
  ): ExecutionResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, maybeArguments, environment)
    try {
      compiler.execute(source, maybeDecl)(programContext) match {
        case Left(errs) => ExecutionValidationFailure(errs)
        case Right(writer) =>
          writer.writeTo(outputStream)
          ExecutionSuccess
      }
    } catch {
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
    val programContext = getProgramContext(compiler, source, None, environment)
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
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    try {
      compiler.dotAutoComplete(source, environment, position)(programContext)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    try {
      compiler.wordAutoComplete(source, environment, prefix, position)(programContext)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    try {
      compiler.hover(source, environment, position)(programContext)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    try {
      compiler.rename(source, environment, position)(programContext)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def goToDefinition(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): GoToDefinitionResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    try {
      compiler.goToDefinition(source, environment, position)(programContext)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def validate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    try {
      compiler.validate(source, environment)(programContext)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    try {
      compiler.aiValidate(source, environment)(programContext)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def doStop(): Unit = {
    compilerCaches.values.foreach(compiler => compiler.compilerContext.inferrer.stop())
    credentials.stop()
  }
}
