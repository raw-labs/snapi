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

package raw.sources.filesystem.mock

import com.typesafe.config.{ConfigException, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import raw.sources.filesystem.{FileSystemLocation, FileSystemLocationBuilder, FileSystemLocationProvider}
import raw.sources.{LocationDescription, LocationException, SourceContext}

class MockFileSystemLocationBuilder extends FileSystemLocationBuilder with StrictLogging {

  override def schemes: Seq[String] = Seq("mock")

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): FileSystemLocation = {
    val url = location.url
    if (url.startsWith("mock:")) {
      val f = url.stripPrefix("mock:")
      if (f.nonEmpty) {
        val collonIdx = f.indexOf(":")
        if (collonIdx == -1) {
          throw new LocationException(s"not a mock location: $url: could not find properties section.")
        }
        val propertiesString = f.substring(0, collonIdx)
        val delegateUri = f.substring(collonIdx + 1)
        logger.debug(
          s"Creating mock filesystem with configuration: $propertiesString and delegate filesystem: $delegateUri"
        )
        try {
          val parser = ConfigFactory.parseString(propertiesString)
          val delay = parser.getDuration("delay").toMillis
          val delegate: FileSystemLocation = FileSystemLocationProvider.build(
            LocationDescription(delegateUri, location.settings)
          )
          new MockPath(delay, delegate)
        } catch {
          case ex: ConfigException => throw new LocationException(s"not a mock location: $url", ex)
        }
      } else {
        throw new LocationException(s"not a mock location: $url")
      }
    } else throw new LocationException(s"not a mock location: $url")
  }
}
