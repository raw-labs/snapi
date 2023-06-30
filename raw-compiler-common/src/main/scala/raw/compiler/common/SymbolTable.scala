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

package raw.compiler.common

import org.bitbucket.inkytonik.kiama.util.Entity
import raw.compiler.base
import raw.compiler.base.source.Type
import raw.compiler.common.source._

/** Entity for a program parameter. */
final class ProgramParamEntity(val p: SourceProgramParam) extends Entity

/**
 *  Entity for a bound identifier.
 *  TODO (msb): Move out of common onto L4 + core.
 */
final class BindEntity(val b: Bind) extends Entity

/**
 * Entity for a compiler intrinsic.
 * TODO (msb): Move out of common onto L4 + core.
 */
final class IntrinsicEntity(val i: Intrinsic, val t: Type) extends Entity

sealed trait IntrinsicType
final case class IntrinsicVal(t: Type) extends IntrinsicType
final case class IntrinsicFun(ins: Vector[Type], out: Type) extends IntrinsicType

object IntrinsicFun {
  def apply(in: Type, out: Type): IntrinsicFun = IntrinsicFun(Vector(in), out)
}

object IntrinsicTypes {

  val intrinsicsTypes: Map[Intrinsic, IntrinsicType] = Map(
    DateNow() -> IntrinsicVal(DateType()),
    DateYear() -> IntrinsicFun(Vector(DateType()), IntType()),
    DateMonth() -> IntrinsicFun(Vector(DateType()), IntType()),
    DateDay() -> IntrinsicFun(Vector(DateType()), IntType()),
    Trim() -> IntrinsicFun(StringType(), StringType()),
    LTrim() -> IntrinsicFun(StringType(), StringType()),
    RTrim() -> IntrinsicFun(StringType(), StringType()),
    ReplaceStr() -> IntrinsicFun(Vector(StringType(), StringType(), StringType()), StringType()),
    ReplaceRegex() -> IntrinsicFun(Vector(StringType(), RegexType(0), StringType()), StringType()),
    Upper() -> IntrinsicFun(StringType(), StringType()),
    Lower() -> IntrinsicFun(StringType(), StringType()),
    Split() -> IntrinsicFun(Vector(StringType(), StringType()), ListType(StringType())),
    Reverse() -> IntrinsicFun(StringType(), StringType()),
    Replicate() -> IntrinsicFun(Vector(StringType(), IntType()), StringType()),
    StartsWith() -> IntrinsicFun(Vector(StringType(), StringType()), BoolType()),
    StrEmpty() -> IntrinsicFun(StringType(), BoolType()),
    LongToDate() -> IntrinsicFun(Vector(LongType()), DateType()),
    StringToDate() -> IntrinsicFun(Vector(StringType(), StringType()), DateType()),
    ToTime() -> IntrinsicFun(Vector(StringType(), StringType()), TimeType()),
    ToTimestamp() -> IntrinsicFun(Vector(StringType(), StringType()), TimestampType()),
    UnixTimestamp() -> IntrinsicFun(Vector(TimestampType()), LongType()),
    FromUnixTime() -> IntrinsicFun(Vector(LongType()), TimestampType()),
    ToInterval() -> IntrinsicFun(Vector(StringType()), IntervalType()),
    TryToDate() -> IntrinsicFun(Vector(StringType(), StringType()), OptionType(DateType())),
    TryToTime() -> IntrinsicFun(Vector(StringType(), StringType()), OptionType(TimeType())),
    TryToTimestamp() -> IntrinsicFun(Vector(StringType(), StringType()), OptionType(TimestampType())),
    Substr() -> IntrinsicFun(Vector(StringType(), IntType(), IntType()), StringType()),
    Len() -> IntrinsicFun(StringType(), IntType()),
    Abs() -> IntrinsicFun(DoubleType(), DoubleType()),
    Acos() -> IntrinsicFun(DoubleType(), DoubleType()),
    Asin() -> IntrinsicFun(DoubleType(), DoubleType()),
    Atan() -> IntrinsicFun(DoubleType(), DoubleType()),
    Atn2() -> IntrinsicFun(Vector(DoubleType(), DoubleType()), DoubleType()),
    Ceiling() -> IntrinsicFun(DecimalType(), LongType()),
    Cos() -> IntrinsicFun(DoubleType(), DoubleType()),
    Cot() -> IntrinsicFun(DoubleType(), DoubleType()),
    Degrees() -> IntrinsicFun(DoubleType(), DoubleType()),
    Exponential() -> IntrinsicFun(DoubleType(), DoubleType()),
    Floor() -> IntrinsicFun(DecimalType(), LongType()),
    Log() -> IntrinsicFun(DoubleType(), DoubleType()),
    Log10() -> IntrinsicFun(DoubleType(), DoubleType()),
    Pi() -> IntrinsicFun(Vector.empty, DoubleType()),
    Power() -> IntrinsicFun(Vector(DoubleType(), DoubleType()), DoubleType()),
    Radians() -> IntrinsicFun(DoubleType(), DoubleType()),
    // TODO (ctm): Add random with a seed
    Rand() -> IntrinsicFun(Vector.empty, DoubleType()),
    Round() -> IntrinsicFun(Vector(DecimalType(), IntType()), DecimalType()),
    Sign() -> IntrinsicFun(DoubleType(), IntType()),
    Sin() -> IntrinsicFun(DoubleType(), DoubleType()),
    Sqrt() -> IntrinsicFun(DoubleType(), DoubleType()),
    Square() -> IntrinsicFun(DoubleType(), DoubleType()),
    Tan() -> IntrinsicFun(DoubleType(), DoubleType()),
    THour() -> IntrinsicFun(TimeType(), IntType()),
    TMinute() -> IntrinsicFun(TimeType(), IntType()),
    TSecond() -> IntrinsicFun(TimeType(), IntType()),
    TMillis() -> IntrinsicFun(TimeType(), IntType()),
    TsYear() -> IntrinsicFun(TimestampType(), IntType()),
    TsMonth() -> IntrinsicFun(TimestampType(), IntType()),
    TsDay() -> IntrinsicFun(TimestampType(), IntType()),
    TsHour() -> IntrinsicFun(TimestampType(), IntType()),
    TsMinute() -> IntrinsicFun(TimestampType(), IntType()),
    TsSecond() -> IntrinsicFun(TimestampType(), IntType()),
    TsMillis() -> IntrinsicFun(TimestampType(), IntType()),
    MillisToI() -> IntrinsicFun(LongType(), IntervalType()),
    IToMillis() -> IntrinsicFun(IntervalType(), LongType()),
    CurrentTimestamp() -> IntrinsicFun(Vector(), TimestampType()),
    DateTrunc() -> IntrinsicFun(Vector(StringType(), TimestampType()), TimestampType()),
    TestAccess() -> IntrinsicFun(StringType(), BoolType()),
    CountSubstr() -> IntrinsicFun(Vector(StringType(), StringType()), IntType()),
    LevenshteinDistance() -> IntrinsicFun(Vector(StringType(), StringType()), IntType()),
    ReadIntProperty() -> IntrinsicFun(Vector(StringType()), IntType()),
    ReadBoolProperty() -> IntrinsicFun(Vector(StringType()), BoolType()),
    ReadIntervalProperty() -> IntrinsicFun(Vector(StringType()), IntervalType()),
    TryReadIntervalProperty() -> IntrinsicFun(Vector(StringType()), OptionType(IntervalType())),
    DecodeString() -> IntrinsicFun(Vector(BinaryType(), StringType()), StringType()),
    EncodeString() -> IntrinsicFun(Vector(StringType(), StringType()), BinaryType()),
    TimeBucket() -> IntrinsicFun(Vector(IntervalType(), TimestampType()), TimestampType()),
    HttpRequest() -> IntrinsicFun(
      Vector(
        StringType(), // url
        StringType(), // method
        OptionType(StringType()), // http-body-string
        OptionType(BinaryType()), // http-body-binary
        OptionType(StringType()), // http-token
        OptionType(StringType()), // http-auth-cred-name
        OptionType(StringType()), // http-client-id
        OptionType(StringType()), // http-client-secret
        OptionType(StringType()), // http-auth-provider
        OptionType(StringType()), // http-token-url
        OptionType(BoolType()), // http-use-basic-auth
        OptionType(StringType()), // http-username
        OptionType(StringType()), // http-password
        ListType(TupleType(StringType(), StringType())), // url args
        ListType(TupleType(StringType(), StringType())), // headers
        OptionType(ListType(IntType())) // expected http status
      ),
      RecordType(
        Vector(
          AttrType("status", IntType()),
          AttrType("data", BinaryType()),
          AttrType("headers", ListType(TupleType(StringType(), StringType())))
        )
      )
    ),
    DescribeInternal() -> IntrinsicFun(
      Vector(
        StringType(), // url
        OptionType(IntType()), // sample_size
        StringType(), // method
        OptionType(StringType()), // http-body-string
        OptionType(BinaryType()), // http-body-binary
        OptionType(StringType()), // http-token
        OptionType(StringType()), // http-oauth-token-name
        OptionType(StringType()), // http-client-id
        OptionType(StringType()), // http-client-secret
        OptionType(StringType()), // http-auth-provider
        OptionType(StringType()), // http-token-url
        OptionType(BoolType()), // http-use-basic-auth
        OptionType(StringType()), // http-username
        OptionType(StringType()), // http-password
        ListType(TupleType(StringType(), StringType())), // url args
        ListType(TupleType(StringType(), StringType())) // headers
      ),
      RecordType(
        Vector(
          AttrType("format", StringType()),
          AttrType("comment", StringType()),
          AttrType("type", StringType()),
          AttrType(
            "properties",
            ListType(
              RecordType(
                Vector(
                  AttrType("name", StringType()),
                  AttrType("value", OptionType(StringType()))
                )
              )
            )
          ),
          AttrType("is_collection", BoolType()),
          AttrType(
            "columns",
            ListType(
              RecordType(
                Vector(
                  AttrType("col_name", OptionType(StringType())),
                  AttrType("col_type", StringType()),
                  AttrType("nullable", BoolType())
                )
              )
            )
          ),
          AttrType("sampled", BoolType())
        )
      )
    ),
    Secret() -> IntrinsicFun(Vector(StringType()), StringType()),
    Base64Encode() -> IntrinsicFun(Vector(StringType()), StringType()),
    ReadByteParam() -> IntrinsicFun(Vector(StringType()), ByteType()),
    ReadShortParam() -> IntrinsicFun(Vector(StringType()), ShortType()),
    ReadIntParam() -> IntrinsicFun(Vector(StringType()), IntType()),
    ReadLongParam() -> IntrinsicFun(Vector(StringType()), LongType()),
    ReadFloatParam() -> IntrinsicFun(Vector(StringType()), FloatType()),
    ReadDoubleParam() -> IntrinsicFun(Vector(StringType()), DoubleType()),
    ReadDecimalParam() -> IntrinsicFun(Vector(StringType()), DecimalType()),
    ReadBoolParam() -> IntrinsicFun(Vector(StringType()), BoolType()),
    ReadStringParam() -> IntrinsicFun(Vector(StringType()), StringType()),
    ReadDateParam() -> IntrinsicFun(Vector(StringType()), DateType()),
    ReadTimeParam() -> IntrinsicFun(Vector(StringType()), TimeType()),
    ReadTimestampParam() -> IntrinsicFun(Vector(StringType()), TimestampType()),
    ReadIntervalParam() -> IntrinsicFun(Vector(StringType()), IntervalType()),
    Scopes() -> IntrinsicVal(ListType(StringType()))
  )

}

trait SymbolTable extends base.SymbolTable {

  /**
   * Built-in compiler intrinsics.
   *
   * Intrinsics keys should be defined in lowercase. TODO: Assert this, or find a better mechanism.
   */
  val intrinsicEntities: Map[String, IntrinsicEntity] = Map.empty

}
