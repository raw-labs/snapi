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

package com.rawlabs.compiler.snapi.common

import com.rawlabs.compiler.snapi.common.source._

final case class PhaseDescriptor(
    name: String,
    phase: Class[com.rawlabs.compiler.snapi.base.PipelinedPhase[SourceProgram]]
) extends com.rawlabs.compiler.snapi.base.PhaseDescriptor[SourceProgram] {

  override def instance(
      cur: com.rawlabs.compiler.snapi.base.Phase[SourceProgram],
      programContext: com.rawlabs.compiler.snapi.base.ProgramContext
  ): com.rawlabs.compiler.snapi.base.PipelinedPhase[SourceProgram] = {
    phase
      .getConstructor(
        classOf[com.rawlabs.compiler.snapi.base.Phase[SourceProgram]],
        classOf[String],
        classOf[com.rawlabs.compiler.snapi.base.ProgramContext]
      )
      .newInstance(cur, name, programContext)
  }

}
