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
import raw.{RawTestSuite, SettingsTestContext}
import raw.creds.api.S3Bucket
import raw.creds.s3.S3TestCreds

class TestRootS3FileSystem extends RawTestSuite with S3TestCreds with SettingsTestContext with StrictLogging {

  test("list ''") { _ =>
    val fs = new S3FileSystem(UnitTestPrivateBucket)

    val list = fs.listContents("").toList
    logger.debug("Result: " + list)
  }

  test("list bucket region us-east-1") { _ =>
    val fs = new S3FileSystem(unitTestPrivateBucketUsEast1)
    val list = fs.listContents("").toList
    logger.debug("Result: " + list)
  }

  test("list bucket us-east-1 without specifying a region") { _ =>
    val bucket = S3Bucket(unitTestPrivateBucketUsEast1.name, None, unitTestPrivateBucketUsEast1.credentials)
    val fs = new S3FileSystem(bucket)
    val list = fs.listContents("").toList
    logger.debug("Result: " + list)
  }

}

class TestRootOfEmptyBucketS3FileSystem
    extends RawTestSuite
    with S3TestCreds
    with SettingsTestContext
    with StrictLogging {

  test("list ''") { _ =>
    val fs = new S3FileSystem(UnitTestEmptyBucketPrivateBucket)
    val list = fs.listContents("").toList
    assert(list.isEmpty)
  }

}
