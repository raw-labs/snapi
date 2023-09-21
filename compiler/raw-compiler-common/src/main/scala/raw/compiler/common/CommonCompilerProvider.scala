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

import raw.compiler.base.CompilerContext
import raw.compiler.CompilerException
import raw.utils.RawConcurrentHashMap

import java.util.ServiceLoader
import scala.collection.JavaConverters._

object CommonCompilerProvider {

  private val services = ServiceLoader.load(classOf[CommonCompilerBuilder]).asScala.toArray

  private val serviceMap = new RawConcurrentHashMap[String, CommonCompilerBuilder]

  private def findBuilder(language: String): CommonCompilerBuilder = {
    serviceMap.getOrElseUpdate(
      language,
      services.find(p => p.names.contains(language)) match {
        case Some(builder) => builder
        case None => throw new CompilerException(s"cannot find support for language: $language")
      }
    )
  }

  def apply(language: String)(implicit compilerContext: CompilerContext): Compiler = {
    findBuilder(language).getCompiler(compilerContext)
  }
}
