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

package raw.auth.api

import raw.utils.RawSettings
import java.util.ServiceLoader

import scala.collection.JavaConverters._

object AuthServiceProvider {

  private val AUTH_IMPL = "raw.auth.impl"

  private var instance: AuthService = _
  private val instanceLock = new Object

  def apply()(implicit settings: RawSettings): AuthService = {
    instanceLock.synchronized {
      if (instance == null) {
        instance = build()
      }
      return instance
    }
  }

  private[raw] def set(instance: AuthService): Unit = {
    instanceLock.synchronized {
      this.instance = instance
    }
  }

  private def build()(implicit settings: RawSettings): AuthService = {
    val services = ServiceLoader.load(classOf[AuthServiceBuilder]).asScala.toArray
    if (services.isEmpty) {
      throw new GenericAuthException("no authentication service available")
    } else if (services.size > 1) {
      val implClassName = settings.getString(AUTH_IMPL)
      services.find(p => p.name == implClassName) match {
        case Some(builder) => builder.build
        case None => throw new GenericAuthException(s"cannot find authentication service: $implClassName")
      }
    } else {
      services.head.build
    }
  }

}
