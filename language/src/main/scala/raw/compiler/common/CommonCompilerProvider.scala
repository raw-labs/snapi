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

import java.util.ServiceLoader
import scala.collection.JavaConverters._

object CommonCompilerProvider {

  def apply(language: String, maybeClassLoader: Option[ClassLoader] = None)(
      implicit compilerContext: CompilerContext
  ): Compiler = {
    val services = maybeClassLoader match {
      case Some(cl) => ServiceLoader.load(classOf[CommonCompilerBuilder], cl).asScala.toArray
      case None => ServiceLoader.load(classOf[CommonCompilerBuilder]).asScala.toArray
    }
    services.find(p => p.names.contains(language)) match {
      case Some(builder) => builder.getCompiler(compilerContext)
      case None => throw new CompilerException(s"cannot find support for language: $language")
    }
  }
}
