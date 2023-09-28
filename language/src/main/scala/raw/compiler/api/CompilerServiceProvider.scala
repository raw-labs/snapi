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

package raw.compiler.api

import raw.compiler.CompilerException
import raw.utils.RawSettings

import java.util.ServiceLoader
import scala.collection.JavaConverters._

object CompilerServiceProvider {

  private val COMPILER_IMPL = "raw.compiler.impl"

  private val services = ServiceLoader.load(classOf[CompilerServiceBuilder]).asScala.toArray

  private var instance: CompilerService = _
  private val instanceLock = new Object

  def apply()(implicit settings: RawSettings): CompilerService = {
    instanceLock.synchronized {
      if (instance == null) {
        instance = build()
      }
      return instance
    }
  }

  private[raw] def set(instance: CompilerService): Unit = {
    instanceLock.synchronized {
      this.instance = instance
    }
  }

  private def build()(implicit settings: RawSettings): CompilerService = {
    if (services.isEmpty) {
      throw new CompilerException("no authentication service available")
    } else if (services.size > 1) {
      val implClassName = settings.getString(COMPILER_IMPL)
      services.find(p => p.name == implClassName) match {
        case Some(builder) => builder.build
        case None => throw new CompilerException(s"cannot find authentication service: $implClassName")
      }
    } else {
      services.head.build
    }
  }
}
