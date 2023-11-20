package raw.sources.filesystem.s3

import com.typesafe.scalalogging.StrictLogging
import org.scalatest.BeforeAndAfterAll
import raw.client.api.{LocationDescription, LocationSettingKey, LocationStringSetting}
import raw.creds.api.CredentialsTestContext
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

  override def beforeAll(): Unit = {
    super.beforeAll()
    val creds = new LocalCredentialsService

    setCredentials(creds)
  }

  override def afterAll(): Unit = {
    credentials.stop()
    super.afterAll()
  }
  test("hard-coded s3 credentials have priority") { _ =>
    // First register credentials a bucket with good credentials
    val user = InteractiveUser(Uid("test"), "test", "test@email.com")
    val sourceContext = new SourceContext(user, credentials, settings, None)
    credentials.registerS3Bucket(user, UnitTestPrivateBucket)

    // If we pass a location without credentials it should get them from the credentials service
    val locationBuilder = new S3FileSystemLocationBuilder
    val locationNoCreds = LocationDescription(s"s3://${UnitTestPrivateBucket.name}/")
    val goodS3 = locationBuilder.build(locationNoCreds)(sourceContext)
    val result1 = goodS3.ls().toList
    assert(result1.nonEmpty)

    // Now if we pass a location with wrong hardcoded credentials it should fail to list
    val locationBadCreds = LocationDescription(
      s"s3://${UnitTestPrivateBucket.name}/",
      Map(
        LocationSettingKey("region") -> LocationStringSetting("eu-west-1"),
        LocationSettingKey("s3-access-key") -> LocationStringSetting("wrong key"),
        LocationSettingKey("s3-secret-key") -> LocationStringSetting("wrong secret")
      )
    )
    val badS3 = locationBuilder.build(locationBadCreds)(sourceContext)
    val error = intercept[PathUnauthorizedException](badS3.ls().toList)
    logger.info(error.getMessage)

  }

}
