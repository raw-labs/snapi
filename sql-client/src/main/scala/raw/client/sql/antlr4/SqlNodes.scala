package raw.client.sql.antlr4

trait BaseSqlNode extends Product

final case class SqlProgram(statements: Vector[SqlStatement], comments: Vector[SqlComment]) extends BaseSqlNode

// A comment can be a single line or a multi line comment
// The values that we are interested in are the subComments which are "@param", "@return", "@type", "@default"
trait SqlComment extends BaseSqlNode
final case class SqlSingleLineComment(subComment: SqlSubComment) extends SqlComment
final case class SqlMultiLineComment(subComments: Vector[SqlSubComment]) extends SqlComment

trait SqlSubComment extends BaseSqlNode
final case class SqlParamDefComment(name: String, description: String) extends SqlSubComment
final case class SqlParamTypeComment(name: String, tipe: String) extends SqlSubComment
final case class SqlParamDefaultComment(name: String, value: String) extends SqlSubComment
final case class SqlParamReturnsComment(value: String) extends SqlSubComment
final case class SqlNormalComment(value: String) extends SqlSubComment

final case class SqlStatement(statementItems: Vector[SqlStatementItem]) extends BaseSqlNode

trait SqlStatementItem extends BaseSqlNode

final case class SqlProj(identifiers: Vector[SqlIdentifier]) extends SqlStatementItem
final case class SqlIdentifier(idn: String) extends SqlStatementItem

trait SqlLiteral extends SqlStatementItem
final case class SqlStringLiteral(value: String) extends SqlLiteral
final case class SqlIntLiteral(value: String) extends SqlLiteral
final case class SqlFloatingPointLiteral(value: String) extends SqlLiteral
final case class SqlBooleanLiteral(value: String) extends SqlLiteral
final case class SqlKeyword(value: String) extends SqlStatementItem
final case class SqlOperator(value: String) extends BaseSqlNode
final case class SqlType(value: String) extends BaseSqlNode

final case class SqlBinaryExp(left: SqlStatement, op: SqlOperator, right: SqlStatement) extends SqlStatementItem

final case class SqlParamUse(name: String, tipe: Option[SqlType]) extends SqlStatementItem

final case class SqlWithComaSeparator(statements: Vector[SqlIdentifier]) extends SqlStatementItem

final case class SqlErrorNode() extends BaseSqlNode
