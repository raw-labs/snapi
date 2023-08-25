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

import org.bitbucket.inkytonik.kiama.util.Positions
import raw.compiler.{ErrorMessage, ProgramOutputWriter}
import raw.compiler.base.source.{BaseNode, BaseProgram, Type}
import raw.runtime.Entrypoint
import raw.runtime.interpreter.Value

trait CompilerBuilder {

  def names: Seq[String]

  def parse(code: String): (BaseProgram, Positions)

  def prettyPrint(node: BaseNode): String

  def validate(program: BaseProgram)(implicit programContext: ProgramContext): Either[List[ErrorMessage], Option[Type]]

  def compile(program: BaseProgram)(implicit programContext: ProgramContext): Either[List[ErrorMessage], Entrypoint]

  def eval(program: BaseProgram)(implicit programContext: ProgramContext): Either[List[ErrorMessage], Value]

  def execute(entrypoint: Entrypoint)(implicit programContext: ProgramContext): ProgramOutputWriter

  def clone(program: BaseProgram)(implicit compilerContext: CompilerContext): BaseProgram

  /**
   * Prune program based on type and return new program. If cannot prune, return None.
   *
   * @param tipe The complete type returned by the program. By complete we mean *not* the inner type in case of a
   *             GeneratorType(...) for instance.
   */
  def prune(program: BaseProgram, tipe: Type)(implicit programContext: ProgramContext): Option[BaseProgram]

  /**
   * Project top-level record.
   */
  def project(program: BaseProgram, field: String)(implicit programContext: ProgramContext): Option[BaseProgram]

  def normalize(program: BaseProgram)(implicit programContext: ProgramContext): BaseProgram

}
