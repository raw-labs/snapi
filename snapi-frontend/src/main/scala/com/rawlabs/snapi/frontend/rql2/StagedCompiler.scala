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

package com.rawlabs.snapi.frontend.rql2

import com.rawlabs.compiler.{CompilerService, ErrorMessage, ErrorPosition, ErrorRange, ProgramEnvironment}
import com.rawlabs.utils.core.RawSettings
import org.graalvm.polyglot.{Context, PolyglotAccess, PolyglotException, Source, Value}
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.rql2.antlr4.ParserErrors
import com.rawlabs.snapi.frontend.rql2.extensions._
import com.rawlabs.snapi.frontend.rql2.source._

import scala.collection.mutable
import scala.util.control.NonFatal

sealed trait StagedCompilerResponse
final case class StagedCompilerSuccess(v: SnapiValue) extends StagedCompilerResponse
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

  private def polyglotValueToRql2Value(v: Value, t: Type)(implicit settings: RawSettings): SnapiValue = {
    t match {
      case t: SnapiTypeWithProperties if t.props.contains(SnapiIsTryableTypeProperty()) =>
        if (v.isException) {
          try {
            v.throwException()
            throw new AssertionError("should not happen")
          } catch {
            case NonFatal(ex) => SnapiTryValue(Left(ex.getMessage))
          }
        } else {
          SnapiTryValue(Right(polyglotValueToRql2Value(v, t.cloneAndRemoveProp(SnapiIsTryableTypeProperty()))))
        }
      case t: SnapiTypeWithProperties if t.props.contains(SnapiIsNullableTypeProperty()) =>
        if (v.isNull) {
          SnapiOptionValue(None)
        } else {
          SnapiOptionValue(Some(polyglotValueToRql2Value(v, t.cloneAndRemoveProp(SnapiIsNullableTypeProperty()))))
        }
      case _: SnapiUndefinedType => throw new AssertionError("Rql2Undefined is not triable and is not nullable.")
      case _: SnapiBoolType => SnapiBoolValue(v.asBoolean())
      case _: SnapiStringType => SnapiStringValue(v.asString())
      case _: SnapiByteType => SnapiByteValue(v.asByte())
      case _: SnapiShortType => SnapiShortValue(v.asShort())
      case _: SnapiIntType => SnapiIntValue(v.asInt())
      case _: SnapiLongType => SnapiLongValue(v.asLong())
      case _: SnapiFloatType => SnapiFloatValue(v.asFloat())
      case _: SnapiDoubleType => SnapiDoubleValue(v.asDouble())
      case _: SnapiDecimalType =>
        val bg = BigDecimal(v.asString())
        SnapiDecimalValue(bg)
      case _: SnapiDateType =>
        val date = v.asDate()
        SnapiDateValue(date)
      case _: SnapiTimeType =>
        val time = v.asTime()
        SnapiTimeValue(time)
      case _: SnapiTimestampType =>
        val localDate = v.asDate()
        val localTime = v.asTime()
        SnapiTimestampValue(localDate.atTime(localTime))
      case _: SnapiIntervalType =>
        val d = v.asDuration()
        SnapiIntervalValue(0, 0, 0, d.toDaysPart.toInt, d.toHoursPart, d.toMinutesPart, d.toSecondsPart, d.toMillisPart)
      case _: SnapiBinaryType =>
        val bufferSize = v.getBufferSize.toInt
        val byteArray = new Array[Byte](bufferSize)
        for (i <- 0 until bufferSize) {
          byteArray(i) = v.readBufferByte(i)
        }
        SnapiBinaryValue(byteArray)
      case SnapiRecordType(atts, _) =>
        val vs = atts.map(att => SnapiRecordAttr(att.idn, polyglotValueToRql2Value(v.getMember(att.idn), att.tipe)))
        SnapiRecordValue(vs)
      case SnapiListType(innerType, _) =>
        val seq = mutable.ArrayBuffer[SnapiValue]()
        for (i <- 0L until v.getArraySize) {
          val v1 = v.getArrayElement(i)
          seq.append(polyglotValueToRql2Value(v1, innerType))
        }
        SnapiListValue(seq)
      case SnapiIterableType(innerType, _) =>
        val seq = mutable.ArrayBuffer[SnapiValue]()
        val it = v.getIterator
        while (it.hasIteratorNextElement) {
          val v1 = it.getIteratorNextElement
          seq.append(polyglotValueToRql2Value(v1, innerType))
        }
        if (it.canInvokeMember("close")) {
          val callable = it.getMember("close")
          callable.execute()
        }
        SnapiIterableValue(seq)
      case SnapiOrType(tipes, _) =>
        val idx = v.invokeMember("getIndex").asInt()
        val v1 = v.invokeMember("getValue")
        val tipe = tipes(idx)
        polyglotValueToRql2Value(v1, tipe)
      case _: SnapiLocationType =>
        val bufferSize = v.getBufferSize.toInt
        val byteArray = new Array[Byte](bufferSize)
        for (i <- 0 until bufferSize) {
          byteArray(i) = v.readBufferByte(i)
        }
        val location = LocationDescription.toLocation(LocationDescription.deserialize(byteArray))
        val publicDescription = v.asString()
        SnapiLocationValue(location, publicDescription)
    }
  }

}
