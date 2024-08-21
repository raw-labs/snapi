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

import com.rawlabs.utils.core.TestData
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class RD5484Test extends Rql2TruffleCompilerTestContext {

  // top-level and correct.
  test("""Timestamp.Build(2015, 1, 4, 22, 0)""")(_ should run)

  // top-level and wrong, we expect a failure as a result, not an internal error
  test("""Timestamp.Build(2015, 1, 4, 24, 0)""")(
    _ should runErrorAs("Invalid value for HourOfDay (valid values 0 - 23): 24")
  )

  // correct value (22)
  test("""Try.IsError(Timestamp.Build(2015, 1, 4, 22, 0))""")(_ should evaluateTo("false"))

  // wrong value (24) should just evaluate to true, not fail
  test("""Try.IsError(Timestamp.Build(2015, 1, 4, 24, 0))""")(_ should evaluateTo("true"))

  // same with Date.Build
  test("""Try.IsError(Date.Build(2015, 1, 4))""")(_ should evaluateTo("false"))
  test("""Try.IsError(Date.Build(2015, 1, 42))""")(_ should evaluateTo("true"))

  // same with Time.Build
  test("""Try.IsError(Time.Build(9, 30))""")(_ should evaluateTo("false"))
  test("""Try.IsError(Time.Build(9, 74))""")(_ should evaluateTo("true"))

  // in such case below, it should stop and be wrapped in the list
  test("""[Timestamp.Build(2015, 1, 4, 24, 0)]""")(
    _ shouldNot runErrorAs("Invalid value for HourOfDay (valid values 0 - 23): 24")
  )

  // This is the original query. Disabled because Truffle doesn't support methods yet.
  test("original JIRA query") { _ =>
    TestData(s"""
      |main(start: timestamp, end: timestamp) =
      |    let lines = String.ReadLines("s3://raw-tutorial/ipython-demos/predictive-maintenance/machine_logs.log"),
      |        parsed = Collection.Transform(lines, l ->
      |            let groups = Regex.Groups(l, "(\\\\d+-\\\\d+-\\\\d+T\\\\d+:\\\\d+:\\\\d+\\\\.\\\\d{3})(?:\\\\d+)? (\\\\w+) (.*)"),
      |                timestamp =  Timestamp.Parse(List.Get(groups, 0), "yyyy-M-d'T'H:m:s.SSS"),
      |                level = List.Get(groups, 1),
      |                message = List.Get(groups, 2)
      |            in {timestamp: timestamp, level: level, message: message}
      |        )
      |    in Collection.Filter(parsed, l -> l.timestamp > start and l.timestamp < end)
      |
      |main(Timestamp.Build(2015, 1, 4, 0, 0), Timestamp.Build(2015, 1, 4, 24, 0))
      |  """.stripMargin) should run
  }

  // This is the original query written as a `let in`
  test("original JIRA query with let in") { _ =>
    TestData(s"""
      |let main(start: timestamp, end: timestamp) =
      |    let lines = String.ReadLines("s3://raw-tutorial/ipython-demos/predictive-maintenance/machine_logs.log"),
      |        parsed = Collection.Transform(lines, l ->
      |            let groups = Regex.Groups(l, "(\\\\d+-\\\\d+-\\\\d+T\\\\d+:\\\\d+:\\\\d+\\\\.\\\\d{3})(?:\\\\d+)? (\\\\w+) (.*)"),
      |                timestamp =  Timestamp.Parse(List.Get(groups, 0), "yyyy-M-d'T'H:m:s.SSS"),
      |                level = List.Get(groups, 1),
      |                message = List.Get(groups, 2)
      |            in {timestamp: timestamp, level: level, message: message}
      |        )
      |    in Collection.Filter(parsed, l -> l.timestamp > start and l.timestamp < end)
      |
      |in main(Timestamp.Build(2015, 1, 4, 0, 0), Timestamp.Build(2015, 1, 4, 24, 0))
      |  """.stripMargin) should run
  }

  // Timestamp.Parse
  test("""Try.IsError(Timestamp.Parse("9:30 23 June 1975", "H:m d MMMM yyyy"))""")(_ should evaluateTo("false"))
  test("""Try.IsError(Timestamp.Parse("9:30 23 June 1975", "H:m d yyyy MMMM"))""")(
    _ should evaluateTo("true")
  )

  // Date.Parse
  test("""Try.IsError(Date.Parse("23 June 1975", "d MMMM yyyy"))""")(_ should evaluateTo("false"))
  test("""Try.IsError(Date.Parse("23 June 1975", "d yyyy MMMM"))""")(_ should evaluateTo("true"))

  // Time.Parse
  test("""Try.IsError(Time.Parse("9:30", "H:m"))""")(_ should evaluateTo("false"))
  test("""Try.IsError(Time.Parse("9:30", "m:H"))""")(_ should evaluateTo("true"))

  // Interval.Parse
  test("""Try.IsError(Interval.Parse("P1Y2M3DT4H5M6.7S"))""")(_ should evaluateTo("false"))
  test("""Try.IsError(Interval.Parse("hello!"))""")(_ should evaluateTo("true"))

}
