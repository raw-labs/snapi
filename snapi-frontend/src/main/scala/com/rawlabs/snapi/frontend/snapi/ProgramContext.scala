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

package com.rawlabs.snapi.frontend.snapi

import com.rawlabs.compiler.ProgramEnvironment
import com.rawlabs.snapi.frontend.base.CompilerContext
import com.rawlabs.snapi.frontend.base.errors.ErrorCompilerMessage
import com.rawlabs.snapi.frontend.snapi.extensions.{PackageExtension, PackageExtensionProvider}
import com.rawlabs.snapi.frontend.snapi.source.SnapiProgram
import com.rawlabs.snapi.frontend.inferrer.api.{InferrerInput, InferrerOutput}

import scala.collection.mutable

class ProgramContext(
    override val programEnvironment: ProgramEnvironment,
    override val compilerContext: CompilerContext
) extends com.rawlabs.snapi.frontend.base.ProgramContext {

  private val inferCache = new mutable.HashMap[InferrerInput, Either[String, InferrerOutput]]

  private val dynamicPackageCache = new mutable.HashMap[String, PackageExtension]

  private val stageCompilerCache = new mutable.HashMap[SnapiProgram, Either[ErrorCompilerMessage, SnapiValue]]

  def infer(
      inferrerProperties: InferrerInput
  ): Either[String, InferrerOutput] = {
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
                                 program: SnapiProgram,
                                 f: => Either[ErrorCompilerMessage, SnapiValue]
  ): Either[ErrorCompilerMessage, SnapiValue] = {
    stageCompilerCache.getOrElseUpdate(program, f)
  }

  override def dumpDebugInfo: List[(String, String)] = {
    super.dumpDebugInfo ++
      List("Inferrer Cache" -> inferCache.map { case (k, v) => s"$k -> $v" }.mkString("\n"))
  }

}
