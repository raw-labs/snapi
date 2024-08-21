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
import raw.compiler.rql2.source.{Rql2BoolType, Rql2IntType, Rql2StringType}
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class ConstTest extends Rql2TruffleCompilerTestContext with TableDrivenPropertyChecks {

  test("1") { it =>
    it should typeAs("int")
    it should astTypeAs(Rql2IntType())
    it should evaluateTo("1")
  }

  test(""" "Hello" """) { it =>
    it should typeAs("string")
    it should astTypeAs(Rql2StringType())
    it should evaluateTo(""""Hello"""")
  }

  test("\"x\\u2192x+1\" // RD-10265") { it =>
    // The source code contains the escaped unicode character. It should parse and run as if the character
    // was in the source code.
    it should parse
    it should typeAs("string")
    it should astTypeAs(Rql2StringType())
    it should run
    it should evaluateTo(""""x\u2192x+1"""")
  }

  test("\"x\u2192x+1\" // RD-10265") { it =>
    // The source code contains the unicode character. It should parse and run.
    it should parse
    it should typeAs("string")
    it should astTypeAs(Rql2StringType())
    it should run
    it should evaluateTo(""""x\u2192x+1"""")
  }

  test("\"\"\"x\u2192x+1\"\"\"// RD-10265") { it =>
    // The source code is using triple quoted string, it can contain the unicode character
    it should parse
    it should typeAs("string")
    it should astTypeAs(Rql2StringType())
    it should run
    it should evaluateTo(""""x\u2192x+1"""")
  }

  test("""  true """) { it =>
    it should typeAs("bool")
    it should astTypeAs(Rql2BoolType())
    it should evaluateTo("""true""")
  }

  private val consts = Table(
    "constants",
    TestValue("byte", "1b"),
    TestValue("short", "Short.From(1)"),
    TestValue("int", "1"),
    TestValue("long", "1l"),
    TestValue("float", "1f"),
    TestValue("double", "1d"),
    TestValue("decimal", "Decimal.From(1)"),
    TestValue("record(a: int)", "{a: 1}"),
    TestValue("collection(int)", "Collection.Build(1, 2, 3)"),
    TestValue("list(int)", "[1, 2, 3]"),
    TestValue("date", """Date.Build(2023, 2, 6)"""),
    TestValue("time", """Time.Build(10, 0)"""),
    TestValue("timestamp", """Timestamp.Build(2023, 2, 6, 10, 0)"""),
    TestValue("interval", """Interval.Build(months=1, days=2, hours=3)"""),
    TestValue("binary", """Binary.FromString("Hello World") """),
    TestValue("bool", "true")
  ).flatMap(tv =>
    Seq(
      tv, // plain
      tv.copy(v1 = s"let v = if true then ${tv.v1} else null in v"), // nullable
      tv.copy(v1 = s"""let v = if true then ${tv.v1} else Error.Build("argh!") in v"""), // tryable
      tv.copy(v1 = s"let v: ${tv.tipe} = ${tv.v1} in v")
    )
  ) // nullable and tryable

  test("consts several types")(_ => forAll(consts)(x => TestData(x.v1) should (evaluateTo(x.v1) and typeAs(x.tipe))))

}
