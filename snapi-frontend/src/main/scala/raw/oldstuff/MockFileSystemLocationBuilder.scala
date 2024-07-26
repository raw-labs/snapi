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
import raw.sources.filesystem.api.{FileSystemLocation, FileSystemLocationBuilder}
import raw.sources.api.{LocationDescription, LocationException, OptionDefinition, SourceContext}

object MockFileSystemLocationBuilder {
  private val REGEX = "mock:(?://)?([^:]+):(.*)".r
}

class MockFileSystemLocationBuilder extends FileSystemLocationBuilder with StrictLogging {
  import MockFileSystemLocationBuilder._

  override def schemes: Seq[String] = Seq("mock")

  override def validOptions: Seq[OptionDefinition] = Seq.empty

  override def build(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): FileSystemLocation = {
    val url = desc.url
    val groups = getRegexMatchingGroups(url, REGEX)
    if (groups.length != 2) {
      throw new LocationException(s"invalid URL for mock source: $url")
    }
    val properties = groups(0)
    val underlyingUrl = groups(1)
    try {
      val parser = ConfigFactory.parseString(properties)
      val delay = parser.getDuration("delay").toMillis
      val underlyingLocation = LocationDescription(underlyingUrl, desc.options)
      val delegate = sourceContext.getFileSystem(underlyingLocation)
      new MockPath(delay, delegate)
    } catch {
      case ex: ConfigException => throw new LocationException(s"not a mock location: $underlyingUrl", ex)
    }
  }
}
