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

package raw.inferrer.api

import java.util.ServiceLoader
import scala.collection.JavaConverters._
import raw.sources.api.SourceContext

object InferrerServiceProvider {

  private val INFERRER_IMPL = "raw.inferrer.impl"

  def apply(maybeClassLoader: Option[ClassLoader] = None)(implicit sourceContext: SourceContext): InferrerService = {
    maybeClassLoader match {
      case Some(cl) => apply(cl)
      case None => apply()
    }
  }

  def apply()(implicit sourceContext: SourceContext): InferrerService = {
    build()
  }

  def apply(classLoader: ClassLoader)(implicit sourceContext: SourceContext): InferrerService = {
    build(Some(classLoader))
  }

  @throws[InferrerException]
  private def build(
      maybeClassLoader: Option[ClassLoader] = None
  )(implicit sourceContext: SourceContext): InferrerService = {
    val services = maybeClassLoader match {
      case Some(cl) => ServiceLoader.load(classOf[InferrerServiceBuilder], cl).asScala.toArray
      case None => ServiceLoader.load(classOf[InferrerServiceBuilder]).asScala.toArray
    }
    if (services.isEmpty) {
      throw new InferrerException("no inferrer service available")
    } else if (services.length > 1) {
      val implClassName = sourceContext.settings.getString(INFERRER_IMPL)
      services.find(p => p.name == implClassName) match {
        case Some(builder) => builder.build
        case None => throw new InferrerException(s"cannot find inferrer service: $implClassName")
      }
    } else {
      services.head.build
    }
  }

}
