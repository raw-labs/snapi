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

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class DatePackageTest extends SnapiTestContext {

  test("Date.Build(2022, 1, 15)") { it =>
    it should typeAs("date")
    it should run
  }

  test("Date.FromEpochDay(0)")(it => it should evaluateTo("Date.Build(1970, 1, 1)"))

  test("Date.FromEpochDay(1000)")(it => it should evaluateTo("Date.Build(1972, 9, 27)"))

  test("""Date.Parse("2018-02-01", "yyyy-MM-dd")""")(it => it should evaluateTo("Date.Build(2018, 2, 1)"))

  test("""Date.Parse("23 June 1975", "d MMMM yyyy")""")(it => it should evaluateTo("Date.Build(1975, 6, 23)"))

  test("""Date.Now()""") { it =>
    it should typeAs("date")
    it should run
  }

  test("Date.Year(Date.Build(1975, 6, 23))")(it => it should evaluateTo("1975"))

  test("Date.Month(Date.Build(1975, 6, 23))")(it => it should evaluateTo("6"))

  test("Date.Day(Date.Build(1975, 6, 23))")(it => it should evaluateTo("23"))

  test("Date.FromTimestamp(Timestamp.Build(2022, 1, 15, 9, 30))")(it => it should evaluateTo("Date.Build(2022, 1, 15)"))

  test("""let
    |  t1 = Date.Build(2019, 3, 4),
    |  t2 = Date.Build(2018, 1, 1)
    |in
    |  Date.Subtract(t1, t2)""".stripMargin)(
    _ should evaluateTo("""Interval.Build(years = 1, months = 2, days = 3)""".stripMargin)
  )

  test("""let
    |  t1 = Date.Build(2019, 3, 4),
    |  t2 = Date.Build(2018, 4, 5)
    |in
    |  Date.Subtract(t1, t2)""".stripMargin)(
    _ should evaluateTo("""Interval.Build(months = 11, days = -1)""".stripMargin)
  )

  test("""let
    |  t = Date.Build(2018, 1, 1),
    |  i = Interval.Build(years = 1, months = 2, days = 3, hours = 9, minutes = 30)
    |in
    |  Date.AddInterval(t, i)""".stripMargin)(
    _ should evaluateTo(""" Timestamp.Build(2019, 3, 4, 9, 30) """.stripMargin)
  )

  test("""let
    |  t = Date.Build(2019, 3, 4),
    |  i = Interval.Build(years = 1, months = 2, days = 3)
    |in
    |  Date.SubtractInterval(t, i)""".stripMargin)(
    _ should evaluateTo(""" Timestamp.Build(2018, 1, 1, 0, 0) """.stripMargin)
  )

  test("""Date.Parse("23 June 1975", "d yyyy MMMM")""")(
    _ should runErrorAs("string '23 June 1975' does not match date template 'd yyyy MMMM'")
  )
  test("""Date.Parse("23 June 1975", "d tralala MMMM")""")(_ should runErrorAs("invalid date template: d tralala MMMM"))

}
