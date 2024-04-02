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

package raw.compiler.rql2.tests.builtin.collection

import raw.compiler.rql2.tests.CompilerTestContext

trait CollectionRangeTest extends CompilerTestContext {

  // Long.Range

  test("""Long.Range(0, 10)""")(_ should evaluateTo("[0L,1L,2L,3L,4L,5L,6L,7L,8L,9L]"))
  test("""Long.Range(0, 1)""")(_ should evaluateTo("[0L]"))

  test("""Collection.Filter(Long.Range(0, 1000000), x -> x == 999999)""")(
    _ should evaluateTo("Collection.Build(999999L)")
  )

  // end = start
  test("""Long.Range(0, 0)""")(_ should evaluateTo("[]"))
  test("""Long.Range(12, 12)""")(_ should evaluateTo("[]"))
  // wrong direction (end < start)
  test("""Long.Range(3, 0)""")(_ should evaluateTo("[]"))

  test("""Long.Range(3, 8)""")(_ should evaluateTo("[3L,4L,5L,6L,7L]"))
  test("""Long.Range(3, 8, step=1)""")(_ should evaluateTo("[3L,4L,5L,6L,7L]"))

  // wrong step parameters
  test("""Long.Range(3, 8, step=-1)""")(_ should runErrorAs("range step has to be strictly positive"))
  test("""Long.Range(0, 10, step=0)""")(_ should runErrorAs("range step has to be strictly positive"))

  test("""Long.Range(0, 10, step=5)""")(_ should evaluateTo("[0L, 5L]"))
  test("""Long.Range(0, 11, step=5)""")(_ should evaluateTo("[0L, 5L, 10L]"))
  test("""Long.Range(0, 11, step=3)""")(_ should evaluateTo("[0L, 3L, 6L, 9L]"))

  test("""Int.Range(0, 10)""")(_ should evaluateTo("[0,1,2,3,4,5,6,7,8,9]"))
  test("""Int.Range(0, 1)""")(_ should evaluateTo("[0]"))

  // Int.Range

  // end = start
  test("""Int.Range(0, 0)""")(_ should evaluateTo("[]"))
  test("""Int.Range(12, 12)""")(_ should evaluateTo("[]"))
  // wrong direction (end < start)
  test("""Int.Range(3, 0)""")(_ should evaluateTo("[]"))

  test("""Int.Range(3, 8)""")(_ should evaluateTo("[3,4,5,6,7]"))
  test("""Int.Range(3, 8, step=1)""")(_ should evaluateTo("[3,4,5,6,7]"))

  // wrong step parameters
  test("""Int.Range(3, 8, step=-1)""")(_ should runErrorAs("range step has to be strictly positive"))
  test("""Int.Range(0, 10, step=0)""")(_ should runErrorAs("range step has to be strictly positive"))

  test("""Int.Range(0, 10, step=5)""")(_ should evaluateTo("[0, 5]"))
  test("""Int.Range(0, 11, step=5)""")(_ should evaluateTo("[0, 5, 10]"))
  test("""Int.Range(0, 11, step=3)""")(_ should evaluateTo("[0, 3, 6, 9]"))

  // Timestamp.Range

  // end = start
  test("""Timestamp.Range(Timestamp.Build(1975, 6, 23, 9, 30), Timestamp.Build(1975, 6, 23, 9, 30))""")(
    _ should evaluateTo("[]")
  )
  // wrong direction (end < start)
  test("""Timestamp.Range(Timestamp.Build(1975, 6, 23, 9, 30), Timestamp.Build(1975, 6, 21, 9, 30))""")(
    _ should evaluateTo("[]")
  )

  test("""Timestamp.Range(Timestamp.Build(1975, 6, 23, 9, 30), Timestamp.Build(1975, 6, 28, 9, 30))""")(
    _ should evaluateTo("""[
      |Timestamp.Build(1975, 6, 23, 9, 30),
      |Timestamp.Build(1975, 6, 24, 9, 30),
      |Timestamp.Build(1975, 6, 25, 9, 30),
      |Timestamp.Build(1975, 6, 26, 9, 30),
      |Timestamp.Build(1975, 6, 27, 9, 30)
      |]""".stripMargin)
  )

  test(
    """Timestamp.Range(Timestamp.Build(1975, 6, 23, 9, 30), Timestamp.Build(1975, 6, 28, 9, 30), step=Interval.Build(days=2))"""
  )(_ should evaluateTo("""[
    |Timestamp.Build(1975, 6, 23, 9, 30),
    |Timestamp.Build(1975, 6, 25, 9, 30),
    |Timestamp.Build(1975, 6, 27, 9, 30)
    |]""".stripMargin))

  test(
    """Timestamp.Range(Timestamp.Build(1975, 6, 23, 9, 30), Timestamp.Build(1975, 6, 28, 9, 30), step=Interval.Build(hours=12))"""
  )(_ should evaluateTo("""[
    |Timestamp.Build(1975, 6, 23, 9, 30),
    |Timestamp.Build(1975, 6, 23, 21, 30),
    |Timestamp.Build(1975, 6, 24, 9, 30),
    |Timestamp.Build(1975, 6, 24, 21, 30),
    |Timestamp.Build(1975, 6, 25, 9, 30),
    |Timestamp.Build(1975, 6, 25, 21, 30),
    |Timestamp.Build(1975, 6, 26, 9, 30),
    |Timestamp.Build(1975, 6, 26, 21, 30),
    |Timestamp.Build(1975, 6, 27, 9, 30),
    |Timestamp.Build(1975, 6, 27, 21, 30)
    |]""".stripMargin))

  test(
    """Timestamp.Range(Timestamp.Build(1975, 6, 23, 9, 30), Timestamp.Build(1975, 6, 28, 9, 30), step=Interval.Build(days=-1))"""
  )(_ should runErrorAs("range step has to be strictly positive"))

  test(
    """Timestamp.Range(Timestamp.Build(1975, 6, 23, 9, 30), Timestamp.Build(1975, 6, 28, 9, 30), step=Interval.Build(days=0))"""
  )(_ should runErrorAs("range step has to be strictly positive"))
}
