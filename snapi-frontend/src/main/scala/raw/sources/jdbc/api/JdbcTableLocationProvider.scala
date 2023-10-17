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

package raw.sources.jdbc.api

import raw.client.api.LocationDescription

import java.util.ServiceLoader
import scala.collection.JavaConverters._
import raw.sources.api.{LocationProvider, SourceContext}

object JdbcTableLocationProvider extends LocationProvider {

  private var services: Array[JdbcTableLocationBuilder] = _
  private val servicesLock = new Object

  private val lock = new Object

  def isSupported(url: String): Boolean = {
    lock.synchronized {
      getScheme(url) match {
        case Some(scheme) => services.exists(_.schemes.contains(scheme))
        case None => false
      }
    }
  }

  private def loadServices()(implicit sourceContext: SourceContext): Unit = {
    servicesLock.synchronized {
      services = sourceContext.maybeClassLoader match {
        case Some(cl) => ServiceLoader.load(classOf[JdbcTableLocationBuilder], cl).asScala.toArray
        case None => ServiceLoader.load(classOf[JdbcTableLocationBuilder]).asScala.toArray
      }
    }
  }

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcTableLocation = {
    loadServices()
    lock.synchronized {
      getScheme(location.url) match {
        case Some(scheme) =>
          val impls = services.filter(_.schemes.contains(scheme))
          if (impls.isEmpty) throw new JdbcLocationException(s"no table location implementation found for $scheme")
          else if (impls.size > 1)
            throw new JdbcLocationException(s"more than one table location implementation found for $scheme")
          else impls.head.build(location)
        case None => throw new JdbcLocationException(s"invalid url: '${location.url}'")
      }
    }
  }

}
