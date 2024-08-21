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

@TruffleTests class RD5488Test extends Rql2TruffleCompilerTestContext {

  // All these tests are valid queries. They fail in the middle of compilation, at L0, with "lines is declared more than once".

  // a short version of RD-5488 (note process isn't called in that version, but that's valid code).
  test("""let lines = [1,2,3,4,5],
    |    process(lines: list(int)) = List.Transform(lines, i -> i * 10)
    |    in lines""".stripMargin)(_ should run)

  // same but calling 'process', in case it changes anything.
  test("""let lines = [1,2,3,4,5],
    |    process(lines: list(int)) = List.Transform(lines, i -> i * 10)
    |    in process(lines)""".stripMargin)(_ should run)

  test("original JIRA query") { _ =>
    TestData(s"""
      | main(start: timestamp, end: timestamp) =
      |      let lines = Collection.Take(String.ReadLines("s3://raw-tutorial/ipython-demos/predictive-maintenance/machine_logs.log"), 100),
      |          parseLine(lines: collection(string)) = Collection.Transform(lines, l ->
      |              let groups = Regex.Groups(l, "([^\\\\s]+) (INFO|WARN|ERROR) (\\\\w+)"),
      |                  timestamp =  Timestamp.Parse(List.Get(groups, 0), "yyyy-M-d'T'H:m:s.SSS"),
      |                  level = List.Get(groups, 1),
      |                  message = List.Get(groups, 2)
      |              in {timestamp: timestamp, level: level, message: message}
      |          )
      |      in lines
      |    //       parsed =
      |    //   in parsed
      |    //   Collection.Filter(parsed, l -> l.timestamp > start and l.timestamp < end)
      |
      |  // The following test will run if you press the [Play] button directly.
      |  main(Timestamp.Build(2015, 1, 4, 0, 0), Timestamp.Build(2015, 1, 5, 0, 0))
      |  """.stripMargin) should run
  }
}
