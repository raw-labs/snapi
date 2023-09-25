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

import com.typesafe.scalalogging.StrictLogging
import raw.compiler.base.CompilerContext
import raw.compiler.base.errors.BaseError
import raw.compiler.rql2.api.{PackageExtension, PackageExtensionProvider}
import raw.compiler.rql2.source.Rql2Program
import raw.inferrer.api.{InferrerProperties, InputFormatDescriptor}
import raw.runtime.RuntimeContext
import raw.runtime.interpreter.Value

import scala.collection.mutable

class ProgramContext(runtimeContext: RuntimeContext)(
    implicit override val compilerContext: CompilerContext
) extends raw.compiler.base.ProgramContext(runtimeContext, compilerContext)
    with StrictLogging {

  private val inferCache = new mutable.HashMap[InferrerProperties, Either[String, InputFormatDescriptor]]

  private val dynamicPackageCache = new mutable.HashMap[String, PackageExtension]

  private val stageCompilerCache = new mutable.HashMap[Rql2Program, Either[BaseError, Value]]

  def infer(
      inferrerProperties: InferrerProperties
  ): Either[String, InputFormatDescriptor] = {
    inferCache.getOrElseUpdate(
      inferrerProperties,
      compilerContext.infer(inferrerProperties)
    )
  }

  def getPackage(name: String): Option[PackageExtension] = {
    dynamicPackageCache.get(name).orElse(PackageExtensionProvider.getPackage(name))
  }

  def addPackage(pkg: PackageExtension): Unit = {
    dynamicPackageCache.put(pkg.name, pkg)
  }

  def getOrAddStagedCompilation(
      program: Rql2Program,
      f: => Either[BaseError, Value]
  ): Either[BaseError, Value] = {
    stageCompilerCache.getOrElseUpdate(program, f)
  }

  override def dumpDebugInfo: List[(String, String)] = {
    super.dumpDebugInfo ++
      List("Inferrer Cache" -> inferCache.map { case (k, v) => s"$k -> $v" }.mkString("\n"))
  }

}
