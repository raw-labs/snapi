package raw.client.jinja.sql

import raw.client.api._


class JinjaSqlCompilerServiceBuilder extends CompilerServiceBuilder {

  def build(maybeClassLoader: Option[ClassLoader])(implicit settings: raw.utils.RawSettings): raw.client.api.CompilerService = ???

  def language: Set[String] = ???
}
