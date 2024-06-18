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

package raw.compiler.rql2.tests.builtin

import raw.creds.s3.S3TestCreds
import raw.compiler.rql2.tests.Rql2CompilerTestContext

trait S3PackageTest extends Rql2CompilerTestContext with S3TestCreds {

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
    |  data = Csv.InferAndRead(S3.Build("s3://${UnitTestPublicBucket.name}/students.csv"))
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))

}
