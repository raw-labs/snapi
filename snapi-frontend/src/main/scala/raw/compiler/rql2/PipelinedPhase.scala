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

package raw.compiler.rql2

import raw.compiler.rql2.source.SourceProgram

trait PipelinedPhase extends raw.compiler.base.PipelinedPhase[SourceProgram] with source.SourcePrettyPrinter {

  protected def baseProgramContext: raw.compiler.base.ProgramContext

  implicit protected val programContext: ProgramContext = baseProgramContext.asInstanceOf[ProgramContext]

  override protected def checkPhaseTypeStability(input: SourceProgram, output: SourceProgram): Unit = {
    // Check input tree.
    val inputTree = new Tree(input)
    // No need to check the input tree, since it's the output of the previous phase, and that has been checked already.
    // assert(inputTree.checkTree(), s"Input tree to phase $phaseName is not valid")
    val inputType = inputTree.rootType

    // Check output tree.
    val outputTree = new Tree(output)
    assert(outputTree.checkTree(), s"Output tree of phase $phaseName is not valid")
    val outputType = outputTree.rootType

    // Ensure tree type is stable during phase.
    assert(
      inputType == outputType,
      s"""Tree root type changed during phase $phaseName !!
        |Was: ${inputType.map(format).getOrElse("-")}
        |Now: ${outputType.map(format).getOrElse("-")}
        |
        |Input Tree:
        |${inputTree.pretty}
        |
        |Output Tree:
        |${outputTree.pretty}""".stripMargin
    )
  }

}
