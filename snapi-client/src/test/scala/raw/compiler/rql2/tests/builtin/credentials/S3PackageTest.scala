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

package raw.compiler.rql2.tests.builtin.credentials

import raw.compiler.rql2.tests.TestCredentials
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class S3PackageTest extends Rql2TruffleCompilerTestContext {

  import TestCredentials._

  s3Bucket(UnitTestPrivateBucket2, UnitTestPrivateBucket2Cred)

  // reading a non public s3 bucket passing credentials in the location settings
  test(s"""let
    |  data = Csv.InferAndRead(
    |    S3.Build(
    |      "s3://rawlabs-private-test-data/students.csv",
    |      region = "${UnitTestPrivateBucketCred.region.get}",
    |      accessKey = "${UnitTestPrivateBucketCred.accessKey.get}",
    |      secretKey = "${UnitTestPrivateBucketCred.secretKey.get}"
    |    )
    |  )
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))

  // using a private bucket registered in the credentials server
  test(s"""String.Read(S3.Build("s3://$UnitTestPrivateBucket2/file1.csv"))
    |""".stripMargin)(it => it should evaluateTo(""" "foobar" """))

  // listing a s3 bucket from us-east-1 (non default region)
  test(s"""let
    |  data = Location.Ls(
    |    S3.Build(
    |      "s3://$unitTestPrivateBucketUsEast1/csvs/01",
    |      region = "${unitTestPrivateBucketUsEast1Cred.region.get}",
    |      accessKey = "${unitTestPrivateBucketUsEast1Cred.accessKey.get}",
    |      secretKey = "${unitTestPrivateBucketUsEast1Cred.secretKey.get}"
    |    )
    |  )
    |in
    |  data
    |""".stripMargin)(it => it should evaluateTo("""[
    |   "s3://rawlabs-unit-tests-us-east-1/csvs/01/data2.csv",
    |   "s3://rawlabs-unit-tests-us-east-1/csvs/01/data1.csv"
    |]""".stripMargin))

  // listing a s3 bucket from us-east-1 without passing the region
  test(s"""let
    |  data = Location.Ls(
    |    S3.Build(
    |      "s3://$unitTestPrivateBucketUsEast1/csvs/01",
    |      accessKey = "${unitTestPrivateBucketUsEast1Cred.accessKey.get}",
    |      secretKey = "${unitTestPrivateBucketUsEast1Cred.secretKey.get}"
    |    )
    |  )
    |in
    |  data
    |""".stripMargin)(it => it should evaluateTo("""[
    |   "s3://rawlabs-unit-tests-us-east-1/csvs/01/data2.csv",
    |   "s3://rawlabs-unit-tests-us-east-1/csvs/01/data1.csv"
    |]""".stripMargin))

}
