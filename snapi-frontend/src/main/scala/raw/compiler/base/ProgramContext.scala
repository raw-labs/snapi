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

import raw.utils.RawSettings
import raw.client.api.{CompilerService, ProgramEnvironment}

/**
 * Contains state that is shared between compilation phases of a single program.
 */
trait ProgramContext {

  def programEnvironment: ProgramEnvironment

  def compilerContext: CompilerContext

  def settings: RawSettings = compilerContext.settings

  def dumpDebugInfo: List[(String, String)] = {
    CompilerService.getDebugInfo(programEnvironment)
  }

}
