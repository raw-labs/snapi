package raw.client.jinja.sql

import raw.client.api._
import raw.utils.RawSettings

class JinjaSqlCompilerService(maybeClassLoader: Option[ClassLoader] = None)(implicit protected val settings: RawSettings) extends CompilerService {
  

  /** As seen from class JinjaSqlCompilerService, the missing signatures are as follows.
   *   *  For convenience, these are usable as stub implementations.
   *  */
   // Members declared in raw.client.api.CompilerService
  

  def aiValidate(source: String, environment: raw.client.api.ProgramEnvironment): raw.client.api.ValidateResponse = ???
    

  def dotAutoComplete(source: String, environment: raw.client.api.ProgramEnvironment, position: raw.client.api.Pos): raw.client.api.AutoCompleteResponse = ???
    

  def eval(source: String, tipe: raw.client.api.RawType, environment: raw.client.api.ProgramEnvironment): raw.client.api.EvalResponse = ???
    

  def execute(source: String, environment: raw.client.api.ProgramEnvironment, maybeDecl: Option[String], outputStream: java.io.OutputStream): raw.client.api.ExecutionResponse = {
    logger.debug("execute")
    ???
  }
    

  def formatCode(source: String, environment: raw.client.api.ProgramEnvironment, maybeIndent: Option[Int], maybeWidth: Option[Int]): raw.client.api.FormatCodeResponse = ???
    

  def getProgramDescription(source: String, environment: raw.client.api.ProgramEnvironment): raw.client.api.GetProgramDescriptionResponse = ???
    

  def goToDefinition(source: String, environment: raw.client.api.ProgramEnvironment, position: raw.client.api.Pos): raw.client.api.GoToDefinitionResponse = ???
    

  def hover(source: String, environment: raw.client.api.ProgramEnvironment, position: raw.client.api.Pos): raw.client.api.HoverResponse = ???
    

  def rename(source: String, environment: raw.client.api.ProgramEnvironment, position: raw.client.api.Pos): raw.client.api.RenameResponse = ???


  def validate(source: String, environment: raw.client.api.ProgramEnvironment): raw.client.api.ValidateResponse = ???
    

  def wordAutoComplete(source: String, environment: raw.client.api.ProgramEnvironment, prefix: String, position: raw.client.api.Pos): raw.client.api.AutoCompleteResponse = ???
    
     // Members declared in raw.utils.RawService
    

  def doStop(): Unit = ???

  def build(maybeClassLoader: Option[ClassLoader])(implicit settings: raw.utils.RawSettings): raw.client.api.CompilerService = ???
    

  def language: Set[String] = ???

}
