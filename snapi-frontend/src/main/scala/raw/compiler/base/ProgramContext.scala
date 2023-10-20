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

import raw.runtime.RuntimeContext
import raw.utils.RawSettings

/**
 * Contains state that is shared between compilation phases of a single program.
 */
class ProgramContext(
    val runtimeContext: RuntimeContext,
    val compilerContext: CompilerContext
) {

  def settings: RawSettings = runtimeContext.settings

  def dumpDebugInfo: List[(String, String)] = {
    List(
      "Trace ID" -> runtimeContext.environment.maybeTraceId.getOrElse("<undefined>"),
      "Arguments" -> runtimeContext.maybeArguments
        .map(args => args.map { case (k, v) => s"$k -> $v" }.mkString("\n"))
        .getOrElse("<undefined>"),
      "User" -> runtimeContext.environment.user.toString,
      "Scopes" -> runtimeContext.environment.scopes.mkString(","),
      "Options" -> runtimeContext.environment.options.map { case (k, v) => s"$k -> $v" }.mkString("\n")
      //"Settings" -> runtimeContext.settings.toString
    )
  }

}
