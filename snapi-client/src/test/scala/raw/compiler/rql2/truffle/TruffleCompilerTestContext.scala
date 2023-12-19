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

import raw.compiler.rql2.api.Rql2OutputTestContext
import raw.compiler.rql2.tests.CompilerTestContext
import raw.runtime.truffle.RawLanguage

class TruffleCompilerTestContext
    extends CompilerTestContext
    with Rql2OutputTestContext
    with Rql2TruffleCompilerServiceTestContext {

  override def beforeAll(): Unit = {
    super.beforeAll()
    // Forcibly drop all language caches before each test suite.
    RawLanguage.dropCaches()
  }

  override def afterAll(): Unit = {
    // Forcibly drop all language caches after each test suite.
    RawLanguage.dropCaches()
    super.afterAll()
  }

}
