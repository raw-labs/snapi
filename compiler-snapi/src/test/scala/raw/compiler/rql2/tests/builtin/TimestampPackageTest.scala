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

class TimestampPackageTest extends Rql2TruffleCompilerTestContext {

  test("Timestamp.Build(2022, 1, 15, 9, 30)") { it =>
    it should typeAs("timestamp")
    it should evaluateTo("Timestamp.Build(2022, 1, 15, 9, 30, seconds = 0, millis = 0)")
  }

  test("Timestamp.Build(2022, 1, 15, 9, 30, seconds = 15, millis = 123)") { it =>
    it should typeAs("timestamp")
    it should evaluateTo("Timestamp.Build(2022, 1, 15, 9, 30, millis = 123, seconds = 15)")
  }

  test("Timestamp.Build(2022, 1, 15, 9, 30, millis = 123)") { it =>
    it should typeAs("timestamp")
    it should evaluateTo("Timestamp.Build(2022, 1, 15, 9, 30, seconds = 0, millis = 123)")
  }

  test("""Timestamp.Parse("23/06/1975 09:30:15.123", "d/M/yyyy H:m:s.SSS")""")(it =>
    it should evaluateTo(""" Timestamp.Build(1975, 6, 23, 9, 30, seconds = 15, millis = 123) """)
  )

  test("""Timestamp.Parse("9:30 23 June 1975", "H:m d MMMM yyyy")""")(it =>
    it should evaluateTo(""" Timestamp.Build(1975, 6, 23, 9, 30, seconds = 0, millis = 0) """)
  )

  test("""Timestamp.Year(Timestamp.Build(1975, 6, 23, 9, 30))""")(it => it should evaluateTo("""1975"""))

  test("""Timestamp.Month(Timestamp.Build(1975, 6, 23, 9, 30))""")(it => it should evaluateTo("""6"""))

  test("""Timestamp.Day(Timestamp.Build(1975, 6, 23, 9, 30))""")(it => it should evaluateTo("""23"""))

  test("""Timestamp.Hour(Timestamp.Build(1975, 6, 23, 9, 30))""")(it => it should evaluateTo("""9"""))

  test("""Timestamp.Minute(Timestamp.Build(1975, 6, 23, 9, 30))""")(it => it should evaluateTo("""30"""))

  test("""Timestamp.Second(Timestamp.Build(1975, 6, 23, 9, 30, seconds = 15))""")(it => it should evaluateTo("""15"""))

  test("""Timestamp.Millis(Timestamp.Build(1975, 6, 23, 9, 30, seconds = 15, millis = 123))""")(it =>
    it should evaluateTo("""123""")
  )

  test("""Timestamp.Now()""") { it =>
    it should typeAs("timestamp")
    it should run
  }

  test("""Timestamp.FromUnixTimestamp(1517443320l)""")(it =>
    it should evaluateTo("""Timestamp.Build(2018, 2, 1, 0, 2)""")
  )

  test("""Timestamp.ToUnixTimestamp(Timestamp.Build(2018, 2, 1, 1,2))""")(it => it should evaluateTo("""1517446920"""))

  test("""Timestamp.TimeBucket("hour", Timestamp.Build(2007, 3, 14, 1, 2, seconds=3, millis=4))""")(it =>
    it should evaluateTo("""Timestamp.Build(2007, 3, 14, 1, 0)""")
  )

  test("""Timestamp.TimeBucket("year", Timestamp.Build(2007, 3, 14, 1, 2, seconds=3, millis=4))""")(it =>
    it should evaluateTo("""Timestamp.Build(2007, 1, 1, 0, 0)""")
  )

  test(
    """Timestamp.TimeBucket(Interval.Build(millis = 100), Timestamp.Build(2007, 3, 14, 1, 2, seconds=3, millis=4))"""
  )(it => it should evaluateTo("""Timestamp.Build(2007, 3, 14, 1, 2, seconds = 3)"""))

  test("""Timestamp.TimeBucket(Interval.Build(years = 2), Timestamp.Build(2007, 3, 14, 1, 2, seconds=3, millis=4))""")(
    it => it should evaluateTo("""Timestamp.Build(2006, 1, 1, 0, 0)""")
  )

  test("""let
    |  t1 = Timestamp.Build(2019, 3, 4, 9, 30),
    |  t2 = Timestamp.Build(2018, 1, 1, 0, 0)
    |in
    |  Timestamp.Subtract(t1, t2)""".stripMargin)(
    _ should evaluateTo("""Interval.Build(years = 1, months = 2, days = 3, hours = 9, minutes = 30)""".stripMargin)
  )

  test("""let
    |  t1 = Timestamp.Build(2019, 3, 4, 9, 30),
    |  t2 = Timestamp.Build(2018, 4, 5, 10, 40)
    |in
    |  Timestamp.Subtract(t1, t2)""".stripMargin)(
    _ should evaluateTo("""Interval.Build(months = 11, days = -1, hours = -1, minutes = -10)""".stripMargin)
  )

  test("""let
    |  t = Timestamp.Build(2018, 1, 1, 0, 0),
    |  i = Interval.Build(years = 1, months = 2, days = 3, hours = 9, minutes = 30)
    |in
    |  Timestamp.AddInterval(t, i)""".stripMargin)(
    _ should evaluateTo(""" Timestamp.Build(2019, 3, 4, 9, 30) """.stripMargin)
  )

  test("""let
    |  t = Timestamp.Build(2019, 3, 4, 9, 30),
    |  i = Interval.Build(years = 1, months = 2, days = 3, hours = 9, minutes = 30)
    |in
    |  Timestamp.SubtractInterval(t, i)""".stripMargin)(
    _ should evaluateTo(""" Timestamp.Build(2018, 1, 1, 0, 0) """.stripMargin)
  )

  test("""Timestamp.FromDate(Date.Build(2012, 01, 02))""")(
    _ should evaluateTo("Timestamp.Build(2012, 01, 02, 00, 00, seconds=00)")
  )

  test("""Timestamp.Parse("9:30 23 June 1975", "H:m d yyyy MMMM")""")(
    _ should runErrorAs("string '9:30 23 June 1975' does not match timestamp template 'H:m d yyyy MMMM'")
  )
  test("""Timestamp.Parse("9:30 23 June 1975", "H:m d tralala MMMM")""")(
    _ should runErrorAs("invalid timestamp template: H:m d tralala MMMM")
  )

}
