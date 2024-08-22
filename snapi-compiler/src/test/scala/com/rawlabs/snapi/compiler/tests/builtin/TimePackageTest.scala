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

package com.rawlabs.snapi.compiler.tests.builtin

import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class TimePackageTest extends Rql2TruffleCompilerTestContext {

  test("Time.Build(9, 30)") { it =>
    it should typeAs("time")
    it should evaluateTo("Time.Build(9, 30, seconds=0, millis=0)")
  }

  test("Time.Build(9, 30, millis=100)") { it =>
    it should typeAs("time")
    it should evaluateTo("Time.Build(9, 30, seconds=0, millis=100)")
  }

  test("Time.Build(9, 30, millis=100, seconds=20)") { it =>
    it should typeAs("time")
    it should evaluateTo("Time.Build(9, 30, seconds=20, millis=100)")
  }

  test("""Time.Hour(Time.Build(9, 30))""")(it => it should evaluateTo("9"))

  test("""Time.Minute(Time.Build(9, 30))""")(it => it should evaluateTo("30"))

  test("""Time.Second(Time.Build(9, 30, seconds=20))""")(it => it should evaluateTo("20"))

  test("""Time.Millis(Time.Build(9, 30, millis=100))""")(it => it should evaluateTo("100"))

  test("""Time.Parse("09:12:23.450", "H:m:s.SSS")""")(it =>
    it should evaluateTo("Time.Build(9, 12, seconds=23, millis=450)")
  )

  test("""Time.Parse("09:12 PM", "h:m a")""")(it => it should evaluateTo("Time.Build(21, 12)"))

  test("""Time.Now()""") { it =>
    it should typeAs("time")
    it should run
  }

  test("""let
    |  t1 = Time.Build(9, 30),
    |  t2 = Time.Build(0, 0)
    |in
    |  Time.Subtract(t1, t2)""".stripMargin)(
    _ should evaluateTo("""Interval.Build(hours = 9, minutes = 30)""".stripMargin)
  )

  test("""let
    |  t1 = Time.Build(9, 30),
    |  t2 = Time.Build(10, 40)
    |in
    |  Time.Subtract(t1, t2)""".stripMargin)(
    _ should evaluateTo("""Interval.Build(hours = -1, minutes = -10)""".stripMargin)
  )

  test("""let
    |  t = Time.Build(0, 0),
    |  i = Interval.Build(hours = 9, minutes = 30)
    |in
    |  Time.AddInterval(t, i)""".stripMargin)(
    _ should evaluateTo(""" Time.Build(9, 30) """.stripMargin)
  )

  // Years, months, weeks, days of interval are ignored.
  test("""let
    |  t = Time.Build(0, 0),
    |  i = Interval.Build(years = 1, months = 2, days = 3, hours = 9, minutes = 30)
    |in
    |  Time.AddInterval(t, i)""".stripMargin)(
    _ should evaluateTo(""" Time.Build(9, 30) """.stripMargin)
  )

  test("""let
    |  t = Time.Build(9, 30),
    |  i = Interval.Build(hours = 9, minutes = 30)
    |in
    |  Time.SubtractInterval(t, i)""".stripMargin)(
    _ should evaluateTo(""" Time.Build(0, 0) """.stripMargin)
  )

  // Years, months, weeks, days of interval are ignored.
  test("""let
    |  t = Time.Build(9, 30),
    |  i = Interval.Build(years = 1, months = 2, days = 3, hours = 9, minutes = 30)
    |in
    |  Time.SubtractInterval(t, i)""".stripMargin)(
    _ should evaluateTo(""" Time.Build(0, 0) """.stripMargin)
  )

  test("""Time.Parse("9h30", "H:m")""")(_ should runErrorAs("string '9h30' does not match time template 'H:m'"))
  test("""Time.Parse("9:30", "tralala")""")(_ should runErrorAs("invalid time template: tralala"))

}
