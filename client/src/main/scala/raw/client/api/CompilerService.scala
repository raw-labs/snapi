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

package raw.client.api

import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import org.graalvm.polyglot.Engine
import raw.utils.{RawException, RawService, RawSettings}

import java.io.OutputStream
import scala.collection.mutable
import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}

// Exception that wraps the underlying error so that it includes the extra debug info.
final class CompilerServiceException(
    message: String,
    val debugInfo: List[(String, String)] = List.empty,
    cause: Throwable = null
) extends RawException(message, cause) {

  def this(t: Throwable, debugInfo: List[(String, String)]) = this(t.getMessage, debugInfo, t)

  def this(t: Throwable, environment: ProgramEnvironment) = {
    this(t, CompilerService.getDebugInfo(environment))
  }

  def this(t: Throwable) = this(t.getMessage, cause = t)

}

object CompilerService {

  private val enginesLock = new Object
  private val enginesCache = mutable.HashMap[RawSettings, Engine]()

  // Return engine and a flag indicating if the engine was created.
  def getEngine()(implicit settings: RawSettings): (Engine, Boolean) = {
    enginesLock.synchronized {
      enginesCache.get(settings) match {
        case Some(engine) =>
          // Re-using an engine someone else create before. This typically happens on staged compilation.
          (engine, false)
        case None =>
          // First time creating an engine for this settings.
          val options = new java.util.HashMap[String, String]()
          if (settings.onTrainingWheels) {
            // Refer to settings at:
            // https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/Options/
            //          options.put("engine.CompileImmediately", "true")
            //          options.put("engine.TraceCompilation", "true")
            //          options.put("engine.BackgroundCompilation", "false")
            //          options.put("engine.CompilationFailureAction", "Throw")
            //          options.put("engine.CompilationFailureAction", "Diagnose")
            //          options.put("compiler.LogInlinedTargets", "true")
          }
          val engine = Engine.newBuilder().allowExperimentalOptions(true).options(options).build()
          enginesCache.put(settings, engine)
          (engine, true)
      }
    }
  }

  def releaseEngine()(implicit settings: RawSettings): Unit = {
    enginesLock.synchronized {
      enginesCache.remove(settings).foreach(engine => engine.close(true))
    }
  }

  def getDebugInfo(environment: ProgramEnvironment): List[(String, String)] = {
    List(
      "Trace ID" -> environment.maybeTraceId.getOrElse("<undefined>"),
      "Arguments" -> environment.maybeArguments
        .map(args => args.map { case (k, v) => s"$k -> $v" }.mkString("\n"))
        .getOrElse("<undefined>"),
      "User" -> environment.user.toString,
      "Scopes" -> environment.scopes.mkString(","),
      "Options" -> environment.options.map { case (k, v) => s"$k -> $v" }.mkString("\n")
      //"Settings" -> runtimeContext.settings.toString
    )
  }

}

trait CompilerService extends RawService {

  implicit protected def settings: RawSettings

  def language: Set[String]

  // Get the description of a source program.
  @throws[CompilerServiceException]
  def getProgramDescription(
      source: String,
      environment: ProgramEnvironment
  ): GetProgramDescriptionResponse

  // Execute a source program and write the results to the output stream.
  @throws[CompilerServiceException]
  def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream,
      maxRows: Option[Long] = None
  ): ExecutionResponse

  // Format a source program.
  @throws[CompilerServiceException]
  def formatCode(
      source: String,
      environment: ProgramEnvironment,
      maybeIndent: Option[Int] = None,
      maybeWidth: Option[Int] = None
  ): FormatCodeResponse

  // Auto-complete a source program.
  @throws[CompilerServiceException]
  def dotAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): AutoCompleteResponse

  // Auto-complete a word in a source program.
  @throws[CompilerServiceException]
  def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse

  // Get the hover information for a source program.
  @throws[CompilerServiceException]
  def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse

  // Rename an identifier in a source program.
  @throws[CompilerServiceException]
  def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse

  // Go to definition of an identifier in a source program.
  @throws[CompilerServiceException]
  def goToDefinition(source: String, environment: ProgramEnvironment, position: Pos): GoToDefinitionResponse

  // Validate a source program.
  @throws[CompilerServiceException]
  def validate(source: String, environment: ProgramEnvironment): ValidateResponse

  // Validate a source program for the AI service.
  @throws[CompilerServiceException]
  def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse

}

final case class Pos(line: Int, column: Int)

sealed trait GetProgramDescriptionResponse
final case class GetProgramDescriptionFailure(errors: List[ErrorMessage]) extends GetProgramDescriptionResponse
final case class GetProgramDescriptionSuccess(programDescription: ProgramDescription)
    extends GetProgramDescriptionResponse

sealed trait ExecutionResponse
final case class ExecutionSuccess(complete: Boolean) extends ExecutionResponse
final case class ExecutionValidationFailure(errors: List[ErrorMessage]) extends ExecutionResponse
final case class ExecutionRuntimeFailure(error: String) extends ExecutionResponse

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[FormatCodeResponse], name = "formatCode"),
    new JsonType(value = classOf[AutoCompleteResponse], name = "autoComplete"),
    new JsonType(value = classOf[HoverResponse], name = "hover"),
    new JsonType(value = classOf[GoToDefinitionResponse], name = "definition"),
    new JsonType(value = classOf[RenameResponse], name = "rename"),
    new JsonType(value = classOf[ErrorResponse], name = "error"),
    new JsonType(value = classOf[ValidateResponse], name = "validate")
  )
)
sealed trait ClientLspResponse
final case class FormatCodeResponse(code: Option[String]) extends ClientLspResponse
final case class HoverResponse(completion: Option[Completion]) extends ClientLspResponse
final case class RenameResponse(positions: Array[Pos]) extends ClientLspResponse
final case class GoToDefinitionResponse(position: Option[Pos]) extends ClientLspResponse
final case class ValidateResponse(messages: List[Message]) extends ClientLspResponse
final case class ErrorResponse(errors: List[ErrorMessage]) extends ClientLspResponse
final case class AutoCompleteResponse(completions: Array[Completion]) extends ClientLspResponse

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[TypeCompletion], name = "tipe"),
    new JsonType(value = classOf[FieldCompletion], name = "field"),
    new JsonType(value = classOf[LetBindCompletion], name = "bind"),
    new JsonType(value = classOf[LetFunCompletion], name = "function"),
    new JsonType(value = classOf[LetFunRecCompletion], name = "recursiveFunction"),
    new JsonType(value = classOf[FunParamCompletion], name = "functionParameter"),
    new JsonType(value = classOf[PackageCompletion], name = "package"),
    new JsonType(value = classOf[PackageEntryCompletion], name = "packageEntry")
  )
)
sealed trait Completion
final case class TypeCompletion(name: String, tipe: String) extends Completion
final case class FieldCompletion(name: String, tipe: String) extends Completion
final case class LetBindCompletion(name: String, tipe: String) extends Completion
final case class LetFunCompletion(name: String, tipe: String) extends Completion
final case class LetFunRecCompletion(name: String, tipe: String) extends Completion
final case class FunParamCompletion(name: String, tipe: String) extends Completion
final case class PackageCompletion(name: String, doc: PackageDoc) extends Completion
final case class PackageEntryCompletion(name: String, doc: EntryDoc) extends Completion
