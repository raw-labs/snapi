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
import raw.compiler.base.source.{BaseNode, Type}
import raw.compiler.common.source.SourceProgram
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

  override def parse(source: String, environment: ProgramEnvironment): SourceProgram = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    compiler.parse(source)(programContext)
  }

  override def parseType(tipe: String, user: AuthenticatedUser): Type = {
    val compiler = getCompiler(user)
    compiler.parseType(tipe).getOrElse(throw new CompilerException("could not parse type"))
  }

  override def prettyPrint(node: BaseNode, user: AuthenticatedUser): String = {
    val compiler = getCompiler(user)
    compiler.prettyPrint(node)
  }

  override def getType(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): Either[List[ErrorMessage], Option[Type]] = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, maybeArguments, environment)
    compiler.getType(source)(programContext)
  }

  override def getProgramDescription(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): Either[List[ErrorMessage], ProgramDescription] = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, maybeArguments, environment)
    compiler.getProgramDescription(source)(programContext)
  }

  override def compile(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment,
      ref: Any
  ): CompilationResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, maybeArguments, environment)
    compiler.compile(source, ref)(programContext) match {
      case Left(errs) => CompilationFailure(errs)
      case Right(program) => CompilationSuccess(program)
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
    compiler.execute(source, maybeDecl)(programContext) match {
      case Left(errs) => ExecutionValidationFailure(errs)
      case Right(writer) =>
        try {
          writer.writeTo(outputStream)
          ExecutionSuccess
        } catch {
          case ex: RawException => ExecutionRuntimeFailure(ex.getMessage)
        }
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
    compiler.lsp(FormatCodeLSPRequest(source, environment, maybeIndent, maybeWidth))(programContext) match {
      case FormatCodeLSPResponse(code, errors) => FormatCodeResponse(Some(code), errors)
      case ErrorLSPResponse(errors) => FormatCodeResponse(None, errors)
    }
  }

  override def dotAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): AutoCompleteResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    compiler.lsp(DotAutoCompleteLSPRequest(source, environment, position))(programContext) match {
      case AutoCompleteLSPResponse(entries, errors) =>
        AutoCompleteResponse(entries.map(convertCompletionResponse), errors)
      case ErrorLSPResponse(errors) => AutoCompleteResponse(Array.empty, errors)
    }
  }

  private def convertCompletionResponse(entry: LSPAutoCompleteResponse): Completion = {
    entry match {
      case FieldLSPAutoCompleteResponse(name, tipe) => FieldCompletion(name, tipe)
      case LetBindLSPAutoCompleteResponse(name, tipe) => LetBindCompletion(name, tipe)
      case LetFunLSPAutoCompleteResponse(name, tipe) => LetFunCompletion(name, tipe)
      case LetFunRecAutoCompleteResponse(name, tipe) => LetFunRecCompletion(name, tipe)
      case FunParamLSPAutoCompleteResponse(name, tipe) => FunParamCompletion(name, tipe)
      case PackageLSPAutoCompleteResponse(name, doc) => PackageCompletion(name, doc)
      case PackageEntryLSPAutoCompleteResponse(name, doc) => PackageEntryCompletion(name, doc)
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
    compiler.lsp(WordAutoCompleteLSPRequest(source, environment, prefix, position))(programContext) match {
      case AutoCompleteLSPResponse(entries, errors) =>
        AutoCompleteResponse(entries.map(convertCompletionResponse), errors)
      case ErrorLSPResponse(errors) => AutoCompleteResponse(Array.empty, errors)
    }
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    compiler.lsp(HoverLSPRequest(source, environment, position))(programContext) match {
      case HoverLSPResponse(hoverResponse, errors) => HoverResponse(Some(convertHoverResponse(hoverResponse)), errors)
      case ErrorLSPResponse(errors) => HoverResponse(None, errors)
    }
  }

  private def convertHoverResponse(response: LSPHoverResponse): Completion = {
    response match {
      case PackageLSPHoverResponse(name, doc) => PackageCompletion(name, doc)
      case PackageEntryLSPHoverResponse(name, doc) => PackageEntryCompletion(name, doc)
      case TypeHoverResponse(name, tipe) => TypeCompletion(name, tipe)
    }
  }

  override def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    compiler.lsp(RenameLSPRequest(source, environment, position))(programContext) match {
      case RenameLSPResponse(positions, errors) => RenameResponse(positions, errors)
      case ErrorLSPResponse(errors) => RenameResponse(Array.empty, errors)
    }
  }

  override def goToDefinition(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): GoToDefinitionResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    compiler.lsp(DefinitionLSPRequest(source, environment, position))(programContext) match {
      case DefinitionLSPResponse(position, errors) => GoToDefinitionResponse(position, errors)
      case ErrorLSPResponse(errors) => GoToDefinitionResponse(Pos(0, 0), errors)
    }
  }

  override def validate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    val ErrorLSPResponse(errors) = compiler.lsp(ValidateLSPRequest(source, environment))(programContext)
    ValidateResponse(errors)
  }

  override def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    val compiler = getCompiler(environment.user)
    val programContext = getProgramContext(compiler, source, None, environment)
    val ErrorLSPResponse(errors) = compiler.lsp(AiValidateLSPRequest(source, environment))(programContext)
    ValidateResponse(errors)
  }

  override def doStop(): Unit = {
    compilerCaches.values.foreach(compiler => compiler.compilerContext.inferrer.stop())
    credentials.stop()
  }
}
