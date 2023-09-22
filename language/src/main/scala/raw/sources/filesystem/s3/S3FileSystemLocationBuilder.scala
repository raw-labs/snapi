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
import raw.sources.filesystem.{FileSystemLocation, FileSystemLocationBuilder}
import raw.sources.{LocationDescription, LocationException, SourceContext}

class S3FileSystemLocationBuilder extends FileSystemLocationBuilder with StrictLogging {

  // TODO (msb): Maybe we should have it support 's3a' as well?
  private val s3Regex = """s3://([a-z\d][-a-z\d.]*)(/.*)?""".r

  override def schemes: Seq[String] = Seq("s3")

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): FileSystemLocation = {
    location.url match {
      case s3Regex(bucket, key) =>
        val nonNullKey =
          if (key == null) {
            ""
          } else {
            key
          }

        // If the bucket is not registered then we bypass the credentials provider and use local credentials.
        val s3Bucket = sourceContext.credentialsService.getS3Bucket(sourceContext.user, bucket) match {
          case Some(cred) => cred
          case None =>
            val region = location.getStringSetting("s3-region")
            val credentials = for {
              accessKey <- location.getStringSetting("s3-access-key")
              secretKey <- location.getStringSetting("s3-secret-key")
            } yield AWSCredentials(accessKey, secretKey)
            S3Bucket(bucket, region, credentials)
        }
        val cli = new S3FileSystem(s3Bucket)(
          sourceContext.settings
        )
        new S3Path(
          cli,
          nonNullKey,
          location
        )
      case _ => throw new LocationException(s"not an S3 location")
    }

  }

}
