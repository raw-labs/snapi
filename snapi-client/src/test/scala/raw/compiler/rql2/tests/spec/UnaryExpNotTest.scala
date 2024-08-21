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

import com.rawlabs.utils.core.TestData
import org.scalatest.prop.TableDrivenPropertyChecks
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class UnaryExpNotTest extends Rql2TruffleCompilerTestContext with TableDrivenPropertyChecks {
  test("""not true""".stripMargin) { it =>
    it should typeAs("bool")
    it should evaluateTo("false")
  }

  test("""not false""".stripMargin) { it =>
    it should typeAs("bool")
    it should evaluateTo("true")
  }

  test("""not (not true)""".stripMargin) { it =>
    it should typeAs("bool")
    it should evaluateTo("true")
  }

  test(" not not true")(it => it should evaluateTo("true"))

  test(" not false and not false")(it => it should evaluateTo("true"))

  test("not Success.Build(true)")(it => it should evaluateTo("false"))

  test("""not Error.Build("argh!")""")(it => it should runErrorAs("argh!"))

  test("""not () -> true""")(it => it should runErrorAs("expected bool but got () -> bool"))

  val nonBooleans = Table(
    "nonBooleans",
    TestValue("byte", "1b"),
    TestValue("short", "Short.From(1)"),
    TestValue("int", "1"),
    TestValue("long", "1l"),
    TestValue("float", "1f"),
    TestValue("double", "1d"),
    TestValue("decimal", "Decimal.From(1)"),
    TestValue("string", """ "hello!" """),
    TestValue("record(a: int)", "{a: 1}"),
    TestValue("collection(int)", "Collection.Build(1, 2, 3)"),
    TestValue("list(int)", "[1, 2, 3]"),
    TestValue("binary", """Binary.FromString("Hello") """),
    TestValue("date", "Date.Build(2022, 1, 5)"),
    TestValue("timestamp", "Timestamp.Build(2022, 1, 5, 0, 0)"),
    TestValue("time", "Time.Build(9, 0)"),
    TestValue("interval", "Interval.Build(hours=3, minutes=2, seconds=1)")
  )

  test("not non boolean") { _ =>
    forAll(nonBooleans)(n => TestData(s""" not ${n.v1} """) should runErrorAs(s"expected bool but got ${n.tipe}"))
  }
}
