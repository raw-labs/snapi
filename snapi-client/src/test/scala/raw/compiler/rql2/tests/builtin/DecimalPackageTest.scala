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

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class DecimalPackageTest extends Rql2TruffleCompilerTestContext {

  test("""Decimal.Round(Decimal.From("1.423"), 2)""") { it =>
    it should evaluateTo("""1.42q""")
    it should evaluateTo("""Decimal.From("1.42")""")
  }

  test("""Decimal.Round(Decimal.From("1.423"), 3)""")(it => it should evaluateTo("""1.423q"""))

  test("""Decimal.Round("123")""")(it => it should typeErrorAs("expected decimal but got string"))

  test("""Decimal.From("123.45")""")(it => it should evaluateTo("""123.45q"""))

  test("""Decimal.From(123)""")(it => it should evaluateTo("""123q"""))

  // Make sure that the decimal can be written to record
  test("""{a:Decimal.From(123), b:1}""")(it => it should evaluateTo("""{a:123q, b:1}"""))

  // Errors don't propagate through
  test(""" [Decimal.From("abc") + 12]""")(it =>
    it should evaluateTo("""[Error.Build("cannot cast 'abc' to decimal")]""")
  )

  // Nullability is handled
  test(""" [Decimal.From("abc" + null) + 12]""")(it => it should evaluateTo("""[null]"""))
  test(""" [Decimal.From(1b + null) + 12b]""")(it => it should evaluateTo("""[null]"""))
}
