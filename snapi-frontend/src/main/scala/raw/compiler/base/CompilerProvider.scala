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
import raw.client.api._
import raw.compiler.base.source.{BaseNode, BaseProgram, Type}
import raw.compiler.ProgramOutputWriter
import raw.utils.RawConcurrentHashMap

import java.util.ServiceLoader
import scala.collection.JavaConverters._

object CompilerProvider {

  private val services = ServiceLoader.load(classOf[CompilerBuilder]).asScala.toArray

  private val serviceMap = new RawConcurrentHashMap[String, CompilerBuilder]

  private def apply(language: String): CompilerBuilder = {
    serviceMap.getOrElseUpdate(
      language,
      services.find(p => p.names.contains(language)) match {
        case Some(builder) => builder
        case None => throw new CompilerException(s"cannot find support for language: $language")
      }
    )
  }

  def parse(language: String, code: String): (BaseProgram, Positions) = {
    val builder = apply(language)
    builder.parse(code)
  }

  def prettyPrint(language: String, node: BaseNode): String = {
    val builder = apply(language)
    builder.prettyPrint(node)
  }

  def validate(language: String, program: BaseProgram)(
      implicit programContext: ProgramContext
  ): Either[List[ErrorMessage], Option[Type]] = {
    val builder = apply(language)
    builder.validate(program)
  }

  def compile(language: String, program: BaseProgram)(
      implicit programContext: ProgramContext
  ): Either[List[ErrorMessage], Entrypoint] = {
    val builder = apply(language)
    builder.compile(program)
  }

  def eval(language: String, program: BaseProgram)(
      implicit programContext: ProgramContext
  ): Either[List[ErrorMessage], Value] = {
    val builder = apply(language)
    builder.eval(program)
  }

  def execute(language: String, entrypoint: Entrypoint)(
      implicit programContext: ProgramContext
  ): ProgramOutputWriter = {
    val builder = apply(language)
    builder.execute(entrypoint)
  }

  def clone(language: String, program: BaseProgram)(implicit compilerContext: CompilerContext): BaseProgram = {
    val builder = apply(language)
    builder.clone(program)
  }

  def prune(language: String, program: BaseProgram, tipe: Type)(
      implicit programContext: ProgramContext
  ): Option[BaseProgram] = {
    val builder = apply(language)
    builder.prune(program, tipe)
  }

  def project(language: String, program: BaseProgram, field: String)(
      implicit programContext: ProgramContext
  ): Option[BaseProgram] = {
    val builder = apply(language)
    builder.project(program, field)
  }

  def normalize(language: String, program: BaseProgram)(implicit programContext: ProgramContext): BaseProgram = {
    val builder = apply(language)
    builder.normalize(program)
  }

}
