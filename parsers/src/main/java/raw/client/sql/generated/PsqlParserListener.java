// Generated from PsqlParser.g4 by ANTLR 4.13.1
package raw.client.sql.generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PsqlParser}.
 */
public interface PsqlParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PsqlParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(PsqlParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(PsqlParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#code}.
	 * @param ctx the parse tree
	 */
	void enterCode(PsqlParser.CodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#code}.
	 * @param ctx the parse tree
	 */
	void exitCode(PsqlParser.CodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(PsqlParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(PsqlParser.CommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#singleline_comment}.
	 * @param ctx the parse tree
	 */
	void enterSingleline_comment(PsqlParser.Singleline_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#singleline_comment}.
	 * @param ctx the parse tree
	 */
	void exitSingleline_comment(PsqlParser.Singleline_commentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code singleParamComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 */
	void enterSingleParamComment(PsqlParser.SingleParamCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code singleParamComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 */
	void exitSingleParamComment(PsqlParser.SingleParamCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code singleTypeComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 */
	void enterSingleTypeComment(PsqlParser.SingleTypeCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code singleTypeComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 */
	void exitSingleTypeComment(PsqlParser.SingleTypeCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code singleReturnComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 */
	void enterSingleReturnComment(PsqlParser.SingleReturnCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code singleReturnComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 */
	void exitSingleReturnComment(PsqlParser.SingleReturnCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code singleDefaultComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 */
	void enterSingleDefaultComment(PsqlParser.SingleDefaultCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code singleDefaultComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 */
	void exitSingleDefaultComment(PsqlParser.SingleDefaultCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code singleUnknownTypeComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 */
	void enterSingleUnknownTypeComment(PsqlParser.SingleUnknownTypeCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code singleUnknownTypeComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 */
	void exitSingleUnknownTypeComment(PsqlParser.SingleUnknownTypeCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code singleNormalComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 */
	void enterSingleNormalComment(PsqlParser.SingleNormalCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code singleNormalComment}
	 * labeled alternative in {@link PsqlParser#singleline_value_comment}.
	 * @param ctx the parse tree
	 */
	void exitSingleNormalComment(PsqlParser.SingleNormalCommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#singleline_param_comment}.
	 * @param ctx the parse tree
	 */
	void enterSingleline_param_comment(PsqlParser.Singleline_param_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#singleline_param_comment}.
	 * @param ctx the parse tree
	 */
	void exitSingleline_param_comment(PsqlParser.Singleline_param_commentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#singleline_type_comment}.
	 * @param ctx the parse tree
	 */
	void enterSingleline_type_comment(PsqlParser.Singleline_type_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#singleline_type_comment}.
	 * @param ctx the parse tree
	 */
	void exitSingleline_type_comment(PsqlParser.Singleline_type_commentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#singleline_default_comment}.
	 * @param ctx the parse tree
	 */
	void enterSingleline_default_comment(PsqlParser.Singleline_default_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#singleline_default_comment}.
	 * @param ctx the parse tree
	 */
	void exitSingleline_default_comment(PsqlParser.Singleline_default_commentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#singleline_return_comment}.
	 * @param ctx the parse tree
	 */
	void enterSingleline_return_comment(PsqlParser.Singleline_return_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#singleline_return_comment}.
	 * @param ctx the parse tree
	 */
	void exitSingleline_return_comment(PsqlParser.Singleline_return_commentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#singleline_unknown_type_comment}.
	 * @param ctx the parse tree
	 */
	void enterSingleline_unknown_type_comment(PsqlParser.Singleline_unknown_type_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#singleline_unknown_type_comment}.
	 * @param ctx the parse tree
	 */
	void exitSingleline_unknown_type_comment(PsqlParser.Singleline_unknown_type_commentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#singleline_normal_comment_value}.
	 * @param ctx the parse tree
	 */
	void enterSingleline_normal_comment_value(PsqlParser.Singleline_normal_comment_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#singleline_normal_comment_value}.
	 * @param ctx the parse tree
	 */
	void exitSingleline_normal_comment_value(PsqlParser.Singleline_normal_comment_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#multiline_comment}.
	 * @param ctx the parse tree
	 */
	void enterMultiline_comment(PsqlParser.Multiline_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#multiline_comment}.
	 * @param ctx the parse tree
	 */
	void exitMultiline_comment(PsqlParser.Multiline_commentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multilineParamComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 */
	void enterMultilineParamComment(PsqlParser.MultilineParamCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multilineParamComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 */
	void exitMultilineParamComment(PsqlParser.MultilineParamCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multilineTypeComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 */
	void enterMultilineTypeComment(PsqlParser.MultilineTypeCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multilineTypeComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 */
	void exitMultilineTypeComment(PsqlParser.MultilineTypeCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multilineDefaultComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 */
	void enterMultilineDefaultComment(PsqlParser.MultilineDefaultCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multilineDefaultComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 */
	void exitMultilineDefaultComment(PsqlParser.MultilineDefaultCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multilineReturnComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 */
	void enterMultilineReturnComment(PsqlParser.MultilineReturnCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multilineReturnComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 */
	void exitMultilineReturnComment(PsqlParser.MultilineReturnCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multilineUnknownTypeComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 */
	void enterMultilineUnknownTypeComment(PsqlParser.MultilineUnknownTypeCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multilineUnknownTypeComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 */
	void exitMultilineUnknownTypeComment(PsqlParser.MultilineUnknownTypeCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multilineNormalComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 */
	void enterMultilineNormalComment(PsqlParser.MultilineNormalCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multilineNormalComment}
	 * labeled alternative in {@link PsqlParser#multiline_value_comment}.
	 * @param ctx the parse tree
	 */
	void exitMultilineNormalComment(PsqlParser.MultilineNormalCommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#multiline_param_comment}.
	 * @param ctx the parse tree
	 */
	void enterMultiline_param_comment(PsqlParser.Multiline_param_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#multiline_param_comment}.
	 * @param ctx the parse tree
	 */
	void exitMultiline_param_comment(PsqlParser.Multiline_param_commentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#multiline_type_comment}.
	 * @param ctx the parse tree
	 */
	void enterMultiline_type_comment(PsqlParser.Multiline_type_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#multiline_type_comment}.
	 * @param ctx the parse tree
	 */
	void exitMultiline_type_comment(PsqlParser.Multiline_type_commentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#multiline_default_comment}.
	 * @param ctx the parse tree
	 */
	void enterMultiline_default_comment(PsqlParser.Multiline_default_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#multiline_default_comment}.
	 * @param ctx the parse tree
	 */
	void exitMultiline_default_comment(PsqlParser.Multiline_default_commentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#multiline_return_comment}.
	 * @param ctx the parse tree
	 */
	void enterMultiline_return_comment(PsqlParser.Multiline_return_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#multiline_return_comment}.
	 * @param ctx the parse tree
	 */
	void exitMultiline_return_comment(PsqlParser.Multiline_return_commentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#multiline_unknown_type_comment}.
	 * @param ctx the parse tree
	 */
	void enterMultiline_unknown_type_comment(PsqlParser.Multiline_unknown_type_commentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#multiline_unknown_type_comment}.
	 * @param ctx the parse tree
	 */
	void exitMultiline_unknown_type_comment(PsqlParser.Multiline_unknown_type_commentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#multiline_normal_comment_value}.
	 * @param ctx the parse tree
	 */
	void enterMultiline_normal_comment_value(PsqlParser.Multiline_normal_comment_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#multiline_normal_comment_value}.
	 * @param ctx the parse tree
	 */
	void exitMultiline_normal_comment_value(PsqlParser.Multiline_normal_comment_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#multiline_word_or_star}.
	 * @param ctx the parse tree
	 */
	void enterMultiline_word_or_star(PsqlParser.Multiline_word_or_starContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#multiline_word_or_star}.
	 * @param ctx the parse tree
	 */
	void exitMultiline_word_or_star(PsqlParser.Multiline_word_or_starContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenStmt}
	 * labeled alternative in {@link PsqlParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterParenStmt(PsqlParser.ParenStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenStmt}
	 * labeled alternative in {@link PsqlParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitParenStmt(PsqlParser.ParenStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenStmtSqureBr}
	 * labeled alternative in {@link PsqlParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterParenStmtSqureBr(PsqlParser.ParenStmtSqureBrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenStmtSqureBr}
	 * labeled alternative in {@link PsqlParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitParenStmtSqureBr(PsqlParser.ParenStmtSqureBrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtItems}
	 * labeled alternative in {@link PsqlParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtItems(PsqlParser.StmtItemsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtItems}
	 * labeled alternative in {@link PsqlParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtItems(PsqlParser.StmtItemsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code idntStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterIdntStmt(PsqlParser.IdntStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code idntStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitIdntStmt(PsqlParser.IdntStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funCallStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterFunCallStmt(PsqlParser.FunCallStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funCallStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitFunCallStmt(PsqlParser.FunCallStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code paramStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterParamStmt(PsqlParser.ParamStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code paramStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitParamStmt(PsqlParser.ParamStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code commentStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterCommentStmt(PsqlParser.CommentStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code commentStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitCommentStmt(PsqlParser.CommentStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code keywordStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterKeywordStmt(PsqlParser.KeywordStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code keywordStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitKeywordStmt(PsqlParser.KeywordStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code operatorStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterOperatorStmt(PsqlParser.OperatorStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code operatorStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitOperatorStmt(PsqlParser.OperatorStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code commaSeparated}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterCommaSeparated(PsqlParser.CommaSeparatedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code commaSeparated}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitCommaSeparated(PsqlParser.CommaSeparatedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code typeStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterTypeStmt(PsqlParser.TypeStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code typeStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitTypeStmt(PsqlParser.TypeStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterLiteralStmt(PsqlParser.LiteralStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitLiteralStmt(PsqlParser.LiteralStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code typeCast}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterTypeCast(PsqlParser.TypeCastContext ctx);
	/**
	 * Exit a parse tree produced by the {@code typeCast}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitTypeCast(PsqlParser.TypeCastContext ctx);
	/**
	 * Enter a parse tree produced by the {@code projStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterProjStmt(PsqlParser.ProjStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code projStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitProjStmt(PsqlParser.ProjStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nestedStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterNestedStmt(PsqlParser.NestedStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nestedStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitNestedStmt(PsqlParser.NestedStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unknownStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterUnknownStmt(PsqlParser.UnknownStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unknownStmt}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitUnknownStmt(PsqlParser.UnknownStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nestedStmtSqureBr}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void enterNestedStmtSqureBr(PsqlParser.NestedStmtSqureBrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nestedStmtSqureBr}
	 * labeled alternative in {@link PsqlParser#stmt_items}.
	 * @param ctx the parse tree
	 */
	void exitNestedStmtSqureBr(PsqlParser.NestedStmtSqureBrContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#reserved_keyword}.
	 * @param ctx the parse tree
	 */
	void enterReserved_keyword(PsqlParser.Reserved_keywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#reserved_keyword}.
	 * @param ctx the parse tree
	 */
	void exitReserved_keyword(PsqlParser.Reserved_keywordContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#non_reserved_keyword}.
	 * @param ctx the parse tree
	 */
	void enterNon_reserved_keyword(PsqlParser.Non_reserved_keywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#non_reserved_keyword}.
	 * @param ctx the parse tree
	 */
	void exitNon_reserved_keyword(PsqlParser.Non_reserved_keywordContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#operator}.
	 * @param ctx the parse tree
	 */
	void enterOperator(PsqlParser.OperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#operator}.
	 * @param ctx the parse tree
	 */
	void exitOperator(PsqlParser.OperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code properProj}
	 * labeled alternative in {@link PsqlParser#proj}.
	 * @param ctx the parse tree
	 */
	void enterProperProj(PsqlParser.ProperProjContext ctx);
	/**
	 * Exit a parse tree produced by the {@code properProj}
	 * labeled alternative in {@link PsqlParser#proj}.
	 * @param ctx the parse tree
	 */
	void exitProperProj(PsqlParser.ProperProjContext ctx);
	/**
	 * Enter a parse tree produced by the {@code missingIdenProj}
	 * labeled alternative in {@link PsqlParser#proj}.
	 * @param ctx the parse tree
	 */
	void enterMissingIdenProj(PsqlParser.MissingIdenProjContext ctx);
	/**
	 * Exit a parse tree produced by the {@code missingIdenProj}
	 * labeled alternative in {@link PsqlParser#proj}.
	 * @param ctx the parse tree
	 */
	void exitMissingIdenProj(PsqlParser.MissingIdenProjContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#idnt}.
	 * @param ctx the parse tree
	 */
	void enterIdnt(PsqlParser.IdntContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#idnt}.
	 * @param ctx the parse tree
	 */
	void exitIdnt(PsqlParser.IdntContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link PsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(PsqlParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link PsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(PsqlParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code integerLiteral}
	 * labeled alternative in {@link PsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterIntegerLiteral(PsqlParser.IntegerLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code integerLiteral}
	 * labeled alternative in {@link PsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitIntegerLiteral(PsqlParser.IntegerLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code floatingPointLiteral}
	 * labeled alternative in {@link PsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterFloatingPointLiteral(PsqlParser.FloatingPointLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code floatingPointLiteral}
	 * labeled alternative in {@link PsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitFloatingPointLiteral(PsqlParser.FloatingPointLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link PsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(PsqlParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code booleanLiteral}
	 * labeled alternative in {@link PsqlParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(PsqlParser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#tipe}.
	 * @param ctx the parse tree
	 */
	void enterTipe(PsqlParser.TipeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#tipe}.
	 * @param ctx the parse tree
	 */
	void exitTipe(PsqlParser.TipeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PsqlParser#psql_type}.
	 * @param ctx the parse tree
	 */
	void enterPsql_type(PsqlParser.Psql_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PsqlParser#psql_type}.
	 * @param ctx the parse tree
	 */
	void exitPsql_type(PsqlParser.Psql_typeContext ctx);
}