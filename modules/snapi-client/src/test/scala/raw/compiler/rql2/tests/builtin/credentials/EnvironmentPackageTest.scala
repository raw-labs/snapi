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

import raw.compiler.rql2.tests.CompilerTestContext
import raw.creds.api.CredentialsTestContext

trait EnvironmentPackageTest extends CompilerTestContext with CredentialsTestContext {

  secret(authorizedUser, "my-secret", "my-secret-value")

  test("""Environment.Secret("my-secret")""")(it => it should evaluateTo(""" "my-secret-value" """))

  test("""Environment.Secret("my-typo")""")(it => it should runErrorAs("could not find secret my-typo"))

  // checking it doesn't fail if one of the secrets is not found (RD-9041)
  test("""[Environment.Secret("my-secret"), Environment.Secret("my-typo")]""")(it =>
    it should evaluateTo("""["my-secret-value", Error.Build("could not find secret my-typo")]
      |""".stripMargin)
  )

}
