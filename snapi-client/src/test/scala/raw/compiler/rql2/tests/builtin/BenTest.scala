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

package raw.compiler.rql2.tests.builtin

import raw.compiler.rql2.tests.CompilerTestContext

trait BenTest extends CompilerTestContext {

  test(""" Byte.From(1)""")(it => it should evaluateTo("1b"))

  test("""let d = Json.Read("file:/tmp/int", type any)""")(_ should run)
}
