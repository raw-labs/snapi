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

package raw.compiler.base

import raw.compiler.base.errors.{BaseError, ExternalError}
import raw.compiler.base.source.{BaseProgram, Type}
import raw.config.RawSettings

import java.time.Duration
import raw.runtime.ExecutionLogger
import raw.runtime.RuntimeContext

import scala.collection.mutable

/**
 * Contains state that is shared between compilation phases of a single program.
 */
class ProgramContext(
    val runtimeContext: RuntimeContext,
    val compilerContext: CompilerContext
) {

  def settings: RawSettings = runtimeContext.settings

  def executionLogger: ExecutionLogger = runtimeContext.executionLogger

  ///////////////////////////////////////////////////////////////////////////
  // Metrics
  ///////////////////////////////////////////////////////////////////////////

  def addPhaseTiming(phaseName: String, duration: Duration): Unit = {
    trace(s"Phase timing for $phaseName: ${duration.toMillis} ms")
  }

  def addCompilerTiming(name: String, duration: Duration): Unit = {
    trace(s"Compiler timing for $name: ${duration.toMillis} ms")
  }

  ///////////////////////////////////////////////////////////////////////////
  // Logging
  ///////////////////////////////////////////////////////////////////////////

  def warn(msg: String): Unit = {
    executionLogger.warn(msg)
  }

  def info(msg: String): Unit = {
    executionLogger.info(msg)
  }

  def debug(msg: String): Unit = {
    executionLogger.debug(msg)
  }

  def trace(msg: String): Unit = {
    executionLogger.trace(msg)
  }

  ///////////////////////////////////////////////////////////////////////////
  // Validate program cache
  ///////////////////////////////////////////////////////////////////////////

  private val validateProgramCache = mutable.HashMap[(String, BaseProgram), Either[Seq[BaseError], Option[Type]]]()

  def validateProgram(language: String, program: BaseProgram): Either[Seq[BaseError], Option[Type]] = {
    validateProgramCache.getOrElseUpdate(
      (language, program),
      CompilerProvider
        .validate(language, program)(this)
        .left
        .map(errors => Seq(ExternalError(program, language, errors)))
    )
  }

}
