package raw.compiler.api

import raw.utils.RawSettings

trait CompilerServiceBuilder {
  def name: String

  def build(implicit settings: RawSettings): CompilerService
}
