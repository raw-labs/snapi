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

package raw.creds.api

import raw.client.utils.RawSettings

import java.util.ServiceLoader
import scala.collection.JavaConverters._

object CredentialsServiceProvider {

  private val CREDS_IMPL = "raw.creds.impl"

  def apply(maybeClassLoader: Option[ClassLoader] = None)(implicit settings: RawSettings): CredentialsService = {
    maybeClassLoader match {
      case Some(cl) => apply(cl)
      case None => apply()
    }
  }

  def apply()(implicit settings: RawSettings): CredentialsService = {
    build()
  }

  def apply(classLoader: ClassLoader)(implicit settings: RawSettings): CredentialsService = {
    build(Some(classLoader))
  }

  private def build(
      maybeClassLoader: Option[ClassLoader] = None
  )(implicit settings: RawSettings): CredentialsService = {
    val services = maybeClassLoader match {
      case Some(cl) => ServiceLoader.load(classOf[CredentialsServiceBuilder], cl).asScala.toArray
      case None => ServiceLoader.load(classOf[CredentialsServiceBuilder]).asScala.toArray
    }
    if (services.isEmpty) {
      throw new CredentialsException("no credentials service available")
    } else if (services.size > 1) {
      val implClassName = settings.getString(CREDS_IMPL)
      services.find(p => p.name == implClassName) match {
        case Some(builder) => builder.build
        case None => throw new CredentialsException(s"cannot find credentials service: $implClassName")
      }
    } else {
      services.head.build
    }
  }

}
