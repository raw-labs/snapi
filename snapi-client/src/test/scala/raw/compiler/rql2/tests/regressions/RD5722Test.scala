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

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class RD5722Test extends Rql2TruffleCompilerTestContext {

  test("""let data = Collection.Build({
    |  a: { b: 123}
    |})
    |in data.a.b""".stripMargin)(_ should evaluateTo("Collection.Build(123)"))

  test("""let data = [{
    |       a: { b: 123}
    |   }]
    |in data.a.b""".stripMargin)(_ should evaluateTo("[123]"))
}
