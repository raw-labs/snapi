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
import raw.sources.filesystem.api.{FileSystemLocation, FileSystemLocationBuilder, FileSystemLocationProvider}
import raw.sources.api.{LocationException, SourceContext}
import raw.client.api.{LocationDescription, OptionType, OptionValue}

import scala.util.matching.Regex

object MockFileSystemLocationBuilder {
  private val REGEX = "mock:(?://)?([^:]+):(.*)".r
}

class MockFileSystemLocationBuilder extends FileSystemLocationBuilder with StrictLogging {
  import MockFileSystemLocationBuilder._

  override def schemes: Seq[String] = Seq("mock")

  override def regex: Regex = REGEX

  override def validOptions: Map[String, OptionType] = Map.empty

  override def build(groups: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): FileSystemLocation = {
    val properties = groups(0)
    val url = groups(1)
    try {
      val parser = ConfigFactory.parseString(properties)
      val delay = parser.getDuration("delay").toMillis
      val delegate = sourceContext.getFileSystem(url, options)
      new MockPath(delay, delegate)
    } catch {
      case ex: ConfigException => throw new LocationException(s"not a mock location: $url", ex)
    }
  }
}
