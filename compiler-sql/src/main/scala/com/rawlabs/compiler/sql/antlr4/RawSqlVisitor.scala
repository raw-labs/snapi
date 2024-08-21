/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.compiler.sql.antlr4

import com.rawlabs.compiler.api.{ErrorMessage, ErrorPosition, ErrorRange}
import com.rawlabs.compiler.sql.generated.{PsqlParser, PsqlParserBaseVisitor}
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.misc.Interval
import org.bitbucket.inkytonik.kiama.util.{Positions, Source}

import scala.collection.JavaConverters._
import scala.collection.mutable

class RawSqlVisitor(
    val positions: Positions,
    params: mutable.Map[String, SqlParam],
    private val source: Source,
    private val errors: RawSqlVisitorParseErrors,
    var returnDescription: Option[String] = None
) extends PsqlParserBaseVisitor[SqlBaseNode] {

  private val positionsWrapper = new RawSqlPositions(positions, source)

  private val assertionMessage = "Should never reach this node."

  private var isFirstStatement = true

  private def addError(message: String, ctx: ParserRuleContext): Unit = {
    val offset = Option(ctx)
      .flatMap(context => {
        Option(context.children).flatMap(children => {
          Option(children.get(children.size() - 1)).map(last => last.getText.length)
        })
      })
      .getOrElse(0)

    errors.addError(
      ErrorMessage(
        message,
        List(
          ErrorRange(
            ErrorPosition(ctx.getStart.getLine, ctx.getStart.getCharPositionInLine + 1),
            ErrorPosition(ctx.getStop.getLine, ctx.getStop.getCharPositionInLine + offset + 1)
          )
        ),
        SqlParserErrors.ParserErrorCode
      )
    )
  }

  def sourceTextForContext(context: ParserRuleContext): Option[String] = Option(context)
    .map(context => {
      val cs = context.getStart.getTokenSource.getInputStream
      val stopIndex =
        if (context.getStop != null) context.getStop.getStopIndex
        else -1
      cs.getText(new Interval(context.getStart.getStartIndex, stopIndex))
    })

  override def visitProg(ctx: PsqlParser.ProgContext): SqlBaseNode = {
    isFirstStatement = true
    Option(ctx).flatMap(context => Option(context.code()).map(codeCtx => visit(codeCtx))).getOrElse(SqlErrorNode())
  }

  override protected def defaultResult(): SqlErrorNode = {
    SqlErrorNode();
  }

  override def visitCode(ctx: PsqlParser.CodeContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val statement = Option(context.stmt())
        .flatMap(st =>
          Option(st.get(0))
            .map(s => {
              val res = visit(s)
              positionsWrapper.setPosition(ctx, res)
              res
            })
        )
        .getOrElse(SqlErrorNode())

      // set a flag to ignore comment internals
      isFirstStatement = false

      Option(context.stmt())
        .map(m =>
          m.asScala.tail.map(md =>
            Option(md)
              .flatMap(mdContext =>
                Option(visit(mdContext)).map {
                  case stmt: SqlStatementNode => if (!stmt.statementItems.forall(_.isInstanceOf[SqlCommentNode]))
                      addError("Only one statement is allowed", md)
                }
              )
          )
        )

      val prog = SqlProgramNode(statement)
      positionsWrapper.setPosition(ctx, prog)
      prog
    }
    .getOrElse(SqlErrorNode())

  override def visitComment(ctx: PsqlParser.CommentContext): SqlBaseNode = Option(ctx)
    .flatMap { context =>
      // if in first statement we visit children
      if (isFirstStatement)
        Option(context.multiline_comment()).map(visit).orElse(Option(context.singleline_comment()).map(visit))
      // In second we ignore them
      else Some(SqlMultiLineCommentNode(Vector.empty))
    }
    .getOrElse(SqlErrorNode())

  override def visitSingleLineComment(ctx: PsqlParser.SingleLineCommentContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val singleLineComment = SqlSingleLineCommentNode(visit(context.singleline_value_comment()))
      positionsWrapper.setPosition(ctx, singleLineComment)
      singleLineComment
    }
    .getOrElse(SqlErrorNode())

  override def visitSingleLineCommentEOF(ctx: PsqlParser.SingleLineCommentEOFContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val singleLineComment = SqlSingleLineCommentNode(visit(context.singleline_value_comment()))
      positionsWrapper.setPosition(ctx.getStart, ctx.singleline_value_comment().getStop, singleLineComment)
      singleLineComment
    }
    .getOrElse(SqlErrorNode())

  override def visitSingleParamComment(ctx: PsqlParser.SingleParamCommentContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.singleline_param_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleTypeComment(ctx: PsqlParser.SingleTypeCommentContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.singleline_type_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleReturnComment(ctx: PsqlParser.SingleReturnCommentContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.singleline_return_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleDefaultComment(ctx: PsqlParser.SingleDefaultCommentContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.singleline_default_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleUnknownTypeComment(ctx: PsqlParser.SingleUnknownTypeCommentContext): SqlBaseNode = {
    Option(ctx)
      .foreach(context =>
        addError(
          "Unknown param annotation the only keywords allowed are @param, @return, @type, @default",
          context
        )
      )
    SqlErrorNode()
  }

  override def visitSingleNormalComment(ctx: PsqlParser.SingleNormalCommentContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.singleline_normal_comment_value()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitSingleline_param_comment(ctx: PsqlParser.Singleline_param_commentContext): SqlBaseNode = Option(ctx)
    .map(context => {
      if (context.SL_WORD(0) == null) {
        addError("Missing parameter name for syntax @param <name> <description>", context)
        SqlErrorNode()
      } else {
        val name = context.SL_WORD(0).getText.trim
        val nextLinesComments = " " + Option(
          context
            .singleline_normal_comment_value()
            .asScala
            .map(snc => snc.SL_WORD().asScala.map(w => w.getText).mkString(" "))
            .toVector
            .mkString(" ")
        ).getOrElse("")
        val description = (context.SL_WORD().asScala.drop(1).map(_.getText).mkString(" ") + nextLinesComments).trim
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

  override def visitSingleline_type_comment(ctx: PsqlParser.Singleline_type_commentContext): SqlBaseNode = Option(ctx)
    .map(context => {
      if (context.SL_WORD(0) == null) {
        addError("Missing name for syntax @type <name> <type>", context)
        SqlErrorNode()
      } else if (context.SL_WORD.size() <= 1) {
        addError("Missing type name for syntax @type <name> <type>", context)
        SqlErrorNode()
      } else {
        val name = context.SL_WORD(0).getText.trim
        val tipe = context.SL_WORD.asScala.tail.map(_.getText.trim).mkString(" ")
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

  override def visitSingleline_default_comment(ctx: PsqlParser.Singleline_default_commentContext): SqlBaseNode = Option(
    ctx
  )
    .map(context => {
      if (context.SL_WORD(0) == null) {
        addError("Missing name for syntax @default <name> <value>", context)
        SqlErrorNode()
      } else if (context.SL_WORD.size() <= 1) {
        addError("Missing default value for syntax @default <name> <value>", context)
        SqlErrorNode()
      } else {
        val name = context.SL_WORD(0).getText.trim
        val defaultValue = context.SL_WORD.asScala.tail.map(_.getText.trim).mkString(" ")
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

  override def visitSingleline_return_comment(ctx: PsqlParser.Singleline_return_commentContext): SqlBaseNode = Option(
    ctx
  )
    .map(context => {
      if (context.SL_WORD(0) == null) {
        addError("Missing description for syntax @return <description>", context)
        SqlErrorNode()
      } else {
        val nextLinesComments = " " + Option(
          context
            .singleline_normal_comment_value()
            .asScala
            .map(snc => snc.SL_WORD().asScala.map(w => w.getText).mkString(" "))
            .toVector
            .mkString(" ")
        ).getOrElse("")
        val description = (context.SL_WORD().asScala.map(_.getText).mkString(" ") + nextLinesComments).trim
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
  ): SqlBaseNode = Option(
    ctx
  )
    .map(context => {
      val value = context.getText
      val normalComment = SqlNormalCommentNode(value)
      positionsWrapper.setPosition(ctx, normalComment)
      normalComment
    })
    .getOrElse(SqlErrorNode())

  override def visitMultiline_comment(ctx: PsqlParser.Multiline_commentContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val multilineComments = Option(context.multiline_value_comment())
        .map(m =>
          m.asScala.map(md =>
            Option(md)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqlBaseNode]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val multiLineComment = SqlMultiLineCommentNode(multilineComments)
      positionsWrapper.setPosition(ctx, multiLineComment)
      multiLineComment
    }
    .getOrElse(SqlErrorNode())

  override def visitMultilineParamComment(ctx: PsqlParser.MultilineParamCommentContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.multiline_param_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultilineTypeComment(ctx: PsqlParser.MultilineTypeCommentContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.multiline_type_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultilineDefaultComment(ctx: PsqlParser.MultilineDefaultCommentContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.multiline_default_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultilineReturnComment(ctx: PsqlParser.MultilineReturnCommentContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.multiline_return_comment()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultilineUnknownTypeComment(ctx: PsqlParser.MultilineUnknownTypeCommentContext): SqlBaseNode = {
    Option(ctx)
      .foreach(context =>
        addError(
          "Unknown param annotation the only keywords allowed are @param, @return, @type, @default",
          context
        )
      )
    SqlErrorNode()
  }

  override def visitMultilineNormalComment(ctx: PsqlParser.MultilineNormalCommentContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.multiline_normal_comment_value()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitMultiline_param_comment(ctx: PsqlParser.Multiline_param_commentContext): SqlBaseNode = Option(ctx)
    .map(context => {
      if (context.multiline_word_or_star(0) == null) {
        addError("Missing parameter name for syntax @param <name> <description>", context)
        SqlErrorNode()
      } else {
        val name = context.multiline_word_or_star(0).getText
        val description = context.multiline_word_or_star().asScala.drop(1).map(_.getText).mkString(" ")
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

  override def visitMultiline_type_comment(ctx: PsqlParser.Multiline_type_commentContext): SqlBaseNode = Option(ctx)
    .map(context => {
      if (context.multiline_word_or_star(0) == null) {
        addError("Missing name for syntax @type <name> <type>", context)
        SqlErrorNode()
      } else if (context.multiline_word_or_star.size() <= 1) {
        addError("Missing type name for syntax @type <name> <type>", context)
        SqlErrorNode()
      } else {
        val name = context.multiline_word_or_star(0).getText.trim
        val tipe = context.multiline_word_or_star.asScala.tail.map(_.getText.trim).mkString(" ")
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

  override def visitMultiline_default_comment(ctx: PsqlParser.Multiline_default_commentContext): SqlBaseNode = Option(
    ctx
  )
    .map(context => {
      if (context.multiline_word_or_star(0) == null) {
        addError("Missing name for syntax @default <name> <value>", context)
        SqlErrorNode()
      } else if (context.multiline_word_or_star.size() <= 1) {
        addError("Missing default value for syntax @default <name> <value>", context)
        SqlErrorNode()
      } else {
        val name = context.multiline_word_or_star(0).getText
        val defaultValue = context.multiline_word_or_star.asScala.tail.map(_.getText.trim).mkString(" ")
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

  override def visitMultiline_return_comment(ctx: PsqlParser.Multiline_return_commentContext): SqlBaseNode = Option(
    ctx
  )
    .map(context => {
      if (context.multiline_word_or_star(0) == null) {
        addError("Missing description for syntax @return <description>", context)
        SqlErrorNode()
      } else {
        val description = context.multiline_word_or_star().asScala.map(_.getText).mkString(" ")
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

  override def visitMultiline_normal_comment_value(ctx: PsqlParser.Multiline_normal_comment_valueContext): SqlBaseNode =
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

  override def visitStmtItems(ctx: PsqlParser.StmtItemsContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val statements = Option(context.stmt_items())
        .map(s =>
          s.asScala.map(st =>
            Option(st)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqlBaseNode]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val last = ctx.stmt_items().getLast.getStop
      val first = ctx.getStart
      val stmt = SqlStatementNode(statements)
      positionsWrapper.setPosition(first, last, stmt)
      stmt
    }
    .getOrElse(SqlErrorNode())

  override def visitParenStmt(ctx: PsqlParser.ParenStmtContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.stmt()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitParenStmtSqureBr(ctx: PsqlParser.ParenStmtSqureBrContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.stmt()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitNestedStmtSqureBr(ctx: PsqlParser.NestedStmtSqureBrContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.stmt()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitFunCallStmt(ctx: PsqlParser.FunCallStmtContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val name = Option(context.idnt())
        .map(i => i.getText)
        .getOrElse(context.reserved_keyword().getText)
      val arguments = Option(context.stmt_items())
        .map(visit)
      val funCall = SqlFunctionCall(name, arguments)
      positionsWrapper.setPosition(ctx, funCall)
      funCall
    }
    .getOrElse(SqlErrorNode())

  override def visitUnknownStmt(ctx: PsqlParser.UnknownStmtContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val unknown = SqlUnknownNode(context.getText)
      positionsWrapper.setPosition(ctx, unknown)
      unknown
    }
    .getOrElse(SqlErrorNode())

  override def visitCommaSeparated(ctx: PsqlParser.CommaSeparatedContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val statements = Option(context.stmt_items())
        .map(s =>
          s.asScala.map(st =>
            Option(st)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqlBaseNode]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val withComaSeparator = SqlWithComaSeparatorNode(statements)
      positionsWrapper.setPosition(ctx, withComaSeparator)
      withComaSeparator
    }
    .getOrElse(SqlErrorNode())

  override def visitTypeCast(ctx: PsqlParser.TypeCastContext): SqlBaseNode = Option(ctx)
    .flatMap { context =>
      Option(context.stmt_items()).map { items =>
        val stmt = Option(items.get(0)).map(visit).getOrElse(SqlErrorNode())
        val tipe = Option(items.get(1)).map(visit).getOrElse(SqlErrorNode())
        val typeCast = SqlTypeCastNode(stmt, tipe)
        positionsWrapper.setPosition(ctx, typeCast)
        typeCast
      }
    }
    .getOrElse(SqlErrorNode())

  override def visitNestedStmt(ctx: PsqlParser.NestedStmtContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.stmt()).map(visit))
    .getOrElse(SqlErrorNode())

  //  override def visitParenStmtItems(ctx: PsqlParser.ParenStmtItemsContext): SqBaseNode = Option(ctx)
//    .flatMap(context => Option(context.stmt_items()).map(visit))
//    .getOrElse(SqlErrorNode())

  override def visitProjStmt(ctx: PsqlParser.ProjStmtContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.proj()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitLiteralStmt(ctx: PsqlParser.LiteralStmtContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.literal()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitKeywordStmt(ctx: PsqlParser.KeywordStmtContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.reserved_keyword()).map(visit))
    .getOrElse(SqlErrorNode())

//  override def visitBinaryExpStmt(ctx: PsqlParser.BinaryExpStmtContext): SqBaseNode = Option(ctx)
//    .flatMap(context => Option(context.binary_exp()).map(visit))
//    .getOrElse(SqlErrorNode())

  override def visitIdntStmt(ctx: PsqlParser.IdntStmtContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.idnt()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitParamStmt(ctx: PsqlParser.ParamStmtContext): SqlBaseNode = Option(ctx)
    .map(context => {
      val name = context.getText.replace(":", "")
      val param = SqlParamUseNode(name)

      param match {
        case use: SqlParamUseNode => params.get(use.name) match {
            case Some(value) => params.update(use.name, value.copy(occurrences = value.occurrences :+ use))
            case None => params += (use.name -> SqlParam(use.name, None, None, None, Vector.empty, Vector(use)))
          }
        case _ =>
      }
      positionsWrapper.setPosition(ctx, param)
      param
    })
    .getOrElse(SqlErrorNode())

//  override def visitKeyword(ctx: PsqlParser.KeywordContext): SqlBaseNode = Option(ctx)
//    .map { context =>
//      val keyword = SqlKeywordNode(context.getText)
//      positionsWrapper.setPosition(ctx, keyword)
//      keyword
//    }
//    .getOrElse(SqlErrorNode())

  override def visitReserved_keyword(ctx: PsqlParser.Reserved_keywordContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val keyword = SqlKeywordNode(context.getText)
      positionsWrapper.setPosition(ctx, keyword)
      keyword
    }
    .getOrElse(SqlErrorNode())

  override def visitOperator(ctx: PsqlParser.OperatorContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val operator = SqlOperatorNode(context.getText)
      positionsWrapper.setPosition(ctx, operator)
      operator
    }
    .getOrElse(SqlErrorNode())

  override def visitOperatorStmt(ctx: PsqlParser.OperatorStmtContext): SqlBaseNode = Option(ctx)
    .flatMap(context => Option(context.operator()).map(visit))
    .getOrElse(SqlErrorNode())

  override def visitProperProj(ctx: PsqlParser.ProperProjContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val identifiers = Option(context.idnt())
        .map(i =>
          i.asScala.map(idn =>
            Option(idn)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SqlErrorNode())
              .asInstanceOf[SqlBaseNode]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val proj = SqlProjNode(identifiers)
      positionsWrapper.setPosition(ctx, proj)
      proj
    }
    .getOrElse(SqlErrorNode())

  override def visitMissingIdenProj(ctx: PsqlParser.MissingIdenProjContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val identifier = Option(context.idnt())
        .map(visit)
        .getOrElse(SqlErrorNode())
      val proj = SqlProjNode(Vector(identifier))
      positionsWrapper.setPosition(ctx, proj)
      proj
    }
    .getOrElse(SqlErrorNode())

  override def visitIdnt(ctx: PsqlParser.IdntContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val isDoubleQuoted = context.STRING_IDENTIFIER_START() != null
      val value =
        if (isDoubleQuoted) {
          val text = context.getText
          val withoutLast =
            if (!text.endsWith("\"")) {
              addError("Missing closing \"", context)
              text
            } else text.dropRight(1)
          withoutLast.drop(1)
        } else context.getText
      val idnt = SqlIdentifierNode(value, isDoubleQuoted)
      positionsWrapper.setPosition(ctx, idnt)
      idnt
    }
    .getOrElse(SqlErrorNode())

  override def visitStringLiteral(ctx: PsqlParser.StringLiteralContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val value = context.getText
      val stringLiteral = SqlStringLiteralNode(value)
      positionsWrapper.setPosition(ctx, stringLiteral)
      stringLiteral
    }
    .getOrElse(SqlErrorNode())

  override def visitIntegerLiteral(ctx: PsqlParser.IntegerLiteralContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val value = context.getText
      val intLiteral = SqlIntLiteralNode(value)
      positionsWrapper.setPosition(ctx, intLiteral)
      intLiteral
    }
    .getOrElse(SqlErrorNode())

  override def visitFloatingPointLiteral(ctx: PsqlParser.FloatingPointLiteralContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val value = context.getText
      val floatingPointLiteral = SqlFloatingPointLiteralNode(value)
      positionsWrapper.setPosition(ctx, floatingPointLiteral)
      floatingPointLiteral
    }
    .getOrElse(SqlErrorNode())

  override def visitBooleanLiteral(ctx: PsqlParser.BooleanLiteralContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val value = context.getText
      val booleanLiteral = SqlBooleanLiteralNode(value)
      positionsWrapper.setPosition(ctx, booleanLiteral)
      booleanLiteral
    }
    .getOrElse(SqlErrorNode())

  override def visitTipe(ctx: PsqlParser.TipeContext): SqlBaseNode = Option(ctx)
    .map { context =>
      val tipe = SqlTypeNode(context.getText)
      positionsWrapper.setPosition(ctx, tipe)
      tipe
    }
    .getOrElse(SqlErrorNode())

  override def visitCommentStmt(ctx: PsqlParser.CommentStmtContext): SqlBaseNode = super.visitCommentStmt(ctx)

  override def visitTypeStmt(ctx: PsqlParser.TypeStmtContext): SqlBaseNode = super.visitTypeStmt(ctx)

  // ignored
  override def visitPsql_type(ctx: PsqlParser.Psql_typeContext): SqlBaseNode =
    throw new AssertionError(assertionMessage)

  override def visitSingleline_unknown_type_comment(
      ctx: PsqlParser.Singleline_unknown_type_commentContext
  ): SqlBaseNode = throw new AssertionError(assertionMessage)

  override def visitMultiline_unknown_type_comment(ctx: PsqlParser.Multiline_unknown_type_commentContext): SqlBaseNode =
    throw new AssertionError(assertionMessage)

  override def visitNon_reserved_keyword(ctx: PsqlParser.Non_reserved_keywordContext): SqlBaseNode =
    throw new AssertionError(assertionMessage)

  override def visitMultiline_word_or_star(ctx: PsqlParser.Multiline_word_or_starContext): SqlBaseNode =
    throw new AssertionError(assertionMessage)
}
