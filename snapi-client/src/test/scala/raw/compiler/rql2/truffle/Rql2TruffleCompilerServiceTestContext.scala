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

package raw.compiler.rql2.truffle

import raw.client.rql2.truffle.Rql2TruffleCompilerService
import raw.compiler.rql2.api.Rql2CompilerServiceTestContext
import raw.runtime.truffle.RawLanguage
import raw.utils.{RawTestSuite, RawUtils, SettingsTestContext}

trait Rql2TruffleCompilerServiceTestContext extends Rql2CompilerServiceTestContext {
  this: RawTestSuite with SettingsTestContext =>

  var rql2TruffleCompilerService: Rql2TruffleCompilerService = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    // Forcibly drop all language caches before each test suite.
    RawLanguage.dropCaches()

    property("raw.compiler.impl", "rql2-truffle")

    rql2TruffleCompilerService = new Rql2TruffleCompilerService
    setCompilerService(rql2TruffleCompilerService)
  }

  override def afterAll(): Unit = {
    if (rql2TruffleCompilerService != null) {
      RawUtils.withSuppressNonFatalException(rql2TruffleCompilerService.stop())
      rql2TruffleCompilerService = null
    }
    // Forcibly drop all language caches after each test suite.
    RawLanguage.dropCaches()
    super.afterAll()
  }

}
