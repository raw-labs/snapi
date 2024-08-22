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

package com.rawlabs.compiler

import com.rawlabs.utils.core.RawSettings

import java.util.ServiceLoader
import scala.collection.JavaConverters._
import scala.collection.mutable

object CompilerServiceProvider {

  private val instanceLock = new Object
  private val instanceMap = new mutable.HashMap[(Set[String]), CompilerService]

  def apply(language: String)(implicit settings: RawSettings): CompilerService = {
    instanceLock.synchronized {
      instanceMap.collectFirst { case (l, i) if l.contains(language) => i } match {
        case Some(instance) => instance
        case None =>
          val instance = build(language)
          instanceMap.put((instance.language), instance)
          instance
      }
    }
  }

  // This method is only used by the test framework.
  def set(language: Set[String], instance: CompilerService): Unit = {
    instanceLock.synchronized {
      if (instance == null) {
        // Stop and remove entries that match the `language`, regardless the class loader.
        instanceMap.filterKeys(_ == language).foreach {
          case (key, compiler) =>
            compiler.stop()
            instanceMap.remove(key)
        }
      } else {
        instanceMap.put(language, instance)
      }
    }
  }

  private def build(language: String)(implicit settings: RawSettings): CompilerService = {
    val services = ServiceLoader.load(classOf[CompilerServiceBuilder]).asScala.toArray
    if (services.isEmpty) {
      throw new CompilerException("no compiler service available")
    } else {
      services.find(p => p.language.contains(language)) match {
        case Some(builder) => builder.build
        case None => throw new CompilerException(s"cannot find compiler service: $language")
      }
    }
  }

}
