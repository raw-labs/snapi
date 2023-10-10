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

package raw.compiler.rql2.truffle

import com.oracle.truffle.api.interop.TruffleObject
import org.bitbucket.inkytonik.kiama.relation.EnsureTree
import org.bitbucket.inkytonik.kiama.util.{Position, Positions}
import org.graalvm.polyglot.{Context, PolyglotAccess, Source, Value}
import raw.compiler.api.{
  AutoCompleteResponse,
  CompilationFailure,
  CompilationResponse,
  CompilationSuccess,
  CompilerService,
  CompilerServiceException,
  EvalResponse,
  EvalRuntimeFailure,
  EvalSuccess,
  ExecutionResponse,
  ExecutionRuntimeFailure,
  ExecutionSuccess,
  ExecutionValidationFailure,
  FormatCodeResponse,
  GetProgramDescriptionFailure,
  GetProgramDescriptionResponse,
  GetProgramDescriptionSuccess,
  GetTypeFailure,
  GetTypeResponse,
  GetTypeSuccess,
  GoToDefinitionResponse,
  HoverResponse,
  ParseFailure,
  ParseResponse,
  ParseSuccess,
  ParseTypeFailure,
  ParseTypeResponse,
  ParseTypeSuccess,
  Pos,
  RenameResponse,
  ValidateResponse
}
import raw.compiler.base.errors.{BaseError, UnexpectedType, UnknownDecl}
import raw.compiler.common.{CommonCompilerProvider, Compiler}
import raw.runtime.{
  ParamBool,
  ParamByte,
  ParamDate,
  ParamDecimal,
  ParamDouble,
  ParamFloat,
  ParamInt,
  ParamInterval,
  ParamLong,
  ParamNull,
  ParamShort,
  ParamString,
  ParamTime,
  ParamTimestamp,
  ParamValue,
  ProgramEnvironment,
  RuntimeContext
}
import raw.runtime.truffle.RawLanguage
import raw.compiler.{
  base,
  CompilerParserException,
  DeclDescription,
  ErrorMessage,
  ErrorPosition,
  ErrorRange,
  ParamDescription,
  ProgramDescription
}
import raw.compiler.base.{CompilerContext, TreeDeclDescription, TreeDescription, TreeParamDescription}
import raw.compiler.base.source.{BaseNode, Type}
import raw.compiler.common.source.{SourceNode, SourceProgram}
import raw.compiler.rql2.errors._
import raw.compiler.rql2.lsp.{CompilerLspService, LspSyntaxAnalyzer}
import raw.compiler.rql2.{FrontendSyntaxAnalyzer, ProgramContext, SemanticAnalyzer, TreeWithPositions}
import raw.compiler.rql2.source._
import raw.compiler.scala2.Scala2CompilerContext
import raw.creds.api.CredentialsServiceProvider
import raw.inferrer.api.InferrerServiceProvider
import raw.runtime.truffle.runtime.primitives.{DateObject, DecimalObject, IntervalObject, TimeObject, TimestampObject}
import raw.sources.api.SourceContext
import raw.utils.{endsWithIgnoreCase, AuthenticatedUser, RawConcurrentHashMap, RawSettings}

import java.io.OutputStream
import scala.util.control.NonFatal

class Rql2TruffleCompilerService(maybeClassLoader: Option[ClassLoader] = None)(implicit settings: RawSettings)
    extends CompilerService {

  private val credentials = CredentialsServiceProvider(maybeClassLoader)

  // Map of users to compiler context.
  private val compilerContextCaches = new RawConcurrentHashMap[AuthenticatedUser, CompilerContext]

  private def getCompilerContext(user: AuthenticatedUser): CompilerContext = {
    compilerContextCaches.getOrElseUpdate(user, createCompilerContext(user, "rql2-truffle"))
  }

  private def createCompilerContext(user: AuthenticatedUser, language: String): CompilerContext = {
    // Initialize source context
    implicit val sourceContext = new SourceContext(user, credentials, settings)

    // Initialize inferrer
    val inferrer = InferrerServiceProvider(maybeClassLoader)

    // Initialize compiler context
    new Scala2CompilerContext(language, user, sourceContext, inferrer, maybeClassLoader)
  }

  private def getProgramContext(user: AuthenticatedUser, environment: ProgramEnvironment): ProgramContext = {
    val compilerContext = getCompilerContext(user)
    val runtimeContext = new RuntimeContext(compilerContext.sourceContext, settings, environment)
    new ProgramContext(runtimeContext)(compilerContext)
  }

  override def prettyPrint(node: BaseNode, user: AuthenticatedUser): String = {
    SourcePrettyPrinter.format(node)
  }

  // TODO (msb): Change signature to include position of the parsing error.
  override def parseType(tipe: String, user: AuthenticatedUser): ParseTypeResponse = {
    val positions = new Positions()
    val parser = new FrontendSyntaxAnalyzer(positions)
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
            GetTypeFailure(List(ErrorMessage(ex.getMessage, List(raw.compiler.ErrorRange(ex.position, ex.position)))))
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
                    DeclDescription(None, SourcePrettyPrinter.format(outType), comment)
                  case TreeDeclDescription(Some(params), outType, comment) =>
                    val formattedParams = params.map {
                      case TreeParamDescription(idn, tipe, required) =>
                        ParamDescription(idn, SourcePrettyPrinter.format(tipe), required)
                    }
                    DeclDescription(Some(formattedParams), SourcePrettyPrinter.format(outType), comment)
                }
                (idn, formattedDecls)
            }
            val programDescription =
              ProgramDescription(formattedDecls, maybeType.map(t => SourcePrettyPrinter.format(t)), comment)
            GetProgramDescriptionSuccess(programDescription)
          } else {
            GetProgramDescriptionFailure(tree.errors)
          }
        } catch {
          case ex: CompilerParserException => GetProgramDescriptionFailure(
              List(ErrorMessage(ex.getMessage, List(raw.compiler.ErrorRange(ex.position, ex.position))))
            )
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  // TODO (msb): Remove this method once emission is in Java.
  override def compile(
      source: String,
      environment: ProgramEnvironment,
      ref: Any
  ): CompilationResponse = {
//    val programContext = getProgramContext(environment.user, environment)
//    try {
//      val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
//      if (tree.valid) {
//        val outputProgram = transpile(inputProgram)
//        val signature = UUID.randomUUID().toString.replace("-", "").replace("_", "")
//        emit(signature, outputProgram, rawLanguageAsAny)
//      } else {
//        CompilationFailure(tree.errors)
//      }
//    } catch {
//      case ex: CompilerParserException =>
//        CompilationFailure(List(ErrorMessage(ex.getMessage, List(raw.compiler.ErrorRange(ex.position, ex.position)))))
//      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
//    }
    // We do NOT create a TruffleContext here, because this method is only used from the RawLanguage,
    // where there is already a TruffleContext created.
    implicit val compilerContext = getCompilerContext(environment.user)
    val compiler = new Rql2TruffleCompiler
    val sourceContext = compiler.compilerContext.sourceContext
    val runtimeContext = new RuntimeContext(
      sourceContext,
      settings,
      environment
    )
    val programContext = compiler.getProgramContext(runtimeContext)
    try {
      compiler.compile(source, ref)(programContext) match {
        case Left(errs) => CompilationFailure(errs)
        case Right(program) => CompilationSuccess(program)
      }
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def eval(source: String, environment: ProgramEnvironment): EvalResponse = {
    // TODO (msb): This is not handling Validation vs Runtime errors!
    withTruffleContext(
      environment,
      ctx =>
        try {
          val polyglotValue = ctx.eval("rql", source)
          val rawValue = convertPolyglotValueToRawValue(polyglotValue)
          EvalSuccess(rawValue)
        } catch {
          case NonFatal(t) => EvalRuntimeFailure(t.getMessage)
        }
    )
  }

  private def convertPolyglotValueToRawValue(v: Value): raw.runtime.interpreter.Value = {
    ???
  }

  override def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream
  ): ExecutionResponse = {
    // TODO (msb): This is not handling Validation vs Runtime errors!
    val ctx = buildTruffleContext(environment, maybeOutputStream = Some(outputStream))
    ctx.initialize(RawLanguage.ID)
    ctx.enter()
    try {
      val v = maybeDecl match {
        case Some(decl) =>
//          ctx.eval("rql", source)
//          val f = ctx.getBindings("rql").getMember(decl)
//          f.execute(maybeArguments)
          ???
        case None => ctx.eval("rql", source)
      }

//      if (v.isString) {
//        logger.debug(s" ---> ${v.getMetaQualifiedName} ${v.getMetaSimpleName}")
//      }

      if (v.isException) {
        logger.debug(s" ---> IS EXCEPTION!")
        logger.debug(v.throwException().toString)
      } else {
        if (v.isNumber) {
          logger.debug("v is number!")
          if (v.fitsInInt()) {
            logger.debug(v.asInt().toString)
          }
        } else if (v.isString) {
          logger.debug("v is string!")
          logger.debug(v.asString())
        }
      }
//      v.isException
//      v.throwException()

      //logger.debug(v.toString)
      logger.debug("isString: " + v.isString.toString)
      logger.debug("isNumber: " + v.isNumber.toString)

      // FIXME (msb): If we do the output format check client-side, this means we validate the tree twice.

      environment.options
        .get("output-format") match {
//        case Some("csv") =>
//          if (!isCsvCompatible(dataType)) return ExecutionRuntimeFailure("unsupported type")
        case Some("json") =>
//          if (!isJsonCompatible(dataType)) return ExecutionRuntimeFailure("unsupported type")
//          else ctx.eval(
          ctx.eval(
            "python",
            s"""import json
              |import polyglot
              |import sys
              |
              |def serialize_foreign(obj):
              |  if isinstance(obj, int) or isinstance(obj, bool):
              |    return obj
              |  if hasattr(obj, '__str__'):
              |    return obj.__str__() + "cucu"
              |  raise Exception("bum")
              |
              |def f(data):
              |  print(len(data))
              |  #json.dump(serialize_foreign(data), sys.stdout, indent=4)""".stripMargin
          )
//        case Some("text") =>
//          if (!isTextCompatible(dataType)) return ExecutionRuntimeFailure("unsupported type")
//        case Some("binary") =>
//          if (!isBinaryCompatible(dataType)) return ExecutionRuntimeFailure("unsupported type")
        case None => return ExecutionRuntimeFailure("unknown output format")
      }
      val wf = ctx.getBindings("python").getMember("f")
      wf.execute(v)
      outputStream.flush()
      ExecutionSuccess
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

//  private def isJsonCompatible(dataType: Type): Boolean = {
//    true
//  }
//
//  private def isCsvCompatible(dataType: Type): Boolean = {
//    true
//  }
//
//  private def isTextCompatible(dataType: Type): Boolean = {
//    true
//  }
//
//  private def isBinaryCompatible(dataType: Type): Boolean = {
//    true
//  }

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
//        val compiler = getCompiler(environment.user)
//        val programContext = getProgramContext(compiler, source, environment)
//        try {
//          compiler.hover(source, environment, position)(programContext)
//        } catch {
//          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
//        }
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
      val lspService = new CompilerLspService(
        analyzer,
        positions,
        n => SourcePrettyPrinter.format(n)
      )(programContext.asInstanceOf[ProgramContext])
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
    compilerContextCaches.values.foreach(compilerContext => compilerContext.inferrer.stop())
    credentials.stop()
  }

  private def javaValueOf(value: ParamValue) = {
    value match {
      case ParamNull() => null
      case ParamByte(v) => v
      case ParamShort(v) => v
      case ParamInt(v) => v
      case ParamLong(v) => v
      case ParamFloat(v) => v
      case ParamDouble(v) => v
      case ParamBool(v) => v
      case ParamString(v) => v
      case ParamDecimal(v) => new DecimalObject(v)
      case ParamDate(v) => new DateObject(v)
      case ParamTime(v) => new TimeObject(v)
      case ParamTimestamp(v) => new TimestampObject(v)
      case ParamInterval(v) => IntervalObject.fromDuration(v)
    }
  }

  private def buildTruffleContext(
      environment: ProgramEnvironment,
      maybeOutputStream: Option[OutputStream] = None
  ): Context = {
    // Add environment settings as hardcoded environment variables.
    val ctxBuilder = Context
      .newBuilder(RawLanguage.ID, "python")
      .environment("RAW_SETTINGS", settings.renderAsString)
      .environment("RAW_USER", environment.user.uid.toString)
      .environment("RAW_TRACE_ID", environment.user.uid.toString)
      .environment("RAW_SCOPES", environment.scopes.mkString(","))
      .allowExperimentalOptions(true)
      .allowPolyglotAccess(PolyglotAccess.ALL)
    maybeOutputStream.foreach(os => ctxBuilder.out(os))
    // Add arguments as polyglot bindings.
    val ctx = ctxBuilder.build()
    environment.maybeArguments.foreach { args =>
      args.foreach(arg =>
        ctx.getPolyglotBindings.putMember(
          arg._1,
          javaValueOf(arg._2)
        )
      )
    }
    ctx
  }

  private def withTruffleContext[T](
      environment: ProgramEnvironment,
      f: Context => T
  ): T = {
    val ctx = buildTruffleContext(environment)
    ctx.initialize(RawLanguage.ID)
    ctx.enter()
    try {
      f(ctx)
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

}
