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

package raw.creds.s3

import raw.creds.api._

trait S3TestCreds {

  // Bucket with public access
  val UnitTestPublicBucket = S3Bucket("rawlabs-public-test-data", Some("eu-west-1"), None)
  val accessKeyId = sys.env("RAW_AWS_ACCESS_KEY_ID")
  val secretKeyId = sys.env("RAW_AWS_SECRET_ACCESS_KEY")
  // IAM user 'unit-test-private-bucket', which only has permissions only to access bucket 'rawlabs-private-test-data'
  val UnitTestPrivateBucket = S3Bucket(
    "rawlabs-private-test-data",
    Some("eu-west-1"),
    Some(AWSCredentials(accessKeyId, secretKeyId))
  )
  val UnitTestPrivateBucket2 = S3Bucket(
    "rawlabs-unit-tests",
    Some("eu-west-1"),
    Some(AWSCredentials(accessKeyId, secretKeyId))
  )
  val UnitTestEmptyBucketPrivateBucket = S3Bucket(
    "rawlabs-unit-test-empty-bucket",
    Some("eu-west-1"),
    Some(AWSCredentials(accessKeyId, secretKeyId))
  )

  val UnitTestListRootPrivateBucket = S3Bucket(
    "rawlabs-unit-test-list-root",
    Some("eu-west-1"),
    Some(AWSCredentials(accessKeyId, secretKeyId))
  )

  val unitTestPrivateBucketUsEast1 = S3Bucket(
    "rawlabs-unit-tests-us-east-1",
    Some("us-east-1"),
    Some(AWSCredentials(accessKeyId, secretKeyId))
  )
}
