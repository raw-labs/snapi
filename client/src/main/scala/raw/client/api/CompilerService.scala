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

import raw.utils.{RawException, RawService}

import java.io.OutputStream

// Exception that wraps the underlying error so that it includes the extra debug info.
final class CompilerServiceException(
    message: String,
    val debugInfo: List[(String, String)] = List.empty,
    cause: Throwable = null
) extends RawException(message, cause) {

  def this(t: Throwable, debugInfo: List[(String, String)]) = this(t.getMessage, debugInfo, t)

}

trait CompilerService extends RawService {

  def language: Set[String]

  // Get the description of a source program.
  @throws[CompilerServiceException]
  def getProgramDescription(
      source: String,
      environment: ProgramEnvironment
  ): GetProgramDescriptionResponse

  // Evaluate a source program and return the result as a RawValue.
  @throws[CompilerServiceException]
  def eval(
      source: String,
      tipe: RawType,
      environment: ProgramEnvironment
  ): EvalResponse

  // Execute a source program and write the results to the output stream.
  @throws[CompilerServiceException]
  def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream
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

sealed trait EvalResponse
final case class EvalSuccess(v: RawValue) extends EvalResponse
final case class EvalValidationFailure(errors: List[ErrorMessage]) extends EvalResponse
final case class EvalRuntimeFailure(error: String) extends EvalResponse

sealed trait ExecutionResponse
case object ExecutionSuccess extends ExecutionResponse
final case class ExecutionValidationFailure(errors: List[ErrorMessage]) extends ExecutionResponse
final case class ExecutionRuntimeFailure(error: String) extends ExecutionResponse

final case class FormatCodeResponse(code: Option[String], errors: List[ErrorMessage])

final case class AutoCompleteResponse(completions: Array[Completion], errors: List[ErrorMessage])

sealed trait Completion
final case class TypeCompletion(name: String, tipe: String) extends Completion
final case class FieldCompletion(name: String, tipe: String) extends Completion
final case class LetBindCompletion(name: String, tipe: String) extends Completion
final case class LetFunCompletion(name: String, tipe: String) extends Completion
final case class LetFunRecCompletion(name: String, tipe: String) extends Completion
final case class FunParamCompletion(name: String, tipe: String) extends Completion
final case class PackageCompletion(name: String, doc: PackageDoc) extends Completion
final case class PackageEntryCompletion(name: String, doc: EntryDoc) extends Completion

final case class HoverResponse(completion: Option[Completion], errors: List[ErrorMessage])

final case class RenameResponse(positions: Array[Pos], errors: List[ErrorMessage])

final case class GoToDefinitionResponse(position: Option[Pos], errors: List[ErrorMessage])

final case class ValidateResponse(errors: List[ErrorMessage])
