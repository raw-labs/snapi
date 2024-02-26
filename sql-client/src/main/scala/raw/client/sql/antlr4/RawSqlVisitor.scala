package raw.client.sql.antlr4

import org.antlr.v4.runtime.ParserRuleContext
import org.bitbucket.inkytonik.kiama.util.{Positions, Source}
import raw.client.api.{ErrorMessage, ErrorPosition, ErrorRange}
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

  private val assertionMessage = "Should never reach this node."

  private def addError(message: String, ctx: ParserRuleContext): Unit = {
    errors.addError(
      ErrorMessage(
        message,
        List(
          ErrorRange(
            ErrorPosition(ctx.getStart.getLine, ctx.getStart.getCharPositionInLine + 1),
            ErrorPosition(ctx.getStop.getLine, ctx.getStop.getCharPositionInLine + 1)
          )
        ),
        SqlParserErrors.ParserErrorCode
      )
    )
  }

  override def visitProg(ctx: PsqlParser.ProgContext): BaseSqlNode =
    Option(ctx).flatMap(context => Option(context.code()).map(codeCtx => visit(codeCtx))).getOrElse(SqlErrorNode())

  override def visitCode(ctx: PsqlParser.CodeContext): BaseSqlNode = Option(ctx)
    .map { context =>
      val statements = Option(context.stmt())
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

      val prog = SqlProgram(statements, comments)
      positionsWrapper.setPosition(ctx, prog)
      prog
    }
    .getOrElse(SqlErrorNode())

  override def visitComment(ctx: PsqlParser.CommentContext): BaseSqlNode = Option(ctx)
    .flatMap { context =>
      Option(context.multiline_comment()).map(visit).orElse(Option(context.singleline_comment()).map(visit))
    }
    .getOrElse(SqlErrorNode())

  override def visitSingleline_comment(ctx: PsqlParser.Singleline_commentContext): BaseSqlNode = Option(ctx)
    .map { context =>
      val singleLineComment =
        SqlSingleLineComment(visit(context.singleline_value_comment()).asInstanceOf[SqlSubComment])
      positionsWrapper.setPosition(ctx, singleLineComment)
      singleLineComment
    }
    .getOrElse(SqlErrorNode())

  override def visitSingleParamComment(ctx: PsqlParser.SingleParamCommentContext): BaseSqlNode = Option(ctx)
    .flatMap(context => Option(context.singleline_param_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleTypeComment(ctx: PsqlParser.SingleTypeCommentContext): BaseSqlNode = Option(ctx)
    .flatMap(context => Option(context.singleline_type_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleReturnComment(ctx: PsqlParser.SingleReturnCommentContext): BaseSqlNode = Option(ctx)
    .flatMap(context => Option(context.singleline_return_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleDefaultComment(ctx: PsqlParser.SingleDefaultCommentContext): BaseSqlNode = Option(ctx)
    .flatMap(context => Option(context.singleline_default_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleUnknownTypeComment(ctx: PsqlParser.SingleUnknownTypeCommentContext): BaseSqlNode = {
    Option(ctx)
      .foreach(context =>
        addError(
          "Unknown param annotation the only keywords allowed are @param, @return, @type, @default",
          context
        )
      )
    SqlErrorNode()
  }

  override def visitSingleNormalComment(ctx: PsqlParser.SingleNormalCommentContext): BaseSqlNode = Option(ctx)
    .flatMap(context => Option(context.singleline_normal_comment_value()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleline_param_comment(ctx: PsqlParser.Singleline_param_commentContext): BaseSqlNode = Option(ctx)
    .map(context => {
      if (context.SL_WORD(0) == null) {
        addError("Missing parameter name for syntax @param <name> <description>", context)
        SqlErrorNode()
      } else {
        val name = context.SL_WORD(0).getText
        val description = context.SL_WORD().asScala.drop(1).map(_.getText).mkString(" ")
        val paramDefComment = SqlParamDefComment(name, description)
        positionsWrapper.setPosition(ctx, paramDefComment)

        // check for duplication
        params.get(name) match {
          case Some(value) =>
            val existsParamDef = value.nodes.exists {
              case _: SqlParamDefComment => true
              case _ => false
            }
            if (existsParamDef) addError(s"Duplicate parameter definition for $name", context)
            else
              params.update(name, value.copy(description = Some(description), nodes = value.nodes :+ paramDefComment))
          case None =>
            params += (name -> SqlParam(name, Some(description), None, None, Vector(paramDefComment), Vector.empty))
        }

        paramDefComment
      }
    })
    .getOrElse(SqlErrorNode())

  override def visitSingleline_type_comment(ctx: PsqlParser.Singleline_type_commentContext): BaseSqlNode = Option(ctx)
    .map(context => {
      if (context.SL_WORD(0) == null) {
        addError("Missing name for syntax @type <name> <type>", context)
        SqlErrorNode()
      } else if (context.SL_WORD(1) == null) {
        addError("Missing type name for syntax @type <name> <type>", context)
        SqlErrorNode()
      } else {
        val name = context.SL_WORD(0).getText
        val tipe = context.SL_WORD(1).getText
        val paramTypeComment = SqlParamTypeComment(name, tipe)
        positionsWrapper.setPosition(ctx, paramTypeComment)

        // check for duplication
        params.get(name) match {
          case Some(value) =>
            val existsParamType = value.nodes.exists {
              case _: SqlParamTypeComment => true
              case _ => false
            }
            if (existsParamType) addError(s"Duplicate parameter type definition for $name", context)
            else params.update(name, value.copy(tipe = Some(tipe), nodes = value.nodes :+ paramTypeComment))
          case None =>
            params += (name -> SqlParam(name, None, Some(tipe), None, Vector(paramTypeComment), Vector.empty))
        }

        paramTypeComment
      }
    })
    .getOrElse(SqlErrorNode())

  override def visitSingleline_default_comment(ctx: PsqlParser.Singleline_default_commentContext): BaseSqlNode = Option(
    ctx
  )
    .map(context => {
      if (context.SL_WORD(0) == null) {
        addError("Missing name for syntax @default <name> <value>", context)
        SqlErrorNode()
      } else if (context.SL_WORD(1) == null) {
        addError("Missing default value for syntax @default <name> <value>", context)
        SqlErrorNode()
      } else {
        val name = context.SL_WORD(0).getText
        val defaultValue = context.SL_WORD(1).getText
        val paramDefaultComment = SqlParamDefaultComment(name, defaultValue)
        positionsWrapper.setPosition(ctx, paramDefaultComment)

        // check for duplication
        params.get(name) match {
          case Some(value) =>
            val existsParamType = value.nodes.exists {
              case _: SqlParamTypeComment => true
              case _ => false
            }
            if (existsParamType) addError(s"Duplicate parameter type definition for $name", context)
            else
              params.update(name, value.copy(default = Some(defaultValue), nodes = value.nodes :+ paramDefaultComment))
          case None => params += (name -> SqlParam(
              name,
              None,
              None,
              Some(defaultValue),
              Vector(paramDefaultComment),
              Vector.empty
            ))
        }

        paramDefaultComment
      }
    })
    .getOrElse(SqlErrorNode())

  override def visitSingleline_return_comment(ctx: PsqlParser.Singleline_return_commentContext): BaseSqlNode = Option(
    ctx
  )
    .map(context => {
      if (context.SL_WORD(0) == null) {
        addError("Missing description for syntax @return <description>", context)
        SqlErrorNode()
      } else {
        val description = context.SL_WORD().asScala.map(_.getText).mkString(" ")
        val paramReturnsComment = SqlParamReturnsComment(description)
        positionsWrapper.setPosition(ctx, paramReturnsComment)

        returnDescription match {
          case Some(_) => addError("Duplicate return description", context)
          case None => returnDescription = Some(description)
        }

        paramReturnsComment
      }
    })
    .getOrElse(SqlErrorNode())

  override def visitSingleline_unknown_type_comment(
      ctx: PsqlParser.Singleline_unknown_type_commentContext
  ): BaseSqlNode = throw new AssertionError(assertionMessage)

  override def visitSingleline_normal_comment_value(
      ctx: PsqlParser.Singleline_normal_comment_valueContext
  ): BaseSqlNode = Option(
    ctx
  )
    .map(context => {
      val value = context.getText
      val normalComment = SqlNormalComment(value)
      positionsWrapper.setPosition(ctx, normalComment)
      normalComment
    })
    .getOrElse(SqlErrorNode())

  override def visitMultiline_comment(ctx: PsqlParser.Multiline_commentContext): BaseSqlNode = Option(ctx)
    .map { context =>
      val multilineComments = Option(context.multiline_value_comment())
        .map(m =>
          m.asScala.map(md =>
            Option(md)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqlSubComment]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val multiLineComment = SqlMultiLineComment(multilineComments)
      positionsWrapper.setPosition(ctx, multiLineComment)
      multiLineComment
    }
    .getOrElse(SqlErrorNode())

  override def visitMultilineParamComment(ctx: PsqlParser.MultilineParamCommentContext): BaseSqlNode = Option(ctx)
    .flatMap(context => Option(context.multiline_param_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultilineTypeComment(ctx: PsqlParser.MultilineTypeCommentContext): BaseSqlNode = Option(ctx)
    .flatMap(context => Option(context.multiline_type_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultilineDefaultComment(ctx: PsqlParser.MultilineDefaultCommentContext): BaseSqlNode = Option(ctx)
    .flatMap(context => Option(context.multiline_default_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultilineReturnComment(ctx: PsqlParser.MultilineReturnCommentContext): BaseSqlNode = Option(ctx)
    .flatMap(context => Option(context.multiline_return_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultilineUnknownTypeComment(ctx: PsqlParser.MultilineUnknownTypeCommentContext): BaseSqlNode = {
    Option(ctx)
      .foreach(context =>
        addError(
          "Unknown param annotation the only keywords allowed are @param, @return, @type, @default",
          context
        )
      )
    SqlErrorNode()
  }

  override def visitMultilineNormalComment(ctx: PsqlParser.MultilineNormalCommentContext): BaseSqlNode = Option(ctx)
    .flatMap(context => Option(context.multiline_normal_comment_value()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultiline_param_comment(ctx: PsqlParser.Multiline_param_commentContext): BaseSqlNode = Option(ctx)
    .map(context => {
      if (context.ML_WORD(0) == null) {
        addError("Missing parameter name for syntax @param <name> <description>", context)
        SqlErrorNode()
      } else {
        val name = context.ML_WORD(0).getText
        val description = context.ML_WORD().asScala.drop(1).map(_.getText).mkString(" ")
        val paramDefComment = SqlParamDefComment(name, description)
        positionsWrapper.setPosition(ctx, paramDefComment)

        // check for duplication
        params.get(name) match {
          case Some(value) =>
            val existsParamDef = value.nodes.exists {
              case _: SqlParamDefComment => true
              case _ => false
            }
            if (existsParamDef) addError(s"Duplicate parameter definition for $name", context)
            else
              params.update(name, value.copy(description = Some(description), nodes = value.nodes :+ paramDefComment))
          case None =>
            params += (name -> SqlParam(name, Some(description), None, None, Vector(paramDefComment), Vector.empty))
        }

        paramDefComment
      }
    })
    .getOrElse(SqlErrorNode())

  override def visitMultiline_type_comment(ctx: PsqlParser.Multiline_type_commentContext): BaseSqlNode = Option(ctx)
    .map(context => {
      if (context.ML_WORD(0) == null) {
        addError("Missing name for syntax @type <name> <type>", context)
        SqlErrorNode()
      } else if (context.ML_WORD(1) == null) {
        addError("Missing type name for syntax @type <name> <type>", context)
        SqlErrorNode()
      } else {
        val name = context.ML_WORD(0).getText
        val tipe = context.ML_WORD(1).getText
        val paramTypeComment = SqlParamTypeComment(name, tipe)
        positionsWrapper.setPosition(ctx, paramTypeComment)

        // check for duplication
        params.get(name) match {
          case Some(value) =>
            val existsParamType = value.nodes.exists {
              case _: SqlParamTypeComment => true
              case _ => false
            }
            if (existsParamType) addError(s"Duplicate parameter type definition for $name", context)
            else params.update(name, value.copy(tipe = Some(tipe), nodes = value.nodes :+ paramTypeComment))
          case None =>
            params += (name -> SqlParam(name, None, Some(tipe), None, Vector(paramTypeComment), Vector.empty))
        }

        paramTypeComment
      }
    })
    .getOrElse(SqlErrorNode())

  override def visitMultiline_default_comment(ctx: PsqlParser.Multiline_default_commentContext): BaseSqlNode = Option(
    ctx
  )
    .map(context => {
      if (context.ML_WORD(0) == null) {
        addError("Missing name for syntax @default <name> <value>", context)
        SqlErrorNode()
      } else if (context.ML_WORD(1) == null) {
        addError("Missing default value for syntax @default <name> <value>", context)
        SqlErrorNode()
      } else {
        val name = context.ML_WORD(0).getText
        val defaultValue = context.ML_WORD(1).getText
        val paramDefaultComment = SqlParamDefaultComment(name, defaultValue)
        positionsWrapper.setPosition(ctx, paramDefaultComment)

        // check for duplication
        params.get(name) match {
          case Some(value) =>
            val existsParamType = value.nodes.exists {
              case _: SqlParamTypeComment => true
              case _ => false
            }
            if (existsParamType) addError(s"Duplicate parameter type definition for $name", context)
            else
              params.update(name, value.copy(default = Some(defaultValue), nodes = value.nodes :+ paramDefaultComment))
          case None => params += (name -> SqlParam(
              name,
              None,
              None,
              Some(defaultValue),
              Vector(paramDefaultComment),
              Vector.empty
            ))
        }

        paramDefaultComment
      }
    })
    .getOrElse(SqlErrorNode())

  override def visitMultiline_return_comment(ctx: PsqlParser.Multiline_return_commentContext): BaseSqlNode = Option(
    ctx
  )
    .map(context => {
      if (context.ML_WORD(0) == null) {
        addError("Missing description for syntax @return <description>", context)
        SqlErrorNode()
      } else {
        val description = context.ML_WORD().asScala.map(_.getText).mkString(" ")
        val paramReturnsComment = SqlParamReturnsComment(description)
        positionsWrapper.setPosition(ctx, paramReturnsComment)

        returnDescription match {
          case Some(_) => addError("Duplicate return description", context)
          case None => returnDescription = Some(description)
        }

        paramReturnsComment
      }
    })
    .getOrElse(SqlErrorNode())

  override def visitMultiline_unknown_type_comment(ctx: PsqlParser.Multiline_unknown_type_commentContext): BaseSqlNode =
    throw new AssertionError(assertionMessage)

  override def visitMultiline_normal_comment_value(ctx: PsqlParser.Multiline_normal_comment_valueContext): BaseSqlNode =
    Option(
      ctx
    )
      .map(context => {
        val value = context.getText
        val normalComment = SqlNormalComment(value)
        positionsWrapper.setPosition(ctx, normalComment)
        normalComment
      })
      .getOrElse(SqlErrorNode())

  override def visitStmtItems(ctx: PsqlParser.StmtItemsContext): BaseSqlNode = Option(ctx)
    .map { context =>
      val statements = Option(context.stmt_items())
        .map(s =>
          s.asScala.map(st =>
            Option(st)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqlStatementItem]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val stmt = SqlStatement(statements)
      positionsWrapper.setPosition(ctx, stmt)
      stmt
    }
    .getOrElse(SqlErrorNode())

  override def visitParenStmt(ctx: PsqlParser.ParenStmtContext): BaseSqlNode = Option(ctx)
    .flatMap(context => Option(context.stmt()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitParenStmtItems(ctx: PsqlParser.ParenStmtItemsContext): BaseSqlNode = Option(ctx)
    .flatMap(context => Option(context.stmt_items()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitProjStmt(ctx: PsqlParser.ProjStmtContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitLiteralStmt(ctx: PsqlParser.LiteralStmtContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitKeywordStmt(ctx: PsqlParser.KeywordStmtContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitBinaryExpStmt(ctx: PsqlParser.BinaryExpStmtContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitIdntStmt(ctx: PsqlParser.IdntStmtContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitParamStmt(ctx: PsqlParser.ParamStmtContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitWithCommaSepStmt(ctx: PsqlParser.WithCommaSepStmtContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitStar(ctx: PsqlParser.StarContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitWith_comma_sep(ctx: PsqlParser.With_comma_sepContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitKeyword(ctx: PsqlParser.KeywordContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitOperator(ctx: PsqlParser.OperatorContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitProj(ctx: PsqlParser.ProjContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitIdnt(ctx: PsqlParser.IdntContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitParam(ctx: PsqlParser.ParamContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitBinary_exp(ctx: PsqlParser.Binary_expContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitLiteral(ctx: PsqlParser.LiteralContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitString_literal(ctx: PsqlParser.String_literalContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitInteger(ctx: PsqlParser.IntegerContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitFloating_point(ctx: PsqlParser.Floating_pointContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitBoolean_literal(ctx: PsqlParser.Boolean_literalContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitTipe(ctx: PsqlParser.TipeContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitPsql_type(ctx: PsqlParser.Psql_typeContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())

  override def visitParam_with_tipe(ctx: PsqlParser.Param_with_tipeContext): BaseSqlNode = Option(ctx)
    .map { context => }
    .getOrElse(SqlErrorNode())
}
