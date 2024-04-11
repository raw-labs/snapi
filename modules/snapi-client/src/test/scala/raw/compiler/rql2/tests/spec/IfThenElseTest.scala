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

import raw.client.utils.TestData
import raw.compiler.rql2.tests.CompilerTestContext

trait IfThenElseTest extends CompilerTestContext with CombinationSpecTestHelper {

  test("""
    |if true then "a" else "b"
    |
    |""".stripMargin) { it =>
    it should typeAs("string")
    it should evaluateTo(""" "a" """)
  }

  test("""
    |if false then 1 else 2
    |
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("2")
  }

  test("""
    |if (true) then "a" else "b"
    |
    |""".stripMargin) { it =>
    it should typeAs("string")
    it should evaluateTo(""" "a" """)
  }

  test("""
    |if (false) then 1 else 2
    |
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("2")
  }

  test("""
    |if (1) then "a" else "b"
    |
    |""".stripMargin)(it => it should typeErrorAs("expected bool but got int"))

  test("""
    |if (true) then "a" else 1
    |
    |""".stripMargin)(it => it should typeErrorAs("expected compatible with string but got int"))

  test("""
    |if (false) then "a" else 1
    |
    |""".stripMargin)(it => it should typeErrorAs("expected compatible with string but got int"))

  test("""
    |if (true) then 1 else 2f""".stripMargin) { it =>
    it should typeAs("float")
    it should evaluateTo("1f")
  }

  test("""
    |if (true) then 2d else 1""".stripMargin) { it =>
    it should typeAs("double")
    it should evaluateTo("2d")
  }

  test("""if (true) then 1 else "hello" """)(_ should typeErrorAs("expected compatible with int but got string"))

  val numbers = Seq(
    TestValue("byte", "1b", "0b", 0),
    TestValue("short", "Short.From(1)", "Short.From(0)", 1),
    TestValue("int", "1", "0", 2),
    TestValue("long", "1l", "0l", 3),
    TestValue("float", "1f", "0f", 4),
    TestValue("double", "1d", "0d", 5),
    TestValue("decimal", "Decimal.From(1)", "Decimal.From(0)", 6)
  )

  val dateTimestamp = Seq(
    TestValue("date", "Date.Build(2022, 1, 5)", "Date.Build(2023, 2, 6)", 0),
    TestValue("timestamp", "Timestamp.Build(2022, 1, 5, 0, 0)", "Timestamp.Build(2023, 2, 6, 0, 0)", 1)
  )

  val nonNumbers = Seq(
    TestValue("record(a: int)", "{a: 1}", "{a: 2}"),
    TestValue("collection(int)", "Collection.Build(1, 2, 3)", "Collection.Build(4, 5, 6)"),
    TestValue("list(int)", "[1, 2, 3]", "[4, 5, 6]"),
    TestValue("time", """Time.Build(10, 0)""", """Time.Build(9, 0)"""),
    TestValue("binary", """Binary.FromString("Hello") """, """Binary.FromString("World") """),
    TestValue(
      "interval",
      """Interval.Build(years=1, months=2, days=3)""",
      """Interval.Build(days=1, hours=2, minutes=3)"""
    ),
    TestValue("string", """ "hello!" """, """ "world!" """),
    TestValue("bool", "true", "false")
  )

  test("if then else number number") { _ =>
    forAll(combinations(numbers, numbers)) {
      case (n1, n2) =>
        // it has to type as the one with the biggest priority
        val max = if (n1.priority > n2.priority) n1 else n2
        TestData(s"if (true) then ${n1.v1} else ${n2.v2}") should (evaluateTo(max.v1) and typeAs(max.tipe))
        TestData(s"if (false) then ${n1.v1} else ${n2.v2}") should (evaluateTo(max.v2) and typeAs(max.tipe))
    }
  }

  test("if then else date-timestamp") { _ =>
    forAll(combinations(dateTimestamp, dateTimestamp)) {
      case (n1, n2) =>
        val max = if (n1.priority > n2.priority) n1 else n2
        TestData(s"if (true) then ${n1.v1} else ${n2.v2}") should (evaluateTo(max.v1) and typeAs(max.tipe))
        TestData(s"if (false) then ${n1.v1} else ${n2.v2}") should (evaluateTo(max.v2) and typeAs(max.tipe))
    }
  }

  test("if then else non-number non-number") { _ =>
    forAll(combinations(nonNumbers, nonNumbers)) {
      case (n1, n2) =>
        if (n1.tipe == n2.tipe) {
          TestData(s"if (true) then  ${n1.v1} else ${n1.v2}") should (evaluateTo(n1.v1) and typeAs(n1.tipe))
          TestData(s"if (false) then  ${n1.v1} else ${n1.v2}") should (evaluateTo(n1.v2) and typeAs(n1.tipe))
        } else {
          TestData(s"if (true) then  ${n1.v1} else ${n2.v2}") should typeErrorAs(
            s"expected compatible with ${n1.tipe} but got ${n2.tipe}"
          )
        }
    }
  }

  test("if then else Success non-number non-number") { _ =>
    forAll(combinations(nonNumbers, nonNumbers)) {
      case (n1, n2) =>
        if (n1.tipe == n2.tipe) {
          TestData(s"if (true) then let x: ${n1.tipe} = ${n1.v1} in x else ${n1.v2}") should
            (evaluateTo(n1.v1) and typeAs(n1.tipe))
          TestData(s"if (false) then  ${n1.v1} else let x: ${n1.tipe} = ${n1.v2} in x") should
            (evaluateTo(n1.v2) and typeAs(n1.tipe))
        } else {
          TestData(s"if (true) then let x: ${n1.tipe} = ${n1.v1} in x else ${n2.v2}") should typeErrorAs(
            s"expected compatible with ${n1.tipe} but got ${n2.tipe}"
          )
        }
    }
  }
}
