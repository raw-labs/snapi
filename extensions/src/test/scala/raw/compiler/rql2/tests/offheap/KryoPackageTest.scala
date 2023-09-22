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

package raw.compiler.rql2.tests.offheap

import org.scalatest.prop.TableDrivenPropertyChecks
import raw.compiler.rql2.tests.TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests
class KryoPackageTest extends TruffleCompilerTestContext with TableDrivenPropertyChecks {

  private val cases = Table(
    ("value", "type"),
    ("22b", "byte"),
    ("22s", "short"),
    ("22", "int"),
    ("22L", "long"),
    ("22.13f", "float"),
    ("22.14", "double"),
    ("22.12345q", "decimal"),
    (""""et voila"""", "string"),
    ("false", "bool"),
    ("Date.Build(2012, 1, 1)", "date"),
    ("Time.Build(15, 14, seconds=13, millis=123)", "time"),
    ("Timestamp.Build(2012, 1, 1, 15, 14, seconds=13, millis=123)", "timestamp"),
    ("Interval.Build(years  = 3, months = 6, days = 5)", "interval"),
    ("""{a: 1, b: 2.12f}""", "record(a: int, b: float)"),
    ("[1, 2, 3]", "list(int)")
  )

  test("all") { _ =>
    forAll(cases) { (value, tpe) =>
      raw.TestData(s"""let n: $tpe = $value in Kryo.Decode(Kryo.Encode(n), type $tpe)""") should evaluateTo(value)
    }
  }

  test("let n: undefined = null in Kryo.Decode(Kryo.Encode(n), type undefined)")(_ should evaluateTo("null"))
  test("let n: undefined = Error.Build(\"olala\") in Kryo.Decode(Kryo.Encode(n), type undefined)")(
    _ should runErrorAs("olala")
  )
  test("let n: byte = 14b in Kryo.Decode(Kryo.Encode(n), type byte)")(_ should evaluateTo("14b"))
  test("let n: short = 14s in Kryo.Decode(Kryo.Encode(n), type short)")(_ should evaluateTo("14s"))
  test("let n: int = 14 in Kryo.Decode(Kryo.Encode(n), type int)")(_ should evaluateTo("14"))
  test("let n: long = 14L in Kryo.Decode(Kryo.Encode(n), type long)")(_ should evaluateTo("14L"))
  test("let n: float = 14.12f in Kryo.Decode(Kryo.Encode(n), type float)")(_ should evaluateTo("14.12f"))
  test("let n: double = 14.12 in Kryo.Decode(Kryo.Encode(n), type double)")(_ should evaluateTo("14.12"))
  test("""let n: string = "tralala" in Kryo.Decode(Kryo.Encode(n), type string)""")(
    _ should evaluateTo(""""tralala"""")
  )
  test("let n: bool = true in Kryo.Decode(Kryo.Encode(n), type bool)")(_ should evaluateTo("true"))
  test("let n: date = Date.Build(2012, 01, 01) in Kryo.Decode(Kryo.Encode(n), type date)")(
    _ should evaluateTo("Date.Build(2012, 01, 01)")
  )
  test("let n: time = Time.Build(15, 15, seconds=23, millis=123) in Kryo.Decode(Kryo.Encode(n), type time)")(
    _ should evaluateTo("Time.Build(15, 15, seconds=23, millis=123)")
  )
  test(
    "let n: timestamp = Timestamp.Build(2012, 01, 01, 15, 15, seconds=23, millis=123) in Kryo.Decode(Kryo.Encode(n), type timestamp)"
  )(
    _ should evaluateTo("Timestamp.Build(2012, 01, 01, 15, 15, seconds=23, millis=123)")
  )

}
