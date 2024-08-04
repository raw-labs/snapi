/*
 * Copyright 2024 RAW Labs S.A.
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
import raw.testing.tags.TruffleTests

@TruffleTests class RD9616Test extends Rql2TruffleCompilerTestContext {

  test(
    """let
      |    date1=Date.Build(2002,1,1),
      |    date2=Date.Build(2002,2,1),
      |    date3=Date.Build(2002,1,31)
      |in
      |    {Date.Subtract(date2,date1), Date.Subtract(date3,date1)}""".stripMargin
  )(it => it should evaluateTo("{Interval.Build(days=30, hours=10, minutes=30), Interval.Build(days=30)}"))

  test(
    """let
      |    date1=Date.Build(2002,1,1),
      |    date2=Date.Build(2002,2,1),
      |    date3=Date.Build(2002,1,31)
      |in
      |    {Interval.ToMillis(Date.Subtract(date2,date1)), Interval.ToMillis(Date.Subtract(date3,date1))}""".stripMargin
  )(it => it should evaluateTo("{2629800000L, 2592000000L}"))

}
