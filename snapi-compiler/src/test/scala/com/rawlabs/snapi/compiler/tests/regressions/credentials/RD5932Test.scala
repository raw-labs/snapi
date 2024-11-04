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
import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class RD5932Test extends SnapiTestContext {

  awsCreds("private-creds", TestCredentials.rawAwsCredentials)

  test(
    """Json.InferAndRead(S3.Build("rawlabs-private-test-data", "rd-5932.json", awsCredential = "private-creds"))"""
  ) {
    _ should run
  }

}
