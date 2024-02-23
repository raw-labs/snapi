package raw.client.sql.antlr4

import raw.client.api.Message

case class RawSqlVisitorParseErrors() {
  private var errors: List[Message] = List.empty
  def addError(error: Message): Unit = errors = errors :+ error
  def getErrors: List[Message] = errors
}

