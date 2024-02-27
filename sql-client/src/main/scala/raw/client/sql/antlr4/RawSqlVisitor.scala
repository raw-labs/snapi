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
) extends PsqlParserBaseVisitor[SqBaseNode] {

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

  override def visitProg(ctx: PsqlParser.ProgContext): SqBaseNode =
    Option(ctx).flatMap(context => Option(context.code()).map(codeCtx => visit(codeCtx))).getOrElse(SqlErrorNode())

  override def visitCode(ctx: PsqlParser.CodeContext): SqBaseNode = Option(ctx)
    .map { context =>
      val statements = Option(context.stmt())
        .map(m =>
          m.asScala.map(md =>
            Option(md)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqBaseNode]
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
              .asInstanceOf[SqBaseNode]
          )
        )
        .getOrElse(Vector.empty)
        .toVector

      val prog = SqlProgramNode(statements, comments)
      positionsWrapper.setPosition(ctx, prog)
      prog
    }
    .getOrElse(SqlErrorNode())

  override def visitComment(ctx: PsqlParser.CommentContext): SqBaseNode = Option(ctx)
    .flatMap { context =>
      Option(context.multiline_comment()).map(visit).orElse(Option(context.singleline_comment()).map(visit))
    }
    .getOrElse(SqlErrorNode())

  override def visitSingleline_comment(ctx: PsqlParser.Singleline_commentContext): SqBaseNode = Option(ctx)
    .map { context =>
      val singleLineComment = SqlSingleLineCommentNode(visit(context.singleline_value_comment()))
      positionsWrapper.setPosition(ctx, singleLineComment)
      singleLineComment
    }
    .getOrElse(SqlErrorNode())

  override def visitSingleParamComment(ctx: PsqlParser.SingleParamCommentContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.singleline_param_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleTypeComment(ctx: PsqlParser.SingleTypeCommentContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.singleline_type_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleReturnComment(ctx: PsqlParser.SingleReturnCommentContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.singleline_return_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleDefaultComment(ctx: PsqlParser.SingleDefaultCommentContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.singleline_default_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleUnknownTypeComment(ctx: PsqlParser.SingleUnknownTypeCommentContext): SqBaseNode = {
    Option(ctx)
      .foreach(context =>
        addError(
          "Unknown param annotation the only keywords allowed are @param, @return, @type, @default",
          context
        )
      )
    SqlErrorNode()
  }

  override def visitSingleNormalComment(ctx: PsqlParser.SingleNormalCommentContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.singleline_normal_comment_value()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleline_param_comment(ctx: PsqlParser.Singleline_param_commentContext): SqBaseNode = Option(ctx)
    .map(context => {
      if (context.SL_WORD(0) == null) {
        addError("Missing parameter name for syntax @param <name> <description>", context)
        SqlErrorNode()
      } else {
        val name = context.SL_WORD(0).getText
        val description = context.SL_WORD().asScala.drop(1).map(_.getText).mkString(" ")
        val paramDefComment = SqlParamDefCommentNode(name, description)
        positionsWrapper.setPosition(ctx, paramDefComment)

        // check for duplication
        params.get(name) match {
          case Some(value) =>
            val existsParamDef = value.nodes.exists {
              case _: SqlParamDefCommentNode => true
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

  override def visitSingleline_type_comment(ctx: PsqlParser.Singleline_type_commentContext): SqBaseNode = Option(ctx)
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
        val paramTypeComment = SqlParamTypeCommentNode(name, tipe)
        positionsWrapper.setPosition(ctx, paramTypeComment)

        // check for duplication
        params.get(name) match {
          case Some(value) =>
            val existsParamType = value.nodes.exists {
              case _: SqlParamTypeCommentNode => true
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

  override def visitSingleline_default_comment(ctx: PsqlParser.Singleline_default_commentContext): SqBaseNode = Option(
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
        val paramDefaultComment = SqlParamDefaultCommentNode(name, defaultValue)
        positionsWrapper.setPosition(ctx, paramDefaultComment)

        // check for duplication
        params.get(name) match {
          case Some(value) =>
            val existsParamDefault = value.nodes.exists {
              case _: SqlParamDefaultCommentNode => true
              case _ => false
            }
            if (existsParamDefault) addError(s"Duplicate parameter default definition for $name", context)
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

  override def visitSingleline_return_comment(ctx: PsqlParser.Singleline_return_commentContext): SqBaseNode = Option(
    ctx
  )
    .map(context => {
      if (context.SL_WORD(0) == null) {
        addError("Missing description for syntax @return <description>", context)
        SqlErrorNode()
      } else {
        val description = context.SL_WORD().asScala.map(_.getText).mkString(" ")
        val paramReturnsComment = SqlParamReturnsCommentNode(description)
        positionsWrapper.setPosition(ctx, paramReturnsComment)

        returnDescription match {
          case Some(_) => addError("Duplicate return description", context)
          case None => returnDescription = Some(description)
        }

        paramReturnsComment
      }
    })
    .getOrElse(SqlErrorNode())

  override def visitSingleline_normal_comment_value(
      ctx: PsqlParser.Singleline_normal_comment_valueContext
  ): SqBaseNode = Option(
    ctx
  )
    .map(context => {
      val value = context.getText
      val normalComment = SqlNormalCommentNode(value)
      positionsWrapper.setPosition(ctx, normalComment)
      normalComment
    })
    .getOrElse(SqlErrorNode())

  override def visitMultiline_comment(ctx: PsqlParser.Multiline_commentContext): SqBaseNode = Option(ctx)
    .map { context =>
      val multilineComments = Option(context.multiline_value_comment())
        .map(m =>
          m.asScala.map(md =>
            Option(md)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqBaseNode]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val multiLineComment = SqlMultiLineCommentNode(multilineComments)
      positionsWrapper.setPosition(ctx, multiLineComment)
      multiLineComment
    }
    .getOrElse(SqlErrorNode())

  override def visitMultilineParamComment(ctx: PsqlParser.MultilineParamCommentContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.multiline_param_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultilineTypeComment(ctx: PsqlParser.MultilineTypeCommentContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.multiline_type_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultilineDefaultComment(ctx: PsqlParser.MultilineDefaultCommentContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.multiline_default_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultilineReturnComment(ctx: PsqlParser.MultilineReturnCommentContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.multiline_return_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultilineUnknownTypeComment(ctx: PsqlParser.MultilineUnknownTypeCommentContext): SqBaseNode = {
    Option(ctx)
      .foreach(context =>
        addError(
          "Unknown param annotation the only keywords allowed are @param, @return, @type, @default",
          context
        )
      )
    SqlErrorNode()
  }

  override def visitMultilineNormalComment(ctx: PsqlParser.MultilineNormalCommentContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.multiline_normal_comment_value()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultiline_param_comment(ctx: PsqlParser.Multiline_param_commentContext): SqBaseNode = Option(ctx)
    .map(context => {
      if (context.ML_WORD(0) == null) {
        addError("Missing parameter name for syntax @param <name> <description>", context)
        SqlErrorNode()
      } else {
        val name = context.ML_WORD(0).getText
        val description = context.ML_WORD().asScala.drop(1).map(_.getText).mkString(" ")
        val paramDefComment = SqlParamDefCommentNode(name, description)
        positionsWrapper.setPosition(ctx, paramDefComment)

        // check for duplication
        params.get(name) match {
          case Some(value) =>
            val existsParamDef = value.nodes.exists {
              case _: SqlParamDefCommentNode => true
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

  override def visitMultiline_type_comment(ctx: PsqlParser.Multiline_type_commentContext): SqBaseNode = Option(ctx)
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
        val paramTypeComment = SqlParamTypeCommentNode(name, tipe)
        positionsWrapper.setPosition(ctx, paramTypeComment)

        // check for duplication
        params.get(name) match {
          case Some(value) =>
            val existsParamType = value.nodes.exists {
              case _: SqlParamTypeCommentNode => true
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

  override def visitMultiline_default_comment(ctx: PsqlParser.Multiline_default_commentContext): SqBaseNode = Option(
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
        val paramDefaultComment = SqlParamDefaultCommentNode(name, defaultValue)
        positionsWrapper.setPosition(ctx, paramDefaultComment)

        // check for duplication
        params.get(name) match {
          case Some(value) =>
            val existsParamDefault = value.nodes.exists {
              case _: SqlParamDefaultCommentNode => true
              case _ => false
            }
            if (existsParamDefault) addError(s"Duplicate parameter default definition for $name", context)
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

  override def visitMultiline_return_comment(ctx: PsqlParser.Multiline_return_commentContext): SqBaseNode = Option(
    ctx
  )
    .map(context => {
      if (context.ML_WORD(0) == null) {
        addError("Missing description for syntax @return <description>", context)
        SqlErrorNode()
      } else {
        val description = context.ML_WORD().asScala.map(_.getText).mkString(" ")
        val paramReturnsComment = SqlParamReturnsCommentNode(description)
        positionsWrapper.setPosition(ctx, paramReturnsComment)

        returnDescription match {
          case Some(_) => addError("Duplicate return description", context)
          case None => returnDescription = Some(description)
        }

        paramReturnsComment
      }
    })
    .getOrElse(SqlErrorNode())

  override def visitMultiline_normal_comment_value(ctx: PsqlParser.Multiline_normal_comment_valueContext): SqBaseNode =
    Option(
      ctx
    )
      .map(context => {
        val value = context.getText
        val normalComment = SqlNormalCommentNode(value)
        positionsWrapper.setPosition(ctx, normalComment)
        normalComment
      })
      .getOrElse(SqlErrorNode())

  override def visitStmtItems(ctx: PsqlParser.StmtItemsContext): SqBaseNode = Option(ctx)
    .map { context =>
      val statements = Option(context.stmt_items())
        .map(s =>
          s.asScala.map(st =>
            Option(st)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqBaseNode]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val stmt = SqlStatementNode(statements)
      positionsWrapper.setPosition(ctx, stmt)
      stmt
    }
    .getOrElse(SqlErrorNode())

  override def visitParenStmt(ctx: PsqlParser.ParenStmtContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.stmt()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitParenStmtItems(ctx: PsqlParser.ParenStmtItemsContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.stmt_items()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitProjStmt(ctx: PsqlParser.ProjStmtContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.proj()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitLiteralStmt(ctx: PsqlParser.LiteralStmtContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.literal()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitKeywordStmt(ctx: PsqlParser.KeywordStmtContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.keyword()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitBinaryExpStmt(ctx: PsqlParser.BinaryExpStmtContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.binary_exp()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitIdntStmt(ctx: PsqlParser.IdntStmtContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.idnt()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitParamStmt(ctx: PsqlParser.ParamStmtContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.param()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitWithCommaSepStmt(ctx: PsqlParser.WithCommaSepStmtContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.with_comma_sep()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitWith_comma_sep(ctx: PsqlParser.With_comma_sepContext): SqBaseNode = Option(ctx)
    .map { context =>
      val idnts = Option(context.idnt())
        .map(i =>
          i.asScala.map(idn =>
            Option(idn)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqBaseNode]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val withComaSeparator = SqlWithComaSeparatorNode(idnts)
      positionsWrapper.setPosition(ctx, withComaSeparator)
      withComaSeparator
    }
    .getOrElse(SqlErrorNode())

  override def visitKeyword(ctx: PsqlParser.KeywordContext): SqBaseNode = Option(ctx)
    .map { context =>
      val keyword = SqlKeywordNode(context.getText)
      positionsWrapper.setPosition(ctx, keyword)
      keyword
    }
    .getOrElse(SqlErrorNode())

  override def visitOperator(ctx: PsqlParser.OperatorContext): SqBaseNode = Option(ctx)
    .map { context =>
      val operator = SqlOperatorNode(context.getText)
      positionsWrapper.setPosition(ctx, operator)
      operator
    }
    .getOrElse(SqlErrorNode())

  override def visitProj(ctx: PsqlParser.ProjContext): SqBaseNode = Option(ctx)
    .map { context =>
      val identifiers = Option(context.idnt())
        .map(i =>
          i.asScala.map(idn =>
            Option(idn)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqBaseNode]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val proj = SqlProjNode(identifiers)
      positionsWrapper.setPosition(ctx, proj)
      proj
    }
    .getOrElse(SqlErrorNode())

  override def visitIdnt(ctx: PsqlParser.IdntContext): SqBaseNode = Option(ctx)
    .map { context =>
      val isDoubleQuoted = context.DOUBLE_QUOTED_STRING() != null
      val value =
        if (isDoubleQuoted) context.DOUBLE_QUOTED_STRING().getText.replace("\"", "")
        else context.getText

      val idnt = SqlIdentifierNode(value, isDoubleQuoted)
      positionsWrapper.setPosition(ctx, idnt)
      idnt
    }
    .getOrElse(SqlErrorNode())

  override def visitParam(ctx: PsqlParser.ParamContext): SqBaseNode = Option(ctx)
    .map { context =>
      val param =
        if (context.PARAM() != null) {
          val name = context.getText.replace(":", "")
          SqlParamUseNode(name, None)
        } else {
          Option(context.param_with_tipe()).map(visit).getOrElse(SqlErrorNode())
        }

      param match {
        case use: SqlParamUseNode => params.get(use.name) match {
            case Some(value) => params.update(use.name, value.copy(occurrences = value.occurrences :+ use))
            case None => params += (use.name -> SqlParam(use.name, None, None, None, Vector.empty, Vector(use)))
          }
        case _ =>
      }
      positionsWrapper.setPosition(ctx, param)
      param
    }
    .getOrElse(SqlErrorNode())

  override def visitBinary_exp(ctx: PsqlParser.Binary_expContext): SqBaseNode = Option(ctx)
    .map { context =>
      val left = Option(context.binary_exp_elem(0)).map(visit).getOrElse(SqlErrorNode())
      val op = Option(context.operator()).map(visit).getOrElse(SqlErrorNode())
      val right = Option(context.binary_exp_elem(1)).map(visit).getOrElse(SqlErrorNode())
      val binaryExp = SqlBinaryExpNode(left, op, right)
      positionsWrapper.setPosition(ctx, binaryExp)
      binaryExp
    }
    .getOrElse(SqlErrorNode())

  override def visitIdntBinaryExpElem(ctx: PsqlParser.IdntBinaryExpElemContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.idnt()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitParamBinaryExpElem(ctx: PsqlParser.ParamBinaryExpElemContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.param()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitLiteralBinaryExpElem(ctx: PsqlParser.LiteralBinaryExpElemContext): SqBaseNode = Option(ctx)
    .flatMap(context => Option(context.literal()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitStringLiteral(ctx: PsqlParser.StringLiteralContext): SqBaseNode = Option(ctx)
    .map { context =>
      val value = context.getText
      val stringLiteral = SqlStringLiteralNode(value)
      positionsWrapper.setPosition(ctx, stringLiteral)
      stringLiteral
    }
    .getOrElse(SqlErrorNode())

  override def visitIntegerLiteral(ctx: PsqlParser.IntegerLiteralContext): SqBaseNode = Option(ctx)
    .map { context =>
      val value = context.getText
      val intLiteral = SqlIntLiteralNode(value)
      positionsWrapper.setPosition(ctx, intLiteral)
      intLiteral
    }
    .getOrElse(SqlErrorNode())

  override def visitFloatingPointLiteral(ctx: PsqlParser.FloatingPointLiteralContext): SqBaseNode = Option(ctx)
    .map { context =>
      val value = context.getText
      val floatingPointLiteral = SqlFloatingPointLiteralNode(value)
      positionsWrapper.setPosition(ctx, floatingPointLiteral)
      floatingPointLiteral
    }
    .getOrElse(SqlErrorNode())

  override def visitBooleanLiteral(ctx: PsqlParser.BooleanLiteralContext): SqBaseNode = Option(ctx)
    .map { context =>
      val value = context.getText
      val booleanLiteral = SqlBooleanLiteralNode(value)
      positionsWrapper.setPosition(ctx, booleanLiteral)
      booleanLiteral
    }
    .getOrElse(SqlErrorNode())

  override def visitTipe(ctx: PsqlParser.TipeContext): SqBaseNode = Option(ctx)
    .map { context =>
      val tipe = SqlTypeNode(context.getText)
      positionsWrapper.setPosition(ctx, tipe)
      tipe
    }
    .getOrElse(SqlErrorNode())

  override def visitParam_with_tipe(ctx: PsqlParser.Param_with_tipeContext): SqBaseNode = Option(ctx)
    .map { context =>
      val name = context.PARAM().getText.replace(":", "")
      val tipe = Option(context.tipe()).map(visit)
      val param = SqlParamUseNode(name, tipe)
      positionsWrapper.setPosition(ctx, param)
      param
    }
    .getOrElse(SqlErrorNode())

// ignored
  override def visitPsql_type(ctx: PsqlParser.Psql_typeContext): SqBaseNode = throw new AssertionError(assertionMessage)

  override def visitSingleline_unknown_type_comment(
      ctx: PsqlParser.Singleline_unknown_type_commentContext
  ): SqBaseNode = throw new AssertionError(assertionMessage)

  override def visitMultiline_unknown_type_comment(ctx: PsqlParser.Multiline_unknown_type_commentContext): SqBaseNode =
    throw new AssertionError(assertionMessage)
}
