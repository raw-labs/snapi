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
import org.scalatest.BeforeAndAfterAll
import raw.client.api.{LocationDescription, LocationSettingKey, LocationStringSetting}
import raw.creds.TestCredentialsService
import raw.creds.api.{AWSCredentials, CredentialsTestContext, S3Bucket}
import raw.creds.local.LocalCredentialsService
import raw.creds.s3.S3TestCreds
import raw.sources.api.SourceContext
import raw.sources.filesystem.api.PathUnauthorizedException
import raw.utils.{InteractiveUser, RawTestSuite, SettingsTestContext, Uid}

class RD10080
    extends RawTestSuite
    with BeforeAndAfterAll
    with SettingsTestContext
    with StrictLogging
    with S3TestCreds
    with CredentialsTestContext {

  val user: InteractiveUser = InteractiveUser(Uid("test"), "test", "test@email.com")
  implicit var sourceContext: SourceContext = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    setCredentials(new TestCredentialsService)
    sourceContext = new SourceContext(user, credentials, settings, None)
  }

  override def afterAll(): Unit = {
    credentials.stop()
    super.afterAll()
  }

  test("credentials passed in location settings have priority") { _ =>
    // Registering a bucket with wrong credentials
    val wrongS3 =
      S3Bucket(UnitTestPrivateBucket.name, Some("eu-west-1"), Some(AWSCredentials("wrong key", "wrong secret")))
    credentials.registerS3Bucket(user, wrongS3)

    try {
      val locationBuilder = new S3FileSystemLocationBuilder
      val locationNoCreds = LocationDescription(s"s3://${UnitTestPrivateBucket.name}/")

      val badS3 = locationBuilder.build(locationNoCreds)
      val error = intercept[PathUnauthorizedException](badS3.ls().toList)
      logger.info(error.getMessage)

      // Now if we pass a location with correct credentials it should work
      val hardCodedCreds = LocationDescription(
        s"s3://${UnitTestPrivateBucket.name}/",
        Map(
          LocationSettingKey("region") -> LocationStringSetting(UnitTestPrivateBucket.region.get),
          LocationSettingKey("s3-access-key") -> LocationStringSetting(UnitTestPrivateBucket.credentials.get.accessKey),
          LocationSettingKey("s3-secret-key") -> LocationStringSetting(UnitTestPrivateBucket.credentials.get.secretKey)
        )
      )

      val goodS3 = locationBuilder.build(hardCodedCreds)(sourceContext)
      val listResult = goodS3.ls().toList
      assert(listResult.nonEmpty)
    } finally {
      credentials.unregisterS3Bucket(user, wrongS3.name)
    }
  }

}
