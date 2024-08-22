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

package com.rawlabs.snapi.compiler.tests.spec

import com.rawlabs.utils.core.TestData
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class BinaryExpDivTest extends Rql2TruffleCompilerTestContext with CombinationSpecTestHelper {

  test("""4 / 2""") { it =>
    it should typeAs("int")
    it should evaluateTo("2")
  }

  test("""Decimal.From(4) / Decimal.From(2)""".stripMargin) { it =>
    it should typeAs("decimal")
    it should evaluateTo("2")
  }

  test("""4f / 2""") { it =>
    it should typeAs("float")
    it should evaluateTo("2f")
  }

  test("""1 / 2""".stripMargin) { it =>
    it should typeAs("int")
    it should evaluateTo("0")
  }

  test("""1 / 2l""".stripMargin) { it =>
    it should typeAs("long")
    it should evaluateTo("0l")
  }

  test("""1 / 2.0""".stripMargin) { it =>
    it should typeAs("double")
    it should evaluateTo("0.5")
  }

  test(""" "abc" / 1  """)(it => it should typeErrorAs("expected number but got string"))

  test("""1 / "abc" """)(it => it should typeErrorAs("expected number but got string"))

  test("""Time.Build(10, 0) / Time.Build(10, 0)""") {
    _ should typeErrorAs("expected number but got time")
  }

  test("""Time.Build(10, 0) / null""") {
    _ should typeErrorAs("expected number but got time")
  }

  test("""null / Time.Build(10, 0)""") {
    _ should typeErrorAs("expected number but got time")
  }

  test("1 / null") { it =>
    it should evaluateTo("null")
    it should typeAs("int")
  }

  val numbers = Table(
    "numbers",
    TestValue("byte", "2b", "1b", 0),
    TestValue("short", "Short.From(2)", "Short.From(1)", 1),
    TestValue("int", "2", "1", 2),
    TestValue("long", "2l", "1l", 3),
    TestValue("float", "2f", "1f", 4),
    TestValue("double", "2d", "1d", 5),
    TestValue("decimal", "Decimal.From(2)", "Decimal.From(1)", 6)
  )

  val nonNumbers = Table(
    "non-numbers",
    TestValue("string", """ "hello!" """),
    TestValue("record(a: int)", "{a: 1}"),
    TestValue("collection(int)", "Collection.Build(1, 2, 3)"),
    TestValue("list(int)", "[1, 2, 3]"),
    TestValue("date", """Date.Build(2023, 2, 6)"""),
    TestValue("time", """Time.Build(10, 0)"""),
    TestValue("timestamp", """Timestamp.Build(2023, 2, 6, 10, 0)"""),
    TestValue("interval", """Interval.Build(months=1, days=2, hours=3)"""),
    TestValue("binary", """Binary.FromString("Hello World") """),
    TestValue("bool", "true")
  )

  test("number / number") { _ =>
    forAll(combinations(numbers, numbers)) {
      case (n1, n2) =>
        val max = if (n1.priority > n2.priority) n1 else n2
        TestData(s"${n1.v1} / ${n2.v1}") should (evaluateTo(max.v2) and typeAs(max.tipe))
    }

  }

  test("number / null") { _ =>
    forAll(numbers) { n =>
      TestData(s" ${n.v1} / null") should evaluateTo("null")
      TestData(s"null/${n.v1}") should evaluateTo("null")
    }
  }

  test("number / non-number") { _ =>
    forAll(combinations(numbers, nonNumbers)) {
      case (n, other) =>
        TestData(s" ${n.v1}  / ${other.v1}") should typeErrorAs(s"expected number but got ${other.tipe}")
        TestData(s" ${other.v1}  / ${n.v1}") should typeErrorAs(s"expected number but got ${other.tipe}")
    }
  }

  test("non-number / non-number") { _ =>
    forAll(nonNumbers) { x =>
      TestData(s"""${x.v1} / 1""") should typeErrorAs(s"expected number but got ${x.tipe}")
      TestData(s"""1 / ${x.v1} """) should typeErrorAs(s"expected number but got ${x.tipe}")
    }
  }

  test("number / error") { _ =>
    forAll(numbers) { n =>
      TestData(s""" ${n.v1} / Error.Build("argh!")""") should runErrorAs("argh!")
      TestData(s"""Error.Build("argh!") / ${n.v1}""") should runErrorAs("argh!")
    }
  }

  test("success number / number") { _ =>
    forAll(numbers) { n =>
      TestData(s""" Success.Build(${n.v1}) / ${n.v1}""") should evaluateTo(n.v2)
      TestData(s""" ${n.v1} /Success.Build(${n.v1})""") should evaluateTo(n.v2)
    }
  }

  test("non number / error") { _ =>
    forAll(nonNumbers) { other =>
      TestData(s""" ${other.v1} / Error.Build("argh!")""") should typeErrorAs(s"expected number but got ${other.tipe}")
      TestData(s""" Error.Build("argh!") / ${other.v1}""") should typeErrorAs(s"expected number but got ${other.tipe}")
    }
  }
}
