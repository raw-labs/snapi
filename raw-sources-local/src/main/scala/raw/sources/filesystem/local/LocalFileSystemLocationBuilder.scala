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

package raw.sources.filesystem.local

import raw.sources.{LocationDescription, LocationException, SourceContext}
import raw.sources.filesystem.{FileSystemLocation, FileSystemLocationBuilder}

class LocalFileSystemLocationBuilder extends FileSystemLocationBuilder {

  override def schemes: Seq[String] = Seq("file")

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): FileSystemLocation = {
    val url = location.url
    if (url.startsWith("file:")) {
      val f = url.stripPrefix("file:")
      if (f.nonEmpty) new LocalPath(url.stripPrefix("file:"), location.cacheStrategy, location.retryStrategy)
      else throw new LocationException("not a local location")
    } else throw new LocationException("not a local location")
  }

}
