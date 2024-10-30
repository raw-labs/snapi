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

import com.rawlabs.compiler.{
  AutoCompleteResponse,
  CompilerService,
  CompilerServiceException,
  DeclDescription,
  ErrorMessage,
  ErrorPosition,
  ErrorRange,
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
  RawValue,
  RenameResponse,
  ValidateResponse
}
import com.rawlabs.compiler.writers.{PolyglotBinaryWriter, PolyglotTextWriter}
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
    assert(!stopped.get())
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
    assert(!stopped.get())
    val ctx = buildTruffleContext(environment, maybeOutputStream = Some(outputStream))
    ctx.initialize("snapi")
    ctx.enter()
    try {
      val (v, tipe) = maybeDecl match {
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
            return ExecutionRuntimeFailure("missing mandatory arguments")
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
          (result, tipe)
        case None =>
          val truffleSource = Source
            .newBuilder("snapi", source, "unnamed")
            .cached(false) // Disable code caching because of the inferrer.
            .build()
          val result = ctx.eval(truffleSource)
          // The value type is found in polyglot bindings after calling eval().
          val rawType = ctx.getPolyglotBindings.getMember("@type").asString()
          val ParseTypeSuccess(tipe) = parseType(rawType, environment.uid, internal = true)
          (result, tipe)
      }

      environment.options
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
    assert(!stopped.get())
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
    assert(!stopped.get())
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
    assert(!stopped.get())
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
