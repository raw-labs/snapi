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

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class LocationPackageTest extends Rql2TruffleCompilerTestContext {

  import raw.compiler.rql2.tests.TestCredentials._

  httpHeaders("dropbox-token", Map("Authorization" -> ("Bearer " + dropboxLongLivedAccessToken)))

  s3Bucket(UnitTestPrivateBucket2, UnitTestPrivateBucket2Cred)

  property("raw.sources.dropbox.clientId", dropboxClientId)

  test("""
    |String.Read(
    |    Http.Post(
    |        "https://api.dropboxapi.com/2/users/get_space_usage",
    |        authCredentialName = "dropbox-token"
    |    )
    |)""".stripMargin)(it => it should run)

  // reading a public s3 bucket without registering or passing credentials
  test(s"""let
    |  data = Csv.InferAndRead("s3://$UnitTestPublicBucket/students.csv")
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))

  test(s"""Location.Ls("s3://$UnitTestPublicBucket/publications/")""") { it =>
    it should evaluateTo("""Collection.Build(
      |  "s3://rawlabs-public-test-data/publications/authors.parquet",
      |  "s3://rawlabs-public-test-data/publications/authors.hjson",
      |  "s3://rawlabs-public-test-data/publications/authors.json",
      |  "s3://rawlabs-public-test-data/publications/authorsSmall.hjson",
      |  "s3://rawlabs-public-test-data/publications/authorsSmall.json",
      |  "s3://rawlabs-public-test-data/publications/publications.hjson",
      |  "s3://rawlabs-public-test-data/publications/publications.json",
      |  "s3://rawlabs-public-test-data/publications/publicationsLarge.hjson",
      |  "s3://rawlabs-public-test-data/publications/publicationsLarge.json",
      |  "s3://rawlabs-public-test-data/publications/publicationsSmall.hjson",
      |  "s3://rawlabs-public-test-data/publications/publicationsSmall.json",
      |  "s3://rawlabs-public-test-data/publications/publicationsSmallWithDups.json"
      |)
      |""".stripMargin)
  }

  test(s"""Location.Ll("s3://$UnitTestPublicBucket/publications/authors.hjson")
    |""".stripMargin) { it =>
    it should evaluateTo("""Collection.Build(
      |  Record.Build(
      |    url="s3://rawlabs-public-test-data/publications/authors.hjson",
      |    metadata=Record.Build(
      |       modified="2016-08-23T04:17:45.000",
      |       size=3036,
      |       blocks=[]
      |    )
      |  )
      |)
      |""".stripMargin)
  }

  // Cannot count lists
  test(s"""
    |let
    |  data = Location.Ll("s3://$UnitTestPublicBucket/publications")
    |in
    |  List.Count(data)
    |""".stripMargin)(it => it should evaluateTo("""12L""".stripMargin))

  // reading a non public s3 bucket passing credentials in the location settings
  test(s"""let
    |  data = Csv.InferAndRead(
    |    S3.Build(
    |      "s3://$UnitTestPrivateBucket/students.csv",
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

}
