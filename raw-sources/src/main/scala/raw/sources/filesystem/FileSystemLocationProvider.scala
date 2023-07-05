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

package raw.sources.filesystem

import raw.api.RawException

import java.util.ServiceLoader
import scala.collection.JavaConverters._
import raw.sources.{LocationDescription, LocationProvider, SourceContext}

object FileSystemLocationProvider extends LocationProvider {

  private val services = ServiceLoader.load(classOf[FileSystemLocationBuilder]).asScala.toArray

  private val lock = new Object

  @throws[RawException]
  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): FileSystemLocation = {
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
