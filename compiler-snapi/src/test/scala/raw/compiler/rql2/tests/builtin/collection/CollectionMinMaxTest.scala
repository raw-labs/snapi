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

import raw.compiler.rql2.errors.ItemsNotComparable
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class CollectionMinMaxTest extends Rql2TruffleCompilerTestContext {

  // Date
  test("""
    |Collection.Min(Collection.Build(
    |   Date.Build(2022, 1, 15),
    |   Date.Build(2022, 1, 14),
    |   Date.Build(2022, 1, 16))
    |)""".stripMargin)(_ should evaluateTo("Date.Build(2022, 1, 14)"))

  test("""
    |Collection.Max(Collection.Build(
    |   Date.Build(2022, 1, 15),
    |   Date.Build(2022, 1, 16),
    |   Date.Build(2022, 1, 14))
    |)""".stripMargin)(_ should evaluateTo("Date.Build(2022, 1, 16)"))

  // Time
  test("""
    |Collection.Min(Collection.Build(
    |   Time.Build(9, 31),
    |   Time.Build(9, 30),
    |   Time.Build(9, 32))
    |)""".stripMargin)(_ should evaluateTo("Time.Build(9, 30)"))

  test("""
    |Collection.Max(Collection.Build(
    |   Time.Build(9, 31),
    |   Time.Build(9, 32),
    |   Time.Build(9, 30))
    |)""".stripMargin)(_ should evaluateTo("Time.Build(9, 32)"))

  // Timestamps
  test("""
    |Collection.Min(Collection.Build(
    |   Timestamp.Build(2022, 1, 15, 9, 31),
    |   Timestamp.Build(2022, 1, 15, 9, 30),
    |   Timestamp.Build(2022, 1, 15, 9, 32))
    |)""".stripMargin)(_ should evaluateTo("Timestamp.Build(2022, 1, 15, 9, 30)"))

  test("""
    |Collection.Max(Collection.Build(
    |   Timestamp.Build(2022, 1, 15, 9, 31),
    |   Timestamp.Build(2022, 1, 15, 9, 32),
    |   Timestamp.Build(2022, 1, 15, 9, 30))
    |)""".stripMargin)(_ should evaluateTo("Timestamp.Build(2022, 1, 15, 9, 32)"))

  // strings

  test("""
    |Collection.Min(Collection.Build(
    |   "tralala",
    |   "ploum",
    |   "boum")
    |)""".stripMargin)(_ should evaluateTo("\"boum\""))

  test("""
    |Collection.Max(Collection.Build(
    |   "tralala",
    |   "ploum",
    |   "boum")
    |)""".stripMargin)(_ should evaluateTo("\"tralala\""))

  // Min

  test("""
    |Collection.Min(Collection.Build(4,2,7,3,1,5))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""
    |Collection.Min(Collection.Build(4,2,null,7,3))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("2")
  }

  test("""
    |let empty = Collection.Filter(Collection.Build(1), x -> x == 0)
    |in Collection.Min(empty)""".stripMargin) { it =>
    it should typeAs("int")
    it should run
  }

  test("""let
    |    data = Collection.Build("1", "2", "3", "4", "5", "6")
    |in
    |    Collection.Filter(data, n -> n == "4")""".stripMargin)(it =>
    it should evaluateTo("""Collection.Build("4") """)
  )
  // Max

  test("""
    |Collection.Max(Collection.Build(4,2,7,3,1,5))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("7")
  }

  test("""
    |Collection.Max(Collection.Build(4,2,7,null,3))""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("7")
  }

  test("""
    |let empty = Collection.Filter(Collection.Build(1), x -> x == 0)
    |in Collection.Max(empty)""".stripMargin) { it =>
    it should typeAs("int")
    it should run
  }

  // Errors break the whole aggregation (like sum and company)
  test("""Collection.Min(Collection.Build(1,2,3,Error.Build("bug")))""")(_ should runErrorAs("bug"))

  // Non-comparable types aren't accepted.
  test("""
    |let f(x: int) = x + 1,
    |    g(x: int) = x + 2,
    |    min = Collection.Min(Collection.Build(f, g))
    |in min(10)""".stripMargin)(_ should runErrorAs(ItemsNotComparable.message))

  test("""
    |let f(x: int) = x + 1,
    |    g(x: int) = x + 2,
    |    r1 = {f1: f, f2: g},
    |    r2 = {f1: g, f2: f},
    |    min = Collection.Min(Collection.Build(r1, r2))
    |in min.f2(10) * min.f2(10)""".stripMargin)(_ should runErrorAs(ItemsNotComparable.message))
}
