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

package raw.auth.api

import org.scalatest.BeforeAndAfterAll
import raw.utils.RawTestSuite

trait AuthTestContext extends BeforeAndAfterAll {
  this: RawTestSuite =>

  private var instance: AuthService = _

  def auth: AuthService = instance

  def setAuth(auth: AuthService): Unit = {
    instance = auth
    AuthServiceProvider.set(auth)
  }

  override def afterAll(): Unit = {
    setAuth(null)
    super.afterAll()
  }

}
