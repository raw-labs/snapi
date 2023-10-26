package raw.client.rql2.api

import raw.client.api.{CompilerService, ErrorMessage, ErrorPosition, ProgramEnvironment}
import raw.compiler.base.source.{BaseNode, Type}
import raw.compiler.common.source.SourceProgram
import raw.utils.AuthenticatedUser

trait Rql2CompilerService extends CompilerService {

  def prettyPrint(node: BaseNode, user: AuthenticatedUser): String

  def parseType(tipe: String, user: AuthenticatedUser, internal: Boolean = false): ParseTypeResponse

  def parse(source: String, environment: ProgramEnvironment): ParseResponse

  def getType(
      source: String,
      environment: ProgramEnvironment
  ): GetTypeResponse

}

sealed trait ParseResponse
final case class ParseSuccess(program: SourceProgram) extends ParseResponse
final case class ParseFailure(error: String, pos: ErrorPosition) extends ParseResponse

sealed trait ParseTypeResponse
final case class ParseTypeSuccess(tipe: Type) extends ParseTypeResponse
final case class ParseTypeFailure(error: String) extends ParseTypeResponse

sealed trait GetTypeResponse
final case class GetTypeFailure(errors: List[ErrorMessage]) extends GetTypeResponse
final case class GetTypeSuccess(tipe: Option[Type]) extends GetTypeResponse
