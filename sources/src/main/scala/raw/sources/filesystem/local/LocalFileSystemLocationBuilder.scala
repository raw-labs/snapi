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

import raw.sources.api.{LocationException, SourceContext}
import raw.client.api.{OptionType, OptionValue}
import raw.sources.filesystem.api.{FileSystemLocation, FileSystemLocationBuilder}
import raw.utils.RawSettings

import scala.util.matching.Regex

object LocalFileSystemLocationBuilder {
  private val REGEX: Regex = "file:(?://)?(.*)".r
}

class LocalFileSystemLocationBuilder extends FileSystemLocationBuilder {

  override def schemes: Seq[String] = Seq("file")

  override def regex: Regex = LocalFileSystemLocationBuilder.REGEX

  override def validOptions: Map[String, OptionType] = Map.empty

  override def build(groups: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): FileSystemLocation = {
    val url = groups(0)
    val f = url.stripPrefix("file:")
    if (f.nonEmpty) new LocalPath(url.stripPrefix("file:"))
    else throw new LocationException("not a local location")
  }

}
