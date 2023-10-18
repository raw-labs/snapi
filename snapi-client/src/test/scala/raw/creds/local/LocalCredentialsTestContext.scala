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

package raw.creds.local

import raw.utils.{RawTestSuite, SettingsTestContext}
import raw.creds.api.CredentialsTestContext
import raw.utils._

trait LocalCredentialsTestContext extends CredentialsTestContext {
  this: RawTestSuite with SettingsTestContext =>

  protected var localCredentialsService: LocalCredentialsService = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    property("raw.creds.impl", "local")

    localCredentialsService = new LocalCredentialsService()
    setCredentials(localCredentialsService)
  }

  override def afterAll(): Unit = {
    if (localCredentialsService != null) {
      withSuppressNonFatalException(localCredentialsService.stop())
      localCredentialsService = null
    }
    super.afterAll()
  }
}
