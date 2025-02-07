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

package com.rawlabs.compiler

import com.rawlabs.protocol.raw.{
  AnyType,
  AttrType,
  BinaryType,
  BoolType,
  ByteType,
  DateType,
  DecimalType,
  DoubleType,
  FloatType,
  IntType,
  IntervalType,
  IterableType,
  ListType,
  LongType,
  OrType,
  RecordType,
  ShortType,
  StringType,
  TimeType,
  TimestampType,
  Type,
  UndefinedType
}

import scala.collection.JavaConverters._

object TypeConverter {

  /**
   * Converts a raw compiler API type to a gRPC protocol type.
   *
   * @param t the raw compiler API type
   * @return the gRPC protocol type
   */
  def toProtocolType(t: com.rawlabs.compiler.RawType): Type = {
    t match {
      case com.rawlabs.compiler.RawAnyType() => Type.newBuilder().setAny(AnyType.newBuilder().build()).build()
      case com.rawlabs.compiler.RawUndefinedType(nullable, triable) => Type
          .newBuilder()
          .setUndefined(UndefinedType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawByteType(nullable, triable) => Type
          .newBuilder()
          .setByte(ByteType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawShortType(nullable, triable) => Type
          .newBuilder()
          .setShort(ShortType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawIntType(nullable, triable) => Type
          .newBuilder()
          .setInt(IntType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawLongType(nullable, triable) => Type
          .newBuilder()
          .setLong(LongType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawFloatType(nullable, triable) => Type
          .newBuilder()
          .setFloat(FloatType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawDoubleType(nullable, triable) => Type
          .newBuilder()
          .setDouble(DoubleType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawDecimalType(nullable, triable) => Type
          .newBuilder()
          .setDecimal(DecimalType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawBoolType(nullable, triable) => Type
          .newBuilder()
          .setBool(BoolType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawStringType(nullable, triable) => Type
          .newBuilder()
          .setString(StringType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawBinaryType(nullable, triable) => Type
          .newBuilder()
          .setBinary(BinaryType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawDateType(nullable, triable) => Type
          .newBuilder()
          .setDate(DateType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawTimeType(nullable, triable) => Type
          .newBuilder()
          .setTime(TimeType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawTimestampType(nullable, triable) => Type
          .newBuilder()
          .setTimestamp(TimestampType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawIntervalType(nullable, triable) => Type
          .newBuilder()
          .setInterval(IntervalType.newBuilder().setNullable(nullable).setTriable(triable).build())
          .build()
      case com.rawlabs.compiler.RawRecordType(atts, nullable, triable) => Type
          .newBuilder()
          .setRecord(
            RecordType
              .newBuilder()
              .addAllAtts(
                atts.map(f => AttrType.newBuilder().setIdn(f.idn).setTipe(toProtocolType(f.tipe)).build()).asJava
              )
              .setNullable(nullable)
              .setTriable(triable)
              .build()
          )
          .build()
      case com.rawlabs.compiler.RawListType(t, nullable, triable) => Type
          .newBuilder()
          .setList(
            ListType.newBuilder().setInnerType(toProtocolType(t)).setNullable(nullable).setTriable(triable).build()
          )
          .build()
      case com.rawlabs.compiler.RawIterableType(t, nullable, triable) => Type
          .newBuilder()
          .setIterable(
            IterableType.newBuilder().setInnerType(toProtocolType(t)).setNullable(nullable).setTriable(triable).build()
          )
          .build()
      case com.rawlabs.compiler.RawOrType(ors, nullable, triable) => Type
          .newBuilder()
          .setOr(
            OrType
              .newBuilder()
              .addAllOrs(ors.map(toProtocolType).asJava)
              .setNullable(nullable)
              .setTriable(triable)
              .build()
          )
          .build()
      case t => throw new AssertionError(s"support for $t not implemented yet")
    }
  }

}
