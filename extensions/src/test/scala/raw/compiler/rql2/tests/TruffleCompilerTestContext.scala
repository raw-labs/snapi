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

import raw.compiler.rql2.{Rql2OutputTestContext, Rql2TruffleCompilerServiceTestContext}

class TruffleCompilerTestContext
    extends CompilerTestContext
    with Rql2OutputTestContext
    with Rql2TruffleCompilerServiceTestContext {
  override def language: String = "rql2-truffle"

}
