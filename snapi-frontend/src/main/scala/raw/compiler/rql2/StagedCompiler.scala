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
import raw.client.api.{CompilerService, ErrorMessage, ErrorPosition, ErrorRange, ProgramEnvironment}
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
      .environment("RAW_PROGRAM_ENVIRONMENT", ProgramEnvironment.serializeToString(environment))
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

  private def polyglotValueToRql2Value(v: Value, t: Type)(implicit settings: RawSettings): Rql2Value = {
    t match {
      case t: Rql2TypeWithProperties if t.props.contains(Rql2IsTryableTypeProperty()) =>
        if (v.isException) {
          try {
            v.throwException()
            throw new AssertionError("should not happen")
          } catch {
            case NonFatal(ex) => Rql2TryValue(Left(ex.getMessage))
          }
        } else {
          Rql2TryValue(Right(polyglotValueToRql2Value(v, t.cloneAndRemoveProp(Rql2IsTryableTypeProperty()))))
        }
      case t: Rql2TypeWithProperties if t.props.contains(Rql2IsNullableTypeProperty()) =>
        if (v.isNull) {
          Rql2OptionValue(None)
        } else {
          Rql2OptionValue(Some(polyglotValueToRql2Value(v, t.cloneAndRemoveProp(Rql2IsNullableTypeProperty()))))
        }
      case _: Rql2UndefinedType => throw new AssertionError("Rql2Undefined is not triable and is not nullable.")
      case _: Rql2BoolType => Rql2BoolValue(v.asBoolean())
      case _: Rql2StringType => Rql2StringValue(v.asString())
      case _: Rql2ByteType => Rql2ByteValue(v.asByte())
      case _: Rql2ShortType => Rql2ShortValue(v.asShort())
      case _: Rql2IntType => Rql2IntValue(v.asInt())
      case _: Rql2LongType => Rql2LongValue(v.asLong())
      case _: Rql2FloatType => Rql2FloatValue(v.asFloat())
      case _: Rql2DoubleType => Rql2DoubleValue(v.asDouble())
      case _: Rql2DecimalType =>
        val bg = BigDecimal(v.asString())
        Rql2DecimalValue(bg)
      case _: Rql2DateType =>
        val date = v.asDate()
        Rql2DateValue(date)
      case _: Rql2TimeType =>
        val time = v.asTime()
        Rql2TimeValue(time)
      case _: Rql2TimestampType =>
        val localDate = v.asDate()
        val localTime = v.asTime()
        Rql2TimestampValue(localDate.atTime(localTime))
      case _: Rql2IntervalType =>
        val d = v.asDuration()
        Rql2IntervalValue(0, 0, 0, d.toDaysPart.toInt, d.toHoursPart, d.toMinutesPart, d.toSecondsPart, d.toMillisPart)
      case _: Rql2BinaryType =>
        val bufferSize = v.getBufferSize.toInt
        val byteArray = new Array[Byte](bufferSize)
        for (i <- 0 until bufferSize) {
          byteArray(i) = v.readBufferByte(i)
        }
        Rql2BinaryValue(byteArray)
      case Rql2RecordType(atts, _) =>
        val vs = atts.map(att => Rql2RecordAttr(att.idn, polyglotValueToRql2Value(v.getMember(att.idn), att.tipe)))
        Rql2RecordValue(vs)
      case Rql2ListType(innerType, _) =>
        val seq = mutable.ArrayBuffer[Rql2Value]()
        for (i <- 0L until v.getArraySize) {
          val v1 = v.getArrayElement(i)
          seq.append(polyglotValueToRql2Value(v1, innerType))
        }
        Rql2ListValue(seq)
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
        Rql2IterableValue(seq)
      case Rql2OrType(tipes, _) =>
        val idx = v.invokeMember("getIndex").asInt()
        val v1 = v.invokeMember("getValue")
        val tipe = tipes(idx)
        polyglotValueToRql2Value(v1, tipe)
      case _: Rql2LocationType =>
        val bufferSize = v.getBufferSize.toInt
        val byteArray = new Array[Byte](bufferSize)
        for (i <- 0 until bufferSize) {
          byteArray(i) = v.readBufferByte(i)
        }
        val location = LocationDescription.toLocation(LocationDescription.deserialize(byteArray))
        Rql2LocationValue(location)
    }
  }

}
