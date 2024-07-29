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

package raw.compiler.rql2.tests.spec

import raw.compiler.rql2.tests.Rql2CompilerTestContext

trait BasicStagedCompilerTest extends Rql2CompilerTestContext {

  test("""TestPackage.ByteValueArg(22b)""")(_ should evaluateTo("{arg: 22b}"))
  test("""TestPackage.ShortValueArg(22s)""")(_ should evaluateTo("{arg: 22s}"))
  test("""TestPackage.IntValueArg(22)""")(_ should evaluateTo("{arg: 22}"))
  test("""TestPackage.LongValueArg(22L)""")(_ should evaluateTo("{arg: 22L}"))
  test("""TestPackage.FloatValueArg(22.13f)""")(_ should evaluateTo("{arg: 22.13f}"))
  test("""TestPackage.DoubleValueArg(22.14)""")(_ should evaluateTo("{arg: 22.14}"))
  test("""TestPackage.StringValueArg("et voila")""")(_ should evaluateTo("""{arg: "et voila"}"""))
  test("""TestPackage.BoolValueArg(false)""")(_ should evaluateTo("""{arg: false}"""))
  test("""TestPackage.DateValueArg(Date.Build(2012, 1, 1))""")(
    _ should evaluateTo("{arg: Date.Build(2012, 1, 1)}")
  )
  test("""TestPackage.TimeValueArg(Time.Build(15, 14, seconds=13))""")(
    _ should evaluateTo("{arg: Time.Build(15, 14, seconds=13)}")
  )
  test("""TestPackage.TimestampValueArg(Timestamp.Build(2012, 1, 1, 15, 14, seconds=13))""")(
    _ should evaluateTo("{arg: Timestamp.Build(2012, 1, 1, 15, 14, seconds=13)}")
  )
  test("""TestPackage.IntervalValueArg(Interval.Build(years  = 3, months = 6, days = 5))""")(
    _ should evaluateTo("{arg: Interval.Build(years  = 3, months = 6, days = 5)}")
  )
  test("""TestPackage.ListValueArg([1, 2, 3])""")(_ should evaluateTo("{arg: [1, 2, 3]}"))
  test("""TestPackage.RecordValueArg({a: 1, b: 2.12f})""")(_ should evaluateTo("{arg: {a: 1, b: 2.12f}}"))

  test("""
    |let arg = Json.Parse("12", type int or record(a: int))
    |in TestPackage.OrValueArg(arg)""".stripMargin)(_ should evaluateTo("{arg: 12}"))
  private val ttt = "\"\"\""
  test(s"""
    |let arg = Json.Parse($ttt{"a": 14}$ttt, type int or record(a: int))
    |in TestPackage.OrValueArg(arg)""".stripMargin)(_ should evaluateTo("{arg: {a: 14}}"))

}
