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

trait LetBindTest extends CompilerTestContext with CombinationSpecTestHelper {

  test("""let x = 1
    |in x
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""let x: float = 1f
    |in x
    |""".stripMargin) { it =>
    it should typeAs("float")
    it should evaluateTo(""" 1f """)
  }

  test("""let x: string = 1
    |in x
    |""".stripMargin)(it => it should typeErrorAs("expected string but got in"))

  test("""
    |let x = 1,
    |    y = x
    |in
    |  y
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""
    |let x = 1,
    |    x = 2
    |in
    |  x
    |""".stripMargin)(it => it should typeErrorAs("x is declared more than once"))

  test("""
    |let x = 1,
    |    x = x
    |in
    |  x
    |""".stripMargin)(it => it should typeErrorAs("x is declared more than once"))

  test("""
    |let x =
    |   let x = 2
    |   in x
    |in x
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("2")
  }

  test("""
    |let x = 1,
    |    y =
    |    let z = x
    |    in z
    |in y
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("""
    |let x = 1,
    |    y =
    |    let x = 2
    |    in x
    |in y
    |""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("2")
  }

  test("""
    |let x = 1
    |in y
    |""".stripMargin)(it => it should typeErrorAs("y is not declared"))

  test("""let
    |   x: timestamp = Date.Build(2023, 2, 13)
    |in
    |   x""".stripMargin)(it => it should evaluateTo("""Timestamp.Build(2023, 2, 13, 0, 0)"""))

  test("""let
    |   x: string = 123
    |in
    |   x""".stripMargin)(it => it should typeErrorAs("expected string but got int"))

  test("""let
    |   x = 1
    |in
    |   x""".stripMargin)(it => it should typeAs("int"))

  test("""let x = 1""")(it =>
    it should parseErrorAs(
      "mismatched input '<EOF>' expecting {'in', '==', '!=', '<=', '<', '>=', '>', '+', '-', '*', '/', '%', 'and', 'or', '(', ',', '.'}"
    )
  )

  // CTM: Was expecting something like the error before
  test("""let let x = 1 in x""")(it =>
    it should parseErrorAs(
      "extraneous input 'let' expecting {'bool', 'string', 'location', 'binary', 'byte', 'short', 'int', 'long', 'float', 'double', 'decimal', 'date', 'time', 'interval', 'timestamp', 'record', 'collection', 'list', 'rec', 'undefined', NON_ESC_IDENTIFIER, ESC_IDENTIFIER}"
    )
  )

  val numbers = Table(
    "numbers",
    TestValue("byte", "1b", priority = 0),
    TestValue("short", "Short.From(1)", priority = 1),
    TestValue("int", "1", priority = 2),
    TestValue("long", "1l", priority = 3),
    TestValue("float", "1f", priority = 4),
    TestValue("double", "1d", priority = 5),
    TestValue("decimal", "Decimal.From(1)", priority = 6)
  )

  val dateTimestmap = Table(
    "dateTimestmap",
    TestValue("date", "Date.Build(2023, 2, 6)"),
    TestValue("timestamp", "Timestamp.Build(2023, 2, 6, 10, 0)")
  )

  val nonNumbers = Table(
    "nonNumbers",
    TestValue("record(a: int)", "{a: 1}"),
    TestValue("collection(int)", "Collection.Build(1, 2, 3)"),
    TestValue("list(int)", "[1, 2, 3]"),
    TestValue("time", "Time.Build(10, 0)"),
    TestValue("interval", "Interval.Build(months=1, days=2, hours=3)"),
    TestValue("binary", """Binary.FromString("Hello World") """),
    TestValue("string", """ "hello!" """),
    TestValue("bool", "true")
  )

  test("let number as other number") { _ =>
    forAll(combinations(numbers, numbers)) {
      case (n1, n2) =>
        val query = TestData(s"""let
          |   x: ${n1.tipe} = ${n2.v1}
          |in
          |   x""".stripMargin)

        // allowing only up-casting values
        if (n1.priority >= n2.priority) {
          query should (evaluateTo(n1.v1) and typeAs(n1.tipe))
        } else {
          query should typeErrorAs(s"expected ${n1.tipe} but got ${n2.tipe}")
        }
    }
  }

  test("let non-number as non-number") { _ =>
    forAll(combinations(nonNumbers, nonNumbers)) {
      case (n1, n2) =>
        val query = TestData(s"""let
          |   x: ${n1.tipe} = ${n2.v1}
          |in
          |   x""".stripMargin)

        if (n1.tipe == n2.tipe) {
          query should (evaluateTo(n1.v1) and typeAs(n1.tipe))
        } else {
          query should typeErrorAs(s"expected ${n1.tipe} but got ${n2.tipe}")
        }
    }
  }

  test("let value as null") { _ =>
    forAll(numbers ++ dateTimestmap ++ nonNumbers) { x =>
      val query = TestData(s"""let x: ${x.tipe} = null in x""")
      query should (evaluateTo("null") and typeAs(x.tipe))
    }
  }

  test("let number + undefined") { _ =>
    forAll(numbers) { x =>
      val query = TestData(s"""let
        |  y = null,
        |  x = ${x.v1}
        |in x + y""".stripMargin)
      query should (evaluateTo("null") and typeAs(x.tipe))
    }
  }
}
