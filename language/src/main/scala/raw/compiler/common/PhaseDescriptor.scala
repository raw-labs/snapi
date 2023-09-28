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

package raw.compiler.common

import raw.compiler.base.{Phase, ProgramContext}
import raw.compiler.common.source.SourceProgram

final case class PhaseDescriptor(name: String, phase: Class[raw.compiler.base.PipelinedPhase[SourceProgram]])
    extends raw.compiler.PhaseDescriptor[SourceProgram] {

  override def instance(
      cur: Phase[SourceProgram],
      programContext: raw.compiler.base.ProgramContext
  ): raw.compiler.base.PipelinedPhase[SourceProgram] = {
    phase
      .getConstructor(classOf[raw.compiler.base.Phase[SourceProgram]], classOf[String], classOf[ProgramContext])
      .newInstance(cur, name, programContext)
  }

}
