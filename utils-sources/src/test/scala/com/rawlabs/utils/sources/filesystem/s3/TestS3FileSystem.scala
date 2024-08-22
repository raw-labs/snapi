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

package com.rawlabs.utils.sources.filesystem.s3

import com.rawlabs.utils.core.RawTestSuite
import com.rawlabs.utils.sources.filesystem.api.{FileSystem, TestFileSystems}
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

import java.nio.charset.StandardCharsets

trait TestS3FileSystem extends TestFileSystems {
  this: RawTestSuite =>

  val bucketName: String

  val bucketRegion: String

  val bucketAccessKey: String

  val bucketSecretKey: String

  lazy val awsClient = {
    val credentials = AwsBasicCredentials.create(
      bucketAccessKey,
      bucketSecretKey
    )

    S3Client
      .builder()
      .credentialsProvider(StaticCredentialsProvider.create(credentials))
      .region(Region.of(bucketRegion))
      .build()
  }

  override lazy val newFileSystem: FileSystem =
    new S3FileSystem(bucketName, Some(bucketRegion), Some(bucketAccessKey), Some(bucketSecretKey))

  override def writeTestFile(fs: FileSystem, parts: String*): Unit = {
    val s3Path = buildPath(fs, parts.mkString(fs.fileSeparator))
    var retries = 3
    var waitTime = 10;
    var ok = false
    while (!ok) {
      try {
        val putRequest = PutObjectRequest
          .builder()
          .bucket(bucketName)
          .key(s3Path)
          .build()

        val requestBody = RequestBody.fromString("foobar", StandardCharsets.UTF_8)

        awsClient.putObject(putRequest, requestBody)
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
  override val bucketName = "rawlabs-private-test-data"
  override val bucketRegion = "eu-west-1"
  override val bucketAccessKey = sys.env("RAW_AWS_ACCESS_KEY_ID")
  override val bucketSecretKey = sys.env("RAW_AWS_SECRET_ACCESS_KEY")
  override val basePath = "/s3-test"
  override def filterResults(p: String): Boolean = !p.startsWith(s"${basePath.stripPrefix("/")}/tmp-")
}

class TestNoForwardSlashS3FileSystem extends RawTestSuite with TestS3FileSystem {
  override val bucketName = "rawlabs-private-test-data"
  override val bucketRegion = "eu-west-1"
  override val bucketAccessKey = sys.env("RAW_AWS_ACCESS_KEY_ID")
  override val bucketSecretKey = sys.env("RAW_AWS_SECRET_ACCESS_KEY")
  override val basePath = "s3-test"
  override def filterResults(p: String): Boolean = !p.startsWith(s"$basePath/tmp-")
}

class TestRootOfBucketS3FileSystem extends RawTestSuite with TestS3FileSystem {
  override val bucketName = "rawlabs-unit-tests"
  override val bucketRegion = "eu-west-1"
  override val bucketAccessKey = sys.env("RAW_AWS_ACCESS_KEY_ID")
  override val bucketSecretKey = sys.env("RAW_AWS_SECRET_ACCESS_KEY")

  override val basePath = ""

  override def filterResults(p: String): Boolean = !p.startsWith("tmp-")

  override def buildPath(fs: FileSystem, relativePath: String): String = relativePath
}
