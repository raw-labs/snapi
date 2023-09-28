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
import raw.compiler.{CompilerExecutionException, ErrorMessage, Pos, ProgramDescription}
import raw.compiler.api.{
  AutoCompleteResponse,
  CompilationResponse,
  ExecutionResponse,
  ExecutionRuntimeFailure,
  ExecutionSuccess,
  ExecutionValidationFailure,
  GoToDefinitionResponse,
  HoverResponse,
  RenameResponse,
  ValidateResponse
}
import raw.compiler.base.source.Type
import raw.compiler.common.CommonCompilerService
import raw.runtime.{ParamValue, ProgramEnvironment}
import raw.runtime.truffle.RawLanguage
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException
import raw.utils.{RawException, RawSettings}

import java.io.OutputStream

class Rql2TruffleCompilerService(implicit settings: RawSettings) extends CommonCompilerService("rql2-truffle") {

  override def getType(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): Either[List[ErrorMessage], Option[Type]] = {
    withTruffleContext(_ => super.getType(source, maybeArguments, environment))
  }

  override def getProgramDescription(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): Either[List[ErrorMessage], ProgramDescription] = {
    withTruffleContext(_ => super.getProgramDescription(source, maybeArguments, environment))
  }

  override def compile(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment,
      ref: Any
  ): CompilationResponse = {
    withTruffleContext(_ => super.compile(source, maybeArguments, environment, ref))
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
      compiler
        .parseAndValidate(source, maybeDecl)(programContext)
        .right
        .flatMap { program =>
          compiler.compile(program)(programContext).right.map { entrypoint =>
            val ctx: Context = Context.newBuilder(RawLanguage.ID)
              .arguments()
              .environment()

              .out(outputStream).build()
            ctx.initialize(RawLanguage.ID)
            ctx.enter()
            try {
              val target =
                Truffle.getRuntime.createDirectCallNode(entrypoint.asInstanceOf[TruffleEntrypoint].node.getCallTarget)
              target.call()
              ExecutionSuccess
            } catch {
              case ex: RawTruffleRuntimeException =>
                // Instead of passing the cause, we pass null, because otherwise when running Scala2 tests it tries to
                // the AbstractTruffleException which is not exported in JVM (not GraalVM), so it fails.
                throw new CompilerExecutionException(
                  ex.getMessage,
                  null
                )
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
      case ex: RawException => ExecutionRuntimeFailure(ex.getMessage)
    }
  }

  override def dotAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): AutoCompleteResponse = {
    withTruffleContext(_ => super.dotAutoComplete(source, environment, position))
  }

  override def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse = {
    withTruffleContext(_ => super.wordAutoComplete(source, environment, prefix, position))
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    withTruffleContext(_ => super.hover(source, environment, position))
  }

  override def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse = {
    withTruffleContext(_ => super.rename(source, environment, position))
  }

  override def goToDefinition(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): GoToDefinitionResponse = {
    withTruffleContext(_ => super.goToDefinition(source, environment, position))
  }

  override def validate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    withTruffleContext(_ => super.validate(source, environment))
  }

  override def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    withTruffleContext(_ => super.aiValidate(source, environment))
  }

  private def withTruffleContext[T](f: Context => T): T = {
    val ctx: Context = Context.newBuilder(RawLanguage.ID).build()
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
