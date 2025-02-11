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

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class RD9554Test extends SnapiTestContext {

  test("""let range = Int.Range(0,5)
    |in Collection.Union(range, range)""".stripMargin) { it =>
    // version with a duplicated 'range'
    it should evaluateTo("""let range1 = Int.Range(0,5), range2 = Int.Range(0,5)
      |in Collection.Union(range1, range2)""".stripMargin)
    // expected result
    it should evaluateTo("[0,1,2,3,4,0,1,2,3,4]")
  }

  test("""let range = Int.Range(0,5)
    |in {range, range}""".stripMargin) { it =>
    // version with a duplicated 'range'
    it should evaluateTo("""let range1 = Int.Range(0,5), range2 = Int.Range(0,5)
      |in {range1, range2}""".stripMargin)
    // expected result
    it should evaluateTo("{[0,1,2,3,4],[0,1,2,3,4]}")
  }

  test("""let range = Int.Range(0,5),
    |         values = Collection.Unnest(range,
    |                      r1 -> Collection.Filter(range, r2 -> r1 == r2))
    |in Collection.Count(values)""".stripMargin) { it =>
    it should evaluateTo("""let values = Collection.Unnest(Int.Range(0,5),
      |                      r1 -> Collection.Filter(Int.Range(0,5), r2 -> r1 == r2))
      |in Collection.Count(values)""".stripMargin)
    it should evaluateTo("5")
  }

  test("""let range = Long.Range(0,5)
    |in Collection.Union(range, range)""".stripMargin) { it =>
    // version with a duplicated 'range'
    it should evaluateTo("""let range1 = Long.Range(0,5), range2 = Long.Range(0,5)
      |in Collection.Union(range1, range2)""".stripMargin)
    // expected result
    it should evaluateTo("[0L,1L,2L,3L,4L,0L,1L,2L,3L,4L]")
  }

  test("""let range = Long.Range(0,5)
    |in {range, range}""".stripMargin) { it =>
    // version with a duplicated 'range'
    it should evaluateTo("""let range1 = Long.Range(0,5), range2 = Long.Range(0,5)
      |in {range1, range2}""".stripMargin)
    // expected result
    it should evaluateTo("{[0L,1L,2L,3L,4L],[0L,1L,2L,3l,4L]}")
  }

  test("""let range = Long.Range(0,5),
    |         values = Collection.Unnest(range,
    |                      r1 -> Collection.Filter(range, r2 -> r1 == r2))
    |in Collection.Count(values)""".stripMargin) { it =>
    it should evaluateTo("""let values = Collection.Unnest(Long.Range(0,5),
      |                      r1 -> Collection.Filter(Long.Range(0,5), r2 -> r1 == r2))
      |in Collection.Count(values)""".stripMargin)
    it should evaluateTo("5")
  }

  test("""let range = Timestamp.Range(Date.Build(2001, 1, 1), Date.Build(2001, 1, 5), step=Interval.Build(days=1))
    |in Collection.Union(range, range)""".stripMargin) { it =>
    // version with a duplicated 'range'
    it should evaluateTo(
      """let range1 = Timestamp.Range(Date.Build(2001, 1, 1), Date.Build(2001, 1, 5), step=Interval.Build(days=1)),
        |    range2 = Timestamp.Range(Date.Build(2001, 1, 1), Date.Build(2001, 1, 5), step=Interval.Build(days=1))
        |in Collection.Union(range1, range2)""".stripMargin
    )
    // expected result
    it should evaluateTo("""[Timestamp.Build(2001, 1, 1, 0, 0), Timestamp.Build(2001, 1, 2, 0, 0),
      | Timestamp.Build(2001, 1, 3, 0, 0), Timestamp.Build(2001, 1, 4, 0, 0),
      | Timestamp.Build(2001, 1, 1, 0, 0), Timestamp.Build(2001, 1, 2, 0, 0),
      | Timestamp.Build(2001, 1, 3, 0, 0), Timestamp.Build(2001, 1, 4, 0, 0)]""".stripMargin)
  }

  test("""let range = Timestamp.Range(Date.Build(2001, 1, 1), Date.Build(2001, 1, 5), step=Interval.Build(days=1))
    |in {range, range}""".stripMargin) { it =>
    // version with a duplicated 'range'
    it should evaluateTo(
      """let range1 = Timestamp.Range(Date.Build(2001, 1, 1), Date.Build(2001, 1, 5), step=Interval.Build(days=1)),
        |    range2 = Timestamp.Range(Date.Build(2001, 1, 1), Date.Build(2001, 1, 5), step=Interval.Build(days=1))
        |in {range1, range2}""".stripMargin
    )
    // expected result
    it should evaluateTo("""{[Timestamp.Build(2001, 1, 1, 0, 0), Timestamp.Build(2001, 1, 2, 0, 0),
      | Timestamp.Build(2001, 1, 3, 0, 0), Timestamp.Build(2001, 1, 4, 0, 0)],
      | [Timestamp.Build(2001, 1, 1, 0, 0), Timestamp.Build(2001, 1, 2, 0, 0),
      | Timestamp.Build(2001, 1, 3, 0, 0), Timestamp.Build(2001, 1, 4, 0, 0)]}""".stripMargin)
  }

  test("""let range = Timestamp.Range(Date.Build(2001, 1, 1), Date.Build(2001, 1, 5), step=Interval.Build(days=1)),
    |         values = Collection.Unnest(range,
    |                      r1 -> Collection.Filter(range, r2 -> r1 == r2))
    |in Collection.Count(values)""".stripMargin) { it =>
    it should evaluateTo("""let values = Collection.Unnest(Timestamp.Range(Date.Build(2001, 1, 1), Date.Build(2001, 1, 5), step=Interval.Build(days=1)),
      |                      r1 -> Collection.Filter(Timestamp.Range(Date.Build(2001, 1, 1), Date.Build(2001, 1, 5), step=Interval.Build(days=1)), r2 -> r1 == r2))
      |in Collection.Count(values)""".stripMargin)
    it should evaluateTo("4")
  }

}
