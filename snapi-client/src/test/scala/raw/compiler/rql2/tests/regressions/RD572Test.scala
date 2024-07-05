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

import raw.compiler.rql2.tests.Rql2CompilerTestContext

trait RD572Test extends Rql2CompilerTestContext {

  test("-128b") { it =>
    it should typeAs("byte")
    it should evaluateTo("-128b")
  }

  test("-32768s") { it =>
    it should typeAs("short")
    it should evaluateTo("-32768s")
  }

  test("-2147483648") { it =>
    it should typeAs("int")
    it should evaluateTo("-2147483648")
  }

  test("- 2147483648") { it =>
    it should typeAs("int")
    it should evaluateTo(s"-2147483648")
  }

  test(s"-2147483648 / 2") { it =>
    it should typeAs("int")
    it should evaluateTo(s"${-2147483648 / 2}")
  }

  test("- 2147483648 / 2") { it =>
    it should typeAs("int")
    it should evaluateTo(s"${-2147483648 / 2}")
  }

  test("-+2147483648")(it => it should typeAs("long"))

  test(s"- 9223372036854775808")(it => it should typeAs("long"))

  test(s"- (9223372036854775808)")(it => it should typeAs("double"))

  test(s"- 9223372036854775808/2")(it => it should typeAs("long"))

  test(s"-9223372036854775808/2")(it => it should typeAs("long"))

  test(s"-+9223372036854775808/2")(it => it should typeAs("double"))

}
