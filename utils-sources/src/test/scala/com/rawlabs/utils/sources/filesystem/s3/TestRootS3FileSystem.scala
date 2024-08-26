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

import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.utils.core.{RawTestSuite, SettingsTestContext}

class TestRootS3FileSystem extends RawTestSuite with SettingsTestContext with StrictLogging {

  test("list ''") { _ =>
    val fs = new S3FileSystem(
      "rawlabs-private-test-data",
      Some("eu-west-1"),
      Some(sys.env("RAW_AWS_ACCESS_KEY_ID")),
      Some(sys.env("RAW_AWS_SECRET_ACCESS_KEY"))
    )

    val list = fs.listContents("").toList
    logger.debug("Result: " + list)
  }

  test("list bucket region us-east-1") { _ =>
    val fs = new S3FileSystem(
      "rawlabs-unit-tests-us-east-1",
      Some("us-east-1"),
      Some(sys.env("RAW_AWS_ACCESS_KEY_ID")),
      Some(sys.env("RAW_AWS_SECRET_ACCESS_KEY"))
    )
    val list = fs.listContents("").toList
    logger.debug("Result: " + list)
  }

  test("list bucket us-east-1 without specifying a region") { _ =>
    val fs = new S3FileSystem(
      "rawlabs-unit-tests-us-east-1",
      None,
      Some(sys.env("RAW_AWS_ACCESS_KEY_ID")),
      Some(sys.env("RAW_AWS_SECRET_ACCESS_KEY"))
    )
    val list = fs.listContents("").toList
    logger.debug("Result: " + list)
  }

}

class TestRootOfEmptyBucketS3FileSystem extends RawTestSuite with SettingsTestContext with StrictLogging {

  test("list ''") { _ =>
    val fs = new S3FileSystem(
      "rawlabs-unit-test-empty-bucket",
      Some("eu-west-1"),
      Some(sys.env("RAW_AWS_ACCESS_KEY_ID")),
      Some(sys.env("RAW_AWS_SECRET_ACCESS_KEY"))
    )
    val list = fs.listContents("").toList
    assert(list.isEmpty)
  }

}
