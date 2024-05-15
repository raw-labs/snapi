/*
 * Copyright 2024 RAW Labs S.A.
 *
 *   Use of this software is governed by the Business Source License
 *   included in the file licenses/BSL.txt.
 *
 *   As of the Change Date specified in that file, in accordance with
 *   the Business Source License, use of this software will be governed
 *   by the Apache License, Version 2.0, included in the file
 *   licenses/APL.txt.
 */

package raw.client.jinja.sql

import org.graalvm.polyglot._
import org.graalvm.polyglot.io.IOAccess
import raw.client.api._
import raw.utils.RawSettings

class JinjaSqlCompilerService(maybeClassLoader: Option[ClassLoader] = None)(
    implicit protected val settings: RawSettings
) extends CompilerService {

  private val JINJA_ERROR = "jinjaError"

  private val (engine, _) = CompilerService.getEngine
  private val sqlCompilerService = CompilerServiceProvider("sql", maybeClassLoader)

  private val pythonCtx = {
    val graalpyExecutable = settings.getString("raw.client.jinja-sql.graalpy.executable")
    val graalpyHome = settings.getString("raw.client.jinja-sql.graalpy.home")
    val logging = settings.getBoolean("raw.client.jinja-sql.graalpy.logging")
    logger.info("pythonExecutable:" + graalpyExecutable)
    val builder = Context
      .newBuilder("python")
      .engine(engine)
      //      .environment("RAW_SETTINGS", settings.renderAsString)
      //      .environment("RAW_USER", environment.user.uid.toString)
      //      .environment("RAW_TRACE_ID", environment.user.uid.toString)
      //      .environment("RAW_SCOPES", environment.scopes.mkString(","))
      .allowExperimentalOptions(true)
      .allowPolyglotAccess(PolyglotAccess.ALL)
      .allowIO(IOAccess.ALL)
      .allowHostAccess(HostAccess.ALL)
      .allowHostClassLoading(true)
      .allowHostClassLookup(_ => true)
      .allowNativeAccess(true)
      .option("python.DontWriteBytecodeFlag", "true")
      .option("python.ForceImportSite", "true") // otherwise jinja2 isn't found
      .option("python.PythonHome", graalpyHome)
      .option("python.Executable", graalpyExecutable)
    if (logging) builder.option("python.VerboseFlag", "true").option("log.python.level", "NONE")

    builder.build()
  }

  private val bindings = {
    val helper = getClass.getResource("/python/rawjinja.py")
    logger.info(helper.toString)
    val truffleSource = Source.newBuilder("python", helper).build()
    pythonCtx.eval(truffleSource)
    pythonCtx.getBindings("python")
  }
  private val apply = bindings.getMember("apply")
  private val validate = bindings.getMember("validate")
  private val metadataComments = bindings.getMember("metadata_comments")

  def dotAutoComplete(
      source: String,
      environment: raw.client.api.ProgramEnvironment,
      position: raw.client.api.Pos
  ): raw.client.api.AutoCompleteResponse = AutoCompleteResponse(Array.empty)

  def execute(
      source: String,
      environment: raw.client.api.ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: java.io.OutputStream
  ): raw.client.api.ExecutionResponse = {
    val args = new java.util.HashMap[String, Object]
    codeArgs(source, environment) match {
      case Left(errorMessages) => ExecutionRuntimeFailure(errorMessages.map(_.message).mkString(","))
      case Right(params) =>
        for (p <- params; value <- p.defaultValue) args.put(p.idn, rawValueToPolyglot(value))
        for (userArgs <- environment.maybeArguments.toArray; (key, v) <- userArgs) args.put(key, rawValueToPolyglot(v))
        val sqlQuery: String =
          try {
            apply.execute(pythonCtx.asValue(source), args).asString
          } catch {
            case ex: PolyglotException => handlePolyglotException(ex, source, environment) match {
                case Some(errorMessage) => return ExecutionValidationFailure(List(errorMessage))
                case None => throw new CompilerServiceException(ex, environment)
              }
          }
        logger.debug(sqlQuery)
        sqlCompilerService.execute(sqlQuery, environment, None, outputStream)
    }
  }

  private def rawValueToPolyglot(value: RawValue) = value match {
    case RawShort(v) => Value.asValue(v)
    case RawInt(i) => Value.asValue(i)
    case RawLong(v) => Value.asValue(v)
    case RawFloat(v) => Value.asValue(v)
    case RawDouble(v) => Value.asValue(v)
    case RawBool(v) => Value.asValue(v)
    case RawString(s) => Value.asValue(s)
    case RawDate(d) => Value.asValue(d)
    case RawTime(v) => Value.asValue(v)
    case RawTimestamp(v) => Value.asValue(v)
    case _ => ???
  }

  def eval(
      source: String,
      tipe: raw.client.api.RawType,
      environment: raw.client.api.ProgramEnvironment
  ): raw.client.api.EvalResponse = ???

  def aiValidate(source: String, environment: raw.client.api.ProgramEnvironment): raw.client.api.ValidateResponse = ???

  def formatCode(
      source: String,
      environment: raw.client.api.ProgramEnvironment,
      maybeIndent: Option[Int],
      maybeWidth: Option[Int]
  ): raw.client.api.FormatCodeResponse = FormatCodeResponse(None)

  private def codeArgs(
      source: String,
      environment: raw.client.api.ProgramEnvironment
  ): Either[List[ErrorMessage], Vector[ParamDescription]] = {
    val unknownArgs = {
      try {
        validate.execute(pythonCtx.asValue(source))
      } catch {
        case ex: PolyglotException => handlePolyglotException(ex, source, environment) match {
            case Some(errorMessage) => return Left(List(errorMessage))
            case None => throw new CompilerServiceException(ex, environment)
          }
      }
    }
    assert(unknownArgs.hasArrayElements)

    val args = (0L until unknownArgs.getArraySize)
      .map(unknownArgs.getArrayElement)
      .map(_.asString)
      .toVector
      .map(s => s -> ParamDescription(s, Some(RawStringType(false, false)), None, None, true))
      .toMap

    val sqlArgs = {
      val comments = Value.asValue(metadataComments.execute(pythonCtx.asValue(source)))
      val metadata = (0L until comments.getArraySize)
        .map(x => comments.getArrayElement(x))
        .map(_.asString())
        .filter(x => x.strip().startsWith("@"))
        .map(s => "/*" + s + "*/")
      val sqlCode = (metadata :+ "SELECT 1").mkString("\n")
      sqlCompilerService.getProgramDescription(sqlCode, environment) match {
        case GetProgramDescriptionSuccess(programDescription) =>
          programDescription.maybeRunnable.get.params.get.map(p => p.idn -> p).toMap
        case failure: GetProgramDescriptionFailure => return Left(failure.errors)
      }
    }
    Right((args ++ sqlArgs).values.toVector)

  }

  def getProgramDescription(
      source: String,
      environment: raw.client.api.ProgramEnvironment
  ): raw.client.api.GetProgramDescriptionResponse = {
    codeArgs(source, environment) match {
      case Left(errorMessages) => GetProgramDescriptionFailure(errorMessages)
      case Right(allArgs) => GetProgramDescriptionSuccess(
          ProgramDescription(
            Map.empty,
            Some(DeclDescription(Some(allArgs), Some(RawIterableType(RawAnyType(), false, false)), None)),
            None
          )
        )
    }
  }

  def goToDefinition(
      source: String,
      environment: raw.client.api.ProgramEnvironment,
      position: raw.client.api.Pos
  ): raw.client.api.GoToDefinitionResponse = GoToDefinitionResponse(None)

  def hover(
      source: String,
      environment: raw.client.api.ProgramEnvironment,
      position: raw.client.api.Pos
  ): raw.client.api.HoverResponse = HoverResponse(None)

  def rename(
      source: String,
      environment: raw.client.api.ProgramEnvironment,
      position: raw.client.api.Pos
  ): raw.client.api.RenameResponse = RenameResponse(Array.empty)

  def validate(source: String, environment: raw.client.api.ProgramEnvironment): ValidateResponse = {
    {
      try {
        validate.execute(pythonCtx.asValue(source))
      } catch {
        case ex: PolyglotException => handlePolyglotException(ex, source, environment) match {
            case Some(errorMessage) => return ValidateResponse(List(errorMessage))
            case None => throw new CompilerServiceException(ex, environment)
          }
      }
    }
    ValidateResponse(List.empty)
  }

  def wordAutoComplete(
      source: String,
      environment: raw.client.api.ProgramEnvironment,
      prefix: String,
      position: raw.client.api.Pos
  ): raw.client.api.AutoCompleteResponse = AutoCompleteResponse(Array.empty)

  // Members declared in raw.utils.RawService

  def doStop(): Unit = {
    sqlCompilerService.stop()
  }

  private def handlePolyglotException(
      ex: PolyglotException,
      source: String,
      environment: raw.client.api.ProgramEnvironment
  ): Option[ErrorMessage] = {
    if (ex.isInterrupted || ex.getMessage.startsWith("java.lang.InterruptedException")) {
      throw new InterruptedException()
    } else if (ex.getCause.isInstanceOf[InterruptedException]) {
      throw ex.getCause
    } else if (ex.isGuestException && !ex.isInternalError) {
      val guestObject = ex.getGuestObject
      val isException = guestObject.isException
      assert(isException, s"$guestObject not an Exception!")
      val exceptionClass = guestObject.getMetaObject.getMetaSimpleName
      exceptionClass match {
        case "TemplateSyntaxError" =>
          val lineno = guestObject.getMember("lineno").asInt()
          val message = guestObject.getMember("message").asString()
          val location = ErrorPosition(lineno, 1)
          val endLocation = ErrorPosition(lineno, source.split('\n')(lineno - 1).length)
          val range = ErrorRange(location, endLocation)
          Some(ErrorMessage(message, List(range), JINJA_ERROR))
        case "TemplateAssertionError" =>
          val lineno = guestObject.getMember("lineno").asInt()
          val message = guestObject.getMember("message").asString()
          val location = ErrorPosition(lineno, 1)
          val endLocation = ErrorPosition(lineno, source.split('\n')(lineno - 1).length)
          val range = ErrorRange(location, endLocation)
          Some(ErrorMessage(message, List(range), JINJA_ERROR))
        case "TemplateRuntimeError" =>
          val message = guestObject.getMember("message").asString()
          Some(ErrorMessage(message, List.empty, JINJA_ERROR))
        case "UndefinedError" =>
          val message = guestObject.getMember("message").asString()
          Some(ErrorMessage(message, List.empty, JINJA_ERROR))
        case _ => throw new CompilerServiceException(ex, environment)
      }
    } else {
      throw ex
    }
  }

  def build(maybeClassLoader: Option[ClassLoader])(
      implicit settings: raw.utils.RawSettings
  ): raw.client.api.CompilerService = ???

  def language: Set[String] = Set("jinja-sql")

}
