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

import raw.compiler.rql2.tests.CompilerTestContext

trait RD10723Test extends CompilerTestContext {

  private val qqq = "\"\"\""
  test(s"""main() =
         |  let financial = Csv.InferAndParse(${qqq}year;market_cap_in_billion
         |${qqq}
         |  ) in financial
         |
         |main()
         |""".stripMargin)(
    _ should run
  )

}
