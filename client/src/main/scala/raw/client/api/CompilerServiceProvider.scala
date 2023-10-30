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

package raw.client.api

import raw.utils.RawSettings

import java.util.ServiceLoader
import scala.collection.JavaConverters._

object CompilerServiceProvider {

  private var instance: CompilerService = _
  private val instanceLock = new Object

  def apply(language: String, maybeClassLoader: Option[ClassLoader] = None)(
      implicit settings: RawSettings
  ): CompilerService = {
    maybeClassLoader match {
      case Some(cl) => apply(language, cl)
      case None => apply(language)
    }
  }

  def apply(language: String)(implicit settings: RawSettings): CompilerService = {
    instanceLock.synchronized {
      if (instance == null) {
        instance = build(language)
      }
      return instance
    }
  }

  def apply(language: String, classLoader: ClassLoader)(implicit settings: RawSettings): CompilerService = {
    instanceLock.synchronized {
      if (instance == null) {
        instance = build(language, Some(classLoader))
      }
      return instance
    }
  }

  private[raw] def set(instance: CompilerService): Unit = {
    instanceLock.synchronized {
      this.instance = instance
    }
  }

  private def build(language: String, maybeClassLoader: Option[ClassLoader] = None)(
      implicit settings: RawSettings
  ): CompilerService = {
    val services = maybeClassLoader match {
      case Some(cl) => ServiceLoader.load(classOf[CompilerServiceBuilder], cl).asScala.toArray
      case None => ServiceLoader.load(classOf[CompilerServiceBuilder]).asScala.toArray
    }
    if (services.isEmpty) {
      throw new CompilerException("no compiler service available")
    } else if (services.size > 1) {
      services.find(p => p.language.contains(language)) match {
        case Some(builder) => builder.build(maybeClassLoader)
        case None => throw new CompilerException(s"cannot find compiler service: $language")
      }
    } else {
      services.head.build(maybeClassLoader)
    }
  }
}
