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

package com.rawlabs.snapi.compiler.tests.regressions.credentials

import com.rawlabs.snapi.compiler.tests.TestCredentials
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class RD5932Test extends Rql2TruffleCompilerTestContext {

  s3Bucket(TestCredentials.UnitTestPrivateBucket, TestCredentials.UnitTestPrivateBucketCred)

  test("""Json.InferAndRead("s3://rawlabs-private-test-data/rd-5932.json")""") {
    _ should run
  }

}
