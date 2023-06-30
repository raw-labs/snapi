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

import com.amazonaws.SdkClientException
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import raw.RawTestSuite
import raw.creds.{S3Bucket, S3TestCreds}
import raw.sources.filesystem.{FileSystem, TestFileSystems}

trait TestS3FileSystem extends TestFileSystems with S3TestCreds {
  this: RawTestSuite =>

  def bucket: S3Bucket = UnitTestPrivateBucket

  lazy val awsClient = AmazonS3ClientBuilder
    .standard()
    .withCredentials(
      new AWSStaticCredentialsProvider(
        new BasicAWSCredentials(
          bucket.credentials.get.accessKey,
          bucket.credentials.get.secretKey
        )
      )
    )
    .withRegion(bucket.region.get)
    .build()

  override val newFileSystem: FileSystem = new S3FileSystem(bucket)

  override def writeTestFile(fs: FileSystem, parts: String*): Unit = {
    val s3Path = buildPath(fs, parts.mkString(fs.fileSeparator))
    var retries = 3
    var waitTime = 10;
    var ok = false
    while (!ok) {
      try {
        awsClient.putObject(bucket.name, s3Path, "foobar")
        ok = true
      } catch {
        case e: SdkClientException =>
          retries -= 1
          if (retries <= 0) {
            logger.warn(s"Failed to write $s3Path")
            throw e
          } else {
            logger.warn(s"Error trying to write $s3Path, retries $retries, wait time $waitTime", e)
            Thread.sleep(waitTime)
            waitTime *= 2
          }
      }
    }
  }
}

class TestForwardSlashS3FileSystem extends RawTestSuite with TestS3FileSystem {
  override val basePath = "/s3-test"
  override def filterResults(p: String): Boolean = !p.startsWith(s"${basePath.stripPrefix("/")}/tmp-")
}

class TestNoForwardSlashS3FileSystem extends RawTestSuite with TestS3FileSystem {
  override val basePath = "s3-test"
  override def filterResults(p: String): Boolean = !p.startsWith(s"$basePath/tmp-")
}

class TestRootOfBucketS3FileSystem extends RawTestSuite with TestS3FileSystem {
  override val basePath = ""

  override def filterResults(p: String): Boolean = !p.startsWith("tmp-")

  override def bucket: S3Bucket = UnitTestPrivateBucket2

  override def buildPath(fs: FileSystem, relativePath: String): String = relativePath
}
