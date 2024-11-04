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

package com.rawlabs.snapi.compiler.tests.builtin.credentials

import com.rawlabs.snapi.compiler.tests.TestCredentials
import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class S3PackageTest extends SnapiTestContext {

  import TestCredentials._

  awsCreds("raw-aws", rawAwsCredentials)

  // reading a non public s3 bucket passing credentials in the location settings
  test(s"""let
    |  data = Csv.InferAndRead(
    |    S3.Build(
    |      "$UnitTestPrivateBucket",
    |      "/students.csv",
    |      region = "eu-west-1",
    |      accessKey = "${rawAwsCredentials.getAccessKey}",
    |      secretKey = "${rawAwsCredentials.getSecretKey}"
    |    )
    |  )
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))

  // using a private bucket registered in the credentials server
  test(s"""String.Read(S3.Build("$UnitTestPrivateBucket2", "/file1.csv", awsCredential = "raw-aws"))
    |""".stripMargin)(it => it should evaluateTo(""" "foobar" """))

  // listing a s3 bucket from us-east-1 (non default region)
  test(s"""let
    |  data = Location.Ll(
    |    S3.Build(
    |      "$unitTestPrivateBucketUsEast1",
    |      "/csvs/01",
    |      region = "us-east-1",
    |      accessKey = "${rawAwsCredentials.getAccessKey}",
    |      secretKey = "${rawAwsCredentials.getSecretKey}"
    |    )
    |  )
    |in
    |  data.url
    |""".stripMargin)(it => it should evaluateTo("""[
    |   "s3://rawlabs-unit-tests-us-east-1/csvs/01/data2.csv",
    |   "s3://rawlabs-unit-tests-us-east-1/csvs/01/data1.csv"
    |]""".stripMargin))

  // listing a s3 bucket from us-east-1 (non default region, using the credentials name)
  test(s"""let
    |  data = Location.Ll(
    |    S3.Build(
    |      "$unitTestPrivateBucketUsEast1",
    |      "/csvs/01",
    |      region = "us-east-1",
    |      awsCredential = "raw-aws"
    |    )
    |  )
    |in
    |  data.url
    |""".stripMargin)(it => it should evaluateTo("""[
    |   "s3://rawlabs-unit-tests-us-east-1/csvs/01/data2.csv",
    |   "s3://rawlabs-unit-tests-us-east-1/csvs/01/data1.csv"
    |]""".stripMargin))

  // listing a s3 bucket from us-east-1 without passing the region
  test(s"""let
    |  data = Location.Ll(
    |    S3.Build(
    |      "$unitTestPrivateBucketUsEast1",
    |      "/csvs/01",
    |      accessKey = "${rawAwsCredentials.getAccessKey}",
    |      secretKey = "${rawAwsCredentials.getSecretKey}"
    |    )
    |  )
    |in
    |  data.url
    |""".stripMargin)(it => it should evaluateTo("""[
    |   "s3://rawlabs-unit-tests-us-east-1/csvs/01/data2.csv",
    |   "s3://rawlabs-unit-tests-us-east-1/csvs/01/data1.csv"
    |]""".stripMargin))

  // listing a s3 bucket from us-east-1 without passing the region (using the credentials name)
  test(s"""let
    |  data = Location.Ll(
    |    S3.Build(
    |      "$unitTestPrivateBucketUsEast1",
    |      "/csvs/01",
    |      awsCredential = "raw-aws"
    |    )
    |  )
    |in
    |  data.url
    |""".stripMargin)(it => it should evaluateTo("""[
    |   "s3://rawlabs-unit-tests-us-east-1/csvs/01/data2.csv",
    |   "s3://rawlabs-unit-tests-us-east-1/csvs/01/data1.csv"
    |]""".stripMargin))

  // error with wrong credential name
  test(s"""let
    |  data = Location.Ll(
    |    S3.Build(
    |      "$unitTestPrivateBucketUsEast1",
    |      "/csvs/01",
    |      awsCredential = "private.data.us" // a typo
    |    )
    |  )
    |in
    |  data.url
    |""".stripMargin)(it => it should runErrorAs("unknown credential: private.data.us"))

}
