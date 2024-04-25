package raw.client.jinja.sql

import com.hubspot.jinjava.interpret.TemplateError.{ErrorReason, ErrorType}
import com.hubspot.jinjava.interpret.{JinjavaInterpreter, TemplateError}
import com.hubspot.jinjava.lib.fn.ELFunctionDefinition
import com.hubspot.jinjava.lib.tag.Tag
import com.hubspot.jinjava.tree.TagNode
import com.hubspot.jinjava.{Jinjava, JinjavaConfig}
import raw.client.api._
import raw.utils.RawSettings

import java.util
import scala.collection.mutable

class JinjaSqlCompilerService(maybeClassLoader: Option[ClassLoader] = None)(
    implicit protected val settings: RawSettings
) extends CompilerService {

  private val sqlCompilerService = CompilerServiceProvider("sql", maybeClassLoader)

  def dotAutoComplete(
      source: String,
      environment: raw.client.api.ProgramEnvironment,
      position: raw.client.api.Pos
  ): raw.client.api.AutoCompleteResponse = AutoCompleteResponse(Array.empty)

  private val validationConfigBuilder = JinjavaConfig
    .newBuilder()
    .withValidationMode(true)
    .withMaxOutputSize(10000)
    .withFailOnUnknownTokens(false)
    .withNestedInterpretationEnabled(false)

  private val validationConfig = validationConfigBuilder.build()

  private val executionConfig = validationConfigBuilder.withFailOnUnknownTokens(true).build()

  private val validateJinjava = new Jinjava(validationConfig)
  private val executeJinjava = new Jinjava(executionConfig)

  private object FailFunc {
    def doFail(): String = {
      "doFail"
    }
    def dontFail(): String = {
      "dontFail"
    }
  }

  private class RaiseTag(doRaise: Boolean) extends Tag {

    override def interpret(tagNode: TagNode, interpreter: JinjavaInterpreter): String = {
      if (doRaise) {
        val o = interpreter.resolveELExpression(tagNode.getHelpers, tagNode.getLineNumber, tagNode.getStartPosition)
        val error = new TemplateError(ErrorType.FATAL, ErrorReason.EXCEPTION, o.asInstanceOf[String], null, 0, 0, null)
        interpreter.addError(error)
      }
      s"<$getName>"
    }

    override def getEndTagName: String = null

    override def getName: String = "raise"
  }

  private class ParamTag() extends Tag {

    override def interpret(tagNode: TagNode, interpreter: JinjavaInterpreter): String = {
      s"-- <$getName>"
    }

    override def getEndTagName: String = null

    override def getName: String = "param"
  }

  private class TypeTag() extends Tag {

    override def interpret(tagNode: TagNode, interpreter: JinjavaInterpreter): String = {
      s"-- <$getName>"
    }

    override def getEndTagName: String = null

    override def getName: String = "type"
  }

  private class DefaultTag() extends Tag {

    override def interpret(tagNode: TagNode, interpreter: JinjavaInterpreter): String = {
      s"-- <$getName>"
    }

    override def getEndTagName: String = null

    override def getName: String = "default"
  }

  private val doFail = new ELFunctionDefinition("raw", "fail", FailFunc.getClass, "doFail")
  private val dontFail = new ELFunctionDefinition("raw", "fail", FailFunc.getClass, "dontFail")

  validateJinjava.getGlobalContext.registerFunction(dontFail)
  executeJinjava.getGlobalContext.registerFunction(doFail)

  validateJinjava.getGlobalContext.registerTag(new RaiseTag(doRaise = false))
  executeJinjava.getGlobalContext.registerTag(new RaiseTag(doRaise = true))
  for (tag <- List(new ParamTag(), new DefaultTag(), new TypeTag())) {
    validateJinjava.registerTag(tag)
    executeJinjava.registerTag(tag)
  }

  def execute(
      source: String,
      environment: raw.client.api.ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: java.io.OutputStream
  ): raw.client.api.ExecutionResponse = {
    logger.debug("execute")
    val arguments = new util.HashMap[String, Object]()
    environment.maybeArguments.foreach(_.foreach { case (k, v) => arguments.put(k, rawValueToString(v)) })
    val result = executeJinjava.renderForResult(source, arguments)
    if (result.getErrors.isEmpty) {
      val processed = result.getOutput
      sqlCompilerService.execute(processed, environment, maybeDecl, outputStream)
    } else {
      ExecutionValidationFailure(asMessages(result.getErrors))
    }
  }

  private def rawValueToString(value: RawValue) = value match {
    case RawString(v) => v
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

  def getProgramDescription(
      source: String,
      environment: raw.client.api.ProgramEnvironment
  ): raw.client.api.GetProgramDescriptionResponse = {
    val unknownVariables = mutable.Set.empty[String]
    validateJinjava.getGlobalContext.setDynamicVariableResolver(v => {
      unknownVariables.add(v); ""
    })
    val result = validateJinjava.renderForResult(source, new java.util.HashMap)
    if (result.hasErrors) {
      GetProgramDescriptionFailure(asMessages(result.getErrors))
    } else {
      GetProgramDescriptionSuccess(
        ProgramDescription(
          Map.empty,
          Some(
            DeclDescription(
              Some(
                unknownVariables.map(ParamDescription(_, Some(RawStringType(false, false)), None, None, true)).toVector
              ),
              Some(RawIterableType(RawAnyType(), false, false)),
              None
            )
          ),
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
    val result = validateJinjava.renderForResult(source, new java.util.HashMap())
    val errors = asMessages(result.getErrors)
    ValidateResponse(errors)
  }

  private def asMessages(errors: util.List[TemplateError]): List[ErrorMessage] = {
    val errorMessages = mutable.ArrayBuffer.empty[ErrorMessage]
    errors.forEach { error =>
      val message = error.getMessage
      val line = error.getLineno
      val column = error.getStartPosition
      val range = ErrorRange(ErrorPosition(line, column), ErrorPosition(line, column + 1))
      val errorMessage = ErrorMessage(message, List(range), "")
      errorMessages += errorMessage
    }
    errorMessages.toList

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

  def build(maybeClassLoader: Option[ClassLoader])(
      implicit settings: raw.utils.RawSettings
  ): raw.client.api.CompilerService = ???

  def language: Set[String] = Set("jinja-sql")

}
