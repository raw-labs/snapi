// Generated from PsqlParser.g4 by ANTLR 4.13.1
package raw.client.sql.generated;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PsqlParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PsqlParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PsqlParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(PsqlParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#code}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCode(PsqlParser.CodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment(PsqlParser.CommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#singleline_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleline_comment(PsqlParser.Singleline_commentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleParamComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleParamComment(PsqlParser.SingleParamCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleTypeComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleTypeComment(PsqlParser.SingleTypeCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleReturnComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleReturnComment(PsqlParser.SingleReturnCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleDefaultComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleDefaultComment(PsqlParser.SingleDefaultCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleUnknownTypeComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleUnknownTypeComment(PsqlParser.SingleUnknownTypeCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleNormalComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleNormalComment(PsqlParser.SingleNormalCommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#singleline_param_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleline_param_comment(PsqlParser.Singleline_param_commentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#singleline_type_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleline_type_comment(PsqlParser.Singleline_type_commentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#singleline_default_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleline_default_comment(PsqlParser.Singleline_default_commentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#singleline_return_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleline_return_comment(PsqlParser.Singleline_return_commentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#singleline_unknown_type_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleline_unknown_type_comment(PsqlParser.Singleline_unknown_type_commentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#singleline_normal_comment_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleline_normal_comment_value(PsqlParser.Singleline_normal_comment_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#multiline_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiline_comment(PsqlParser.Multiline_commentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multilineParamComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultilineParamComment(PsqlParser.MultilineParamCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multilineTypeComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultilineTypeComment(PsqlParser.MultilineTypeCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multilineDefaultComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultilineDefaultComment(PsqlParser.MultilineDefaultCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multilineReturnComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultilineReturnComment(PsqlParser.MultilineReturnCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multilineUnknownTypeComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultilineUnknownTypeComment(PsqlParser.MultilineUnknownTypeCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multilineNormalComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultilineNormalComment(PsqlParser.MultilineNormalCommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#multiline_param_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiline_param_comment(PsqlParser.Multiline_param_commentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#multiline_type_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiline_type_comment(PsqlParser.Multiline_type_commentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#multiline_default_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiline_default_comment(PsqlParser.Multiline_default_commentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#multiline_return_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiline_return_comment(PsqlParser.Multiline_return_commentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#multiline_unknown_type_comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiline_unknown_type_comment(PsqlParser.Multiline_unknown_type_commentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#multiline_normal_comment_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiline_normal_comment_value(PsqlParser.Multiline_normal_comment_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#multiline_word_or_star}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiline_word_or_star(PsqlParser.Multiline_word_or_starContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenStmt}
	 * labeled alternative in {@link PsqlParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenStmt(PsqlParser.ParenStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenStmtSqureBr}
	 * labeled alternative in {@link PsqlParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenStmtSqureBr(PsqlParser.ParenStmtSqureBrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtItems}
	 * labeled alternative in {@link PsqlParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtItems(PsqlParser.StmtItemsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code idntStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdntStmt(PsqlParser.IdntStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funCallStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunCallStmt(PsqlParser.FunCallStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code paramStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamStmt(PsqlParser.ParamStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code commentStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommentStmt(PsqlParser.CommentStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code keywordStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeywordStmt(PsqlParser.KeywordStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code operatorStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorStmt(PsqlParser.OperatorStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code commaSeparated}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaSeparated(PsqlParser.CommaSeparatedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code typeStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeStmt(PsqlParser.TypeStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralStmt(PsqlParser.LiteralStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code typeCast}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeCast(PsqlParser.TypeCastContext ctx);
	/**
	 * Visit a parse tree produced by the {@code projStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProjStmt(PsqlParser.ProjStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nestedStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNestedStmt(PsqlParser.NestedStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unknownStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnknownStmt(PsqlParser.UnknownStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nestedStmtSqureBr}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNestedStmtSqureBr(PsqlParser.NestedStmtSqureBrContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#reserved_keyword}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReserved_keyword(PsqlParser.Reserved_keywordContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#non_reserved_keyword}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNon_reserved_keyword(PsqlParser.Non_reserved_keywordContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperator(PsqlParser.OperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code properProj}
	 * labeled alternative in {@link PsqlParser#proj}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperProj(PsqlParser.ProperProjContext ctx);
	/**
	 * Visit a parse tree produced by the {@code missingIdenProj}
	 * labeled alternative in {@link PsqlParser#proj}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMissingIdenProj(PsqlParser.MissingIdenProjContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#idnt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdnt(PsqlParser.IdntContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link PsqlParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(PsqlParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code integerLiteral}
	 * labeled alternative in {@link PsqlParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerLiteral(PsqlParser.IntegerLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code floatingPointLiteral}
	 * labeled alternative in {@link PsqlParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatingPointLiteral(PsqlParser.FloatingPointLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link PsqlParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanLiteral(PsqlParser.BooleanLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#tipe}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTipe(PsqlParser.TipeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PsqlParser#psql_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPsql_type(PsqlParser.Psql_typeContext ctx);
}