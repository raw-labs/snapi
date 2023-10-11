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

package raw.compiler.api

import raw.compiler.{EntryDoc, ErrorMessage, ErrorPosition, PackageDoc, ProgramDescription}
import raw.compiler.base.source.{BaseNode, Type}
import raw.compiler.common.source.SourceProgram
import raw.runtime.interpreter.Value
import raw.runtime.{Entrypoint, ProgramEnvironment}
import raw.utils.{AuthenticatedUser, RawException, RawService}

import java.io.OutputStream

// Exception that wraps the underlying error so that it includes the extra debug info.
final class CompilerServiceException(val t: Throwable, val debugInfo: List[(String, String)])
    extends RawException(t.getMessage, cause = t)

trait CompilerService extends RawService {

  // Pretty print a node.
  def prettyPrint(node: BaseNode, user: AuthenticatedUser): String

  // Parse a type and return the Type object.
  def parseType(tipe: String, user: AuthenticatedUser): ParseTypeResponse

  // Parse a source program and return the AST.
  @throws[CompilerServiceException]
  def parse(source: String, environment: ProgramEnvironment): ParseResponse

  // Get the type of a source program.
  @throws[CompilerServiceException]
  def getType(
      source: String,
      environment: ProgramEnvironment
  ): GetTypeResponse

  // Get the description of a source program.
  @throws[CompilerServiceException]
  def getProgramDescription(
      source: String,
      environment: ProgramEnvironment
  ): GetProgramDescriptionResponse

  // TODO (msb): Remove and move inner implementation onto RawLanguage itself (once emission is done in Java)
  // Compile a source program and return an AST.
  @throws[CompilerServiceException]
  def compile(
      source: String,
      environment: ProgramEnvironment,
      ref: Any
  ): CompilationResponse

  // Evaluate a source program and return the result as a Value.
  @throws[CompilerServiceException]
  def eval(
      source: String,
      tipe: Type,
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

sealed trait ParseResponse
final case class ParseSuccess(program: SourceProgram) extends ParseResponse
final case class ParseFailure(error: String, pos: ErrorPosition) extends ParseResponse

sealed trait ParseTypeResponse
final case class ParseTypeSuccess(tipe: Type) extends ParseTypeResponse
final case class ParseTypeFailure(error: String) extends ParseTypeResponse

sealed trait GetTypeResponse
final case class GetTypeFailure(errors: List[ErrorMessage]) extends GetTypeResponse
final case class GetTypeSuccess(tipe: Option[Type]) extends GetTypeResponse

sealed trait GetProgramDescriptionResponse
final case class GetProgramDescriptionFailure(errors: List[ErrorMessage]) extends GetProgramDescriptionResponse
final case class GetProgramDescriptionSuccess(programDescription: ProgramDescription)
    extends GetProgramDescriptionResponse

sealed trait CompilationResponse
final case class CompilationFailure(errors: List[ErrorMessage]) extends CompilationResponse
final case class CompilationSuccess(entrypoint: Entrypoint) extends CompilationResponse

sealed trait EvalResponse
final case class EvalSuccess(v: Value) extends EvalResponse
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
