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

package com.rawlabs.snapi.compiler

import com.google.protobuf.ByteString
import com.rawlabs.compiler.{
  AutoCompleteResponse,
  CompilerService,
  CompilerServiceException,
  DeclDescription,
  ErrorMessage,
  ErrorPosition,
  ErrorRange,
  EvalResponse,
  EvalRuntimeFailure,
  EvalSuccessIterator,
  EvalSuccessValue,
  EvalValidationFailure,
  ExecutionResponse,
  ExecutionRuntimeFailure,
  ExecutionSuccess,
  ExecutionValidationFailure,
  FormatCodeResponse,
  GetProgramDescriptionFailure,
  GetProgramDescriptionResponse,
  GetProgramDescriptionSuccess,
  GoToDefinitionResponse,
  HoverResponse,
  Message,
  ParamDescription,
  Pos,
  ProgramDescription,
  ProgramEnvironment,
  RawBool,
  RawByte,
  RawDate,
  RawDecimal,
  RawDouble,
  RawFloat,
  RawInt,
  RawInterval,
  RawLong,
  RawNull,
  RawShort,
  RawString,
  RawTime,
  RawTimestamp,
  RawType,
  RawValue,
  RenameResponse,
  ValidateResponse
}
import com.rawlabs.compiler.writers.{PolyglotBinaryWriter, PolyglotTextWriter}
import com.rawlabs.protocol.raw.{
  ValueBinary,
  ValueBool,
  ValueByte,
  ValueError,
  ValueInterval,
  ValueList,
  ValueNull,
  ValueRecord,
  ValueRecordField,
  ValueShort
}
import com.rawlabs.snapi.compiler.SnapiCompilerService.getTruffleClassLoader
import com.rawlabs.snapi.compiler.writers.{SnapiCsvWriter, SnapiJsonWriter}
import com.rawlabs.utils.core.{RawSettings, RawUid, RawUtils}
import org.bitbucket.inkytonik.kiama.relation.LeaveAlone
import org.bitbucket.inkytonik.kiama.util.{Position, Positions}
import org.graalvm.polyglot._
import com.rawlabs.snapi.frontend.base
import com.rawlabs.snapi.frontend.base.errors._
import com.rawlabs.snapi.frontend.base.source.{BaseNode, Type}
import com.rawlabs.snapi.frontend.base.{CompilerContext, TreeDeclDescription, TreeDescription, TreeParamDescription}
import com.rawlabs.snapi.frontend.snapi.source.{SourceNode, SourceProgram}
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.antlr4.{Antlr4SyntaxAnalyzer, ParseProgramResult, ParseTypeResult, ParserErrors}
import com.rawlabs.snapi.frontend.snapi.errors._
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.inferrer.api.InferrerServiceProvider
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.{BinaryPackage, CsvPackage, JsonPackage, StringPackage}

import java.io.{IOException, OutputStream}
import scala.collection.mutable
import scala.util.control.NonFatal
import scala.collection.JavaConverters._

object SnapiCompilerService extends CustomClassAndModuleLoader {
  val LANGUAGE: Set[String] = Set("snapi")

  private val JARS_PATH = "raw.snapi.compiler.jars-path"

  private var maybeTruffleClassLoader: Option[ClassLoader] = _
  private val maybeTruffleClassLoaderLock = new Object

  def getTruffleClassLoader(implicit settings: RawSettings): Option[ClassLoader] = {
    maybeTruffleClassLoaderLock.synchronized {
      if (maybeTruffleClassLoader == null) {
        // If defined, contains the path used to create a classloader for the Truffle language runtime.
        val maybeJarsPath = settings.getStringOpt(JARS_PATH)
        maybeJarsPath match {
          case Some(jarsPath) => maybeTruffleClassLoader = Some(createCustomClassAndModuleLoader(jarsPath))
          case None => maybeTruffleClassLoader = None
        }
      }
      maybeTruffleClassLoader
    }
  }

}

class SnapiCompilerService(engineDefinition: (Engine, Boolean))(implicit protected val settings: RawSettings)
    extends CompilerService
    with SnapiTypeUtils {

  private val (engine, initedEngine) = engineDefinition

  private val inferrer = InferrerServiceProvider()

  // The default constructor allows an Engine to be specified, plus a flag to indicate whether it was created here
  // or externally. That's necessary for the test framework.
  // This is actually the "default constructor" which obtains a new engine or reuses an existing one.
  // Note that the engine will be released when the service is stopped only IF this auxiliary constructor created it.
  // Otherwise, we expect the external party - e.g. the test framework - to close it.
  // Refer to SnapiTruffleCompilerServiceTestContext to see the engine being created and released from the test
  // framework, so that every test suite instance has a fresh engine.
  def this()(implicit settings: RawSettings) = {
    this(CompilerService.getEngine(SnapiCompilerService.getTruffleClassLoader))
  }

  override def language: Set[String] = SnapiCompilerService.LANGUAGE

  // Map of users to compiler context.
  private val compilerContextCaches = new mutable.HashMap[RawUid, CompilerContext]
  private val compilerContextCachesLock = new Object

  private def getCompilerContext(user: RawUid): CompilerContext = {
    compilerContextCachesLock.synchronized {
      compilerContextCaches.getOrElseUpdate(user, new CompilerContext(user, inferrer))
    }
  }

  private def getProgramContext(user: RawUid, environment: ProgramEnvironment): ProgramContext = {
    val compilerContext = getCompilerContext(user)
    new ProgramContext(environment, compilerContext)
  }

  def prettyPrint(node: BaseNode, user: RawUid): String = {
    SourcePrettyPrinter.format(node)
  }

  def parseType(tipe: String, user: RawUid, internal: Boolean = false): ParseTypeResponse = {
    val positions = new Positions()
    val parser = new Antlr4SyntaxAnalyzer(positions, !internal)
    parser.parseType(tipe) match {
      case ParseTypeResult(errors, tipe) if errors.isEmpty => ParseTypeSuccess(tipe)
      case ParseTypeResult(errors, _) => ParseTypeFailure(errors)
    }
  }

  def parse(source: String, environment: ProgramEnvironment): ParseResponse = {
    val programContext = getProgramContext(environment.uid, environment)
    try {
      val positions = new Positions()
      val parser = new Antlr4SyntaxAnalyzer(positions, true)
      val parseResult = parser.parse(source)
      if (parseResult.isSuccess) {
        ParseSuccess(parseResult.tree)
      } else {
        ParseFailure(parseResult.errors)
      }
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  def getType(
      source: String,
      environment: ProgramEnvironment
  ): GetTypeResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        try {
          val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
          if (tree.valid) {
            GetTypeSuccess(tree.rootType)
          } else {
            GetTypeFailure(tree.errors)
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def getProgramDescription(
      source: String,
      environment: ProgramEnvironment
  ): GetProgramDescriptionResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        try {
          val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
          if (tree.valid) {
            val TreeDescription(decls, maybeType, comment) = tree.description
            val formattedDecls = decls.map {
              case (idn, programDecls) =>
                val formattedDecls = programDecls.map {
                  case TreeDeclDescription(None, outType, comment) =>
                    DeclDescription(None, snapiTypeToRawType(outType), comment)
                  case TreeDeclDescription(Some(params), outType, comment) =>
                    val formattedParams = params.map {
                      case TreeParamDescription(idn, tipe, required) =>
                        ParamDescription(idn, snapiTypeToRawType(tipe), defaultValue = None, comment = None, required)
                    }
                    DeclDescription(Some(formattedParams), snapiTypeToRawType(outType), comment)
                }
                (idn, formattedDecls)
            }
            val programDescription = ProgramDescription(
              formattedDecls,
              maybeType.map(t => DeclDescription(None, snapiTypeToRawType(t), None)),
              comment
            )
            GetProgramDescriptionSuccess(programDescription)
          } else {
            GetProgramDescriptionFailure(tree.errors.collect { case e: ErrorMessage => e })
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream,
      maxRows: Option[Long]
  ): ExecutionResponse = {
    val ctx = buildTruffleContext(environment, maybeOutputStream = Some(outputStream))
    ctx.initialize("snapi")
    ctx.enter()
    try {
      getValueAndType(source, environment, maybeDecl, ctx) match {
        case Left(err) => ExecutionRuntimeFailure(err)
        case Right((v, tipe)) => environment.options
            .get("output-format")
            .map(_.toLowerCase) match {
            case Some("csv") =>
              if (!CsvPackage.outputWriteSupport(tipe)) {
                return ExecutionRuntimeFailure("unsupported type")
              }
              val windowsLineEnding = environment.options.get("windows-line-ending") match {
                case Some("true") => true
                case _ => false
              }
              val lineSeparator = if (windowsLineEnding) "\r\n" else "\n"
              val w = new SnapiCsvWriter(outputStream, lineSeparator, maxRows)
              try {
                w.write(v, tipe.asInstanceOf[SnapiTypeWithProperties])
                w.flush()
                ExecutionSuccess(w.complete)
              } catch {
                case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
              } finally {
                RawUtils.withSuppressNonFatalException(w.close())
              }
            case Some("json") =>
              if (!JsonPackage.outputWriteSupport(tipe)) {
                return ExecutionRuntimeFailure("unsupported type")
              }
              val w = new SnapiJsonWriter(outputStream, maxRows)
              try {
                w.write(v, tipe.asInstanceOf[SnapiTypeWithProperties])
                w.flush()
                ExecutionSuccess(w.complete)
              } catch {
                case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
              } finally {
                RawUtils.withSuppressNonFatalException(w.close())
              }
            case Some("text") =>
              if (!StringPackage.outputWriteSupport(tipe)) {
                return ExecutionRuntimeFailure("unsupported type")
              }
              val w = new PolyglotTextWriter(outputStream)
              try {
                w.writeAndFlush(v)
                ExecutionSuccess(complete = true)
              } catch {
                case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
              }
            case Some("binary") =>
              if (!BinaryPackage.outputWriteSupport(tipe)) {
                return ExecutionRuntimeFailure("unsupported type")
              }
              val w = new PolyglotBinaryWriter(outputStream)
              try {
                w.writeAndFlush(v)
                ExecutionSuccess(complete = true)
              } catch {
                case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
              }
            case _ => ExecutionRuntimeFailure("unknown output format")
          }
      }
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
          if (ex.isInternalError) {
            // An internal error. It means a regular Exception thrown from the language (e.g. a Java Exception,
            // or a RawTruffleInternalErrorException, which isn't an AbstractTruffleException)
            val programContext = getProgramContext(environment.uid, environment)
            throw new CompilerServiceException(ex, programContext.dumpDebugInfo)
          } else {
            val err = ex.getGuestObject
            if (err != null && err.hasMembers && err.hasMember("errors")) {
              // A validation exception, semantic or syntax error (both come as the same kind of error)
              // that has a list of errors and their positions.
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
              ExecutionValidationFailure(errors.to)
            } else {
              // A runtime failure during execution. The query could be a failed tryable, or a runtime error (e.g. a
              // file not found) hit when processing a reader that evaluates as a _collection_ (processed outside the
              // evaluation of the query).
              ExecutionRuntimeFailure(ex.getMessage)
            }
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

  private def getValueAndType(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      ctx: Context
  ): Either[String, (Value, Type)] = {
    maybeDecl match {
      case Some(decl) =>
        // Eval the code and extract the function referred to by 'decl'
        val truffleSource = Source
          .newBuilder("snapi", source, "unnamed")
          .cached(false) // Disable code caching because of the inferrer.
          .build()
        ctx.eval(truffleSource)
        // 'decl' is found in the context bindings (by its name)
        val bindings = ctx.getBindings("snapi")
        val f = bindings.getMember(decl)
        // its type is found in the polyglot bindings as '@type:<name>'
        val funType = {
          val rawType = ctx.getPolyglotBindings.getMember("@type:" + decl).asString()
          val ParseTypeSuccess(tipe: FunType) = parseType(rawType, environment.uid, internal = true)
          tipe
        }
        // Prior to .execute, some checks on parameters since we may have
        // to fill optional parameters with their default value

        // Mandatory arguments are those that don't have a matching 'name' in optional parameters
        val namedArgs = funType.os.map(arg => arg.i -> arg.t).toMap

        // Split the provided parameters in two (mandatory/optional)
        val (optionalArgs, mandatoryArgs) = environment.maybeArguments match {
          case Some(args) =>
            val (optional, mandatory) = args.partition { case (idn, _) => namedArgs.contains(idn) }
            (optional.map(arg => arg._1 -> arg._2).toMap, mandatory.map(_._2))
          case None => (Map.empty[String, RawValue], Array.empty[RawValue])
        }

        // Mandatory args have to be all provided
        if (mandatoryArgs.length != funType.ms.size) {
          return Left("missing mandatory arguments")
        }
        val mandatoryPolyglotArguments = mandatoryArgs.map(arg => rawValueToPolyglotValue(arg, ctx))
        // Optional arguments can be missing from the provided arguments.
        // We replace the missing ones by their default value.
        val optionalPolyglotArguments = funType.os.map { arg =>
          optionalArgs.get(arg.i) match {
            // if the argument is provided, use it
            case Some(paramValue) => rawValueToPolyglotValue(paramValue, ctx)
            // else, the argument has a default value that can be obtained from `f`.
            case None => f.invokeMember("default_" + arg.i)
          }
        }
        // All arguments are there. Call .execute.
        val result = f.execute(mandatoryPolyglotArguments ++ optionalPolyglotArguments: _*)
        val tipe = funType.r
        // Return the result and its type.
        Right((result, tipe))
      case None =>
        val truffleSource = Source
          .newBuilder("snapi", source, "unnamed")
          .cached(false) // Disable code caching because of the inferrer.
          .build()
        val result = ctx.eval(truffleSource)
        // The value type is found in polyglot bindings after calling eval().
        val rawType = ctx.getPolyglotBindings.getMember("@type").asString()
        val ParseTypeSuccess(tipe) = parseType(rawType, environment.uid, internal = true)
        Right((result, tipe))
    }
  }

  override def eval(source: String, environment: ProgramEnvironment, maybeDecl: Option[String]): EvalResponse = {
    val ctx = buildTruffleContext(environment, maybeOutputStream = None)
    ctx.initialize("snapi")
    ctx.enter()
    try {
      // 1) Get the value+type from your language
      getValueAndType(source, environment, maybeDecl, ctx) match {
        case Left(err) =>
          // Something went wrong at "compilation" or retrieval time
          EvalRuntimeFailure(err)

        case Right((v, t)) =>
          // 2) Convert to either a single RawValue or an Iterator[RawValue]
          //    If there's a "tryable" error at the top level, we handle it, etc.
          val snapiT = t.asInstanceOf[SnapiTypeWithProperties]

          try {
            val (rawType, eitherValueOrIter) = fromTruffleValueOrIterator(v, snapiT)
            eitherValueOrIter match {
              case Left(singleVal) => EvalSuccessValue(rawType, singleVal)
              case Right(iter) => EvalSuccessIterator(rawType, iter)
            }
          } catch {
            case NonFatal(e) =>
              // E.g. we tried to convert a top-level triable and it had an exception,
              // or some other runtime error. We'll produce a failure message:
              EvalRuntimeFailure(e.getMessage)
          }
      }
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
          if (ex.isInternalError) {
            // An internal error. It means a regular Exception thrown from the language (e.g. a Java Exception,
            // or a RawTruffleInternalErrorException, which isn't an AbstractTruffleException)
            val programContext = getProgramContext(environment.uid, environment)
            throw new CompilerServiceException(ex, programContext.dumpDebugInfo)
          } else {
            val err = ex.getGuestObject
            if (err != null && err.hasMembers && err.hasMember("errors")) {
              // A validation exception, semantic or syntax error (both come as the same kind of error)
              // that has a list of errors and their positions.
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
              EvalValidationFailure(errors.to)
            } else {
              // A runtime failure during execution. The query could be a failed tryable, or a runtime error (e.g. a
              // file not found) hit when processing a reader that evaluates as a _collection_ (processed outside the
              // evaluation of the query).
              EvalRuntimeFailure(ex.getMessage)
            }
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

  /**
   * fromTruffleValueOrIterator determines if the top-level type is an iterable (SnapiIterableType / SnapiListType).
   * - If yes, returns Right(Iterator[RawValue]).
   * - Else returns Left(RawValue).
   * Also returns the corresponding RawType to describe the final shape.
   */
  private def fromTruffleValueOrIterator(
      v: Value,
      t: SnapiTypeWithProperties
  ): (RawType, Either[com.rawlabs.protocol.raw.Value, Iterator[com.rawlabs.protocol.raw.Value]]) = {

    // Because we eventually want to produce a RawType:
    val rawT: RawType = snapiTypeToRawType(t).get

    // 1) If top-level is an iterable (and not top-level triable error), we produce an iterator:
    t match {
      case SnapiIterableType(inner, _) if !v.isNull && !maybeIsException(v, t) =>
        // Return an iterator
        val it = new Iterator[com.rawlabs.protocol.raw.Value] {
          private val polyIt = v.getIterator
          override def hasNext: Boolean = polyIt.hasIteratorNextElement
          override def next(): com.rawlabs.protocol.raw.Value = {
            val elemVal = polyIt.getIteratorNextElement
            fromTruffleValue(elemVal, inner.asInstanceOf[SnapiTypeWithProperties])
          }
        }
        (rawT, Right(it))

      case SnapiListType(inner, _) if !v.isNull && !maybeIsException(v, t) =>
        // Return an iterator over array elements
        val arrSize = v.getArraySize
        val it = new Iterator[com.rawlabs.protocol.raw.Value] {
          private var idx = 0L
          override def hasNext: Boolean = idx < arrSize
          override def next(): com.rawlabs.protocol.raw.Value = {
            val elemVal = v.getArrayElement(idx)
            idx += 1
            fromTruffleValue(elemVal, inner.asInstanceOf[SnapiTypeWithProperties])
          }
        }
        (rawT, Right(it))

      // 2) Otherwise, produce a single RawValue
      case _ =>
        val rv = fromTruffleValue(v, t)
        (rawT, Left(rv))
    }
  }

  /**
   * Check if top-level is triable and an exception, in which case fromTruffleValue
   * would yield a RawError. But for iterables, we skip that check at top-level
   * because we want an Iterator that possibly yields items or errors in iteration.
   */
  private def maybeIsException(v: Value, t: SnapiTypeWithProperties): Boolean = {
    val tryableProp = SnapiIsTryableTypeProperty()
    t.props.contains(tryableProp) && v.isException
  }

  /**
   * Convert a single Truffle Value to a RawValue (no iteration at top level).
   * It recurses into records, or-lambda, intervals, etc.  If the type is triable
   * and the value is an exception, we produce a RawError; if the type is nullable
   * and value is null, we produce RawNull, etc.
   *
   * This mirrors your SnapiJsonWriter logic, but instead of writing JSON,
   * we build up the appropriate RawValue.
   */
  private def fromTruffleValue(
      v: Value,
      t: SnapiTypeWithProperties
  ): com.rawlabs.protocol.raw.Value = {
    val tryable = SnapiIsTryableTypeProperty()
    val nullable = SnapiIsNullableTypeProperty()

    // 1) If triable
    if (t.props.contains(tryable)) {
      // if the value is an exception => produce RawError
      if (v.isException) {
        try {
          // attempt to "throw" to get the actual Java exception
          v.throwException()
          // If we get here, it didn't actually throw? We'll produce RawError anyway.
          throw new CompilerServiceException("unknown triable error")
        } catch {
          case NonFatal(ex) => com.rawlabs.protocol.raw.Value
              .newBuilder()
              .setError(ValueError.newBuilder().setMessage(ex.getMessage).build())
              .build()
        }
      } else {
        // remove the property and keep going
        fromTruffleValue(v, t.cloneAndRemoveProp(tryable).asInstanceOf[SnapiTypeWithProperties])
      }
    }
    // 2) If nullable
    else if (t.props.contains(nullable)) {
      if (v.isNull) com.rawlabs.protocol.raw.Value.newBuilder().setNull(ValueNull.newBuilder()).build()
      else fromTruffleValue(v, t.cloneAndRemoveProp(nullable).asInstanceOf[SnapiTypeWithProperties])
    }
    // 3) Otherwise match on the underlying type
    else {
      t match {
        case _: SnapiBinaryType =>
          // read all bytes from the buffer
          val bytes = Array.ofDim[Byte](v.getBufferSize.toInt)
          var idx = 0
          while (idx < bytes.length) {
            bytes(idx) = v.readBufferByte(idx.toLong)
            idx += 1
          }
          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setBinary(ValueBinary.newBuilder().setV(ByteString.copyFrom(bytes)))
            .build()

        case _: SnapiBoolType =>
          com.rawlabs.protocol.raw.Value.newBuilder().setBool(ValueBool.newBuilder().setV(v.asBoolean())).build()
        case _: SnapiByteType =>
          com.rawlabs.protocol.raw.Value.newBuilder().setByte(ValueByte.newBuilder().setV(v.asByte())).build()
        case _: SnapiShortType =>
          com.rawlabs.protocol.raw.Value.newBuilder().setShort(ValueShort.newBuilder().setV(v.asShort())).build()
        case _: SnapiIntType => com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setInt(com.rawlabs.protocol.raw.ValueInt.newBuilder().setV(v.asInt()))
            .build()
        case _: SnapiLongType => com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setLong(com.rawlabs.protocol.raw.ValueLong.newBuilder().setV(v.asLong()))
            .build()
        case _: SnapiFloatType => com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setFloat(com.rawlabs.protocol.raw.ValueFloat.newBuilder().setV(v.asFloat()))
            .build()
        case _: SnapiDoubleType => com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setDouble(com.rawlabs.protocol.raw.ValueDouble.newBuilder().setV(v.asDouble()))
            .build()
        case _: SnapiDecimalType =>
          // If asString() returns decimal textual form, parse into BigDecimal
          val txt = v.asString()
          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setDecimal(com.rawlabs.protocol.raw.ValueDecimal.newBuilder().setV(txt))
            .build()

        case _: SnapiStringType => com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setString(com.rawlabs.protocol.raw.ValueString.newBuilder().setV(v.asString()))
            .build()

        case _: SnapiDateType =>
          // v.asDate() => LocalDate
          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setDate(
              com.rawlabs.protocol.raw.ValueDate
                .newBuilder()
                .setYear(v.asDate().getYear)
                .setMonth(v.asDate().getMonthValue)
                .setDay(v.asDate().getDayOfMonth)
            )
            .build()

        case _: SnapiTimeType =>
          // v.asTime() => LocalTime
          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setTime(
              com.rawlabs.protocol.raw.ValueTime
                .newBuilder()
                .setHour(v.asTime().getHour)
                .setMinute(v.asTime().getMinute)
                .setSecond(v.asTime().getSecond)
                .setNano(v.asTime().getNano)
            )
            .build()

        case _: SnapiTimestampType =>
          // Typically we treat v.asDate() as LocalDate, v.asTime() as LocalTime, then combine
          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setTimestamp(
              com.rawlabs.protocol.raw.ValueTimestamp
                .newBuilder()
                .setYear(v.asDate().getYear)
                .setMonth(v.asDate().getMonthValue)
                .setDay(v.asDate().getDayOfMonth)
                .setHour(v.asTime().getHour)
                .setMinute(v.asTime().getMinute)
                .setSecond(v.asTime().getSecond)
                .setNano(v.asTime().getNano)
            )
            .build()

        case _: SnapiIntervalType =>
          val duration = v.asDuration()
          val days = duration.toDays
          val hours = duration.toHoursPart
          val minutes = duration.toMinutesPart
          val seconds = duration.toSecondsPart
          val millis = duration.toMillisPart

          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setInterval(
              ValueInterval
                .newBuilder()
                .setYears(0)
                .setMonths(0)
                .setWeeks(0)
                .setDays(days.toInt)
                .setHours(hours)
                .setMinutes(minutes)
                .setSeconds(seconds)
                .setMillis(millis)
            )
            .build()

        case SnapiRecordType(attributes, _) =>
          // Build a RawRecord
          val recordAttrs = attributes.map { att =>
            val fieldName = att.idn
            val memberVal = v.getMember(fieldName)
            val fieldValue = fromTruffleValue(
              memberVal,
              att.tipe.asInstanceOf[SnapiTypeWithProperties]
            )
            ValueRecordField.newBuilder().setName(fieldName).setValue(fieldValue).build()
          }
          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setRecord(ValueRecord.newBuilder().addAllFields(recordAttrs.asJava))
            .build()

        case SnapiIterableType(_, _) | SnapiListType(_, _) =>
          // Should not happen here for a single-value function,
          // because we check them earlier in fromTruffleValueOrIterator,
          // but if it does, handle it by consuming to a list:
          val size =
            if (v.hasArrayElements) v.getArraySize
            else {
              // possibly v.getIterator? We'll do array approach if we can
              0
            }
          val items = (0L until size).map { i =>
            val elem = v.getArrayElement(i)
            fromTruffleValue(elem, t.asInstanceOf[SnapiListType].innerType.asInstanceOf[SnapiTypeWithProperties])
          }
          com.rawlabs.protocol.raw.Value.newBuilder().setList(ValueList.newBuilder().addAllValues(items.asJava)).build()

        case SnapiOrType(inners, _) =>
          // We can check which index the union picked. Typically you do:
          val index = v.invokeMember("getIndex").asInt()
          val value = v.invokeMember("getValue")
          fromTruffleValue(value, inners(index).asInstanceOf[SnapiTypeWithProperties])

        case other =>
          // Not recognized or not implemented
          throw new RuntimeException("unsupported type")
      }
    }
  }

  override def formatCode(
      source: String,
      environment: ProgramEnvironment,
      maybeIndent: Option[Int],
      maybeWidth: Option[Int]
  ): FormatCodeResponse = {
    val programContext = getProgramContext(environment.uid, environment)
    try {
      val pretty = new SourceCommentsPrettyPrinter(maybeIndent, maybeWidth)
      pretty.prettyCode(source) match {
        case Right(code) => FormatCodeResponse(Some(code))
        case Left(_) => FormatCodeResponse(None)
      }
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def dotAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): AutoCompleteResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        try {
          withLspTree(source, lspService => lspService.dotAutoComplete(source, environment, position))(
            programContext
          ) match {
            case Right(value) => value
            case Left(_) => AutoCompleteResponse(Array.empty)
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        try {
          withLspTree(source, lspService => lspService.wordAutoComplete(source, environment, prefix, position))(
            programContext
          ) match {
            case Right(value) => value
            case Left(_) => AutoCompleteResponse(Array.empty)
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        try {
          withLspTree(source, lspService => lspService.hover(source, environment, position))(programContext) match {
            case Right(value) => value
            case Left(_) => HoverResponse(None)
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        try {
          withLspTree(source, lspService => lspService.rename(source, environment, position))(programContext) match {
            case Right(value) => value
            case Left(_) => RenameResponse(Array.empty)
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def goToDefinition(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): GoToDefinitionResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        try {
          withLspTree(source, lspService => lspService.definition(source, environment, position))(
            programContext
          ) match {
            case Right(value) => value
            case Left(_) => GoToDefinitionResponse(None)
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def validate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        try {
          withLspTree(
            source,
            lspService => lspService.validate
          )(programContext) match {
            case Right(value) => value
            case Left((err, pos)) => ValidateResponse(parseError(err, pos))
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        // Will analyze the code and return only unknown declarations errors.
        val positions = new Positions()
        val parser = new Antlr4SyntaxAnalyzer(positions, true)
        val parseResult = parser.parse(source)
        if (parseResult.isSuccess) {
          val sourceProgram = parseResult.tree
          val kiamaTree = new org.bitbucket.inkytonik.kiama.relation.Tree[SourceNode, SourceProgram](
            sourceProgram
          )
          val analyzer = new SemanticAnalyzer(kiamaTree)(programContext.asInstanceOf[ProgramContext])

          // Selecting only a subset of the errors
          val selection = analyzer.errors.filter {
            // For the case of a function that does not exist in a package
            case UnexpectedType(_, PackageType(_), ExpectedProjType(_), _, _) => true
            case _: UnknownDecl => true
            case _: OutputTypeRequiredForRecursiveFunction => true
            case _: UnexpectedOptionalArgument => true
            case _: NoOptionalArgumentsExpected => true
            case _: KeyNotComparable => true
            case _: ItemsNotComparable => true
            case _: MandatoryArgumentAfterOptionalArgument => true
            case _: RepeatedFieldNames => true
            case _: UnexpectedArguments => true
            case _: MandatoryArgumentsMissing => true
            case _: RepeatedOptionalArguments => true
            case _: PackageNotFound => true
            case _: NamedParameterAfterOptionalParameter => true
            case _: ExpectedTypeButGotExpression => true
            case _ => false
          }
          ValidateResponse(formatErrors(selection, positions))
        } else {
          ValidateResponse(parseResult.errors)
        }
      }
    )
  }

  private def withLspTree[T](source: String, f: LspAnalyzer => T)(
      implicit programContext: base.ProgramContext
  ): Either[(String, Position), T] = {
    val positions = new Positions()
    val parser = new Antlr4SyntaxAnalyzer(positions, true)
    val ParseProgramResult(errors, program) = parser.parse(source)
    val tree = new org.bitbucket.inkytonik.kiama.relation.Tree[SourceNode, SourceProgram](
      program,
      shape = LeaveAlone // The LSP parser can create "cloned nodes" so this protects it.
    )
    val analyzer = new SemanticAnalyzer(tree)(programContext.asInstanceOf[ProgramContext])
    // Handle the LSP request.
    val lspService = new LspAnalyzer(errors, analyzer, positions)(programContext.asInstanceOf[ProgramContext])
    Right(f(lspService))
  }

  private def parseError(error: String, position: Position): List[ErrorMessage] = {
    val range = ErrorRange(ErrorPosition(position.line, position.column), ErrorPosition(position.line, position.column))
    List(ErrorMessage(error, List(range), ParserErrors.ParserErrorCode))
  }

  private def formatErrors(errors: Seq[CompilerMessage], positions: Positions): List[Message] = {
    errors.map { err =>
      val ranges = positions.getStart(err.node) match {
        case Some(begin) =>
          val Some(end) = positions.getFinish(err.node)
          List(ErrorRange(ErrorPosition(begin.line, begin.column), ErrorPosition(end.line, end.column)))
        case _ => List.empty
      }
      CompilationMessageMapper.toMessage(err, ranges, ErrorsPrettyPrinter.format)
    }.toList
  }

  override def doStop(): Unit = {
    compilerContextCachesLock.synchronized {
      compilerContextCaches.values.foreach(_.stop())
      compilerContextCaches.clear()
    }
    inferrer.stop()
    if (initedEngine) {
      CompilerService.releaseEngine
    }
  }

  private def rawValueToPolyglotValue(rawValue: RawValue, ctx: Context): Value = {
    val code: String = rawValue match {
      case RawNull() => "let x: undefined = null in x"
      case RawByte(v) => s"let x: byte = ${v}b in x"
      case RawShort(v) => s"let x: short = ${v}s in x"
      case RawInt(v) => s"let x: int = $v in x"
      case RawLong(v) => s"let x: long = ${v}L in x"
      case RawFloat(v) => s"let x: float = ${v}f in x"
      case RawDouble(v) => s"let x: double = $v in x"
      case RawBool(v) => s"let x: bool = $v in x"
      case RawString(v) => s"""let x: string = "${RawUtils.descape(v)}" in x"""
      case RawDecimal(v) => s"""let x: decimal = ${v}q in x"""
      case RawDate(v) => s"""let x: date = Date.Build(${v.getYear}, ${v.getMonthValue}, ${v.getDayOfMonth}) in x"""
      case RawTime(v) =>
        s"""let x: time = Time.Build(${v.getHour}, ${v.getMinute}, millis=${v.getNano / 1000000}) in x"""
      case RawTimestamp(v) =>
        s"""let x: timestamp = Timestamp.Build(${v.getYear}, ${v.getMonthValue}, ${v.getDayOfMonth}, ${v.getHour}, ${v.getMinute}, millis=${v.getNano / 1000000}) in x"""
      case RawInterval(years, months, weeks, days, hours, minutes, seconds, millis) =>
        s"""let x: interval = Interval.Build(years=$years, months=$months, weeks=$weeks, days=$days, hours=$hours, minutes=$minutes, seconds=$seconds, millis=$millis) in x"""
      case _ => throw new CompilerServiceException("type not supported")
    }
    val value = ctx.eval("snapi", code)
    ctx.asValue(value)
  }

  private def buildTruffleContext(
      environment: ProgramEnvironment,
      maybeOutputStream: Option[OutputStream] = None
  ): Context = {
    // Add environment settings as hardcoded environment variables.
    val ctxBuilder = Context
      .newBuilder("snapi")
      .engine(engine)
      .environment("RAW_PROGRAM_ENVIRONMENT", ProgramEnvironment.serializeToString(environment))
      .allowExperimentalOptions(true)
      .allowPolyglotAccess(PolyglotAccess.ALL)
    environment.options.get("staged-compiler").foreach { stagedCompiler =>
      ctxBuilder.option("snapi.staged-compiler", stagedCompiler)
    }
    ctxBuilder.option("snapi.settings", settings.renderAsString)
    // If the jars path is defined, create a custom class loader and set it as the host class loader.
    getTruffleClassLoader.map { classLoader =>
      // Set the module class loader as the Truffle runtime classloader.
      // This enables the Truffle language runtime to be fully isolated from the rest of the application.
      ctxBuilder.hostClassLoader(classLoader)
    }

    maybeOutputStream.foreach(os => ctxBuilder.out(os))
    val ctx = ctxBuilder.build()
    ctx
  }

  private def withTruffleContext[T](
      environment: ProgramEnvironment,
      f: Context => T
  ): T = {
    val ctx = buildTruffleContext(environment)
    ctx.initialize("snapi")
    ctx.enter()
    try {
      f(ctx)
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

}

sealed trait ParseResponse
final case class ParseSuccess(program: SourceProgram) extends ParseResponse
final case class ParseFailure(errorMessages: List[Message]) extends ParseResponse

sealed trait ParseTypeResponse
final case class ParseTypeSuccess(tipe: Type) extends ParseTypeResponse
final case class ParseTypeFailure(errorMessages: List[Message]) extends ParseTypeResponse

sealed trait GetTypeResponse
final case class GetTypeFailure(errors: List[Message]) extends GetTypeResponse
final case class GetTypeSuccess(tipe: Option[Type]) extends GetTypeResponse
