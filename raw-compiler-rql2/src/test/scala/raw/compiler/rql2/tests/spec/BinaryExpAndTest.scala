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

import org.scalatest.prop.TableDrivenPropertyChecks
import raw.TestData
import raw.compiler.rql2.tests.CompilerTestContext

trait BinaryExpAndTest extends CompilerTestContext with TableDrivenPropertyChecks {

  test("""true and true""")(_ should evaluateTo("true"))
  test("""true and false""")(_ should evaluateTo("false"))
  test("""false and true""")(_ should evaluateTo("false"))
  test("""false and false""")(_ should evaluateTo("false"))

  // nullable booleans
  test("""true and Nullable.Build(true)""")(_ should evaluateTo("true"))
  test("""true and Nullable.Build(false)""")(_ should evaluateTo("false"))
  test("""true and null""")(_ should evaluateTo("null"))
  test("""false and null""")(_ should evaluateTo("false"))
  test("""null and false""")(_ should evaluateTo("false"))

  test("""true and Success.Build(true)""")(_ should evaluateTo("true"))
  test("""true and Success.Build(false)""")(_ should evaluateTo("false"))
  test("""true and Error.Build("argh!")""")(_ should runErrorAs("argh!"))
  test("""false and Error.Build("argh!")""")(_ should runErrorAs("argh!"))

  test("""Success.Build(true) and true""")(_ should evaluateTo("true"))
  test("""Success.Build(false) and false""")(_ should evaluateTo("false"))
  test("""Error.Build("argh!") and true""")(_ should runErrorAs("argh!"))
  test("""Error.Build("argh!") and false""")(_ should runErrorAs("argh!"))

  test("true and 1")(_ should typeErrorAs("expected bool but got int"))
  test("false and 1")(_ should typeErrorAs("expected bool but got int"))
  test("1 and true")(_ should typeErrorAs("expected bool but got int"))
  test("""1 and 1""")(_ should typeErrorAs("expected bool but got int "))
  test(""" "hello!" and true """)(_ should typeErrorAs("expected bool but got string"))

  test("""true and and true""")(_ shouldNot parse)

  val nonBooleans = Table(
    "non-booleans",
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

  test("and non-boolean") { _ =>
    forAll(nonBooleans) { n =>
      TestData(s"false and ${n.v1}") should typeErrorAs(s"expected bool but got ${n.tipe}")
      TestData(s"${n.v1} and true") should typeErrorAs(s"expected bool but got ${n.tipe}")
    }
  }
}
