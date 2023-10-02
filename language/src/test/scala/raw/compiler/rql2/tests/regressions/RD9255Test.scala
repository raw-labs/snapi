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

package raw.compiler.rql2.tests.regressions

import org.scalatest.EitherValues
import raw.compiler.rql2.tests.CompilerTestContext
import raw.runtime.{
  ParamBool,
  ParamByte,
  ParamDate,
  ParamDecimal,
  ParamDouble,
  ParamFloat,
  ParamInt,
  ParamInterval,
  ParamLong,
  ParamNull,
  ParamShort,
  ParamString,
  ParamTime,
  ParamTimestamp,
  ParamValue
}

import java.time.{Duration, LocalDate, LocalDateTime, LocalTime}

trait RD9255Test extends CompilerTestContext with EitherValues {

  private val declarations = """
    |string_func(x: string) = String.Length(x)
    |byte_func(x: byte) = x + 1
    |short_func(x: short) = x + 1
    |int_func(x: int) = x + 1
    |long_func(x: long) = x + 1
    |float_func(x: float) = x + 1f
    |double_func(x: double) = x + 1.0
    |decimal_func(x: decimal) = x + Decimal.From("1.0")
    |bool_func(x: bool) = not x
    |date_func(x: date) = Date.Year(x)
    |time_func(x: time) = Time.Minute(x)
    |timestamp_func(x: timestamp) = Timestamp.Minute(x)
    |interval_func(x: interval) = Interval.Minutes(x)
    |three_param_func(x: string, y: int, z: string = "!") = x + String.From(y) + z
    |""".stripMargin

  private def exec(f: String, value: Any, t: String) = {
    val x = value match {
      case null => ParamNull()
      case v: String => ParamString(v)
      case v: Boolean => ParamBool(v)
      case v: Byte => ParamByte(v)
      case v: Short => ParamShort(v)
      case v: Int => ParamInt(v)
      case v: Long => ParamLong(v)
      case v: Float => ParamFloat(v)
      case v: Double => ParamDouble(v)
      case v: java.math.BigDecimal => ParamDecimal(v)
      case v: LocalDate => ParamDate(v)
      case v: LocalTime => ParamTime(v)
      case v: LocalDateTime => ParamTimestamp(v)
      case v: Duration => ParamInterval(v)
    }
    callDecl(declarations, f, Array(("x", x)), t)
  }

  private def evalTo(q: String) = be(tryExecuteQuery(q).value._1) compose { e: Either[String, Any] => e.value }

  test("null param")(_ => exec("string_func", null, "string") should evalTo("String.Length(null)"))
  test("byte param")(_ => exec("byte_func", 10.toByte, "byte") should evalTo("Byte.From(11)"))
  test("short param")(_ => exec("short_func", 10.toShort, "short") should evalTo("Short.From(11)"))
  test("int param")(_ => exec("int_func", 10, "int") should evalTo("11"))
  test("long param")(_ => exec("long_func", 10.toLong, "long") should evalTo("Long.From(11)"))
  test("float param")(_ => exec("float_func", 3.14.toFloat, "float") should evalTo("Float.From(3.14) + 1"))
  test("double param")(_ => exec("double_func", 3.14, "double") should evalTo("3.14 + 1"))
  test("decimal param")(_ =>
    exec("decimal_func", new java.math.BigDecimal("3.14"), "decimal") should evalTo("Decimal.From(\"3.14\") + 1")
  )
  test("bool param") { _ =>
    exec("bool_func", true, "bool") should evalTo("false")
    exec("bool_func", false, "bool") should evalTo("true")
  }
  test("string param")(_ => exec("string_func", "tralala", "long") should evalTo("String.Length(\"tralala\")"))

  test("date param")(_ =>
    exec("date_func", LocalDate.of(2013, 1, 1), "int") should evalTo("Date.Year(Date.Build(2013, 1, 1))")
  )
  test("time param")(_ =>
    exec("time_func", LocalTime.of(13, 14, 25), "int") should evalTo("Time.Minute(Time.Build(13, 14, seconds=25))")
  )
  test("timestamp param")(_ =>
    exec("timestamp_func", LocalDateTime.of(2015, 1, 1, 13, 14, 25), "int")
      should evalTo("Timestamp.Minute(Timestamp.Build(2015, 1, 1, 13, 14, seconds=25))")
  )
  test("interval param")(_ =>
    exec("interval_func", Duration.ofMinutes(10), "int") should evalTo("Interval.Minutes(Interval.Build(minutes=10))")
  )
  test("three parameters")(_ =>
    callDecl(
      declarations,
      "three_param_func",
      Array(("x", ParamString("U")), ("y", ParamInt(2)), ("z", ParamString("?"))),
      "string"
    ) should evalTo("\"U2?\"")
  )
  test("two parameters")(_ =>
    callDecl(
      declarations,
      "three_param_func",
      Array(("x", ParamString("U")), ("y", ParamInt(2))),
      "string"
    ) should evalTo(
      "\"U2!\""
    )
  )

  // type errors
  test("expected byte but got string")(_ =>
    exec("byte_func", "tralala", "byte").left.value should include("expected byte but got string")
  )
  test("missing mandatory arguments") { _ =>
    assume(language == "rql2-truffle")
    callDecl(declarations, "three_param_func", Array(("x", ParamString("U"))), "string").left.value should include(
      "missing mandatory arguments"
    )
  }

  // Executes a parameterized query, running 'decl' with the given parameters.
  def callDecl(
      code: String,
      decl: String,
      args: Array[(String, ParamValue)] = Array.empty,
      t: String
  ): Either[String, Any] = {
    doExecute(code, maybeDecl = Some(decl), maybeArgs = Some(args)).right.map(path => outputParser(path, t))
  }

}
