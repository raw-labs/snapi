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

package raw.compiler.rql2.tests.regressions.credentials

import raw.creds.s3.S3TestCreds
import raw.compiler.rql2.tests.Rql2CompilerTestContext
import raw.creds.api.CredentialsTestContext

trait RD5932Test extends Rql2CompilerTestContext with CredentialsTestContext with S3TestCreds {

  s3Bucket(authorizedUser, UnitTestPrivateBucket)

  test("""Json.InferAndRead("s3://rawlabs-private-test-data/rd-5932.json")""") {
    _ should run
  }

}
