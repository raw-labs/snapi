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

package raw.client.rql2.truffle

import org.bitbucket.inkytonik.kiama.relation.EnsureTree
import org.bitbucket.inkytonik.kiama.util.{Position, Positions}
import org.graalvm.polyglot._
import raw.client.api._
import raw.client.rql2.api._
import raw.client.writers.{PolyglotBinaryWriter, PolyglotTextWriter}
import raw.compiler.base
import raw.compiler.base.errors.{BaseError, UnexpectedType, UnknownDecl}
import raw.compiler.base.source.BaseNode
import raw.compiler.base.{CompilerContext, TreeDeclDescription, TreeDescription, TreeParamDescription}
import raw.compiler.common.source.{SourceNode, SourceProgram}
import raw.compiler.rql2.builtin.{BinaryPackage, CsvPackage, JsonPackage, StringPackage}
import raw.compiler.rql2.errors._
import raw.compiler.rql2.lsp.{CompilerLspService, LspSyntaxAnalyzer}
import raw.compiler.rql2.source._
import raw.compiler.rql2._
import raw.creds.api.CredentialsServiceProvider
import raw.inferrer.api.InferrerServiceProvider
import raw.runtime._
import raw.sources.api.SourceContext
import raw.utils.{AuthenticatedUser, RawConcurrentHashMap, RawSettings, RawUtils}

import java.io.{IOException, OutputStream}
import scala.util.control.NonFatal

object Rql2TruffleCompilerService {
  val language: Set[String] = Set("rql2", "rql2-truffle", "snapi")
}

class Rql2TruffleCompilerService(maybeClassLoader: Option[ClassLoader] = None)(
    implicit protected val settings: RawSettings
) extends Rql2CompilerService
    with Rql2TypeUtils {

  override def language: Set[String] = Rql2TruffleCompilerService.language

  private val credentials = CredentialsServiceProvider(maybeClassLoader)

  // Map of users to compiler context.
  private val compilerContextCaches = new RawConcurrentHashMap[AuthenticatedUser, CompilerContext]

  private def getCompilerContext(user: AuthenticatedUser): CompilerContext = {
    compilerContextCaches.getOrElseUpdate(user, createCompilerContext(user, "rql2-truffle"))
  }

  private def createCompilerContext(user: AuthenticatedUser, language: String): CompilerContext = {
    // Initialize source context
    implicit val sourceContext = new SourceContext(user, credentials, settings, maybeClassLoader)

    // Initialize inferrer
    val inferrer = InferrerServiceProvider(maybeClassLoader)

    // Initialize compiler context
    new CompilerContext(language, user, inferrer, sourceContext, maybeClassLoader)
  }

  private def getProgramContext(user: AuthenticatedUser, environment: ProgramEnvironment): ProgramContext = {
    val compilerContext = getCompilerContext(user)
    val runtimeContext = new RuntimeContext(compilerContext.sourceContext, settings, environment)
    new Rql2ProgramContext(runtimeContext, compilerContext)
  }

  override def prettyPrint(node: BaseNode, user: AuthenticatedUser): String = {
    SourcePrettyPrinter.format(node)
  }

  // TODO (msb): Change signature to include position of the parsing error.
  override def parseType(tipe: String, user: AuthenticatedUser, internal: Boolean = false): ParseTypeResponse = {
    val positions = new Positions()
    val parser = if (!internal) new FrontendSyntaxAnalyzer(positions) else new SyntaxAnalyzer(positions)
    parser.parseType(tipe) match {
      case Right(t) => ParseTypeSuccess(t)
      case Left((err, pos)) => ParseTypeFailure(err)
    }
  }

  override def parse(source: String, environment: ProgramEnvironment): ParseResponse = {
    val programContext = getProgramContext(environment.user, environment)
    try {
      val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
      val root = tree.root
      ParseSuccess(root)
    } catch {
      case ex: CompilerParserException => ParseFailure(ex.getMessage, ex.position)
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def getType(
      source: String,
      environment: ProgramEnvironment
  ): GetTypeResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.user, environment)
        try {
          val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
          if (tree.valid) {
            GetTypeSuccess(tree.rootType)
          } else {
            GetTypeFailure(tree.errors)
          }
        } catch {
          case ex: CompilerParserException =>
            GetTypeFailure(List(ErrorMessage(ex.getMessage, List(raw.client.api.ErrorRange(ex.position, ex.position)))))
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
        val programContext = getProgramContext(environment.user, environment)
        try {
          val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
          if (tree.valid) {
            val TreeDescription(decls, maybeType, comment) = tree.description
            val formattedDecls = decls.map {
              case (idn, programDecls) =>
                val formattedDecls = programDecls.map {
                  case TreeDeclDescription(None, outType, comment) =>
                    DeclDescription(None, rql2TypeToRawType(outType), comment)
                  case TreeDeclDescription(Some(params), outType, comment) =>
                    val formattedParams = params.map {
                      case TreeParamDescription(idn, tipe, required) =>
                        ParamDescription(idn, rql2TypeToRawType(tipe), required)
                    }
                    DeclDescription(Some(formattedParams), rql2TypeToRawType(outType), comment)
                }
                (idn, formattedDecls)
            }
            val programDescription =
              ProgramDescription(formattedDecls, maybeType.map(t => rql2TypeToRawType(t)), comment)
            GetProgramDescriptionSuccess(programDescription)
          } else {
            GetProgramDescriptionFailure(tree.errors)
          }
        } catch {
          case ex: CompilerParserException => GetProgramDescriptionFailure(
              List(ErrorMessage(ex.getMessage, List(raw.client.api.ErrorRange(ex.position, ex.position))))
            )
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def eval(source: String, tipe: RawType, environment: ProgramEnvironment): EvalResponse = {
    withTruffleContext(
      environment,
      ctx =>
        try {
          val truffleSource = Source
            .newBuilder("rql", source, "unnamed")
            .cached(false) // Disable code caching because of the inferrer.
            .build()
          val polyglotValue = ctx.eval(truffleSource)
          val rawValue = polyglotValueToRawValue(polyglotValue, tipe)
          EvalSuccess(rawValue)
        } catch {
          case ex: PolyglotException =>
            if (ex.isInterrupted) {
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
                  ErrorMessage(message, positions.to)
                }
                EvalValidationFailure(errors.to)
              } else {
                EvalRuntimeFailure(ex.getMessage)
              }
            } else {
              // Unexpected error. For now we throw the PolyglotException.
              throw ex
            }
        }
    )
  }

  override def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream
  ): ExecutionResponse = {
    val ctx = buildTruffleContext(environment, maybeOutputStream = Some(outputStream))
    ctx.initialize("rql")
    ctx.enter()
    try {
      val (v, tipe) = maybeDecl match {
        case Some(decl) =>
          // Eval the code and extract the function referred to by 'decl'
          val truffleSource = Source
            .newBuilder("rql", source, "unnamed")
            .cached(false) // Disable code caching because of the inferrer.
            .build()
          ctx.eval(truffleSource)
          // 'decl' is found in the context bindings (by its name)
          val bindings = ctx.getBindings("rql")
          val f = bindings.getMember(decl)
          // its type is found in the polyglot bindings as '@type:<name>'
          val funType = {
            val rawType = ctx.getPolyglotBindings.getMember("@type:" + decl).asString()
            val ParseTypeSuccess(tipe: FunType) = parseType(rawType, environment.user, internal = true)
            tipe
          }
          // Prior to .execute, some checks on parameters since we may have
          // to fill optional parameters with their default value

          // mandatory arguments are those that don't have a matching 'name' in optional parameters
          val namedArgs = funType.os.map(arg => arg.i -> arg.t).toMap

          // split the provided parameters in two (mandatory/optional)
          val (optionalArgs, mandatoryArgs) = environment.maybeArguments match {
            case Some(args) =>
              val (optional, mandatory) = args.partition { case (idn, _) => namedArgs.contains(idn) }
              (optional.map(arg => arg._1 -> arg._2).toMap, mandatory.map(_._2))
            case None => (Map.empty[String, RawValue], Array.empty[RawValue])
          }

          // mandatory args have to be all provided
          if (mandatoryArgs.length != funType.ms.size) {
            return ExecutionRuntimeFailure("missing mandatory arguments")
          }
          val mandatoryPolyglotArguments = mandatoryArgs.map(arg => rawValueToPolyglotValue(arg, ctx))
          // optional arguments can be missing from the provided arguments.
          // we replace the missing ones by their default value.
          val optionalPolyglotArguments = funType.os.map { arg =>
            optionalArgs.get(arg.i) match {
              // if the argument is provided, use it
              case Some(paramValue) => rawValueToPolyglotValue(paramValue, ctx)
              // else, the argument has a default value that can be obtained from `f`.
              case None => f.invokeMember("default_" + arg.i)
            }
          }
          // all arguments are there. Call .execute.
          val result = f.execute(mandatoryPolyglotArguments ++ optionalPolyglotArguments: _*)
          val tipe = funType.r
          // return the result and its type
          (result, tipe)
        case None =>
          val truffleSource = Source
            .newBuilder("rql", source, "unnamed")
            .cached(false) // Disable code caching because of the inferrer.
            .build()
          val result = ctx.eval(truffleSource)
          // the value type is found in polyglot bindings after calling eval().
          val rawType = ctx.getPolyglotBindings.getMember("@type").asString()
          val ParseTypeSuccess(tipe) = parseType(rawType, environment.user, internal = true)
          (result, tipe)
      }

      environment.options
        .get("output-format")
        .map(_.toLowerCase) match {
        case Some("csv") =>
          if (!CsvPackage.outputWriteSupport(tipe)) {
            return ExecutionRuntimeFailure("unsupported type")
          }
          val programContext = getProgramContext(environment.user, environment)
          val windowsLineEnding = environment.options.get("windows-line-ending") match {
            case Some("true") => true
            case _ => programContext.settings.config.getBoolean("raw.compiler.windows-line-ending")
          }
          val lineSeparator = if (windowsLineEnding) "\r\n" else "\n"
          val csvWriter = new Rql2CsvWriter(outputStream, lineSeparator)
          try {
            csvWriter.write(v, tipe.asInstanceOf[Rql2TypeWithProperties])
            ExecutionSuccess
          } catch {
            case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
          } finally {
            RawUtils.withSuppressNonFatalException(csvWriter.close())
          }
        case Some("json") =>
          if (!JsonPackage.outputWriteSupport(tipe)) {
            return ExecutionRuntimeFailure("unsupported type")
          }
          val w = new Rql2JsonWriter(outputStream)
          try {
            w.write(v, tipe.asInstanceOf[Rql2TypeWithProperties])
            ExecutionSuccess
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
            w.writeValue(v)
            ExecutionSuccess
          } catch {
            case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
          }
        case Some("binary") =>
          if (!BinaryPackage.outputWriteSupport(tipe)) {
            return ExecutionRuntimeFailure("unsupported type")
          }
          val w = new PolyglotBinaryWriter(outputStream)
          try {
            w.writeValue(v)
            ExecutionSuccess
          } catch {
            case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
          }
        case _ => ExecutionRuntimeFailure("unknown output format")
      }
    } catch {
      case ex: PolyglotException =>
        if (ex.isInterrupted) {
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
              ErrorMessage(message, positions.to)
            }
            ExecutionValidationFailure(errors.to)
          } else {
            ExecutionRuntimeFailure(ex.getMessage)
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
    val programContext = getProgramContext(environment.user, environment)
    try {
      val pretty = new SourceCommentsPrettyPrinter(maybeIndent, maybeWidth)
      pretty.prettyCode(source) match {
        case Right(code) => FormatCodeResponse(Some(code), List.empty)
        case Left((err, pos)) => FormatCodeResponse(None, parseError(err, pos))
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
        val programContext = getProgramContext(environment.user, environment)
        try {
          withLspTree(source, lspService => lspService.dotAutoComplete(source, environment, position))(
            programContext
          ) match {
            case Right(value) => value
            case Left((err, pos)) => AutoCompleteResponse(Array.empty, parseError(err, pos))
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
        val programContext = getProgramContext(environment.user, environment)
        try {
          withLspTree(source, lspService => lspService.wordAutoComplete(source, environment, prefix, position))(
            programContext
          ) match {
            case Right(value) => value
            case Left((err, pos)) => AutoCompleteResponse(Array.empty, parseError(err, pos))
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    /*
    withLspTree(source, lspService => lspService.hover(source, environment, position)) match {
      case Right(value) => value
      case Left((err, pos)) => HoverResponse(None, parseError(err, pos))
    }
     */
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.user, environment)
        try {
          withLspTree(source, lspService => lspService.hover(source, environment, position))(programContext) match {
            case Right(value) => value
            case Left((err, pos)) => HoverResponse(None, parseError(err, pos))
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
        val programContext = getProgramContext(environment.user, environment)
        try {
          withLspTree(source, lspService => lspService.rename(source, environment, position))(programContext) match {
            case Right(value) => value
            case Left((err, pos)) => RenameResponse(Array.empty, parseError(err, pos))
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
        val programContext = getProgramContext(environment.user, environment)
        try {
          withLspTree(source, lspService => lspService.definition(source, environment, position))(
            programContext
          ) match {
            case Right(value) => value
            case Left((err, pos)) => GoToDefinitionResponse(None, parseError(err, pos))
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
        val programContext = getProgramContext(environment.user, environment)
        try {
          withLspTree(
            source,
            lspService => {
              val response = lspService.validate
              if (response.errors.isEmpty) {
                // The "flexible" tree did not find any semantic errors.
                // So now we should parse with the "strict" parser/analyzer to get a proper tree and check for errors
                // in that one.
                val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
                if (tree.valid) {
                  ValidateResponse(List.empty)
                } else {
                  ValidateResponse(tree.errors)
                }
              } else {
                // The "flexible" tree found some semantic errors, so report only those.
                response
              }
            }
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
        val programContext = getProgramContext(environment.user, environment)
        try {
          // Will analyze the code and return only unknown declarations errors.
          val positions = new Positions()
          val parser = new FrontendSyntaxAnalyzer(positions)
          parser.parse(source) match {
            case Right(program) =>
              val sourceProgram = program.asInstanceOf[SourceProgram]
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
            case Left((err, pos)) => ValidateResponse(parseError(err, pos))
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  private def withLspTree[T](source: String, f: CompilerLspService => T)(
      implicit programContext: base.ProgramContext
  ): Either[(String, Position), T] = {
    // Parse tree with dedicated parser, which is more lose and tries to obtain an AST even with broken code.
    val positions = new Positions()
    val parser = new LspSyntaxAnalyzer(positions)
    parser.parse(source).right.map { program =>
      // Manually instantiate an analyzer to create a "flexible tree" that copes with broken code.
      val sourceProgram = program.asInstanceOf[SourceProgram]
      val kiamaTree = new org.bitbucket.inkytonik.kiama.relation.Tree[SourceNode, SourceProgram](
        sourceProgram,
        shape = EnsureTree // The LSP parser can create "cloned nodes" so this protects it.
      )
      // Do not perform any validation on errors as we fully expect the tree to be "broken" in most cases.
      val analyzer = new SemanticAnalyzer(kiamaTree)(programContext.asInstanceOf[ProgramContext])
      // Handle the LSP request.
      val lspService = new CompilerLspService(analyzer, positions)(programContext.asInstanceOf[ProgramContext])
      f(lspService)
    }
  }

  private def parseError(error: String, position: Position): List[ErrorMessage] = {
    val range = ErrorRange(ErrorPosition(position.line, position.column), ErrorPosition(position.line, position.column))
    List(ErrorMessage(error, List(range)))
  }

  private def formatErrors(errors: Seq[BaseError], positions: Positions): List[ErrorMessage] = {
    errors.map { err =>
      val ranges = positions.getStart(err.node) match {
        case Some(begin) =>
          val Some(end) = positions.getFinish(err.node)
          List(ErrorRange(ErrorPosition(begin.line, begin.column), ErrorPosition(end.line, end.column)))
        case _ => List.empty
      }
      ErrorMessage(ErrorsPrettyPrinter.format(err), ranges)
    }.toList
  }

  override def doStop(): Unit = {
    super.doStop()
    compilerContextCaches.values.foreach(compilerContext => compilerContext.inferrer.stop())
    credentials.stop()
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
    val value = ctx.eval("rql", code)
    ctx.asValue(value)
  }

  private def buildTruffleContext(
      environment: ProgramEnvironment,
      maybeOutputStream: Option[OutputStream] = None
  ): Context = {
    // Add environment settings as hardcoded environment variables.
    val ctxBuilder = Context
      .newBuilder("rql")
      .engine(engine)
      .environment("RAW_SETTINGS", settings.renderAsString)
      .environment("RAW_USER", environment.user.uid.toString)
      .environment("RAW_TRACE_ID", environment.user.uid.toString)
      .environment("RAW_SCOPES", environment.scopes.mkString(","))
      .allowExperimentalOptions(true)
      .allowPolyglotAccess(PolyglotAccess.ALL)
    environment.options.get("staged-compiler").foreach { stagedCompiler =>
      ctxBuilder.option("rql.staged-compiler", stagedCompiler)
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
    ctx.initialize("rql")
    ctx.enter()
    try {
      f(ctx)
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

}
