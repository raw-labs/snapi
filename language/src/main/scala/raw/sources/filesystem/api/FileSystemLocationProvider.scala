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

package raw.sources.filesystem.api

import raw.utils.RawException

import java.util.ServiceLoader
import scala.collection.JavaConverters._
import raw.sources.api.{LocationDescription, LocationProvider, SourceContext}

object FileSystemLocationProvider extends LocationProvider {

  private var services: Array[FileSystemLocationBuilder] = _
  private val servicesLock = new Object

  private val lock = new Object

  private def loadServices()(implicit sourceContext: SourceContext): Unit = {
    servicesLock.synchronized {
      services = sourceContext.maybeClassLoader match {
        case Some(cl) => ServiceLoader.load(classOf[FileSystemLocationBuilder], cl).asScala.toArray
        case None => ServiceLoader.load(classOf[FileSystemLocationBuilder]).asScala.toArray
      }
    }
  }

  @throws[RawException]
  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): FileSystemLocation = {
    loadServices()
    lock.synchronized {
      getScheme(location.url) match {
        case Some(scheme) =>
          val impls = services.filter(_.schemes.contains(scheme))
          if (impls.isEmpty) throw new FileSystemException(s"no file system location implementation found for $scheme")
          else if (impls.length > 1)
            throw new FileSystemException(s"more than one file system location implementation found for $scheme")
          else impls.head.build(location)
        case None => throw new FileSystemException(s"invalid url: '${location.url}'")
      }
    }
  }

}
