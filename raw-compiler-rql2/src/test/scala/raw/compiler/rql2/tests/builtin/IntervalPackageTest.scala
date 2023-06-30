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

trait IntervalPackageTest extends CompilerTestContext {

  test("Interval.Build(months=12) == Interval.Build(years=1)")(_ should evaluateTo("true"))
  test("Interval.Build(hours=24) == Interval.Build(days=1)")(_ should evaluateTo("true"))
  test("Interval.Build(minutes=60) == Interval.Build(hours=1)")(_ should evaluateTo("true"))
  test("Interval.Build(seconds=60) == Interval.Build(minutes=1)")(_ should evaluateTo("true"))
  test("Interval.Build(millis=1000) == Interval.Build(seconds=1)")(_ should evaluateTo("true"))

  test("Interval.ToMillis(Interval.Build(minutes=1))")(it => it should evaluateTo("60000"))

  test("Interval.ToMillis(Interval.Build(days=1))")(it => it should evaluateTo("86400000"))

  test("Interval.FromMillis(60000)")(it => it should evaluateTo("Interval.Build(minutes=1)"))

  test("""Interval.Parse("P1Y2M")""")(it => it should evaluateTo("Interval.Build(years=1, months=2)"))

  test("""Interval.Parse("PT1M2S")""")(it => it should evaluateTo("Interval.Build(minutes=1, seconds=2)"))

  test("""Interval.Parse("P1Y2M3DT4H5M6.007S")""")(it => it should evaluateTo("""
    |Interval.Build(
    |  years=1,
    |  months=2,
    |  days=3,
    |  hours=4,
    |  minutes=5,
    |  seconds=6,
    |  millis=7
    |)""".stripMargin))

  test("""let
    |  i = Interval.Build(years=1, months=2, weeks=3, days=4,
    |         hours=5, minutes=6, seconds=7, millis=8)
    |in
    |  Interval.Years(i)
    |""".stripMargin)(_ should evaluateTo("1"))

  test("""let
    |  i = Interval.Build(years=1, months=2, weeks=3, days=4,
    |         hours=5, minutes=6, seconds=7, millis=8)
    |in
    |  Interval.Months(i)
    |""".stripMargin)(_ should evaluateTo("2"))

  test("""let
    |  i = Interval.Build(years=1, months=2, weeks=3, days=4,
    |         hours=5, minutes=6, seconds=7, millis=8)
    |in
    |  Interval.Weeks(i)
    |""".stripMargin)(_ should evaluateTo("3"))

  test("""let
    |  i = Interval.Build(years=1, months=2, weeks=3, days=4,
    |         hours=5, minutes=6, seconds=7, millis=8)
    |in
    |  Interval.Days(i)
    |""".stripMargin)(_ should evaluateTo("4"))

  test("""let
    |  i = Interval.Build(years=1, months=2, weeks=3, days=4,
    |         hours=5, minutes=6, seconds=7, millis=8)
    |in
    |  Interval.Hours(i)
    |""".stripMargin)(_ should evaluateTo("5"))

  test("""let
    |  i = Interval.Build(years=1, months=2, weeks=3, days=4,
    |         hours=5, minutes=6, seconds=7, millis=8)
    |in
    |  Interval.Minutes(i)
    |""".stripMargin)(_ should evaluateTo("6"))

  test("""let
    |  i = Interval.Build(years=1, months=2, weeks=3, days=4,
    |         hours=5, minutes=6, seconds=7, millis=8)
    |in
    |  Interval.Seconds(i)
    |""".stripMargin)(_ should evaluateTo("7"))

  test("""let
    |  i = Interval.Build(years=1, months=2, weeks=3, days=4,
    |         hours=5, minutes=6, seconds=7, millis=8)
    |in
    |  Interval.Millis(i)
    |""".stripMargin)(_ should evaluateTo("8"))

}
