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

package raw.compiler.rql2.tests

import raw.client.rql2.truffle.Rql2TruffleCompilerService
import raw.compiler.rql2.api.CompilerServiceTestContext
import raw.utils.{withSuppressNonFatalException, RawTestSuite, SettingsTestContext}

trait Rql2TruffleCompilerServiceTestContext extends CompilerServiceTestContext {
  this: RawTestSuite with SettingsTestContext =>

  var rql2TruffleCompilerService: Rql2TruffleCompilerService = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    property("raw.compiler.impl", "rql2-truffle")
    rql2TruffleCompilerService = new Rql2TruffleCompilerService
    setCompilerService(rql2TruffleCompilerService)
  }

  override def afterAll(): Unit = {
    if (rql2TruffleCompilerService != null) {
      withSuppressNonFatalException(rql2TruffleCompilerService.stop())
      rql2TruffleCompilerService = null
    }
    super.afterAll()
  }

}
