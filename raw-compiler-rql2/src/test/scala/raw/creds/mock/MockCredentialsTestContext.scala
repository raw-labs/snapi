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

package raw.creds.mock

import raw.{RawTestSuite, SettingsTestContext}
import raw.creds.CredentialsTestContext
import raw.utils._

trait MockCredentialsTestContext extends CredentialsTestContext {
  this: RawTestSuite with SettingsTestContext =>

  protected var mockCredentialsService: MockCredentialsService = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    property("raw.creds.impl", "mock")

    mockCredentialsService = new MockCredentialsService()
    setCredentials(mockCredentialsService)
  }

  override def afterAll(): Unit = {
    if (mockCredentialsService != null) {
      withSuppressNonFatalException(mockCredentialsService.stop())
      mockCredentialsService = null
    }
    super.afterAll()
  }
}
