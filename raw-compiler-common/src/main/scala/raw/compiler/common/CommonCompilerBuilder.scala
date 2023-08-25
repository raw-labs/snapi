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

import raw.runtime.Entrypoint
import raw.compiler.base.source.{BaseProgram, Type}
import raw.compiler.base.{CompilerBuilder, CompilerContext, ProgramContext}
import raw.compiler.common.source.SourceProgram
import raw.compiler.{ErrorMessage, ProgramOutputWriter}
import raw.runtime.interpreter.Value

trait CommonCompilerBuilder extends CompilerBuilder {

  def getCompiler(compilerContext: CompilerContext): Compiler

  override def validate(
      program: BaseProgram
  )(implicit programContext: ProgramContext): Either[List[ErrorMessage], Option[Type]] = {
    val compiler = getCompiler(programContext.compilerContext)
    compiler.validate(program.asInstanceOf[SourceProgram])
  }

  override def compile(
      program: BaseProgram
  )(implicit programContext: ProgramContext): Either[List[ErrorMessage], Entrypoint] = {
    val compiler = getCompiler(programContext.compilerContext)
    compiler.compile(program.asInstanceOf[SourceProgram])
  }

  override def eval(
      program: BaseProgram
  )(implicit programContext: ProgramContext): Either[List[ErrorMessage], Value] = {
    val compiler = getCompiler(programContext.compilerContext)
    compiler.eval(program.asInstanceOf[SourceProgram])
  }

  override def execute(entrypoint: Entrypoint)(implicit programContext: ProgramContext): ProgramOutputWriter = {
    val compiler = getCompiler(programContext.compilerContext)
    compiler.execute(entrypoint)
  }

  override def clone(program: BaseProgram)(implicit compilerContext: CompilerContext): BaseProgram = {
    val compiler = getCompiler(compilerContext)
    compiler.clone(program.asInstanceOf[SourceProgram])
  }

  override def prune(program: BaseProgram, tipe: Type)(implicit programContext: ProgramContext): Option[BaseProgram] = {
    val compiler = getCompiler(programContext.compilerContext)
    compiler.prune(program.asInstanceOf[SourceProgram], tipe)
  }

  override def project(program: BaseProgram, field: String)(
      implicit programContext: ProgramContext
  ): Option[BaseProgram] = {
    val compiler = getCompiler(programContext.compilerContext)
    compiler.project(program.asInstanceOf[SourceProgram], field)
  }

  override def normalize(program: BaseProgram)(implicit programContext: ProgramContext): BaseProgram = {
    val compiler = getCompiler(programContext.compilerContext)
    compiler.normalize(program.asInstanceOf[SourceProgram])
  }

}
