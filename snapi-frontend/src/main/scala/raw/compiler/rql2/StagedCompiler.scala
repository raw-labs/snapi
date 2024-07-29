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
  ProgramEnvironment
}
import raw.compiler.base.source.Type
import raw.compiler.rql2.antlr4.ParserErrors
import raw.compiler.rql2.api._
import raw.compiler.rql2.source._
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
    t match {
      case t: Rql2TypeWithProperties if t.props.contains(Rql2IsTryableTypeProperty()) =>
        if (v.isException) {
          try {
            v.throwException()
            throw new AssertionError("should not happen")
          } catch {
            case NonFatal(ex) => TryRql2Value(Left(ex.getMessage))
          }
        } else {
          TryRql2Value(Right(polyglotValueToRql2Value(v, t.cloneAndRemoveProp(Rql2IsTryableTypeProperty()))))
        }
      case t: Rql2TypeWithProperties if t.props.contains(Rql2IsNullableTypeProperty()) =>
        if (v.isNull) {
          OptionRql2Value(None)
        } else {
          OptionRql2Value(Some(polyglotValueToRql2Value(v, t.cloneAndRemoveProp(Rql2IsNullableTypeProperty()))))
        }

      case _: Rql2UndefinedType => throw new AssertionError("Rql2Undefined is not triable and is not nullable.")
      case _: Rql2BoolType => BoolRql2Value(v.asBoolean())
      case _: Rql2StringType => StringRql2Value(v.asString())
      case _: Rql2ByteType => ByteRql2Value(v.asByte())
      case _: Rql2ShortType => ShortRql2Value(v.asShort())
      case _: Rql2IntType => IntRql2Value(v.asInt())
      case _: Rql2LongType => LongRql2Value(v.asLong())
      case _: Rql2FloatType => FloatRql2Value(v.asFloat())
      case _: Rql2DoubleType => DoubleRql2Value(v.asDouble())
      case _: Rql2DecimalType =>
        val bg = BigDecimal(v.asString())
        DecimalRql2Value(bg)
      case _: Rql2DateType =>
        val date = v.asDate()
        DateRql2Value(date)
      case _: Rql2TimeType =>
        val time = v.asTime()
        TimeRql2Value(time)
      case _: Rql2TimestampType =>
        val localDate = v.asDate()
        val localTime = v.asTime()
        TimestampRql2Value(localDate.atTime(localTime))
      case _: Rql2IntervalType =>
        val d = v.asDuration()
        IntervalRql2Value(0, 0, 0, d.toDaysPart.toInt, d.toHoursPart, d.toMinutesPart, d.toSecondsPart, d.toMillisPart)
      case _: Rql2BinaryType =>
        val bufferSize = v.getBufferSize.toInt
        val byteArray = new Array[Byte](bufferSize)
        for (i <- 0 until bufferSize) {
          byteArray(i) = v.readBufferByte(i)
        }
        BinaryRql2Value(byteArray)
      case Rql2RecordType(atts, _) =>
        val vs = atts.map(att => polyglotValueToRql2Value(v.getMember(att.idn), att.tipe))
        RecordRql2Value(vs)
      case Rql2ListType(innerType, _) =>
        val seq = mutable.ArrayBuffer[Rql2Value]()
        for (i <- 0L until v.getArraySize) {
          val v1 = v.getArrayElement(i)
          seq.append(polyglotValueToRql2Value(v1, innerType))
        }
        ListRql2Value(seq)
      case Rql2IterableType(innerType, _) =>
        val seq = mutable.ArrayBuffer[Rql2Value]()
        val it = v.getIterator
        while (it.hasIteratorNextElement) {
          val v1 = it.getIteratorNextElement
          seq.append(polyglotValueToRql2Value(v1, innerType))
        }
        if (it.canInvokeMember("close")) {
          val callable = it.getMember("close")
          callable.execute()
        }
        IterableRql2Value(seq)
      case Rql2OrType(tipes, _) =>
        val idx = v.getMember("index").asInt()
        val v1 = v.getMember("value")
        val tipe = tipes(idx)
        polyglotValueToRql2Value(v1, tipe)
      case _: Rql2LocationType =>
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
        LocationRql2Value(LocationDescription(url, settings.toMap))
    }
  }

}
