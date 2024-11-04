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

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class LocationPackageTest extends SnapiTestContext {

  import com.rawlabs.snapi.compiler.tests.TestCredentials._

  httpHeaders("dropbox-token", Map("Authorization" -> ("Bearer " + dropboxLongLivedAccessToken)))
  awsCreds("raw-aws", rawAwsCredentials)

  property("raw.utils.sources.dropbox.clientId", dropboxClientId)

  test("""
    |String.Read(
    |    Http.Post(
    |        "https://api.dropboxapi.com/2/users/get_space_usage",
    |        authCredentialName = "dropbox-token"
    |    )
    |)""".stripMargin)(it => it should run)

  test("""
    |String.Read(
    |    Http.Post(
    |        "https://api.dropboxapi.com/2/users/get_space_usage",
    |        authCredentialName = "dropboxToken" // wrong: doesn't exist
    |    )
    |)""".stripMargin)(it => it should runErrorAs("unknown credential: dropboxToken"))

  test("""
    |String.Read(
    |    Http.Post(
    |        "https://api.dropboxapi.com/2/users/get_space_usage",
    |        authCredentialName = "raw-aws" // wrong: AWS credential
    |    )
    |)""".stripMargin)(it => it should runErrorAs("credential is not an HTTP headers"))

  // reading a public s3 bucket without registering or passing credentials
  test(s"""let
    |  data = Csv.InferAndRead("s3://$UnitTestPublicBucket/students.csv")
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))

  test(s"""List.Transform(Location.Ls("s3://$UnitTestPublicBucket/publications/"), x -> String.From(x))""") { it =>
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
    |      "$UnitTestPrivateBucket",
    |      "students.csv",
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

  test(s"""let dir = S3.Build(
    |      "$UnitTestPrivateBucket", "/publications/publications-hjson/*.json",
    |      region = "eu-west-1",
    |      accessKey = "${rawAwsCredentials.getAccessKey}",
    |      secretKey = "${rawAwsCredentials.getSecretKey}"
    |    ),
    |    files = Location.Ls(dir),
    |    lines = List.Unnest(files, f -> List.From(String.ReadLines(f)))
    |in
    |  List.Count(lines)
    |""".stripMargin)(it => it should evaluateTo("1000"))

}
