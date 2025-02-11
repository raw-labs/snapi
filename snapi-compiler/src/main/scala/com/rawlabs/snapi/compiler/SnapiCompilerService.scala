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

import com.rawlabs.compiler._
import com.rawlabs.compiler.writers.{PolyglotBinaryWriter, PolyglotTextWriter}
import com.rawlabs.snapi.compiler.writers.{SnapiCsvWriter, SnapiJsonWriter}
import com.rawlabs.utils.core.{RawSettings, RawUid, RawUtils}
import org.bitbucket.inkytonik.kiama.relation.LeaveAlone
import org.bitbucket.inkytonik.kiama.util.{Position, Positions}
import org.graalvm.polyglot._
import com.rawlabs.snapi.frontend.base

import scala.language.implicitConversions
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
import scala.util.control.NonFatal

object SnapiCompilerService {
  val LANGUAGE: Set[String] = Set("snapi")
}

class SnapiCompilerService(engineDefinition: (Engine, Boolean))(implicit protected val settings: RawSettings)
    extends CompilerService
    with TruffleValueConverter
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
    this(CompilerService.getEngine())
  }

  override def language: Set[String] = SnapiCompilerService.LANGUAGE

  private def getProgramContext(user: RawUid, environment: ProgramEnvironment): ProgramContext = {
    val compilerContext = new CompilerContext(user, inferrer)
    new ProgramContext(environment, compilerContext)
  }

  implicit def convertProgramEnvironment(
      env: com.rawlabs.compiler.ProgramEnvironment
  ): com.rawlabs.snapi.frontend.api.ProgramEnvironment = {
    com.rawlabs.snapi.frontend.api.ProgramEnvironment(
      env.uid,
      env.scopes,
      env.secrets,
      env.locationConfigs,
      env.options,
      env.jdbcUrl,
      env.maybeTraceId
    )
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
    val positions = new Positions()
    val parser = new Antlr4SyntaxAnalyzer(positions, true)
    val parseResult = parser.parse(source)
    if (parseResult.isSuccess) {
      ParseSuccess(parseResult.tree)
    } else {
      ParseFailure(parseResult.errors)
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
        val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
        if (tree.valid) {
          GetTypeSuccess(tree.rootType)
        } else {
          GetTypeFailure(tree.errors)
        }
      }
    )
  }

  implicit def convertMessage(msg: com.rawlabs.snapi.frontend.api.Message): Message = {
    msg match {
      case com.rawlabs.snapi.frontend.api.HintMessage(message, positions, code, tags) =>
        HintMessage(message, positions, code, tags)
      case com.rawlabs.snapi.frontend.api.InfoMessage(message, positions, code, tags) =>
        InfoMessage(message, positions, code, tags)
      case com.rawlabs.snapi.frontend.api.WarningMessage(message, positions, code, tags) =>
        WarningMessage(message, positions, code, tags)
      case com.rawlabs.snapi.frontend.api.ErrorMessage(message, positions, code, tags) =>
        ErrorMessage(message, positions, code, tags)
    }
  }

  implicit def convertListMessage(msgs: List[com.rawlabs.snapi.frontend.api.Message]): List[Message] = {
    msgs.map(convertMessage)
  }

  implicit def convertErrorRange(range: com.rawlabs.snapi.frontend.api.ErrorRange): ErrorRange = {
    ErrorRange(range.begin, range.end)
  }

  implicit def convertListErrorRange(ranges: List[com.rawlabs.snapi.frontend.api.ErrorRange]): List[ErrorRange] = {
    ranges.map(convertErrorRange)
  }

  implicit def convertErrorPosition(pos: com.rawlabs.snapi.frontend.api.ErrorPosition): ErrorPosition = {
    ErrorPosition(pos.line, pos.column)
  }

  override def getProgramDescription(
      source: String,
      environment: ProgramEnvironment
  ): Either[List[ErrorMessage], ProgramDescription] = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
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
          Right(programDescription)
        } else {
          val errs = tree.errors.collect { case e: com.rawlabs.snapi.frontend.api.ErrorMessage => e }
          Left(errs)
        }
      }
    )
  }

  implicit def convertErrorMessage(err: com.rawlabs.snapi.frontend.api.ErrorMessage): ErrorMessage = {
    ErrorMessage(err.message, err.positions.map(convertErrorRange), err.code, err.tags)
  }

  implicit def convertListErrorMessage(errs: List[com.rawlabs.snapi.frontend.api.ErrorMessage]): List[ErrorMessage] = {
    errs.map {
      case com.rawlabs.snapi.frontend.api.ErrorMessage(message, positions, code, tags) =>
        ErrorMessage(message, positions.map(convertErrorRange), code, tags)
    }
  }

  override def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream,
      maxRows: Option[Long]
  ): Either[ExecutionError, ExecutionSuccess] = {
    val ctx = buildTruffleContext(environment, maybeOutputStream = Some(outputStream))
    ctx.initialize("snapi")
    ctx.enter()
    try {
      getValueAndType(source, environment, maybeDecl, ctx) match {
        case Left(err) => Left(err)
        case Right((v, tipe)) => environment.options
            .get("output-format")
            .map(_.toLowerCase) match {
            case Some("csv") =>
              if (!CsvPackage.outputWriteSupport(tipe)) {
                return Left(ExecutionError.RuntimeError("unsupported type"))
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
                Right(ExecutionSuccess(w.complete))
              } catch {
                case ex: IOException => Left(ExecutionError.RuntimeError(ex.getMessage))
              } finally {
                RawUtils.withSuppressNonFatalException(w.close())
              }
            case Some("json") =>
              if (!JsonPackage.outputWriteSupport(tipe)) {
                return Left(ExecutionError.RuntimeError("unsupported type"))
              }
              val w = new SnapiJsonWriter(outputStream, maxRows)
              try {
                w.write(v, tipe.asInstanceOf[SnapiTypeWithProperties])
                w.flush()
                Right(ExecutionSuccess(w.complete))
              } catch {
                case ex: IOException => Left(ExecutionError.RuntimeError(ex.getMessage))
              } finally {
                RawUtils.withSuppressNonFatalException(w.close())
              }
            case Some("text") =>
              if (!StringPackage.outputWriteSupport(tipe)) {
                return Left(ExecutionError.RuntimeError("unsupported type"))
              }
              val w = new PolyglotTextWriter(outputStream)
              try {
                w.writeAndFlush(v)
                Right(ExecutionSuccess(complete = true))
              } catch {
                case ex: IOException => Left(ExecutionError.RuntimeError(ex.getMessage))
              }
            case Some("binary") =>
              if (!BinaryPackage.outputWriteSupport(tipe)) {
                return Left(ExecutionError.RuntimeError("unsupported type"))
              }
              val w = new PolyglotBinaryWriter(outputStream)
              try {
                w.writeAndFlush(v)
                Right(ExecutionSuccess(complete = true))
              } catch {
                case ex: IOException => Left(ExecutionError.RuntimeError(ex.getMessage))
              }
            case _ => Left(ExecutionError.RuntimeError("unknown output format"))
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
            throw ex
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
              Left(ExecutionError.ValidationError(errors.to))
            } else {
              // A runtime failure during execution. The query could be a failed tryable, or a runtime error (e.g. a
              // file not found) hit when processing a reader that evaluates as a _collection_ (processed outside the
              // evaluation of the query).
              Left(ExecutionError.RuntimeError(ex.getMessage))
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
  ): Either[ExecutionError, (Value, Type)] = {
    import ExecutionError._

    try {
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
            return Left(
              ValidationError(
                List(ErrorMessage("missing mandatory arguments", List.empty, ParserErrors.ParserErrorCode))
              )
            )
          }

          // Mandatory arguments are converted to Polyglot values
          val maybeMandatoryPolyglotArguments = mandatoryArgs.map(arg => rawValueToPolyglotValue(arg, ctx))
          val unsupportedMandatoryPolyglotArguments = maybeMandatoryPolyglotArguments.zipWithIndex.collect {
            case (None, idx) => ErrorMessage(
                s"unsupported mandatory argument at position ${idx + 1}",
                List.empty,
                ParserErrors.ParserErrorCode
              )
          }
          if (unsupportedMandatoryPolyglotArguments.nonEmpty) {
            return Left(ValidationError(unsupportedMandatoryPolyglotArguments.to))
          }
          val mandatoryPolyglotArguments: Array[Value] = maybeMandatoryPolyglotArguments.flatten

          // Optional arguments are converted to Polyglot values
          val maybeOptionalPolyglotArguments: Map[String, Option[Value]] = funType.os.collect {
            case arg if optionalArgs.contains(arg.i) =>
              val paramValue = optionalArgs(arg.i)
              arg.i -> rawValueToPolyglotValue(paramValue, ctx)
          }.toMap
          val unsupportedOptionalPolyglotArguments = maybeOptionalPolyglotArguments.collect {
            case (i, None) =>
              ErrorMessage(s"unsupported optional argument $i", List.empty, ParserErrors.ParserErrorCode)
          }
          if (unsupportedOptionalPolyglotArguments.nonEmpty) {
            return Left(ValidationError(unsupportedOptionalPolyglotArguments.to))
          }

          // Optional arguments can be missing from the provided arguments.
          // We replace the missing ones by their default value.
          val optionalPolyglotArguments: Seq[Value] = funType.os.map { arg =>
            maybeOptionalPolyglotArguments.get(arg.i) match {
              // if the argument is provided, use it
              case Some(paramValue) => paramValue.get
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
    } catch {
      case TruffleValidationError(errors) => Left(ValidationError(errors))
      case TruffleRuntimeError(message) => Left(RuntimeError(message))
    }
  }

  override def eval(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String]
  ): Either[ExecutionError, EvalSuccess] = {

    // 1) Build the Truffle context
    val ctx = buildTruffleContext(environment, maybeOutputStream = None)
    ctx.initialize("snapi")
    ctx.enter()

    try {
      // 2) Get the value+type. This is where we handle immediate validation errors, which are caught by the
      // SnapiLanguage during the parse call.
      getValueAndType(source, environment, maybeDecl, ctx) match {
        case Left(error) =>
          // 3) We got an immediate error.
          // We must close the context now, or we leak it.
          ctx.leave()
          ctx.close(true) // Cancel the context and any executing threads.
          Left(error)

        case Right((v, t)) =>
          // 4) We have a value so let's produce the final evaluation result.
          buildEvalResult(ctx, v, t.asInstanceOf[SnapiTypeWithProperties])

      }
    } catch {
      case t: Throwable =>
        // 6) We caught some other exception.
        // We must close the context now, or we leak it.
        ctx.leave()
        ctx.close(true) // Cancel the context and any executing threads.
        throw t
    }
  }

  /**
   * buildTruffleIterator determines if the top-level type is an iterable (SnapiIterableType / SnapiListType).
   * - If yes, returns Right(Iterator[RawValue]).
   * - Else returns Left(RawValue).
   * Also returns the corresponding RawType to describe the final shape.
   */
  private def buildEvalResult(
      ctx: Context,
      v: Value,
      t: SnapiTypeWithProperties
  ): Either[ExecutionError.RuntimeError, EvalSuccess] = {

    import EvalSuccess._
    import ExecutionError._

    // If top-level is triable AND holds an exception, return it as a runtime error.
    if (maybeIsException(v, t)) {
      try {
        v.throwException()
      } catch {
        case NonFatal(ex) =>
          // Close context here directly, since we're done
          ctx.leave()
          ctx.close(true)
          return Left(ExecutionError.RuntimeError(ex.getMessage))
      }
    }

    t match {
      case SnapiIterableType(innerType, _) if !v.isNull =>
        assert(!maybeIsException(v, t))

        // 1) If top-level is an iterable (and not top-level triable error), we produce an iterator
        // Note that the iterator does not close itself on error, as the consumer is expected to call close() itself

        // First obtain the polyglot iterator
        val polyIt =
          try {
            v.getIterator
          } catch {
            case TruffleRuntimeError(message) => return Left(RuntimeError(message))
          }

        val valueIterator = new Iterator[com.rawlabs.protocol.raw.Value] with AutoCloseable {
          // Lazy to avoid calling getIterator if we don't need it
          // Note we do not need to check if v.isException because we know 'v' is not a triable.

          override def hasNext: Boolean = {
            // We do not expect this call to fail; only the creation of the polyIt can fail...
            polyIt.hasIteratorNextElement
          }

          override def next(): com.rawlabs.protocol.raw.Value = {
            // We do not expect this call to fail; only the creation of the polyIt can fail...
            val truffleVal = polyIt.getIteratorNextElement

            // Now convert the Truffle value to protocolValue.
            // If the value is an exception, as per our semantic, it will be converted now to a ValueError.
            val protocolVal = fromTruffleValue(truffleVal, innerType.asInstanceOf[SnapiTypeWithProperties])

            protocolVal
          }

          override def close(): Unit = {
            ctx.leave()
            ctx.close(true)
          }
        }

        // The context remains open since we are returning an iterator.
        // It is up to the caller to close that iterator, which then triggers the Truffle context closure.

        val protocolType = TypeConverter.toProtocolType(snapiTypeToRawType(innerType).get)
        Right(IteratorValue(protocolType, valueIterator))

      case _ =>
        // 2) Otherwise, produce a single value iterator
        val protocolValue = fromTruffleValue(v, t)
        val protocolType = TypeConverter.toProtocolType(snapiTypeToRawType(t).get)
        val evalResult = ResultValue(protocolType, protocolValue)

        // Close context here directly
        ctx.leave()
        ctx.close(true)

        Right(evalResult)
    }
  }

  /**
   * Check if top-level is triable and an exception, in which case fromTruffleValue
   * would yield a RawError. But for iterables, we skip that check at top-level
   * because we want an Iterator that possibly yields items or errors in iteration.
   */
  private def maybeIsException(v: Value, st: SnapiTypeWithProperties): Boolean = {
    val tryableProp = SnapiIsTryableTypeProperty()
    st.props.contains(tryableProp) && v.isException
  }

  override def formatCode(
      source: String,
      environment: ProgramEnvironment,
      maybeIndent: Option[Int],
      maybeWidth: Option[Int]
  ): FormatCodeResponse = {
    val pretty = new SourceCommentsPrettyPrinter(maybeIndent, maybeWidth)
    pretty.prettyCode(source) match {
      case Right(code) => FormatCodeResponse(Some(code))
      case Left(_) => FormatCodeResponse(None)
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
        withLspTree(source, lspService => lspService.dotAutoComplete(source, environment, position))(
          programContext
        ) match {
          case Right(value) => value
          case Left(_) => AutoCompleteResponse(Array.empty)
        }
      }
    )
  }

  implicit def convertFromPos(pos: Pos): com.rawlabs.snapi.frontend.api.Pos = {
    com.rawlabs.snapi.frontend.api.Pos(pos.line, pos.column)
  }

  implicit def convertAutoCompleteResponse(
      response: com.rawlabs.snapi.frontend.api.AutoCompleteResponse
  ): AutoCompleteResponse = {
    AutoCompleteResponse(response.completions)
  }

  implicit def convertCompletion(completion: com.rawlabs.snapi.frontend.api.Completion): Completion = {
    completion match {
      case com.rawlabs.snapi.frontend.api.TypeCompletion(name, tipe) => TypeCompletion(name, tipe)
      case com.rawlabs.snapi.frontend.api.FieldCompletion(name, tipe) => FieldCompletion(name, tipe)
      case com.rawlabs.snapi.frontend.api.LetBindCompletion(name, tipe) => LetBindCompletion(name, tipe)
      case com.rawlabs.snapi.frontend.api.LetFunCompletion(name, tipe) => LetFunCompletion(name, tipe)
      case com.rawlabs.snapi.frontend.api.LetFunRecCompletion(name, tipe) => LetFunRecCompletion(name, tipe)
      case com.rawlabs.snapi.frontend.api.FunParamCompletion(name, tipe) => FunParamCompletion(name, tipe)
      case com.rawlabs.snapi.frontend.api.PackageCompletion(name, doc) => PackageCompletion(name, doc)
      case com.rawlabs.snapi.frontend.api.PackageEntryCompletion(name, doc) => PackageEntryCompletion(name, doc)
    }
  }

  implicit def convertOptionCompletion(
      completion: Option[com.rawlabs.snapi.frontend.api.Completion]
  ): Option[Completion] = {
    completion.map(convertCompletion)
  }

  implicit def convertArrayCompletion(
      completions: Array[com.rawlabs.snapi.frontend.api.Completion]
  ): Array[Completion] = {
    completions.map(convertCompletion)
  }

  implicit def convertFormatCodeResponse(
      response: com.rawlabs.snapi.frontend.api.FormatCodeResponse
  ): FormatCodeResponse = {
    FormatCodeResponse(response.code)
  }

  implicit def convertHoverResponse(response: com.rawlabs.snapi.frontend.api.HoverResponse): HoverResponse = {
    HoverResponse(response.completion)
  }

  implicit def convertRenameResponse(response: com.rawlabs.snapi.frontend.api.RenameResponse): RenameResponse = {
    RenameResponse(response.positions)
  }

  implicit def convertGoToDefinitionResponse(
      response: com.rawlabs.snapi.frontend.api.GoToDefinitionResponse
  ): GoToDefinitionResponse = {
    GoToDefinitionResponse(response.position)
  }

  implicit def convertValidateResponse(response: com.rawlabs.snapi.frontend.api.ValidateResponse): ValidateResponse = {
    ValidateResponse(response.messages)
  }

  implicit def convertPos(pos: com.rawlabs.snapi.frontend.api.Pos): Pos = {
    Pos(pos.line, pos.column)
  }

  implicit def convertListPos(pos: Array[com.rawlabs.snapi.frontend.api.Pos]): Array[Pos] = {
    pos.map(convertPos)
  }

  implicit def convertOptionPos(pos: Option[com.rawlabs.snapi.frontend.api.Pos]): Option[Pos] = {
    pos.map(convertPos)
  }

  implicit def convertPackageDoc(doc: com.rawlabs.snapi.frontend.api.PackageDoc): PackageDoc = {
    PackageDoc(doc.description, doc.info, doc.warning, doc.danger)
  }

  implicit def convertEntryDoc(doc: com.rawlabs.snapi.frontend.api.EntryDoc): EntryDoc = {
    EntryDoc(
      doc.summary,
      doc.description,
      doc.examples.map(convertExampleDoc),
      doc.params.map(convertParamDoc),
      doc.ret.map(convertReturnDoc),
      doc.info,
      doc.warning,
      doc.danger
    )
  }

  implicit def convertExampleDoc(doc: com.rawlabs.snapi.frontend.api.ExampleDoc): ExampleDoc = {
    ExampleDoc(doc.example, doc.result)
  }

  implicit def convertParamDoc(doc: com.rawlabs.snapi.frontend.api.ParamDoc): ParamDoc = {
    ParamDoc(
      doc.name,
      convertTypeDoc(doc.typeDoc),
      doc.description,
      doc.isOptional,
      doc.isVarArg,
      doc.info,
      doc.warning,
      doc.danger,
      doc.customSyntax
    )
  }

  implicit def convertReturnDoc(doc: com.rawlabs.snapi.frontend.api.ReturnDoc): ReturnDoc = {
    ReturnDoc(doc.description, doc.retType.map(convertTypeDoc))
  }

  implicit def convertTypeDoc(doc: com.rawlabs.snapi.frontend.api.TypeDoc): TypeDoc = {
    TypeDoc(doc.possibleTypes)
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
        withLspTree(source, lspService => lspService.wordAutoComplete(source, environment, prefix, position))(
          programContext
        ) match {
          case Right(value) => value
          case Left(_) => AutoCompleteResponse(Array.empty)
        }
      }
    )
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        withLspTree(source, lspService => lspService.hover(source, environment, position))(programContext) match {
          case Right(value) => value
          case Left(_) => HoverResponse(None)
        }
      }
    )
  }

  override def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        withLspTree(source, lspService => lspService.rename(source, environment, position))(programContext) match {
          case Right(value) => value
          case Left(_) => RenameResponse(Array.empty)
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
        withLspTree(source, lspService => lspService.definition(source, environment, position))(
          programContext
        ) match {
          case Right(value) => value
          case Left(_) => GoToDefinitionResponse(None)
        }
      }
    )
  }

  override def validate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.uid, environment)
        withLspTree(
          source,
          lspService => lspService.validate
        )(programContext) match {
          case Right(value) => value
          case Left((err, pos)) => ValidateResponse(parseError(err, pos))
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

  implicit def convertFromErrorRange(range: ErrorRange): com.rawlabs.snapi.frontend.api.ErrorRange = {
    com.rawlabs.snapi.frontend.api.ErrorRange(
      com.rawlabs.snapi.frontend.api.ErrorPosition(range.begin.line, range.begin.column),
      com.rawlabs.snapi.frontend.api.ErrorPosition(range.end.line, range.end.column)
    )
  }

  implicit def convertFromListErrorRange(ranges: List[ErrorRange]): List[com.rawlabs.snapi.frontend.api.ErrorRange] = {
    ranges.map(convertFromErrorRange)
  }

  override def doStop(): Unit = {
    inferrer.stop()
    if (initedEngine) {
      CompilerService.releaseEngine
    }
  }

  private def rawValueToPolyglotValue(rawValue: RawValue, ctx: Context): Option[Value] = {
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
      case _ => return None
    }
    val value = ctx.eval("snapi", code)
    Some(ctx.asValue(value))
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

  // Returns Option[RawType] as not all Snapi types are representable as Raw types.
  private def snapiTypeToRawType(t: Type): Option[RawType] = {
    def convert(t: Type): RawType = {
      // Read nullable and triable properties.
      var nullable = false
      var triable = false
      t match {
        case tp: SnapiTypeWithProperties =>
          if (tp.props.contains(SnapiIsNullableTypeProperty())) {
            nullable = true
          }
          if (tp.props.contains(SnapiIsTryableTypeProperty())) {
            triable = true
          }
        case _ =>
      }
      // Convert type.
      t match {
        case _: SnapiUndefinedType => RawUndefinedType(nullable, triable)
        case _: SnapiByteType => RawByteType(nullable, triable)
        case _: SnapiShortType => RawShortType(nullable, triable)
        case _: SnapiIntType => RawIntType(nullable, triable)
        case _: SnapiLongType => RawLongType(nullable, triable)
        case _: SnapiFloatType => RawFloatType(nullable, triable)
        case _: SnapiDoubleType => RawDoubleType(nullable, triable)
        case _: SnapiDecimalType => RawDecimalType(nullable, triable)
        case _: SnapiBoolType => RawBoolType(nullable, triable)
        case _: SnapiStringType => RawStringType(nullable, triable)
        case _: SnapiBinaryType => RawBinaryType(nullable, triable)
        case _: SnapiDateType => RawDateType(nullable, triable)
        case _: SnapiTimeType => RawTimeType(nullable, triable)
        case _: SnapiTimestampType => RawTimestampType(nullable, triable)
        case _: SnapiIntervalType => RawIntervalType(nullable, triable)
        case SnapiRecordType(atts, _) => RawRecordType(
            atts.map { case SnapiAttrType(idn, t1) => RawAttrType(idn, convert(t1)) },
            nullable,
            triable
          )
        case SnapiListType(inner, _) => RawListType(convert(inner), nullable, triable)
        case SnapiIterableType(inner, _) => RawIterableType(convert(inner), nullable, triable)
        case SnapiOrType(ors, _) => RawOrType(ors.map(convert), nullable, triable)
        case _ => throw new IllegalArgumentException()
      }
    }

    try {
      Some(convert(t))
    } catch {
      case _: IllegalArgumentException => None
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
