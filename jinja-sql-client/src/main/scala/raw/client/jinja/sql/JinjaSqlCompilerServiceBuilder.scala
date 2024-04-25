package raw.client.jinja.sql

import raw.client.api.{CompilerService, CompilerServiceBuilder}
import raw.utils.RawSettings

class JinjaSqlCompilerServiceBuilder extends CompilerServiceBuilder {
  override def language: Set[String] = Set("jinja-sql")

  override def build(maybeClassLoader: Option[ClassLoader])(implicit settings: RawSettings): CompilerService =
    new JinjaSqlCompilerService(maybeClassLoader)

}
