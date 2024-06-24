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

package raw.sources.filesystem.dropbox

import raw.client.api.{OptionType, OptionValue, StringOptionType, StringOptionValue}
import raw.sources.api.SourceContext
import raw.sources.filesystem.api.{FileSystemException, FileSystemLocation, FileSystemLocationBuilder}

import scala.util.matching.Regex

object DropboxFileSystemLocationBuilder {
  private val REGEX: Regex = "dropbox:(?://)?(.*)".r
  private val OPTION_ACCESS_TOKEN = "access_token"
  private val OPTION_USER = "user"
  private val OPTION_PASSWORD = "password"
}

class DropboxFileSystemLocationBuilder extends FileSystemLocationBuilder {

  import DropboxFileSystemLocationBuilder._

  override def schemes: Seq[String] = Seq("dropbox")

  override def regex: Regex = REGEX

  override def validOptions: Map[String, OptionType] = Map(
    OPTION_ACCESS_TOKEN -> StringOptionType,
    OPTION_USER -> StringOptionType,
    OPTION_PASSWORD -> StringOptionType
  )

  override def build(regexCaptures: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): FileSystemLocation = {
    implicit val settings = sourceContext.settings
    val path = regexCaptures(0)
    val dropboxClient =
      if (options.contains(OPTION_ACCESS_TOKEN)) {
        val StringOptionValue(accessToken) = options(OPTION_ACCESS_TOKEN)
        new DropboxFileSystem(accessToken)
      } else if (options.contains(OPTION_USER) && options.contains(OPTION_PASSWORD)) {
        val StringOptionValue(user) = options(OPTION_USER)
        val StringOptionValue(password) = options(OPTION_PASSWORD)
        new DropboxFileSystem(user, password)
      } else {
        throw new FileSystemException("missing options for Dropbox")
      }
    new DropboxPath(dropboxClient, path, options)
  }
}
