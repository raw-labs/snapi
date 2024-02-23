package raw.client.sql.antlr4

trait BaseSqlNode extends Product

final case class SqlProgram(statements: Vector[SqlStatement], comments: Vector[SqlComment]) extends BaseSqlNode

final case class SqlStatement() extends BaseSqlNode

case class SqlComment() extends BaseSqlNode
final case class SqlParamDefComment(name: String, description: String) extends SqlComment
final case class SqlParamTypeComment(name: String, tipe: String) extends SqlComment
final case class SqlParamDefaultComment(name: String, value: String) extends SqlComment
final case class SqlParamReturnsComment(name: String, value: String) extends SqlComment

final case class SqlErrorNode() extends BaseSqlNode
