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

import java.util.ServiceLoader
import scala.collection.JavaConverters._
import raw.sources.api.{LocationProvider, SourceContext}
import raw.client.api.LocationDescription

object JdbcLocationProvider extends LocationProvider {

  private var services: Array[JdbcLocationBuilder] = _
  private val servicesLock = new Object

  private val lock = new Object

  private def loadServices()(implicit sourceContext: SourceContext): Unit = {
    servicesLock.synchronized {
      services = sourceContext.maybeClassLoader match {
        case Some(cl) => ServiceLoader.load(classOf[JdbcLocationBuilder], cl).asScala.toArray
        case None => ServiceLoader.load(classOf[JdbcLocationBuilder]).asScala.toArray
      }
    }
  }

  @throws[JdbcLocationException]
  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcLocation = {
    loadServices()
    lock.synchronized {
      getScheme(location.url) match {
        case Some(scheme) =>
          val impls = services.filter(_.schemes.contains(scheme))
          if (impls.isEmpty)
            throw new JdbcLocationException(s"no relational database location implementation found for $scheme")
          else if (impls.size > 1) throw new JdbcLocationException(
            s"more than one relational database location implementation found for $scheme"
          )
          else impls.head.build(location)
        case None => throw new JdbcLocationException(s"invalid url: '${location.url}'")
      }
    }
  }

}
