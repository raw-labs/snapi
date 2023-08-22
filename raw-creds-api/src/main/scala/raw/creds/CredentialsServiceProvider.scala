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

package raw.creds

import java.util.ServiceLoader
import raw.config.RawSettings

import scala.collection.JavaConverters._

object CredentialsServiceProvider {

  private val CREDS_IMPL = "raw.creds.impl"

  private val services = ServiceLoader.load(classOf[CredentialsServiceBuilder]).asScala.toArray

  private var instance: CredentialsService = _
  private val instanceLock = new Object

  def apply()(implicit settings: RawSettings): CredentialsService = {
    instanceLock.synchronized {
      if (instance == null) {
        instance = build()
      }
      return instance
    }
  }

  def get(): CredentialsService = {
    instanceLock.synchronized {
      assert(instance != null, "credentials service not initialized")
      return instance
    }
  }

  private[raw] def set(instance: CredentialsService): Unit = {
    instanceLock.synchronized {
      this.instance = instance
    }
  }

  private def build()(implicit settings: RawSettings): CredentialsService = {
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
