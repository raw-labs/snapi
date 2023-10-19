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

trait BytePackageTest extends CompilerTestContext {

  test(""" Byte.From(1)""")(it => it should evaluateTo("1b"))

  test(""" Byte.From("1")""")(it => it should evaluateTo("1b"))

  test(""" Byte.From(1.5)""")(it => it should evaluateTo("1b"))

  test(""" Byte.From(1.5f)""")(it => it should evaluateTo("1b"))

  test(""" Byte.From("abc")""")(it => it should runErrorAs("cannot cast 'abc' to byte"))

  // Errors don't propagate through
  test(""" [Byte.From("abc") + 12]""")(it => it should evaluateTo("""[Error.Build("cannot cast 'abc' to byte")]"""))

  // Nullability is handled
  test(""" [Byte.From("abc" + null) + 12]""")(it => it should evaluateTo("""[null]"""))
}
