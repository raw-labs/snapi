package raw.compiler.rql2

import raw.compiler.rql2.source.SourceProgram

final case class PhaseDescriptor(name: String, phase: Class[raw.compiler.base.PipelinedPhase[SourceProgram]])
    extends raw.compiler.base.PhaseDescriptor[SourceProgram] {

  override def instance(
      cur: raw.compiler.base.Phase[SourceProgram],
      programContext: raw.compiler.base.ProgramContext
  ): raw.compiler.base.PipelinedPhase[SourceProgram] = {
    phase
      .getConstructor(
        classOf[raw.compiler.base.Phase[SourceProgram]],
        classOf[String],
        classOf[raw.compiler.base.ProgramContext]
      )
      .newInstance(cur, name, programContext)
  }

}
