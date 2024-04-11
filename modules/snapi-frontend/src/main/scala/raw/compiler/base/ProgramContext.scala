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
import raw.client.utils.RawSettings
import raw.client.api.CompilerService

/**
 * Contains state that is shared between compilation phases of a single program.
 */
trait ProgramContext {

  def runtimeContext: RuntimeContext

  def compilerContext: CompilerContext

  def settings: RawSettings = runtimeContext.settings

  def dumpDebugInfo: List[(String, String)] = {
    CompilerService.getDebugInfo(runtimeContext.environment)
  }

}
