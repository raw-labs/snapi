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

package raw.client.python

import org.graalvm.polyglot.{Context, Engine, PolyglotAccess, PolyglotException, Source, Value}
import raw.client.api._
import raw.client.writers.{PolyglotBinaryWriter, PolyglotCsvWriter, PolyglotJsonWriter, PolyglotTextWriter}
import raw.utils.{RawSettings, RawUtils}

import java.io.{IOException, OutputStream}

class PythonCompilerService(engineDefinition: (Engine, Boolean))(implicit protected val settings: RawSettings)
    extends CompilerService {

  private val (engine, initedEngine) = engineDefinition

  // The default constructor allows an Engine to be specified, plus a flag to indicate whether it was created here
  // or externally. That's necessary for the test framework.
  // This is actually the "default constructor" which obtains a new engine or reuses an existing one.
  // Note that the engine will be released when the service is stopped only IF this auxiliary constructor created it.
  // Otherwise, we expect the external party - e.g. the test framework - to close it.
  // Refer to Rql2TruffleCompilerServiceTestContext to see the engine being created and released from the test
  // framework, so that every test suite instance has a fresh engine.
  def this()(implicit settings: RawSettings) = {
    this(CompilerService.getEngine)
  }

  override def language: Set[String] = Set("python")

  override def getProgramDescription(source: String, environment: ProgramEnvironment): GetProgramDescriptionResponse = {
    ???
//    val source = """
//import ast
//
//class FunctionVisitor(ast.NodeVisitor):
//    def visit_FunctionDef(self, node):
//        print(f"Function name: {node.name}")
//        for arg in node.args.args:
//            arg_name = arg.arg
//            arg_type = ast.unparse(arg.annotation) if arg.annotation else "No annotation"
//            print(f"Argument: {arg_name}, Type annotation: {arg_type}")
//
//        # If you want to parse nested functions as well
//        self.generic_visit(node)
//
//def get_function_annotations(filename):
//    with open(filename, "r") as source:
//        tree = ast.parse(source.read(), filename=filename)
//        FunctionVisitor().visit(tree)
//
//# Test the function with the path to a Python file
//get_function_annotations("/Users/miguel/path_to_your_python_file.py")
//                    """;

//    withTruffleContext(
//      environment,
//      _ => {
//        val programContext = getProgramContext(environment.user, environment)
//        try {
//          val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
//          if (tree.valid) {
//            val TreeDescription(decls, maybeType, comment) = tree.description
//            val formattedDecls = decls.map {
//              case (idn, programDecls) =>
//                val formattedDecls = programDecls.map {
//                  case TreeDeclDescription(None, outType, comment) =>
//                    DeclDescription(None, SourcePrettyPrinter.format(outType), comment)
//                  case TreeDeclDescription(Some(params), outType, comment) =>
//                    val formattedParams = params.map {
//                      case TreeParamDescription(idn, tipe, required) =>
//                        ParamDescription(idn, SourcePrettyPrinter.format(tipe), required)
//                    }
//                    DeclDescription(Some(formattedParams), SourcePrettyPrinter.format(outType), comment)
//                }
//                (idn, formattedDecls)
//            }
//            val programDescription =
//              ProgramDescription(formattedDecls, maybeType.map(t => SourcePrettyPrinter.format(t)), comment)
//            GetProgramDescriptionSuccess(programDescription)
//          } else {
//            GetProgramDescriptionFailure(tree.errors)
//          }
//        } catch {
//          case ex: CompilerParserException => GetProgramDescriptionFailure(
//              List(ErrorMessage(ex.getMessage, List(raw.client.api.ErrorRange(ex.position, ex.position))))
//            )
//          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
//        }
//      }
//    )
  }

  override def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream
  ): ExecutionResponse = {
    val ctx = buildTruffleContext(environment, maybeOutputStream = Some(outputStream))
    ctx.initialize("python")
    ctx.enter()
    try {
      val truffleSource = Source
        .newBuilder("python", source, "unnamed")
        .build()
      val ve = ctx.eval(truffleSource)
      val v = maybeDecl match {
        case Some(decl) =>
          val bindings = ctx.getBindings("python")
          val f = bindings.getMember(decl)
          environment.maybeArguments match {
            case Some(args) =>
              val polyglotArguments = args.map(arg => rawValueToPolyglotValue(arg._2, ctx))
              f.execute(polyglotArguments: _*)
            case None => f.execute()
          }
        case None => ve
      }

      environment.options
        .get("output-format")
        .map(_.toLowerCase) match {
        case Some("csv") =>
//          val windowsLineEnding = environment.options.get("windows-line-ending") match {
//            case Some("true") => true
//            case _ => programContext.settings.config.getBoolean("raw.compiler.windows-line-ending")
//          }
          val csvWriter = new PolyglotCsvWriter(outputStream)
          try {
            csvWriter.writeValue(v)
            ExecutionSuccess
          } catch {
            case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
          } finally {
            RawUtils.withSuppressNonFatalException(csvWriter.close())
          }
        case Some("json") =>
          val w = new PolyglotJsonWriter(outputStream)
          try {
            w.write(v)
            ExecutionSuccess
          } catch {
            case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
          } finally {
            RawUtils.withSuppressNonFatalException(w.close())
          }
        case Some("text") =>
          val w = new PolyglotTextWriter(outputStream)
          try {
            w.writeValue(v)
            ExecutionSuccess
          } catch {
            case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
          }
        case Some("binary") =>
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
        } else {
          ExecutionRuntimeFailure(ex.getMessage)
        }
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

  private def rawValueToPolyglotValue(rawValue: RawValue, ctx: Context): Value = {
    val code: String = rawValue match {
      case RawNull() => "None"
      case RawByte(v) => ???
      case RawShort(v) => ???
      case RawInt(v) => v.toString
      case RawLong(v) => ???
      case RawFloat(v) => ???
      case RawDouble(v) => ???
      case RawBool(v) => if (v) "True" else "False"
      case RawString(v) => s""""${RawUtils.descape(v)}""""
      case RawDecimal(v) => ???
      case RawDate(v) => ???
      case RawTime(v) => ???
      case RawTimestamp(v) => ???
      case RawInterval(years, months, weeks, days, hours, minutes, seconds, millis) => ???
      case _ => throw new CompilerServiceException("type not supported")
    }
    val value = ctx.eval("python", code)
    ctx.asValue(value)
  }

  override def formatCode(
      source: String,
      environment: ProgramEnvironment,
      maybeIndent: Option[Int],
      maybeWidth: Option[Int]
  ): FormatCodeResponse = {
    FormatCodeResponse(Some(source))
  }

  override def dotAutoComplete(source: String, environment: ProgramEnvironment, position: Pos): AutoCompleteResponse = {
    AutoCompleteResponse(Array.empty)
  }

  override def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse = {
    AutoCompleteResponse(Array.empty)
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    HoverResponse(None)
  }

  override def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse = {
    RenameResponse(Array.empty)
  }

  override def goToDefinition(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): GoToDefinitionResponse = {
    GoToDefinitionResponse(None)
  }

  override def validate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    ValidateResponse(List.empty)
  }

  override def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    ValidateResponse(List.empty)
  }

  override def doStop(): Unit = {
    if (initedEngine) {
      CompilerService.releaseEngine
    }
  }

  private def buildTruffleContext(
      environment: ProgramEnvironment,
      maybeOutputStream: Option[OutputStream]
  ): Context = {
    // Add environment settings as hardcoded environment variables.
    val ctxBuilder = Context
      .newBuilder("python")
      .engine(engine)
      .environment("RAW_SETTINGS", settings.renderAsString)
      .environment("RAW_USER", environment.user.uid.toString)
      .environment("RAW_TRACE_ID", environment.user.uid.toString)
      .environment("RAW_SCOPES", environment.scopes.mkString(","))
      .allowExperimentalOptions(true)
      .allowPolyglotAccess(PolyglotAccess.ALL)
    maybeOutputStream.foreach(os => ctxBuilder.out(os))
    val ctx = ctxBuilder.build()
    ctx
  }

//  private def withTruffleContext[T](
//      environment: ProgramEnvironment,
//      f: Context => T
//  ): T = {
//    val ctx = buildTruffleContext(environment)
//    ctx.initialize("python")
//    ctx.enter()
//    try {
//      f(ctx)
//    } finally {
//      ctx.leave()
//      ctx.close()
//    }
//  }

}
