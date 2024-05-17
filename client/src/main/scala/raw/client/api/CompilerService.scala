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

import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import org.graalvm.polyglot.{Engine, Value}
import raw.utils.{RawException, RawService, RawSettings}

import java.io.OutputStream
import scala.collection.mutable
import scala.util.control.NonFatal
import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}

// Exception that wraps the underlying error so that it includes the extra debug info.
final class CompilerServiceException(
    message: String,
    val debugInfo: List[(String, String)] = List.empty,
    cause: Throwable = null
) extends RawException(message, cause) {

  def this(t: Throwable, debugInfo: List[(String, String)]) = this(t.getMessage, debugInfo, t)

  def this(t: Throwable, environment: ProgramEnvironment) =
    this(t.getMessage, CompilerService.getDebugInfo(environment))

  def this(t: Throwable) = this(t.getMessage, cause = t)

}

object CompilerService {

  private val enginesLock = new Object
  private val enginesCache = mutable.HashMap[RawSettings, Engine]()

  // Return engine and a flag indicating if the engine was created.
  def getEngine()(implicit settings: RawSettings): (Engine, Boolean) = {
    enginesLock.synchronized {
      enginesCache.get(settings) match {
        case Some(engine) =>
          // Re-using an engine someone else create before. This typically happens on staged compilation.
          (engine, false)
        case None =>
          // First time creating an engine for this settings.
          val options = new java.util.HashMap[String, String]()
          if (settings.onTrainingWheels) {
            // Refer to settings at:
            // https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/Options/
            //          options.put("engine.CompileImmediately", "true")
            //          options.put("engine.TraceCompilation", "true")
            //          options.put("engine.BackgroundCompilation", "false")
            //          options.put("engine.CompilationFailureAction", "Throw")
            //          options.put("engine.CompilationFailureAction", "Diagnose")
            //          options.put("compiler.LogInlinedTargets", "true")
          }
          val engine = Engine
            .newBuilder()
            .allowExperimentalOptions(true)
            .options(options)
            .build()
          enginesCache.put(settings, engine)
          (engine, true)
      }
    }
  }

  def releaseEngine()(implicit settings: RawSettings): Unit = {
    enginesLock.synchronized {
      enginesCache.remove(settings).foreach(engine => engine.close(true))
    }
  }

  def getDebugInfo(environment: ProgramEnvironment): List[(String, String)] = {
    List(
      "Trace ID" -> environment.maybeTraceId.getOrElse("<undefined>"),
      "Arguments" -> environment.maybeArguments
        .map(args => args.map { case (k, v) => s"$k -> $v" }.mkString("\n"))
        .getOrElse("<undefined>"),
      "User" -> environment.user.toString,
      "Scopes" -> environment.scopes.mkString(","),
      "Options" -> environment.options.map { case (k, v) => s"$k -> $v" }.mkString("\n")
      //"Settings" -> runtimeContext.settings.toString
    )
  }

}

trait CompilerService extends RawService {

  implicit protected def settings: RawSettings

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

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[FormatCodeResponse], name = "formatCode"),
    new JsonType(value = classOf[AutoCompleteResponse], name = "autoComplete"),
    new JsonType(value = classOf[HoverResponse], name = "hover"),
    new JsonType(value = classOf[GoToDefinitionResponse], name = "definition"),
    new JsonType(value = classOf[RenameResponse], name = "rename"),
    new JsonType(value = classOf[ErrorResponse], name = "error"),
    new JsonType(value = classOf[ValidateResponse], name = "validate")
  )
)
sealed trait ClientLspResponse
final case class FormatCodeResponse(code: Option[String]) extends ClientLspResponse
final case class HoverResponse(completion: Option[Completion]) extends ClientLspResponse
final case class RenameResponse(positions: Array[Pos]) extends ClientLspResponse
final case class GoToDefinitionResponse(position: Option[Pos]) extends ClientLspResponse
final case class ValidateResponse(messages: List[Message]) extends ClientLspResponse
final case class ErrorResponse(errors: List[ErrorMessage]) extends ClientLspResponse
final case class AutoCompleteResponse(completions: Array[Completion]) extends ClientLspResponse

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[TypeCompletion], name = "tipe"),
    new JsonType(value = classOf[FieldCompletion], name = "field"),
    new JsonType(value = classOf[LetBindCompletion], name = "bind"),
    new JsonType(value = classOf[LetFunCompletion], name = "function"),
    new JsonType(value = classOf[LetFunRecCompletion], name = "recursiveFunction"),
    new JsonType(value = classOf[FunParamCompletion], name = "functionParameter"),
    new JsonType(value = classOf[PackageCompletion], name = "package"),
    new JsonType(value = classOf[PackageEntryCompletion], name = "packageEntry")
  )
)
sealed trait Completion
final case class TypeCompletion(name: String, tipe: String) extends Completion
final case class FieldCompletion(name: String, tipe: String) extends Completion
final case class LetBindCompletion(name: String, tipe: String) extends Completion
final case class LetFunCompletion(name: String, tipe: String) extends Completion
final case class LetFunRecCompletion(name: String, tipe: String) extends Completion
final case class FunParamCompletion(name: String, tipe: String) extends Completion
final case class PackageCompletion(name: String, doc: PackageDoc) extends Completion
final case class PackageEntryCompletion(name: String, doc: EntryDoc) extends Completion
