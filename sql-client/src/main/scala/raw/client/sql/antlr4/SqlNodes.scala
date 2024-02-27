package raw.client.sql.antlr4

trait SqBaseNode extends Product

final case class SqlProgramNode(statements: Vector[SqBaseNode], comments: Vector[SqBaseNode]) extends SqBaseNode

// A comment can be a single line or a multi line comment
// The values that we are interested in are the subComments which are "@param", "@return", "@type", "@default"
trait SqlCommentNode extends SqBaseNode
final case class SqlSingleLineCommentNode(subComment: SqBaseNode) extends SqlCommentNode
final case class SqlMultiLineCommentNode(subComments: Vector[SqBaseNode]) extends SqlCommentNode

trait SqlSubCommentNode extends SqBaseNode
final case class SqlParamDefCommentNode(name: String, description: String) extends SqlSubCommentNode
final case class SqlParamTypeCommentNode(name: String, tipe: String) extends SqlSubCommentNode
final case class SqlParamDefaultCommentNode(name: String, value: String) extends SqlSubCommentNode
final case class SqlParamReturnsCommentNode(value: String) extends SqlSubCommentNode
final case class SqlNormalCommentNode(value: String) extends SqlSubCommentNode

final case class SqlStatementNode(statementItems: Vector[SqBaseNode]) extends SqBaseNode

trait SqlStatementItemNode extends SqBaseNode

final case class SqlProjNode(identifiers: Vector[SqBaseNode]) extends SqlStatementItemNode
final case class SqlIdentifierNode(name: String, isQuoted: Boolean) extends SqlStatementItemNode

trait SqlLiteralNode extends SqlStatementItemNode
final case class SqlStringLiteralNode(value: String) extends SqlLiteralNode
final case class SqlIntLiteralNode(value: String) extends SqlLiteralNode
final case class SqlFloatingPointLiteralNode(value: String) extends SqlLiteralNode
final case class SqlBooleanLiteralNode(value: String) extends SqlLiteralNode
final case class SqlKeywordNode(value: String) extends SqlStatementItemNode
final case class SqlOperatorNode(value: String) extends SqBaseNode
final case class SqlTypeNode(value: String) extends SqBaseNode

final case class SqlBinaryExpNode(left: SqBaseNode, op: SqBaseNode, right: SqBaseNode) extends SqlStatementItemNode

final case class SqlParamUseNode(name: String, tipe: Option[SqBaseNode]) extends SqlStatementItemNode

final case class SqlWithComaSeparatorNode(statements: Vector[SqBaseNode]) extends SqlStatementItemNode

final case class SqlErrorNode() extends SqBaseNode
