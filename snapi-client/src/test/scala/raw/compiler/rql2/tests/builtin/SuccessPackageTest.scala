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

package raw.compiler.rql2.tests.builtin

import org.scalatest.prop.TableDrivenPropertyChecks
import raw.utils.TestData
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class SuccessPackageTest extends Rql2TruffleCompilerTestContext with TableDrivenPropertyChecks {

  test("Success.Build(1)")(_ should evaluateTo("1"))

  test("""Success.Build("abc")""")(_ should evaluateTo(""" "abc" """))

  test("Success.Build(Time.Build(10, 0))")(_ should typeErrorAs("argument is tryable"))

  test("Success.Build(Success.Build(1))")(_ should typeErrorAs("argument is tryable"))

  test("""let
    |    x: int = 1
    |in
    |    Success.Build(x)""".stripMargin)(_ should typeErrorAs("argument is tryable"))

  val nonTryable = Table(
    ("type", "value"),
    ("byte", "1b"),
    ("short", "Short.From(1)"),
    ("int", "1"),
    ("long", "1l"),
    ("float", "1f"),
    ("double", "1d"),
    ("decimal", "Decimal.From(1)"),
    ("string", """ "hello!" """),
    ("bool", "true"),
    ("record(a: int)", "{a: 1}"),
    ("collection(int)", "Collection.Build(1, 2, 3)"),
    ("list(int)", "[1, 2, 3]"),
    ("binary", """Binary.FromString("Hello") """),
    ("interval", "Interval.Build(hours=3, minutes=2, seconds=1)"),
    ("interval", "Interval.Build(hours=3, minutes=2, seconds=1)")
  )

  val tryable = Table(
    ("type", "value"),
    ("time", "Time.Build(9, 0)"),
    ("date", "Date.Build(2022, 1, 5)"),
    ("timestamp", "Timestamp.Build(2022, 1, 5, 0, 0)")
  )

  test("non-tryable") { _ =>
    forAll(nonTryable)((tipe, v) => TestData(s"Success.Build($v)") should (evaluateTo(v) and typeAs(tipe)))
  }

  test("tryable") { _ =>
    forAll(tryable)((_, v) => TestData(s"Success.Build($v)") should typeErrorAs("argument is tryable"))
  }

}
