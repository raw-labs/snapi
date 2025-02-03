/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.compiler.tests.output

import com.rawlabs.protocol.raw.{
  Value,
  ValueBinary,
  ValueBool,
  ValueByte,
  ValueDate,
  ValueDecimal,
  ValueDouble,
  ValueError,
  ValueFloat,
  ValueInt,
  ValueList,
  ValueLong,
  ValueNull,
  ValueRecord,
  ValueRecordField,
  ValueShort,
  ValueString,
  ValueTime,
  ValueTimestamp
}
import com.rawlabs.snapi.compiler.tests.SnapiTestContext
import com.rawlabs.snapi.frontend.snapi.SnapiInterpolator
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext

import java.io.File

// FIXME (msb): This test should remain in SnapiCompiler while all other tests should become Truffle "value" tests.
class EvalTest extends SnapiTestContext with LocalLocationsTestContext {

  //////////////////////////
  // Basic tests
  //////////////////////////

  // Test single value
  test(snapi"""42""")(it => it should evalSingle(Value.newBuilder().setInt(ValueInt.newBuilder().setV(42)).build()))

  // Test iterator
  test(snapi"""Collection.Build(1,2,3)""") { it =>
    it should evalIterator(
      Value.newBuilder().setInt(ValueInt.newBuilder().setV(1)).build(),
      Value.newBuilder().setInt(ValueInt.newBuilder().setV(2)).build(),
      Value.newBuilder().setInt(ValueInt.newBuilder().setV(3)).build()
    )
  }

  // Test list (vs iterator)
  test(snapi"""[1,2,3]""") { it =>
    it should evalSingle(
      Value
        .newBuilder()
        .setList(
          ValueList
            .newBuilder()
            .addValues(Value.newBuilder().setInt(ValueInt.newBuilder().setV(1)).build())
            .addValues(Value.newBuilder().setInt(ValueInt.newBuilder().setV(2)).build())
            .addValues(Value.newBuilder().setInt(ValueInt.newBuilder().setV(3)).build())
            .build()
        )
        .build()
    )
  }

  // Test validation failure
  test(snapi"""x + 1""")(it => it should evalTypeErrorAs("x is not declared"))

  // Test runtime failure
  test(snapi"""Regex.Groups("aaa", "(\\d+)") """)(it =>
    it should evalRunErrorAs("string 'aaa' does not match pattern '(\\d+)'")
  )

  // Test failure during iteration
  test("""Collection.Build(
    |  1,
    |  2,
    |  Error.Build("foo")
    |)""".stripMargin) { it =>
    it should evalIterator(
      Value.newBuilder().setInt(ValueInt.newBuilder().setV(1)).build(),
      Value.newBuilder().setInt(ValueInt.newBuilder().setV(2)).build(),
      Value.newBuilder().setError(ValueError.newBuilder().setMessage("foo")).build()
    )
  }

  // Record (w/ duplicated fields remain)
  test("""{a: 1, b: 2, a: 3, c: 4, a: 5}""") { it =>
    it should evalSingle(
      Value
        .newBuilder()
        .setRecord(
          ValueRecord
            .newBuilder()
            .addFields(
              ValueRecordField
                .newBuilder()
                .setName("a")
                .setValue(Value.newBuilder().setInt(ValueInt.newBuilder().setV(1)).build())
            )
            .addFields(
              ValueRecordField
                .newBuilder()
                .setName("b")
                .setValue(Value.newBuilder().setInt(ValueInt.newBuilder().setV(2)).build())
            )
            .addFields(
              ValueRecordField
                .newBuilder()
                .setName("a")
                .setValue(Value.newBuilder().setInt(ValueInt.newBuilder().setV(3)).build())
            )
            .addFields(
              ValueRecordField
                .newBuilder()
                .setName("c")
                .setValue(Value.newBuilder().setInt(ValueInt.newBuilder().setV(4)).build())
            )
            .addFields(
              ValueRecordField
                .newBuilder()
                .setName("a")
                .setValue(Value.newBuilder().setInt(ValueInt.newBuilder().setV(5)).build())
            )
        )
        .build()
    )
  }

  //////////////////////////
  // Binary data
  //////////////////////////

  test("""Binary.FromString("Hello World")""") { it =>
    val expected = "Hello World".getBytes("UTF-8")
    it should evalSingle(
      Value
        .newBuilder()
        .setBinary(ValueBinary.newBuilder().setV(com.google.protobuf.ByteString.copyFrom(expected)))
        .build()
    )
  }

  test(s"""Binary.Read("$peopleExcel")""") { it =>
    val expected = new File(peopleExcel.drop("file:".length))
    it should evalSingle(
      Value
        .newBuilder()
        .setBinary(
          ValueBinary.newBuilder().setV(com.google.protobuf.ByteString.readFrom(new java.io.FileInputStream(expected)))
        )
        .build()
    )
  }

  // A nullable result is handled, and null results to an empty file.
  test(s"""Binary.Read(if (1 == 0) then "$peopleExcel" else null)""") { it =>
    it should evalSingle(Value.newBuilder().setNull(ValueNull.newBuilder().build()).build())
  }

  // An error fails the execution.
  test(s"""Binary.Read("file:/not/found")""")(it =>
    it should evalRunErrorAs("file system error: path not found: /not/found")
  )

  //////////////////////////
  // Text data
  //////////////////////////

  test(""" "Hello World" """) { it =>
    val expected = "Hello World"
    it should evalSingle(Value.newBuilder().setString(ValueString.newBuilder().setV(expected)).build())
  }

  test(""" if true then "Hello World" else null""") { it =>
    val expected = "Hello World"
    it should evalSingle(Value.newBuilder().setString(ValueString.newBuilder().setV(expected)).build())
  }

  test(""" if false then "Hello World" else null""") { it =>
    it should evalSingle(Value.newBuilder().setNull(ValueNull.newBuilder()).build())
  }

  //////////////////////////
  // Remaining types
  //////////////////////////

  // format: off
  test("""Collection.Build(
         |  {
         |    byteCol: Byte.From("1"),
         |    shortCol: Short.From("10"),
         |    intCol: Int.From("100"),
         |    longCol: Long.From("1000"),
         |    floatCol: Float.From("3.14"),
         |    doubleCol: Double.From("6.28"),
         |    decimalCol: Decimal.From("9.42"),
         |    boolCol: true,
         |    dateCol: Date.Parse("12/25/2023", "M/d/yyyy"),
         |    timeCol: Time.Parse("01:02:03", "H:m:s"),
         |    timestampCol: Timestamp.Parse("12/25/2023 01:02:03", "M/d/yyyy H:m:s"),
         |    binaryCol: Binary.FromString("Hello World!"),
         |    stringCol: "Hello,World!"
         |  },
         |  {
         |    byteCol: Byte.From("120"),
         |    shortCol: Short.From("2500"),
         |    intCol: Int.From("25000"),
         |    longCol: Long.From("9223372036854775807"),
         |    floatCol: Float.From("30.14"),
         |    doubleCol: Double.From("60.28"),
         |    decimalCol: Decimal.From("90.42"),
         |    boolCol: false,
         |    dateCol: Date.Parse("2/5/2023", "M/d/yyyy"),
         |    timeCol: Time.Parse("11:12:13", "H:m:s"),
         |    timestampCol: Timestamp.Parse("2/5/2023 11:12:13", "M/d/yyyy H:m:s"),
         |    binaryCol: Binary.FromString("Olala!"),
         |    stringCol: "Ciao World!"
         |  }
         |)""".stripMargin) { it =>
    it should evalIterator(
      Value.newBuilder()
        .setRecord(ValueRecord.newBuilder()
          .addFields(ValueRecordField.newBuilder().setName("byteCol").setValue(Value.newBuilder().setByte(ValueByte.newBuilder().setV(1)).build()))
          .addFields(ValueRecordField.newBuilder().setName("shortCol").setValue(Value.newBuilder().setShort(ValueShort.newBuilder().setV(10)).build()))
          .addFields(ValueRecordField.newBuilder().setName("intCol").setValue(Value.newBuilder().setInt(ValueInt.newBuilder().setV(100)).build()))
          .addFields(ValueRecordField.newBuilder().setName("longCol").setValue(Value.newBuilder().setLong(ValueLong.newBuilder().setV(1000)).build()))
          .addFields(ValueRecordField.newBuilder().setName("floatCol").setValue(Value.newBuilder().setFloat(ValueFloat.newBuilder().setV(3.14f)).build()))
          .addFields(ValueRecordField.newBuilder().setName("doubleCol").setValue(Value.newBuilder().setDouble(ValueDouble.newBuilder().setV(6.28)).build()))
          .addFields(ValueRecordField.newBuilder().setName("decimalCol").setValue(Value.newBuilder().setDecimal(ValueDecimal.newBuilder().setV("9.42")).build()))
          .addFields(ValueRecordField.newBuilder().setName("boolCol").setValue(Value.newBuilder().setBool(ValueBool.newBuilder().setV(true)).build()))
          .addFields(ValueRecordField.newBuilder().setName("dateCol").setValue(Value.newBuilder().setDate(ValueDate.newBuilder().setYear(2023).setMonth(12).setDay(25)).build()))
          .addFields(ValueRecordField.newBuilder().setName("timeCol").setValue(Value.newBuilder().setTime(ValueTime.newBuilder().setHour(1).setMinute(2).setSecond(3)).build()))
          .addFields(ValueRecordField.newBuilder().setName("timestampCol").setValue(Value.newBuilder().setTimestamp(ValueTimestamp.newBuilder().setYear(2023).setMonth(12).setDay(25).setHour(1).setMinute(2).setSecond(3)).build()))
          .addFields(ValueRecordField.newBuilder().setName("binaryCol").setValue(Value.newBuilder().setBinary(ValueBinary.newBuilder().setV(com.google.protobuf.ByteString.copyFrom("Hello World!".getBytes("UTF-8")))).build()))
          .addFields(ValueRecordField.newBuilder().setName("stringCol").setValue(Value.newBuilder().setString(ValueString.newBuilder().setV("Hello,World!")).build()))
        ).build(),
      Value.newBuilder()
        .setRecord(ValueRecord.newBuilder()
          .addFields(ValueRecordField.newBuilder().setName("byteCol").setValue(Value.newBuilder().setByte(ValueByte.newBuilder().setV(120)).build()))
          .addFields(ValueRecordField.newBuilder().setName("shortCol").setValue(Value.newBuilder().setShort(ValueShort.newBuilder().setV(2500)).build()))
          .addFields(ValueRecordField.newBuilder().setName("intCol").setValue(Value.newBuilder().setInt(ValueInt.newBuilder().setV(25000)).build()))
          .addFields(ValueRecordField.newBuilder().setName("longCol").setValue(Value.newBuilder().setLong(ValueLong.newBuilder().setV(9223372036854775807L)).build()))
          .addFields(ValueRecordField.newBuilder().setName("floatCol").setValue(Value.newBuilder().setFloat(ValueFloat.newBuilder().setV(30.14f)).build()))
          .addFields(ValueRecordField.newBuilder().setName("doubleCol").setValue(Value.newBuilder().setDouble(ValueDouble.newBuilder().setV(60.28)).build()))
          .addFields(ValueRecordField.newBuilder().setName("decimalCol").setValue(Value.newBuilder().setDecimal(ValueDecimal.newBuilder().setV("90.42")).build()))
          .addFields(ValueRecordField.newBuilder().setName("boolCol").setValue(Value.newBuilder().setBool(ValueBool.newBuilder().setV(false)).build()))
          .addFields(ValueRecordField.newBuilder().setName("dateCol").setValue(Value.newBuilder().setDate(ValueDate.newBuilder().setYear(2023).setMonth(2).setDay(5)).build()))
          .addFields(ValueRecordField.newBuilder().setName("timeCol").setValue(Value.newBuilder().setTime(ValueTime.newBuilder().setHour(11).setMinute(12).setSecond(13)).build()))
          .addFields(ValueRecordField.newBuilder().setName("timestampCol").setValue(Value.newBuilder().setTimestamp(ValueTimestamp.newBuilder().setYear(2023).setMonth(2).setDay(5).setHour(11).setMinute(12).setSecond(13)).build()))
          .addFields(ValueRecordField.newBuilder().setName("binaryCol").setValue(Value.newBuilder().setBinary(ValueBinary.newBuilder().setV(com.google.protobuf.ByteString.copyFrom("Olala!".getBytes("UTF-8")))).build()))
          .addFields(ValueRecordField.newBuilder().setName("stringCol").setValue(Value.newBuilder().setString(ValueString.newBuilder().setV("Ciao World!")).build()))
        ).build()
    )
  }
  // format: on

}
