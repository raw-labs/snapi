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

package raw.compiler.rql2

import org.graalvm.polyglot.{Context, PolyglotAccess, PolyglotException, Source, Value}
import raw.client.api.{
  CompilerService,
  ErrorMessage,
  ErrorPosition,
  ErrorRange,
  LocationBinarySetting,
  LocationBooleanSetting,
  LocationDescription,
  LocationDurationSetting,
  LocationIntArraySetting,
  LocationIntSetting,
  LocationKVSetting,
  LocationSettingKey,
  LocationSettingValue,
  LocationStringSetting,
  ProgramEnvironment,
  RawAny,
  RawAnyType,
  RawAttrType,
  RawBinary,
  RawBinaryType,
  RawBool,
  RawBoolType,
  RawByte,
  RawByteType,
  RawDate,
  RawDateType,
  RawDecimal,
  RawDecimalType,
  RawDouble,
  RawDoubleType,
  RawFloat,
  RawFloatType,
  RawInt,
  RawIntType,
  RawInterval,
  RawIntervalType,
  RawIterableType,
  RawListType,
  RawLocationType,
  RawLong,
  RawLongType,
  RawNull,
  RawOrType,
  RawRecordType,
  RawShort,
  RawShortType,
  RawString,
  RawStringType,
  RawTime,
  RawTimeType,
  RawTimestamp,
  RawTimestampType,
  RawType,
  RawUndefinedType,
  RawValue
}
import raw.compiler.base.source.Type
import raw.compiler.rql2.antlr4.ParserErrors
import raw.compiler.rql2.api.{
  BinaryRql2Value,
  BoolRql2Value,
  ByteRql2Value,
  DateRql2Value,
  DecimalRql2Value,
  DoubleRql2Value,
  FloatRql2Value,
  IntRql2Value,
  IntervalRql2Value,
  IterableRql2Value,
  ListRql2Value,
  LocationRql2Value,
  LongRql2Value,
  OptionRql2Value,
  OrRql2Value,
  RecordRql2Value,
  Rql2Value,
  ShortRql2Value,
  StringRql2Value,
  TimeRql2Value,
  TimestampRql2Value,
  TryRql2Value
}
import raw.compiler.rql2.source.{
  Rql2AttrType,
  Rql2BinaryType,
  Rql2BoolType,
  Rql2ByteType,
  Rql2DateType,
  Rql2DecimalType,
  Rql2DoubleType,
  Rql2FloatType,
  Rql2IntType,
  Rql2IntervalType,
  Rql2IsNullableTypeProperty,
  Rql2IsTryableTypeProperty,
  Rql2IterableType,
  Rql2ListType,
  Rql2LocationType,
  Rql2LongType,
  Rql2OrType,
  Rql2RecordType,
  Rql2ShortType,
  Rql2StringType,
  Rql2TimeType,
  Rql2TimestampType,
  Rql2TypeProperty,
  Rql2UndefinedType
}
import raw.utils.RawSettings

import scala.collection.mutable
import scala.util.control.NonFatal

sealed trait StagedCompilerResponse
final case class StagedCompilerSuccess(v: Rql2Value) extends StagedCompilerResponse
final case class StagedCompilerValidationFailure(errors: List[ErrorMessage]) extends StagedCompilerResponse
final case class StagedCompilerRuntimeFailure(error: String) extends StagedCompilerResponse

/**
 * Support for stage compilation.
 * This allows an RQL2/Snapi source to be evaluated in a new context, with a given environment and RawSettings.
 */
trait StagedCompiler {

  /**
   * Evaluate the given source code in a new context.
   * The source code is expected to be a valid RQL2/Snapi program.
   * The result is a RawValue.
   *
   * If the Truffle runtime execution thread is interrupted, an InterruptedException is thrown.
   *
   * @param source      The source code to evaluate.
   * @param tipe        The expected type of the result.
   * @param environment The environment to use for the evaluation.
   * @param settings    The settings to use for the evaluation.
   * @return A StagedCompilerResponse.
   */
  def eval(source: String, tipe: Type, environment: ProgramEnvironment)(
      implicit settings: RawSettings
  ): StagedCompilerResponse = {
    val (engine, _) = CompilerService.getEngine()

    // Add environment settings as hardcoded environment variables.
    val ctxBuilder = Context
      .newBuilder("rql")
      .engine(engine)
      .environment("RAW_USER", environment.user.uid.toString)
      .environment("RAW_TRACE_ID", environment.user.uid.toString)
      .environment("RAW_SCOPES", environment.scopes.mkString(","))
      .allowExperimentalOptions(true)
      .allowPolyglotAccess(PolyglotAccess.ALL)
    environment.options.get("staged-compiler").foreach { stagedCompiler =>
      ctxBuilder.option("rql.staged-compiler", stagedCompiler)
    }
    ctxBuilder.option("rql.settings", settings.renderAsString)

    val ctx = ctxBuilder.build()
    ctx.initialize("rql")
    ctx.enter()
    try {
      val truffleSource = Source
        .newBuilder("rql", source, "unnamed")
        .cached(false) // Disable code caching because of the inferrer.
        .build()
      val polyglotValue = ctx.eval(truffleSource)
      val rql2Value = polyglotValueToRql2Value(polyglotValue, tipe)
      StagedCompilerSuccess(rql2Value)
    } catch {
      case ex: PolyglotException =>
        // (msb): The following are various "hacks" to ensure the inner language InterruptException propagates "out".
        // Unfortunately, I do not find a more reliable alternative; the branch that does seem to work is the one
        // that does startsWith. That said, I believe with Truffle, the expectation is that one is supposed to
        // "cancel the context", but in our case this doesn't quite match the current architecture, where we have
        // other non-Truffle languages and also, we have parts of the pipeline that are running outside of Truffle
        // and which must handle interruption as well.
        if (ex.isInterrupted) {
          throw new InterruptedException()
        } else if (ex.getCause.isInstanceOf[InterruptedException]) {
          throw ex.getCause
        } else if (ex.getMessage.startsWith("java.lang.InterruptedException")) {
          throw new InterruptedException()
        } else if (ex.isGuestException) {
          val err = ex.getGuestObject
          if (err != null && err.hasMembers && err.hasMember("errors")) {
            val errorsValue = err.getMember("errors")
            val errors = (0L until errorsValue.getArraySize).map { i =>
              val errorValue = errorsValue.getArrayElement(i)
              val message = errorValue.asString
              val positions = (0L until errorValue.getArraySize).map { j =>
                val posValue = errorValue.getArrayElement(j)
                val beginValue = posValue.getMember("begin")
                val endValue = posValue.getMember("end")
                val begin = ErrorPosition(beginValue.getMember("line").asInt, beginValue.getMember("column").asInt)
                val end = ErrorPosition(endValue.getMember("line").asInt, endValue.getMember("column").asInt)
                ErrorRange(begin, end)
              }
              ErrorMessage(message, positions.to, ParserErrors.ParserErrorCode)
            }
            StagedCompilerValidationFailure(errors.to)
          } else {
            StagedCompilerRuntimeFailure(ex.getMessage)
          }
        } else {
          // Unexpected error. For now we throw the PolyglotException.
          throw ex
        }
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

  private def polyglotValueToRql2Value(v: Value, t: Type): Rql2Value = {
    if (t.triable) {
      if (v.isException) {
        try {
          v.throwException()
          throw new AssertionError("should not happen")
        } catch {
          case NonFatal(ex) => RawError(ex.getMessage)
        }
      } else {
        // Success, recurse without the tryable property.
        polyglotValueToRawValue(v, t.cloneNotTriable)
      }
    } else if (t.nullable) {
      if (v.isNull) {
        RawNull()
      } else {
        polyglotValueToRawValue(v, t.cloneNotNullable)
      }
    } else {
      t match {
        case _: RawUndefinedType => throw new AssertionError("RawUndefined is not triable and is not nullable.")
        case _: RawAnyType => RawAny(v)
        case _: RawBoolType => RawBool(v.asBoolean())
        case _: RawStringType => RawString(v.asString())
        case _: RawByteType => RawByte(v.asByte())
        case _: RawShortType => RawShort(v.asShort())
        case _: RawIntType => RawInt(v.asInt())
        case _: RawLongType => RawLong(v.asLong())
        case _: RawFloatType => RawFloat(v.asFloat())
        case _: RawDoubleType => RawDouble(v.asDouble())
        case _: RawDecimalType =>
          val bg = BigDecimal(v.asString())
          RawDecimal(bg.bigDecimal)
        case _: RawDateType =>
          val date = v.asDate()
          RawDate(date)
        case _: RawTimeType =>
          val time = v.asTime()
          RawTime(time)
        case _: RawTimestampType =>
          val localDate = v.asDate()
          val localTime = v.asTime()
          RawTimestamp(localDate.atTime(localTime))
        case _: RawIntervalType =>
          val d = v.asDuration()
          RawInterval(0, 0, 0, d.toDaysPart.toInt, d.toHoursPart, d.toMinutesPart, d.toSecondsPart, d.toMillisPart)
        case _: RawBinaryType =>
          val bufferSize = v.getBufferSize.toInt
          val byteArray = new Array[Byte](bufferSize)
          for (i <- 0 until bufferSize) {
            byteArray(i) = v.readBufferByte(i)
          }
          RawBinary(byteArray)
        case RawRecordType(atts, _, _) =>
          val vs = atts.map(att => polyglotValueToRawValue(v.getMember(att.idn), att.tipe))
          RawRecord(vs)
        case RawListType(innerType, _, _) =>
          val seq = mutable.ArrayBuffer[RawValue]()
          for (i <- 0L until v.getArraySize) {
            val v1 = v.getArrayElement(i)
            seq.append(polyglotValueToRawValue(v1, innerType))
          }
          RawList(seq)
        case RawIterableType(innerType, _, _) =>
          val seq = mutable.ArrayBuffer[RawValue]()
          val it = v.getIterator
          while (it.hasIteratorNextElement) {
            val v1 = it.getIteratorNextElement
            seq.append(polyglotValueToRawValue(v1, innerType))
          }
          if (it.canInvokeMember("close")) {
            val callable = it.getMember("close")
            callable.execute()
          }
          RawIterable(seq)
        case RawOrType(tipes, _, _) =>
          val idx = v.getMember("index").asInt()
          val v1 = v.getMember("value")
          val tipe = tipes(idx)
          polyglotValueToRawValue(v1, tipe)
        case _: RawLocationType =>
          val url = v.asString
          assert(v.hasMembers);
          val members = v.getMemberKeys
          val settings = mutable.Map.empty[LocationSettingKey, LocationSettingValue]
          val keys = members.iterator()
          while (keys.hasNext) {
            val key = keys.next()
            val tv = v.getMember(key)
            val value =
              if (tv.isNumber) LocationIntSetting(tv.asInt)
              else if (tv.isBoolean) LocationBooleanSetting(tv.asBoolean)
              else if (tv.isString) LocationStringSetting(tv.asString)
              else if (tv.hasBufferElements) {
                val bufferSize = tv.getBufferSize.toInt
                val byteArray = new Array[Byte](bufferSize)
                for (i <- 0 until bufferSize) {
                  byteArray(i) = tv.readBufferByte(i)
                }
                LocationBinarySetting(byteArray)
              } else if (tv.isDuration) LocationDurationSetting(tv.asDuration())
              else if (tv.hasArrayElements) {
                // in the context of a location, it's int-array for sure
                val size = tv.getArraySize
                val array = new Array[Int](size.toInt)
                for (i <- 0L until size) {
                  array(i.toInt) = tv.getArrayElement(i).asInt
                }
                LocationIntArraySetting(array)
              } else if (tv.hasHashEntries) {
                // kv settings
                val iterator = tv.getHashEntriesIterator
                val keyValues = mutable.ArrayBuffer.empty[(String, String)]
                while (iterator.hasIteratorNextElement) {
                  val kv = iterator.getIteratorNextElement // array with two elements: key and value
                  val key = kv.getArrayElement(0).asString
                  val value = kv.getArrayElement(1).asString
                  keyValues += ((key, value))
                }
                LocationKVSetting(keyValues)
              } else {
                throw new AssertionError("Unexpected value type: " + tv)
              }
            settings.put(LocationSettingKey(key), value)
          }
          RawLocation(LocationDescription(url, settings.toMap))
      }
    }
  }

  def rawTypeToRql2Type(t: RawType): Type = {

    def flags(nullable: Boolean, triable: Boolean): Set[Rql2TypeProperty] = {
      val props = mutable.HashSet.empty[Rql2TypeProperty]
      if (nullable) {
        props.add(Rql2IsNullableTypeProperty())
      }
      if (triable) {
        props.add(Rql2IsTryableTypeProperty())
      }
      props.toSet
    }

    t match {
      case RawUndefinedType(nullable, triable) => Rql2UndefinedType(flags(nullable, triable))
      case RawByteType(nullable, triable) => Rql2ByteType(flags(nullable, triable))
      case RawShortType(nullable, triable) => Rql2ShortType(flags(nullable, triable))
      case RawIntType(nullable, triable) => Rql2IntType(flags(nullable, triable))
      case RawLongType(nullable, triable) => Rql2LongType(flags(nullable, triable))
      case RawFloatType(nullable, triable) => Rql2FloatType(flags(nullable, triable))
      case RawDoubleType(nullable, triable) => Rql2DoubleType(flags(nullable, triable))
      case RawDecimalType(nullable, triable) => Rql2DecimalType(flags(nullable, triable))
      case RawBoolType(nullable, triable) => Rql2BoolType(flags(nullable, triable))
      case RawStringType(nullable, triable) => Rql2StringType(flags(nullable, triable))
      case RawBinaryType(nullable, triable) => Rql2BinaryType(flags(nullable, triable))
      case RawLocationType(nullable, triable) => Rql2LocationType(flags(nullable, triable))
      case RawDateType(nullable, triable) => Rql2DateType(flags(nullable, triable))
      case RawTimeType(nullable, triable) => Rql2TimeType(flags(nullable, triable))
      case RawTimestampType(nullable, triable) => Rql2TimestampType(flags(nullable, triable))
      case RawIntervalType(nullable, triable) => Rql2IntervalType(flags(nullable, triable))
      case RawRecordType(atts, nullable, triable) => Rql2RecordType(
          atts.map { case RawAttrType(idn, t) => Rql2AttrType(idn, rawTypeToRql2Type(t)) },
          flags(nullable, triable)
        )
      case RawListType(innerType, nullable, triable) =>
        Rql2ListType(rawTypeToRql2Type(innerType), flags(nullable, triable))
      case RawIterableType(innerType, nullable, triable) =>
        Rql2IterableType(rawTypeToRql2Type(innerType), flags(nullable, triable))
      case RawOrType(ors, nullable, triable) => Rql2OrType(ors.map(rawTypeToRql2Type).to, flags(nullable, triable))
    }
  }

  def rawValueToRql2Value(v: RawValue, t: RawType): Rql2Value = {
    def wrap(t: RawType, v: Rql2Value): Rql2Value = {
      assert(!v.isInstanceOf[TryRql2Value] || !v.isInstanceOf[OptionRql2Value])
      if (t.triable && t.nullable) {
        TryRql2Value(Right(OptionRql2Value(Some(v))))
      } else if (t.triable) {
        TryRql2Value(Right(v))
      } else if (t.nullable) {
        OptionRql2Value(Some(v))
      } else {
        v
      }
    }

    // Convert type.
    v match {
      case RawError(v) => TryRql2Value(Left(v))
      case RawNull() =>
        if (t.triable) {
          TryRql2Value(Right(OptionRql2Value(None)))
        } else {
          OptionRql2Value(None)
        }
      case RawByte(v) => wrap(t, ByteRql2Value(v))
      case RawShort(v) => wrap(t, ShortRql2Value(v))
      case RawInt(v) => wrap(t, IntRql2Value(v))
      case RawLong(v) => wrap(t, LongRql2Value(v))
      case RawFloat(v) => wrap(t, FloatRql2Value(v))
      case RawDouble(v) => wrap(t, DoubleRql2Value(v))
      case RawDecimal(v) => wrap(t, DecimalRql2Value(v))
      case RawBool(v) => wrap(t, BoolRql2Value(v))
      case RawString(v) => wrap(t, StringRql2Value(v))
      case RawBinary(v) => wrap(t, BinaryRql2Value(v))
      case RawLocation(v) => wrap(t, LocationRql2Value(v))
      case RawDate(v) => wrap(t, DateRql2Value(v))
      case RawTime(v) => wrap(t, TimeRql2Value(v))
      case RawTimestamp(v) => wrap(t, TimestampRql2Value(v))
      case RawInterval(years, months, weeks, days, hours, minutes, seconds, millis) =>
        wrap(t, IntervalRql2Value(years, months, weeks, days, hours, minutes, seconds, millis))
      case RawRecord(vs) =>
        val recordType = t.asInstanceOf[RawRecordType]
        val atts = vs.zipWithIndex.map { case (v, idx) => rawValueToRql2Value(v, recordType.atts(idx).tipe) }
        wrap(t, RecordRql2Value(atts))
      case RawList(vs) => wrap(t, ListRql2Value(vs.map(rawValueToRql2Value(_, t.asInstanceOf[RawListType].innerType))))
      case RawIterable(vs) =>
        wrap(t, IterableRql2Value(vs.map(rawValueToRql2Value(_, t.asInstanceOf[RawIterableType].innerType))))
      case RawOr(ors) =>
        val orType = t.asInstanceOf[RawOrType]
        val options = ors.zipWithIndex.map { case (v1, idx) => rawValueToRql2Value(v1, orType.ors(idx)) }
        wrap(t, OrRql2Value(options))
    }
  }

}
