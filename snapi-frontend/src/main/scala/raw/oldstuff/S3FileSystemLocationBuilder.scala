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

package raw.sources.filesystem.s3

import com.typesafe.scalalogging.StrictLogging
import raw.sources.filesystem.api.{FileSystemLocation, FileSystemLocationBuilder}
import raw.sources.api.{LocationDescription, OptionDefinition, SourceContext, StringOptionType}

object S3FileSystemLocationBuilder {
  private val REGEX = """s3:(?://)?([a-z\d][-a-z\d.]*)(/.*)?""".r

  private val OPTION_REGION = "region"
  private val OPTION_ACCESS_KEY = "access_key"
  private val OPTION_SECRET_KEY = "secret_key"
}

class S3FileSystemLocationBuilder extends FileSystemLocationBuilder with StrictLogging {
  import S3FileSystemLocationBuilder._

  override def schemes: Seq[String] = Seq("s3")

  override def validOptions: Seq[OptionDefinition] = Seq(
    OptionDefinition(OPTION_REGION, StringOptionType, mandatory = false),
    OptionDefinition(OPTION_ACCESS_KEY, StringOptionType, mandatory = false),
    OptionDefinition(OPTION_SECRET_KEY, StringOptionType, mandatory = false)
  )

  override def build(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): FileSystemLocation = {
    val url = desc.url
    val groups = getRegexMatchingGroups(url, REGEX)
    val bucket = groups(0)
    val key = groups(1)
    val nonNullKey = if (key == null) "" else key
    val maybeRegion = desc.getStringOpt(OPTION_REGION)
    val maybeAccessKey = desc.getStringOpt(OPTION_ACCESS_KEY)
    val maybeSecretKey = desc.getStringOpt(OPTION_SECRET_KEY)
    val cli = new S3FileSystem(bucket, maybeRegion, maybeAccessKey, maybeSecretKey)(sourceContext.settings)
    new S3Path(cli, nonNullKey, desc.options)
  }

}
