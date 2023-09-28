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

import raw.compiler.{CompilerException, EntryDoc, ErrorMessage, PackageDoc, Pos, ProgramDescription}
import raw.compiler.base.source.{BaseNode, Type}
import raw.compiler.common.source.SourceProgram
import raw.runtime.{Entrypoint, ParamValue, ProgramEnvironment}
import raw.utils.{AuthenticatedUser, RawService}

import java.io.OutputStream

trait CompilerService extends RawService {

  // Parse a source program and return the AST.
  @throws[CompilerException]
  def parse(source: String, environment: ProgramEnvironment): SourceProgram

  // Parse a type and return the Type object.
  @throws[CompilerException]
  def parseType(tipe: String, user: AuthenticatedUser): Type

  // Pretty print a node.
  def prettyPrint(node: BaseNode, user: AuthenticatedUser): String

  // Get the type of a source program.
  def getType(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): Either[List[ErrorMessage], Option[Type]]

  // Get the description of a source program.
  def getProgramDescription(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): Either[List[ErrorMessage], ProgramDescription]

  // Compile a source program.
  def compile(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment,
      ref: Any
  ): CompilationResponse

  // Execute a source program.
  def execute(
      source: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream
  ): ExecutionResponse

  // Format a source program.
  def formatCode(
      source: String,
      environment: ProgramEnvironment,
      maybeIndent: Option[Int] = None,
      maybeWidth: Option[Int] = None
  ): FormatCodeResponse

  // Auto-complete a source program.
  def dotAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): AutoCompleteResponse

  // Auto-complete a word in a source program.
  def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse

  // Get the hover information for a source program.
  def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse

  // Rename an identifier in a source program.
  def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse

  // Go to definition of an identifier in a source program.
  def goToDefinition(source: String, environment: ProgramEnvironment, position: Pos): GoToDefinitionResponse

  // Validate a source program.
  def validate(source: String, environment: ProgramEnvironment): ValidateResponse

  // Validate a source program for the AI service.
  def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse

}

sealed trait CompilationResponse
final case class CompilationFailure(errors: List[ErrorMessage]) extends CompilationResponse
final case class CompilationSuccess(entrypoint: Entrypoint) extends CompilationResponse

sealed trait ExecutionResponse
final case class ExecutionValidationFailure(errors: List[ErrorMessage]) extends ExecutionResponse
final case object ExecutionSuccess extends ExecutionResponse
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

final case class GoToDefinitionResponse(position: Pos, errors: List[ErrorMessage])

final case class ValidateResponse(errors: List[ErrorMessage])
