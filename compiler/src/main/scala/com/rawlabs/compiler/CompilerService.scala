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

package com.rawlabs.compiler

import com.rawlabs.protocol.raw.{Type, Value}
import org.graalvm.polyglot.Engine

import java.io.OutputStream
import scala.collection.mutable
import com.rawlabs.utils.core.{RawService, RawSettings}

object CompilerService {

  private val enginesLock = new Object
  private val enginesCache = mutable.HashMap[RawSettings, Engine]()

  def getEngine(maybeClassLoader: Option[ClassLoader])(implicit settings: RawSettings): (Engine, Boolean) = {
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
          val engine = maybeClassLoader match {
            case Some(classLoader) =>
              // If a class loader is provided, use it to create the engine.
              val currentClassLoader = Thread.currentThread().getContextClassLoader
              Thread.currentThread().setContextClassLoader(classLoader)
              try {
                Engine.newBuilder().allowExperimentalOptions(true).options(options).build()
              } finally {
                Thread.currentThread().setContextClassLoader(currentClassLoader)
              }
            case None =>
              // If no class loader is provided, create the engine without it.
              Engine.newBuilder().allowExperimentalOptions(true).options(options).build()
          }
          enginesCache.put(settings, engine)
          (engine, true)
      }
    }
  }

  // Return engine and a flag indicating if the engine was created.
  def getEngine()(implicit settings: RawSettings): (Engine, Boolean) = {
    getEngine(None)
  }

  def releaseEngine()(implicit settings: RawSettings): Unit = {
    enginesLock.synchronized {
      enginesCache.remove(settings).foreach(engine => engine.close(true))
    }
  }

}

trait CompilerService extends RawService {

  implicit protected def settings: RawSettings

  def language: Set[String]

  // Get the description of a source program.
  def getProgramDescription(
      source: String,
      environment: ProgramEnvironment
  ): Either[List[ErrorMessage], ProgramDescription]

  // Execute a source program and write the results to the output stream.
  def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream,
      maxRows: Option[Long] = None
  ): Either[ExecutionError, ExecutionSuccess]

  def eval(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String]
  ): Either[ExecutionError, EvalSuccess]

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

final case class Pos(line: Int, column: Int)

final case class ExecutionSuccess(complete: Boolean)

sealed trait ExecutionError
object ExecutionError {
  final case class ValidationError(errors: List[ErrorMessage]) extends ExecutionError

  final case class RuntimeError(error: String) extends ExecutionError
}

sealed trait EvalSuccess
object EvalSuccess {
  final case class IteratorValue(innerType: Type, valueIterator: Iterator[Value] with AutoCloseable) extends EvalSuccess
  final case class ResultValue(valueType: Type, value: Value) extends EvalSuccess
}

final case class FormatCodeResponse(code: Option[String])
final case class HoverResponse(completion: Option[Completion])
final case class RenameResponse(positions: Array[Pos])
final case class GoToDefinitionResponse(position: Option[Pos])
final case class ValidateResponse(messages: List[Message])
final case class AutoCompleteResponse(completions: Array[Completion])

sealed trait Completion
final case class TypeCompletion(name: String, tipe: String) extends Completion
final case class FieldCompletion(name: String, tipe: String) extends Completion
final case class LetBindCompletion(name: String, tipe: String) extends Completion
final case class LetFunCompletion(name: String, tipe: String) extends Completion
final case class LetFunRecCompletion(name: String, tipe: String) extends Completion
final case class FunParamCompletion(name: String, tipe: String) extends Completion
final case class PackageCompletion(name: String, doc: PackageDoc) extends Completion
final case class PackageEntryCompletion(name: String, doc: EntryDoc) extends Completion
