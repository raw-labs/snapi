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

class BinaryExpOrTest extends Rql2TruffleCompilerTestContext with TableDrivenPropertyChecks {

  test("""true or true""")(_ should evaluateTo("true"))
  test("""true or false""")(_ should evaluateTo("true"))
  test("""false or true""")(_ should evaluateTo("true"))
  test("""false or false""")(_ should evaluateTo("false"))

  // nullable booleans
  test("""true or Nullable.Build(true)""")(_ should evaluateTo("true"))
  test("""true or Nullable.Build(false)""")(_ should evaluateTo("true"))
  test("""true or null""")(_ should evaluateTo("true"))
  test("""false or null""")(_ should evaluateTo("null"))
  test("""null or true""")(_ should evaluateTo("true"))

  test("""true or Success.Build(true)""")(_ should evaluateTo("true"))
  test("""true or Success.Build(false)""")(_ should evaluateTo("true"))
  test("""true or Error.Build("argh!")""")(_ should runErrorAs("argh!"))
  test("""false or Error.Build("argh!")""")(_ should runErrorAs("argh!"))

  test("""Success.Build(true) or true""")(_ should evaluateTo("true"))
  test("""Success.Build(false) or false""")(_ should evaluateTo("false"))
  test("""Error.Build("argh!") or true""")(_ should runErrorAs("argh!"))
  test("""Error.Build("argh!") or false""")(_ should runErrorAs("argh!"))

  test("true or 1")(_ should typeErrorAs("expected bool but got int"))
  test("false or 1")(_ should typeErrorAs("expected bool but got int"))
  test("1 or true")(_ should typeErrorAs("expected bool but got int"))
  test(""" 1 or 1 """)(_ should typeErrorAs("expected bool but got int"))
  test(""" "hello!" or true """)(_ should typeErrorAs("expected bool but got string"))

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

  test("or non-boolean") { _ =>
    forAll(nonBooleans) { n =>
      TestData(s"false or ${n.v1}") should runErrorAs(s"expected bool but got ${n.tipe}")
      TestData(s"${n.v1} or true") should runErrorAs(s"expected bool but got ${n.tipe}")
    }
  }

}
