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

import raw.sources.api.{LocationDescription, LocationException, OptionDefinition, SourceContext}
import raw.sources.filesystem.api.{FileSystemLocation, FileSystemLocationBuilder}

import scala.util.matching.Regex

object LocalFileSystemLocationBuilder {
  private val REGEX: Regex = "file:(?://)?(.*)".r
}

class LocalFileSystemLocationBuilder extends FileSystemLocationBuilder {

  import LocalFileSystemLocationBuilder._

  override def schemes: Seq[String] = Seq("file")

  override def validOptions: Seq[OptionDefinition] = Seq.empty

  override def build(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): FileSystemLocation = {
    val url = desc.url
    val groups = getRegexMatchingGroups(url, REGEX)
    val localUrl = groups(0)
    val f = localUrl.stripPrefix("file:")
    if (f.nonEmpty) new LocalPath(f)
    else throw new LocationException("not a local location")
  }

}
