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

package raw.compiler.rql2.tests.regressions

import raw.TestData
import raw.compiler.rql2.tests.CompilerTestContext

trait RD5502Test extends CompilerTestContext {

  test("test") { _ =>
    TestData(s"""main(id: int) =
      |    if (id == 1) then { api_version: 1,data: { onboarding :{ applicable_aadhaar_steps:null, attempted_steps:[] }},
      |    success: true
      |}
      |    else Error.Build("Unknown identifier")
      |  // The following test will run if you press the [Play] button directly.
      |  main(1)""".stripMargin) should run
  }

  test("original query") { _ =>
    TestData(s"""main(id: int) =
      |    if (id == 1) then { api_version: 1, data: { onboarding :{ applicable_aadhaar_steps:null, attempted_steps:[],
      |cohort:"MCL_ONLY"
      |,status:"SUCCESS",
      |steps:[],
      |updated_at:"2021-06-27T03:06:07.697592Z",
      |user_txn_status_during_onboarding:true,
      |variation:1,
      |verification:{email:{required:null}}},
      |upi_verification_onboarding:null
      | },
      |    success: true
      |}
      |    else Error.Build("Unknown identifier")
      |  // The following test will run if you press the [Play] button directly.
      |  main(1)""".stripMargin) should run
  }
}
