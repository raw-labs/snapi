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

import org.scalatest.BeforeAndAfterAll
import raw.client.utils.{RawTestSuite, RawUtils, SettingsTestContext}
import raw.creds.api.CredentialsTestContext

trait LocalCredentialsTestContext extends BeforeAndAfterAll {
  this: RawTestSuite with SettingsTestContext with CredentialsTestContext =>

  protected var localCredentialsService: LocalCredentialsService = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    property("raw.creds.impl", "local")

    localCredentialsService = new LocalCredentialsService()
    setCredentials(localCredentialsService)
  }

  override def afterAll(): Unit = {
    if (localCredentialsService != null) {
      RawUtils.withSuppressNonFatalException(localCredentialsService.stop())
      localCredentialsService = null
    }
    super.afterAll()
  }
}
