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

import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class S3PackageTest extends Rql2TruffleCompilerTestContext {

  import com.rawlabs.snapi.compiler.tests.TestCredentials._

  // reading a public s3 bucket without registering or passing credentials
  test(s"""let
    |  data = Csv.InferAndRead(
    |    S3.Build("s3://rawlabs-public-test-data/students.csv")
    |  )
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))

  // reading a public s3 bucket without registering or passing credentials
  test(s"""let
    |  data = Csv.InferAndRead(S3.Build("s3://$UnitTestPublicBucket/students.csv"))
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))

}
