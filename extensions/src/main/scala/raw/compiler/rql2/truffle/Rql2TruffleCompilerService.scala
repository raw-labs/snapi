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
import org.graalvm.polyglot.Context
import raw.compiler.Pos
import raw.compiler.api.{
  AutoCompleteResponse,
  CompilationResponse,
  CompilerServiceException,
  ExecutionResponse,
  ExecutionRuntimeFailure,
  ExecutionSuccess,
  ExecutionValidationFailure,
  GetProgramDescriptionResponse,
  GetTypeResponse,
  GoToDefinitionResponse,
  HoverResponse,
  RenameResponse,
  ValidateResponse
}
import raw.compiler.common.CommonCompilerService
import raw.runtime.{ParamValue, ProgramEnvironment}
import raw.runtime.truffle.RawLanguage
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException
import raw.utils.{RawException, RawSettings}

import java.io.OutputStream
import scala.util.control.NonFatal

class Rql2TruffleCompilerService(implicit settings: RawSettings) extends CommonCompilerService("rql2-truffle") {

  override def getType(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): GetTypeResponse = {
    withTruffleContext(environment, _ => super.getType(source, maybeArguments, environment))
  }

  override def getProgramDescription(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): GetProgramDescriptionResponse = {
    withTruffleContext(environment, _ => super.getProgramDescription(source, maybeArguments, environment))
  }

  override def compile(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment,
      ref: Any
  ): CompilationResponse = {
    // This override is redundant but done for clarity: note that unlike all other overrides,
    // we do NOT create a TruffleContext here, because this method is only used from the
    // RawLanguage, where there is already a TruffleContext created.
    super.compile(source, maybeArguments, environment, ref)
  }

  // This method is used by the test framework.
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
      compiler
        .parseAndValidate(source, maybeDecl)(programContext)
        .right
        .flatMap { program =>
          compiler.compile(program)(programContext).right.map { entrypoint =>
            val ctx = buildTruffleContext(environment, maybeOutputStream = Some(outputStream))
            ctx.initialize(RawLanguage.ID)
            ctx.enter()
            try {
              val target =
                Truffle.getRuntime.createDirectCallNode(entrypoint.asInstanceOf[TruffleEntrypoint].node.getCallTarget)
              target.call()
              ExecutionSuccess
            } finally {
              ctx.leave()
              ctx.close()
            }
          }
        }
        .fold(
          errs => ExecutionValidationFailure(errs),
          res => res
        )
    } catch {
      case ex: RawTruffleRuntimeException => ExecutionRuntimeFailure(ex.getMessage)
      case ex: RawException => ExecutionRuntimeFailure(ex.getMessage)
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def dotAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): AutoCompleteResponse = {
    withTruffleContext(environment, _ => super.dotAutoComplete(source, environment, position))
  }

  override def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse = {
    withTruffleContext(environment, _ => super.wordAutoComplete(source, environment, prefix, position))
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    withTruffleContext(environment, _ => super.hover(source, environment, position))
  }

  override def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse = {
    withTruffleContext(environment, _ => super.rename(source, environment, position))
  }

  override def goToDefinition(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): GoToDefinitionResponse = {
    withTruffleContext(environment, _ => super.goToDefinition(source, environment, position))
  }

  override def validate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    withTruffleContext(environment, _ => super.validate(source, environment))
  }

  override def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    withTruffleContext(environment, _ => super.aiValidate(source, environment))
  }

  private def buildTruffleContext(
      environment: ProgramEnvironment,
      maybeOutputStream: Option[OutputStream] = None
  ): Context = {
    val ctxBuilder = Context
      .newBuilder(RawLanguage.ID)
      .environment("RAW_USER", environment.user.uid.toString)
      .environment("RAW_TRACE_ID", environment.user.uid.toString)
      .environment("RAW_SCOPES", environment.scopes.mkString(","))
      .allowExperimentalOptions(true)
    environment.options
      .get("output-format")
      .foreach(f => ctxBuilder.option(RawLanguage.ID + ".output-format", f))
    maybeOutputStream.foreach(os => ctxBuilder.out(os))
    ctxBuilder.build()
  }

  private def withTruffleContext[T](environment: ProgramEnvironment, f: Context => T): T = {
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
