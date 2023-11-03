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

import raw.utils.TestData
import raw.compiler.rql2.tests.CompilerTestContext

trait BinaryExpEqTest extends CompilerTestContext with CombinationSpecTestHelper {

  test("1 == 1")(it => it should evaluateTo("true"))
  test("1 == 2")(it => it should evaluateTo("false"))
  test("2 == 1")(it => it should evaluateTo("false"))

  test("Time.Build(9, 30) == Time.Build(9, 30)")(it => it should evaluateTo("true"))
  test("Time.Build(9, 30) == Time.Build(10, 30)")(it => it should evaluateTo("false"))
  test("Time.Build(9, 30) == 123")(it => it should typeErrorAs("time but got int"))

  test("Date.Build(2022, 1, 15) == Date.Build(2022, 1, 15)")(it => it should evaluateTo("true"))
  test("Date.Build(2022, 1, 15) == Timestamp.Build(2022, 1, 15, 0, 0)")(it => it should evaluateTo("true"))
  test("Date.Build(2022, 1, 15) == Date.Build(2010, 1, 15)")(it => it should evaluateTo("false"))
  test("Date.Build(2022, 1, 15) == Time.Build(10, 15)")(it =>
    it should typeErrorAs("expected compatible with date but got time")
  )

  test("""Nullable.Build(1) == 1""")(_ should evaluateTo("true"))
  test("""Nullable.Build(1) == 2""")(_ should evaluateTo("false"))
  test("""Nullable.Build(1) == Nullable.Build(1)""")(_ should evaluateTo("true"))
  test("""Nullable.Build(1) == Nullable.Build(2)""")(_ should evaluateTo("false"))

  test("""1 == null""")(_ should evaluateTo("null"))
  test("""null == 1""")(_ should evaluateTo("null"))
  test("""(1 + null) == (1 + null)""")(_ should evaluateTo("null"))
  test("""(1 + null) == Nullable.Build(1)""")(_ should evaluateTo("null"))
  test("""Nullable.Build(1) == (1 + null)""")(_ should evaluateTo("null"))

  test("""null == null""")(_ should (typeAs("bool") and evaluateTo("null")))

  test("let t: time = Time.Build(10, 0) in t == Time.Build(10, 0)")(it => it should evaluateTo("true"))

  // try support. Error propagate in all cases.
  test("""1 == Success.Build(1)""")(_ should evaluateTo("true"))
  test("""1 == Success.Build(2)""")(_ should evaluateTo("false"))
  test("""Success.Build(1) == 1""")(_ should evaluateTo("true"))
  test("""Success.Build(1) == 2""")(_ should evaluateTo("false"))
  test("""Success.Build(1) == Success.Build(1)""")(_ should evaluateTo("true"))
  test("""Success.Build(1) == Success.Build(2)""")(_ should evaluateTo("false"))

  test("""1 + Error.Build("argh!") == 1 + Error.Build("gasp!")""")(_ should runErrorAs("argh!"))
  test("""1 + Error.Build("gasp!") == 1 + Error.Build("argh!")""")(_ should runErrorAs("gasp!"))
  test("""Error.Build("argh!") == Error.Build("argh!")""")(_ should (typeAs("bool") and runErrorAs("argh!")))
  test("""Error.Build("gasp!") == Error.Build("argh!")""")(_ should (typeAs("bool") and runErrorAs("gasp!")))
  test("""1 == Error.Build("argh!")""")(_ should runErrorAs("argh!"))
  test("""Error.Build("argh!") == 1""")(_ should runErrorAs("argh!"))

  test("""let x: int = Success.Build(1) in 1 == x""")(_ should evaluateTo("true"))
  test("""let x: int = Success.Build(2) in 1 == x""")(_ should evaluateTo("false"))
  test("""let x: int = Success.Build(1) in x == 1""")(_ should evaluateTo("true"))
  test("""let x: int = Success.Build(1) in x == 2""")(_ should evaluateTo("false"))
  test("""let x: int = Success.Build(1), y: int = Success.Build(1) in y == x""")(_ should evaluateTo("true"))
  test("""let x: int = Success.Build(1), y: int = Success.Build(2) in x == y""")(_ should evaluateTo("false"))
  test("""let x: int = Success.Build(1), y: int = Error.Build("argh!") in y == x""")(_ should runErrorAs("argh!"))
  test("""let x: int = Success.Build(1), y: int = Error.Build("argh!") in x == y""")(_ should runErrorAs("argh!"))
  test("""let x: int = Error.Build("argh!"), y: int = Error.Build("gasp!") in x == y""")(_ should runErrorAs("argh!"))
  test("""let x: int = Error.Build("argh!"), y: int = Error.Build("gasp!") in y == x""")(_ should runErrorAs("gasp!"))
  test("""let x: int = Error.Build("argh!") in 1 == x""")(_ should runErrorAs("argh!"))
  test("""let x: int = Error.Build("argh!") in x == 1""")(_ should runErrorAs("argh!"))

  val numbers = Table(
    "numbers",
    TestValue("byte", "1b", "2b"),
    TestValue("short", "Short.From(1)", "Short.From(2)"),
    TestValue("int", "1", "2"),
    TestValue("long", "1l", "2l"),
    TestValue("float", "1f", "2f"),
    TestValue("double", "1d", "2d"),
    TestValue("decimal", "Decimal.From(1)", "Decimal.From(2)")
  )

  val nonComparable = Table(
    "non-comparable",
    TestValue("record(a: int)", "{a: 1}", "{a: 2}"),
    TestValue("collection(int)", "Collection.Build(1, 2, 3)", "Collection.Build(4, 5, 6)"),
    TestValue("list(int)", "[1, 2, 3]", "[4, 5, 6]"),
    TestValue("binary", """Binary.FromString("Hello") """, """Binary.FromString("World") """)
  )

  val dateTimestamp = Table(
    "date-timestamp",
    TestValue("date", "Date.Build(2023, 2, 6)", "Date.Build(2022, 1, 5)"),
    TestValue("timestamp", "Timestamp.Build(2023, 2, 6, 0, 0)", "Timestamp.Build(2022, 1, 5, 0, 0)")
  )

  val nonNumbers = Table(
    "non-numbers",
    TestValue("string", """ "hello!" """, """ "world!" """),
    TestValue("bool", "true", "false"),
    TestValue("time", "Time.Build(10, 0)", "Time.Build(9, 0)"),
    TestValue("interval", "Interval.Build(years=1, months=2, days=3)", "Interval.Build(hours=3, minutes=2, seconds=1)")
  ) ++ dateTimestamp

  test(" number == number") { _ =>
    forAll(combinations(numbers, numbers)) {
      case (n1, n2) =>
        TestData(s"${n1.v1} == ${n2.v1}") should evaluateTo("true")
        TestData(s"${n1.v1} == ${n2.v2}") should evaluateTo("false")
    }
  }

  test("date timestamp == date timestamp") { _ =>
    forAll(combinations(dateTimestamp, dateTimestamp)) {
      case (n1, n2) =>
        TestData(s"${n1.v1} == ${n2.v1}") should evaluateTo("true")
        TestData(s"${n1.v1} == ${n2.v2}") should evaluateTo("false")
    }
  }

  test("number == non-number") { _ =>
    forAll(combinations(numbers, nonNumbers)) {
      case (n, x) =>
        TestData(s"${n.v1} == ${x.v1}") should typeErrorAs(s"expected compatible with ${n.tipe} but got ${x.tipe}")
        TestData(s"${x.v1} == ${n.v1}") should typeErrorAs(s"expected compatible with ${x.tipe} but got ${n.tipe}")
    }
  }

  test("number == non-comparable") { _ =>
    forAll(combinations(numbers, nonComparable)) {
      case (n, x) =>
        TestData(s"${n.v1} == ${x.v1}") should typeErrorAs(s"expected compatible with ${n.tipe} but got ${x.tipe}")
        TestData(s"${x.v1} == ${n.v1}") should typeErrorAs(s"expected compatible with ${x.tipe} but got ${n.tipe}")
    }
  }

  test("non-number == non-number") { _ =>
    forAll(nonNumbers) { x =>
      TestData(s"${x.v1} == ${x.v1}") should evaluateTo("true")
      TestData(s"${x.v1} == ${x.v2}") should evaluateTo("false")
    }
  }

  test("non-comparable == non-comparable") { _ =>
    forAll(nonComparable) { x =>
      TestData(s"${x.v1} == ${x.v1}") should typeErrorAs(
        s"expected either number or temporal or bool or string but got ${x.tipe}"
      )
    }
  }

  test("error == value") { _ =>
    forAll(nonNumbers ++ numbers)(x => TestData(s"""${x.v1} == Error.Build("argh!")""") should runErrorAs("argh!"))
  }

  test("success comparable == comparable") { _ =>
    forAll(nonNumbers ++ numbers) { x =>
      TestData(s"""let x: ${x.tipe} = ${x.v1} in x == ${x.v1}""") should evaluateTo("true")
      TestData(s"""let x: ${x.tipe} = ${x.v1} in x == ${x.v2}""") should evaluateTo("false")
    }
  }
}
