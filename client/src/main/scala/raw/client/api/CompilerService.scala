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

package raw.client.api

import org.graalvm.polyglot.{Engine, Value}
import raw.utils.{RawException, RawService, RawSettings}

import java.io.OutputStream
import scala.collection.mutable
import scala.util.control.NonFatal

// Exception that wraps the underlying error so that it includes the extra debug info.
final class CompilerServiceException(
    message: String,
    val debugInfo: List[(String, String)] = List.empty,
    cause: Throwable = null
) extends RawException(message, cause) {

  def this(t: Throwable, debugInfo: List[(String, String)]) = this(t.getMessage, debugInfo, t)

}

object CompilerService {

  private var engine: Engine = _
  private var engineCount = 0
  private val engineLock = new Object

  private def getEngine(implicit settings: RawSettings): Engine = {
    engineLock.synchronized {
      if (engine == null) {
        val options = new java.util.HashMap[String, String]()
        if (settings.onTrainingWheels) {
//          options.put("engine.CompileImmediately", "true")
          //          options.put("engine.TraceCompilation", "true")
          //          options.put("engine.BackgroundCompilation", "false")
//          options.put("engine.CompilationFailureAction", "Throw")
          //      options.put("engine.CompilationFailureAction", "Diagnose")
          //      options.put("compiler.LogInlinedTargets", "true")
          //  "-Dpolyglotimpl.CompilationFailureAction=Throw",
          //  "-Dpolyglotimpl.TreatPerformanceWarningsAsErrors=false",
          //  "-Dpolyglotimpl.CompilationExceptionsAreFatal=true",
          //  "-Dpolyglotimpl.BackgroundCompilation=false",
          //  "-Dpolyglotimpl.TraceCompilationDetails=true",
          //  "-Dpolyglotimpl.TraceInlining=true"
          //  "-Dgraal.Dump=Truffle:2",
          //  "-Dgraal.DumpPath=/tmp/graal_dumps",
          //  "-Dgraal.PrintGraph=Network",
          // https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/Options/
        }
        engine = Engine.newBuilder().allowExperimentalOptions(true).options(options).build()
      }
      engineCount += 1
      engine
    }
  }

  private def releaseEngine(): Unit = {
    engineLock.synchronized {
      engineCount -= 1
      if (engineCount == 0) {
        engine.close(true)
        engine = null
      }
    }
  }

}

trait CompilerService extends RawService {

  import CompilerService._

  implicit protected def settings: RawSettings

  protected lazy val engine: Engine = getEngine

  def language: Set[String]

  // Get the description of a source program.
  @throws[CompilerServiceException]
  def getProgramDescription(
      source: String,
      environment: ProgramEnvironment
  ): GetProgramDescriptionResponse

  // Evaluate a source program and return the result as a RawValue.
  @throws[CompilerServiceException]
  def eval(
      source: String,
      tipe: RawType,
      environment: ProgramEnvironment
  ): EvalResponse

  // Execute a source program and write the results to the output stream.
  @throws[CompilerServiceException]
  def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream
  ): ExecutionResponse

  // Format a source program.
  @throws[CompilerServiceException]
  def formatCode(
      source: String,
      environment: ProgramEnvironment,
      maybeIndent: Option[Int] = None,
      maybeWidth: Option[Int] = None
  ): FormatCodeResponse

  // Auto-complete a source program.
  @throws[CompilerServiceException]
  def dotAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): AutoCompleteResponse

  // Auto-complete a word in a source program.
  @throws[CompilerServiceException]
  def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse

  // Get the hover information for a source program.
  @throws[CompilerServiceException]
  def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse

  // Rename an identifier in a source program.
  @throws[CompilerServiceException]
  def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse

  // Go to definition of an identifier in a source program.
  @throws[CompilerServiceException]
  def goToDefinition(source: String, environment: ProgramEnvironment, position: Pos): GoToDefinitionResponse

  // Validate a source program.
  @throws[CompilerServiceException]
  def validate(source: String, environment: ProgramEnvironment): ValidateResponse

  // Validate a source program for the AI service.
  @throws[CompilerServiceException]
  def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse

  protected def polyglotValueToRawValue(v: Value, t: RawType): RawValue = {
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

  override def doStop(): Unit = {
    releaseEngine()
  }

}

final case class Pos(line: Int, column: Int)

sealed trait GetProgramDescriptionResponse
final case class GetProgramDescriptionFailure(errors: List[ErrorMessage]) extends GetProgramDescriptionResponse
final case class GetProgramDescriptionSuccess(programDescription: ProgramDescription)
    extends GetProgramDescriptionResponse

sealed trait EvalResponse
final case class EvalSuccess(v: RawValue) extends EvalResponse
final case class EvalValidationFailure(errors: List[ErrorMessage]) extends EvalResponse
final case class EvalRuntimeFailure(error: String) extends EvalResponse

sealed trait ExecutionResponse
case object ExecutionSuccess extends ExecutionResponse
final case class ExecutionValidationFailure(errors: List[ErrorMessage]) extends ExecutionResponse
final case class ExecutionRuntimeFailure(error: String) extends ExecutionResponse

final case class FormatCodeResponse(code: Option[String], errors: List[ErrorMessage])

final case class AutoCompleteResponse(completions: Array[Completion], errors: List[ErrorMessage])

sealed trait Completion
final case class TypeCompletion(name: String, tipe: String) extends Completion
final case class FieldCompletion(name: String, tipe: String) extends Completion
final case class LetBindCompletion(name: String, tipe: String) extends Completion
final case class LetFunCompletion(name: String, tipe: String) extends Completion
final case class LetFunRecCompletion(name: String, tipe: String) extends Completion
final case class FunParamCompletion(name: String, tipe: String) extends Completion
final case class PackageCompletion(name: String, doc: PackageDoc) extends Completion
final case class PackageEntryCompletion(name: String, doc: EntryDoc) extends Completion

final case class HoverResponse(completion: Option[Completion], errors: List[ErrorMessage])

final case class RenameResponse(positions: Array[Pos], errors: List[ErrorMessage])

final case class GoToDefinitionResponse(position: Option[Pos], errors: List[ErrorMessage])

final case class ValidateResponse(errors: List[ErrorMessage])
