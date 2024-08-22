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
import org.scalatest.prop.TableDrivenPropertyChecks
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class UnaryExpNegTest extends Rql2TruffleCompilerTestContext with TableDrivenPropertyChecks {

  test("- 1") { it =>
    it should typeAs("int")
    it should evaluateTo("-1")
  }

  test("-- 1") { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("- 1.0") { it =>
    it should typeAs("double")
    it should evaluateTo("-1.0")
  }

  test("-- 1.0") { it =>
    it should typeAs("double")
    it should evaluateTo("1.0")
  }

  test("""- null""") { it =>
    it should typeAs("undefined")
    it should evaluateTo("null")
  }

  test("""- () -> 1""")(it => it should runErrorAs("expected number but got () -> int"))

  val numbers = Table(
    "numbers",
    TestValue("byte", "1b", "-1b"),
    TestValue("short", "Short.From(1)", "Short.From(-1)"),
    TestValue("int", "1", "-1"),
    TestValue("long", "1l", "-1l"),
    TestValue("float", "1f", "-1f"),
    TestValue("double", "1d", "-1d"),
    TestValue("decimal", "Decimal.From(1)", "Decimal.From(-1)")
  )

  val nonNumbers = Table(
    "non-numbers",
    TestValue("string", """ "hello!" """),
    TestValue("bool", "true"),
    TestValue("record(a: int)", "{a: 1}"),
    TestValue("collection(int)", "Collection.Build(1, 2, 3)"),
    TestValue("list(int)", "[1, 2, 3]"),
    TestValue("binary", """Binary.FromString("Hello") """),
    TestValue("time", "Time.Build(9, 0)"),
    TestValue("interval", "Interval.Build(hours=3, minutes=2, seconds=1)"),
    TestValue("date", "Date.Build(2022, 1, 5)"),
    TestValue("timestamp", "Timestamp.Build(2022, 1, 5, 0, 0)")
  )

  test("- number")(_ => forAll(numbers)(n => TestData(s" - ${n.v1}") should (evaluateTo(n.v2) and typeAs(n.tipe))))

  test("- non-number") { _ =>
    forAll(nonNumbers)(n => TestData(s" - ${n.v1}") should typeErrorAs(s"expected number but got ${n.tipe}"))
  }
}
