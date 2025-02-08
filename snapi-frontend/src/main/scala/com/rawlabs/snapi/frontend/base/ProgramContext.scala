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

package com.rawlabs.snapi.frontend.base

import com.rawlabs.snapi.frontend.api.ProgramEnvironment
import com.rawlabs.utils.core.RawSettings

/**
 * Contains state that is shared between compilation phases of a single program.
 */
trait ProgramContext {

  def programEnvironment: ProgramEnvironment

  def compilerContext: CompilerContext

  def settings: RawSettings = compilerContext.settings

}
