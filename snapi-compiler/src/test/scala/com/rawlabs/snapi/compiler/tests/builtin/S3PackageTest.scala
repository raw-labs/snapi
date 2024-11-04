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

package com.rawlabs.snapi.compiler.tests.builtin

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class S3PackageTest extends SnapiTestContext {

  import com.rawlabs.snapi.compiler.tests.TestCredentials._

  awsCreds("raw-aws", rawAwsCredentials)
  // Reading a public bucket without credentials
  test(s"""let
    |  data = Csv.InferAndRead(
    |    S3.Build("$UnitTestPublicBucket", "/students.csv")
    |  )
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))

  // Reading the same file without putting leading slash
  test(s"""let
    |  data = Csv.InferAndRead(
    |    S3.Build("$UnitTestPublicBucket", "students.csv")
    |  )
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))

  // Testing the automatic casting from url to S3Location
  test(s"""let
    |  data = Csv.InferAndRead("s3://$UnitTestPublicBucket/students.csv")
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))

  // Reading a private bucket with credentials in the code
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

  // Reading a private bucket without credential
  test(s"""let
    |  data = Csv.InferAndRead(
    |    S3.Build(
    |      "$UnitTestPrivateBucket",
    |      "/students.csv"
    |    )
    |  )
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should runErrorAs("path not authorized"))

  // Reading a private bucket using a registered credential
  test(s"""let
    |  data = Csv.InferAndRead(
    |    S3.Build(
    |      "$UnitTestPrivateBucket",
    |      "/students.csv",
    |      awsCredential = "raw-aws"
    |    )
    |  )
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))
}
