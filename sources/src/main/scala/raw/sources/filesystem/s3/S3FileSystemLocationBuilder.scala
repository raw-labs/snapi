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
import raw.creds.api.{AWSCredentials, S3Bucket}
import raw.sources.filesystem.api.{FileSystemException, FileSystemLocation, FileSystemLocationBuilder}
import raw.sources.api.{LocationException, SourceContext}
import raw.client.api.{LocationDescription, OptionType, OptionValue, StringOptionType, StringOptionValue}

object S3FileSystemLocationBuilder {
  private val REGEX = """s3:(?://)?([a-z\d][-a-z\d.]*)(/.*)?""".r

  private val OPTION_REGION = "region"
  private val OPTION_ACCESS_KEY = "access_key"
  private val OPTION_SECRET_KEY = "secret_key"
}

class S3FileSystemLocationBuilder extends FileSystemLocationBuilder with StrictLogging {
  import S3FileSystemLocationBuilder._

  override def schemes: Seq[String] = Seq("s3")

  override def regex: scala.util.matching.Regex = REGEX

  override def validOptions: Map[String, OptionType] = Map(
    OPTION_REGION -> StringOptionType,
    OPTION_ACCESS_KEY -> StringOptionType,
    OPTION_SECRET_KEY -> StringOptionType
  )

  override def build(groups: List[String], options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): FileSystemLocation = {
    val bucket = groups(0)
    val key = groups(1)
    val nonNullKey = if (key == null) "" else key
    val maybeRegion = options.get(OPTION_REGION).map(_.asInstanceOf[StringOptionValue].value)
    val maybeAccessKey = options.get(OPTION_ACCESS_KEY).map(_.asInstanceOf[StringOptionValue].value)
    val maybeSecretKey = options.get(OPTION_SECRET_KEY).map(_.asInstanceOf[StringOptionValue].value)
    val cli = new S3FileSystem(bucket, maybeRegion, maybeAccessKey, maybeSecretKey)(sourceContext.settings)
    new S3Path(cli, nonNullKey, options)
  }

}
