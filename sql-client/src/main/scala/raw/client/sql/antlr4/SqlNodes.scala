/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.client.sql.antlr4

trait SqlBaseNode extends Product

final case class SqlProgramNode(statements: Vector[SqlBaseNode]) extends SqlBaseNode

// A comment can be a single line or a multi line comment
// The values that we are interested in are the subComments which are "@param", "@return", "@type", "@default"
trait SqlCommentNode extends SqlBaseNode
final case class SqlSingleLineCommentNode(subComment: SqlBaseNode) extends SqlCommentNode
final case class SqlMultiLineCommentNode(subComments: Vector[SqlBaseNode]) extends SqlCommentNode

trait SqlSubCommentNode extends SqlBaseNode
final case class SqlParamDefCommentNode(name: String, description: String) extends SqlSubCommentNode
final case class SqlParamTypeCommentNode(name: String, tipe: String) extends SqlSubCommentNode
final case class SqlParamDefaultCommentNode(name: String, value: String) extends SqlSubCommentNode
final case class SqlParamReturnsCommentNode(value: String) extends SqlSubCommentNode
final case class SqlNormalCommentNode(value: String) extends SqlSubCommentNode

final case class SqlStatementNode(statementItems: Vector[SqlBaseNode]) extends SqlBaseNode

trait SqlStatementItemNode extends SqlBaseNode

final case class SqlProjNode(identifiers: Vector[SqlBaseNode]) extends SqlStatementItemNode
final case class SqlIdentifierNode(name: String, isQuoted: Boolean) extends SqlStatementItemNode

trait SqlLiteralNode extends SqlStatementItemNode
final case class SqlStringLiteralNode(value: String) extends SqlLiteralNode
final case class SqlIntLiteralNode(value: String) extends SqlLiteralNode
final case class SqlFloatingPointLiteralNode(value: String) extends SqlLiteralNode
final case class SqlBooleanLiteralNode(value: String) extends SqlLiteralNode
final case class SqlKeywordNode(value: String) extends SqlStatementItemNode
final case class SqlOperatorNode(value: String) extends SqlBaseNode
final case class SqlTypeNode(value: String) extends SqlBaseNode

final case class SqlFunctionCall(name: String, arguments: Option[SqlBaseNode]) extends SqlStatementItemNode

final case class SqlUnknownNode(value: String) extends SqlStatementItemNode

final case class SqlParamUseNode(name: String) extends SqlStatementItemNode

final case class SqlWithComaSeparatorNode(statements: Vector[SqlBaseNode]) extends SqlStatementItemNode

final case class SqlTypeCastNode(value: SqlBaseNode, tipe: SqlBaseNode) extends SqlStatementItemNode

final case class SqlErrorNode() extends SqlBaseNode
