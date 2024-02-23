package raw.client.sql.antlr4

import org.bitbucket.inkytonik.kiama.util.{Positions, Source}
import raw.client.sql.generated.{PsqlParser, PsqlParserBaseVisitor}

import scala.collection.JavaConverters._

import scala.collection.mutable

class RawSqlVisitor(
    positions: Positions,
    params: mutable.Map[String, SqlParam],
    private val source: Source,
    private val errors: RawSqlVisitorParseErrors,
    var returnDescription: Option[String] = None
) extends PsqlParserBaseVisitor[BaseSqlNode] {

  private val positionsWrapper = new RawSqlPositions(positions, source)

  private val assertionMessage = "This is a helper (better grammar readability)  node, should never visit it"

  override def visitProg(ctx: PsqlParser.ProgContext): BaseSqlNode =
    Option(ctx).flatMap(context => Option(context.code()).map(codeCtx => visit(codeCtx))).getOrElse(SqlErrorNode)

  override def visitCode(ctx: PsqlParser.CodeContext): BaseSqlNode = Option(ctx)
    .map { context =>
      val statments = Option(context.stmt())
        .map(m =>
          m.asScala.map(md =>
            Option(md)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqlStatement]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val comments = Option(context.comment())
        .map(m =>
          m.asScala.map(md =>
            Option(md)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqlComment]
          )
        )
        .getOrElse(Vector.empty)
        .toVector

      SqlProgram(statments, comments)

    }
    .getOrElse(SqlErrorNode())

  override def visitComment(ctx: PsqlParser.CommentContext): BaseSqlNode = super.visitComment(ctx)

  override def visitMultiline_comment(ctx: PsqlParser.Multiline_commentContext): BaseSqlNode =
    super.visitMultiline_comment(ctx)

  override def visitMultiline_value_comment(ctx: PsqlParser.Multiline_value_commentContext): BaseSqlNode =
    super.visitMultiline_value_comment(ctx)

  override def visitSingleline_comment(ctx: PsqlParser.Singleline_commentContext): BaseSqlNode =
    super.visitSingleline_comment(ctx)

  override def visitParamComment(ctx: PsqlParser.ParamCommentContext): BaseSqlNode = super.visitParamComment(ctx)

  override def visitTypeComment(ctx: PsqlParser.TypeCommentContext): BaseSqlNode = super.visitTypeComment(ctx)

  override def visitReturnComment(ctx: PsqlParser.ReturnCommentContext): BaseSqlNode = super.visitReturnComment(ctx)

  override def visitDefaultComment(ctx: PsqlParser.DefaultCommentContext): BaseSqlNode = super.visitDefaultComment(ctx)

  override def visitUnknownTypeComment(ctx: PsqlParser.UnknownTypeCommentContext): BaseSqlNode =
    super.visitUnknownTypeComment(ctx)

  override def visitNormalComment(ctx: PsqlParser.NormalCommentContext): BaseSqlNode = super.visitNormalComment(ctx)

  override def visitSingleline_param_comment(ctx: PsqlParser.Singleline_param_commentContext): BaseSqlNode =
    super.visitSingleline_param_comment(ctx)

  override def visitSingleline_type_comment(ctx: PsqlParser.Singleline_type_commentContext): BaseSqlNode =
    super.visitSingleline_type_comment(ctx)

  override def visitSingleline_default_comment(ctx: PsqlParser.Singleline_default_commentContext): BaseSqlNode =
    super.visitSingleline_default_comment(ctx)

  override def visitSingleline_return_comment(ctx: PsqlParser.Singleline_return_commentContext): BaseSqlNode =
    super.visitSingleline_return_comment(ctx)

  override def visitSingleline_unknown_type_comment(
      ctx: PsqlParser.Singleline_unknown_type_commentContext
  ): BaseSqlNode = super.visitSingleline_unknown_type_comment(ctx)

  override def visitSingleline_normal_comment_value(
      ctx: PsqlParser.Singleline_normal_comment_valueContext
  ): BaseSqlNode = super.visitSingleline_normal_comment_value(ctx)

  override def visitMultiline_param_comment(ctx: PsqlParser.Multiline_param_commentContext): BaseSqlNode =
    super.visitMultiline_param_comment(ctx)

  override def visitMultiline_type_comment(ctx: PsqlParser.Multiline_type_commentContext): BaseSqlNode =
    super.visitMultiline_type_comment(ctx)

  override def visitMultiline_default_comment(ctx: PsqlParser.Multiline_default_commentContext): BaseSqlNode =
    super.visitMultiline_default_comment(ctx)

  override def visitMultiline_return_comment(ctx: PsqlParser.Multiline_return_commentContext): BaseSqlNode =
    super.visitMultiline_return_comment(ctx)

  override def visitMultiline_unknown_type_comment(ctx: PsqlParser.Multiline_unknown_type_commentContext): BaseSqlNode =
    super.visitMultiline_unknown_type_comment(ctx)

  override def visitMultiline_normal_comment_value(ctx: PsqlParser.Multiline_normal_comment_valueContext): BaseSqlNode =
    super.visitMultiline_normal_comment_value(ctx)

  override def visitStmt(ctx: PsqlParser.StmtContext): BaseSqlNode = super.visitStmt(ctx)

  override def visitProjStmt(ctx: PsqlParser.ProjStmtContext): BaseSqlNode = super.visitProjStmt(ctx)

  override def visitLiteralStmt(ctx: PsqlParser.LiteralStmtContext): BaseSqlNode = super.visitLiteralStmt(ctx)

  override def visitKeywordStmt(ctx: PsqlParser.KeywordStmtContext): BaseSqlNode = super.visitKeywordStmt(ctx)

  override def visitBinaryExpStmt(ctx: PsqlParser.BinaryExpStmtContext): BaseSqlNode = super.visitBinaryExpStmt(ctx)

  override def visitIdntStmt(ctx: PsqlParser.IdntStmtContext): BaseSqlNode = super.visitIdntStmt(ctx)

  override def visitParamStmt(ctx: PsqlParser.ParamStmtContext): BaseSqlNode = super.visitParamStmt(ctx)

  override def visitWithCommaSepStmt(ctx: PsqlParser.WithCommaSepStmtContext): BaseSqlNode =
    super.visitWithCommaSepStmt(ctx)

  override def visitAbstractStmtItem(ctx: PsqlParser.AbstractStmtItemContext): BaseSqlNode =
    super.visitAbstractStmtItem(ctx)

  override def visitWith_comma_sep(ctx: PsqlParser.With_comma_sepContext): BaseSqlNode = super.visitWith_comma_sep(ctx)

  override def visitAbstract_stmt_item(ctx: PsqlParser.Abstract_stmt_itemContext): BaseSqlNode =
    super.visitAbstract_stmt_item(ctx)

  override def visitKeyword(ctx: PsqlParser.KeywordContext): BaseSqlNode = super.visitKeyword(ctx)

  override def visitOperator(ctx: PsqlParser.OperatorContext): BaseSqlNode = super.visitOperator(ctx)

  override def visitProj(ctx: PsqlParser.ProjContext): BaseSqlNode = super.visitProj(ctx)

  override def visitIdnt(ctx: PsqlParser.IdntContext): BaseSqlNode = super.visitIdnt(ctx)

  override def visitParam(ctx: PsqlParser.ParamContext): BaseSqlNode = super.visitParam(ctx)

  override def visitBinary_exp(ctx: PsqlParser.Binary_expContext): BaseSqlNode = super.visitBinary_exp(ctx)

  override def visitLiteral(ctx: PsqlParser.LiteralContext): BaseSqlNode = super.visitLiteral(ctx)

  override def visitString_literal(ctx: PsqlParser.String_literalContext): BaseSqlNode = super.visitString_literal(ctx)

  override def visitInteger(ctx: PsqlParser.IntegerContext): BaseSqlNode = super.visitInteger(ctx)

  override def visitFloating_point(ctx: PsqlParser.Floating_pointContext): BaseSqlNode = super.visitFloating_point(ctx)

  override def visitBoolean_literal(ctx: PsqlParser.Boolean_literalContext): BaseSqlNode =
    super.visitBoolean_literal(ctx)

  override def visitTipe(ctx: PsqlParser.TipeContext): BaseSqlNode = super.visitTipe(ctx)

  override def visitParam_with_tipe(ctx: PsqlParser.Param_with_tipeContext): BaseSqlNode =
    super.visitParam_with_tipe(ctx)
}
