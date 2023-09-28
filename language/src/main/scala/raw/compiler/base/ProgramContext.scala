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

import raw.runtime.RuntimeContext
import raw.utils.RawSettings

import scala.collection.mutable

/**
 * Contains state that is shared between compilation phases of a single program.
 */
class ProgramContext(
    val runtimeContext: RuntimeContext,
    val compilerContext: CompilerContext
) {

  def settings: RawSettings = runtimeContext.settings

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

  def dumpDebugInfo: List[(String, String)] = {
    List(
      "Trace ID" -> runtimeContext.environment.maybeTraceId.getOrElse("<undefined>"),
      "Arguments" -> runtimeContext.maybeArguments
        .map(args => args.map { case (k, v) => s"$k -> $v" }.mkString("\n"))
        .getOrElse("<undefined>"),
      "Language" -> runtimeContext.environment.language.getOrElse("<default>"),
      "Scopes" -> runtimeContext.environment.scopes.mkString(","),
      "Options" -> runtimeContext.environment.options.map { case (k, v) => s"$k -> $v" }.mkString("\n")
      //"Settings" -> runtimeContext.settings.toString
    )
  }

}
