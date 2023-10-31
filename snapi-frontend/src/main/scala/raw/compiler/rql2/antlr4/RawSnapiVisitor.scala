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

package raw.compiler.rql2.antlr4

import org.antlr.v4.runtime.{ParserRuleContext, Token}
import org.bitbucket.inkytonik.kiama.util.{Position, Positions, Source}
import raw.compiler.base.source.Type
import raw.compiler.common.source._
import raw.compiler.rql2.Keywords
import raw.compiler.rql2.generated.{SnapiBaseVisitor, SnapiParser}
import raw.compiler.rql2.builtin.{ListPackageBuilder, RecordPackageBuilder}
import raw.compiler.rql2.source._

import scala.collection.JavaConverters._

class RawSnapiVisitor(val positions: Positions, private val source: Source)
    extends SnapiBaseVisitor[SourceNode]
    with Keywords {

  private val assertionMessage = "This is a helper (better grammar readability)  node, should never visit it"

  private val defaultProps: Set[Rql2TypeProperty] = Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty())

  /**
   * * Sets the position of the node in the position map based on start and end of a
   * ParserRuleContext object
   *
   * @param ctx  the context to get the position from
   * @param node the node to store in the positions map
   */
  private def setPosition(ctx: ParserRuleContext, node: SourceNode): Unit = {
    positions.setStart(node, Position(ctx.getStart.getLine, ctx.getStart.getCharPositionInLine + 1, source))
    positions.setFinish(
      node,
      Position(ctx.getStop.getLine, ctx.getStop.getCharPositionInLine + ctx.getStop.getText.length + 1, source)
    )
  }

  /**
   * * Sets the position of the node in the position map based on start and end of a Token object
   *
   * @param token the token to get the position from
   * @param node  the node to store in the positions map
   */
  private def setPosition(token: Token, node: SourceNode): Unit = {
    positions.setStart(node, Position(token.getLine, token.getCharPositionInLine + 1, source))
    positions.setFinish(
      node,
      Position(token.getLine, token.getCharPositionInLine + token.getText.length + 1, source)
    )
  }

  /**
   * * Sets the position of the node in the position map based on start token and end token object
   *
   * @param startToken start of the position
   * @param endToken   end of the position
   * @param node       the node to store in the positions map
   */
  private def setPosition(startToken: Token, endToken: Token, node: SourceNode): Unit = {
    positions.setStart(node, Position(startToken.getLine, startToken.getCharPositionInLine + 1, source))
    positions.setFinish(node, Position(endToken.getLine, endToken.getCharPositionInLine + 1, source))
  }

  // An extension method to extract the identifier from a token (removes the backticks)
  implicit class IdentExtension(ctx: SnapiParser.IdentContext) {
    def getValue: String = {
      val identConst = visit(ctx).asInstanceOf[StringConst]
      identConst.value
    }

  }

  override def visitProg(ctx: SnapiParser.ProgContext): SourceNode = visit(ctx.stat)

  override def visitFunDecStat(ctx: SnapiParser.FunDecStatContext): SourceNode = {
    val methods = ctx.method_dec().asScala.map(m => visit(m).asInstanceOf[Rql2Method]).toVector
    val result = Rql2Program(methods, Option.empty)
    setPosition(ctx, result)
    result
  }

  override def visitFunDecExprStat(ctx: SnapiParser.FunDecExprStatContext): SourceNode = {
    val methods = ctx.method_dec().asScala.map(md => visit(md).asInstanceOf[Rql2Method]).toVector
    val result = Rql2Program(methods, Option(visit(ctx.expr).asInstanceOf[Exp]))
    setPosition(ctx, result)
    result
  }

  override def visitFun_proto(ctx: SnapiParser.Fun_protoContext): SourceNode = {
    val ps = ctx.fun_param.asScala.map(fp => visit(fp).asInstanceOf[FunParam]).toVector
    val funBody = FunBody(visit(ctx.expr).asInstanceOf[Exp])
    setPosition(ctx.expr, funBody)
    val result = FunProto(ps, Option(ctx.tipe()).map(visit(_).asInstanceOf[Type]), funBody)
    setPosition(ctx, result)
    result
  }

  override def visitFun_proto_lambda(ctx: SnapiParser.Fun_proto_lambdaContext): SourceNode = {
    val ps = ctx.fun_param.asScala.map(fp => visit(fp).asInstanceOf[FunParam]).toVector
    val funBody = FunBody(visit(ctx.expr).asInstanceOf[Exp])
    setPosition(ctx.expr, funBody)
    val result = FunProto(ps, Option(ctx.tipe()).map(visit(_).asInstanceOf[Type]), funBody)
    setPosition(ctx, result)
    result
  }

  override def visitMethodDec(ctx: SnapiParser.MethodDecContext): SourceNode = {
    val funProto = visit(ctx.fun_proto).asInstanceOf[FunProto]
    val idnDef = IdnDef(ctx.ident.getValue)
    setPosition(ctx.ident, idnDef)
    val result = Rql2Method(funProto, idnDef)
    setPosition(ctx, result)
    result
  }

  override def visitNormalFun(ctx: SnapiParser.NormalFunContext): SourceNode = {
    val funProto = visit(ctx.fun_proto).asInstanceOf[FunProto]
    val idnDef = IdnDef(ctx.ident.getValue)
    setPosition(ctx.ident, idnDef)
    val result: LetFun = LetFun(funProto, idnDef)
    setPosition(ctx, result)
    result
  }

  override def visitRecFun(ctx: SnapiParser.RecFunContext): SourceNode = {
    val funProto = visit(ctx.fun_proto).asInstanceOf[FunProto]
    val idnDef = IdnDef(ctx.ident.getValue)
    setPosition(ctx.ident, idnDef)
    val result = LetFunRec(idnDef, funProto)
    setPosition(ctx, result)
    result
  }

  override def visitFunParamAttr(ctx: SnapiParser.FunParamAttrContext): SourceNode = {
    val idnDef = IdnDef(ctx.attr.ident.getText)
    setPosition(ctx.attr.ident, idnDef)
    val result = FunParam(
      idnDef,
      Option(ctx.attr.tipe).map(visit(_).asInstanceOf[Type]),
      Option.empty
    )
    setPosition(ctx, result)
    result
  }

  override def visitFunParamAttrExpr(ctx: SnapiParser.FunParamAttrExprContext): SourceNode = {
    val idnDef = IdnDef(ctx.attr.ident.getText)
    setPosition(ctx.attr.ident, idnDef)
    val result = FunParam(
      idnDef,
      Option(ctx.attr.tipe).map(visit(_).asInstanceOf[Type]),
      Option(visit(ctx.expr).asInstanceOf[Exp])
    )
    setPosition(ctx, result)
    result
  }

  override def visitType_attr(ctx: SnapiParser.Type_attrContext): SourceNode = {
    val result = Rql2AttrType(ctx.ident.getValue, visit(ctx.tipe).asInstanceOf[Type])
    setPosition(ctx, result)
    result
  }

  override def visitFunArgExpr(ctx: SnapiParser.FunArgExprContext): SourceNode = {
    val result: FunAppArg = FunAppArg(visit(ctx.expr).asInstanceOf[Exp], Option.empty)
    setPosition(ctx, result)
    result
  }

  override def visitNamedFunArgExpr(ctx: SnapiParser.NamedFunArgExprContext): SourceNode = {
    val result: FunAppArg = FunAppArg(visit(ctx.expr).asInstanceOf[Exp], Option(ctx.ident.getValue))
    setPosition(ctx, result)
    result
  }

  override def visitFunAbs(ctx: SnapiParser.FunAbsContext): SourceNode = {
    val funProto = visit(ctx.fun_proto_lambda).asInstanceOf[FunProto]
    val result = FunAbs(funProto)
    setPosition(ctx, result)
    result
  }

  override def visitFunAbsUnnamed(ctx: SnapiParser.FunAbsUnnamedContext): SourceNode = {
    val idnDef = IdnDef(ctx.ident.getValue)
    setPosition(ctx.ident, idnDef)
    val funParam = FunParam(idnDef, Option.empty, Option.empty)
    setPosition(ctx.ident, funParam)
    val funBody = FunBody(visit(ctx.expr).asInstanceOf[Exp])
    setPosition(ctx.expr, funBody)
    val funProto = FunProto(Vector(funParam), Option.empty, funBody)
    setPosition(ctx, funProto)
    val result = FunAbs(funProto)
    setPosition(ctx, result)
    result
  }

  override def visitFunTypeWithParamsType(ctx: SnapiParser.FunTypeWithParamsTypeContext): SourceNode = {
    val ms = ctx.tipe.asScala.dropRight(1).map(t => visit(t).asInstanceOf[Type]).toVector

    val os = ctx.attr.asScala
      .map(a => {
        val funOptTypeParam = FunOptTypeParam(a.ident.getText, visit(a.tipe).asInstanceOf[Type])
        setPosition(a, funOptTypeParam)
        funOptTypeParam
      })
      .toVector

    val result: FunType = FunType(
      ms,
      os,
      visit(ctx.tipe.getLast).asInstanceOf[Type],
      defaultProps
    )
    setPosition(ctx, result)
    result
  }

  override def visitOrTypeType(ctx: SnapiParser.OrTypeTypeContext): SourceNode = {
    val tipes = Vector(visit(ctx.tipe).asInstanceOf[Type])
    val orType: Rql2OrType = visit(ctx.or_type).asInstanceOf[Rql2OrType]
    val combinedTypes = tipes ++ orType.tipes
    val result = Rql2OrType(combinedTypes, defaultProps)
    setPosition(ctx, result)
    result
  }

  // this one is helper, it doesn't need to set position (basically an accumulator for or_type)
  override def visitOr_type(ctx: SnapiParser.Or_typeContext): SourceNode = {
    Rql2OrType(
      Vector(visit(ctx.tipe).asInstanceOf[Type]) ++ Option(ctx.or_type())
        .map(visit(_).asInstanceOf[Rql2OrType].tipes)
        .getOrElse(Vector.empty),
      defaultProps
    )
  }

  override def visitOrTypeFunType(ctx: SnapiParser.OrTypeFunTypeContext): SourceNode = {
    val types = Vector(visit(ctx.tipe(0)).asInstanceOf[Type])
    val orType = visit(ctx.or_type).asInstanceOf[Rql2OrType]
    val combinedTypes = types ++ orType.tipes
    val domainOrType = Rql2OrType(combinedTypes, defaultProps)
    val funType = FunType(Vector(domainOrType), Vector.empty, visit(ctx.tipe(1)).asInstanceOf[Type], defaultProps)
    setPosition(ctx, funType)
    funType
  }

  override def visitUndefinedTypeType(ctx: SnapiParser.UndefinedTypeTypeContext): SourceNode = {
    val result = Rql2UndefinedType(defaultProps)
    setPosition(ctx, result)
    result
  }

  override def visitRecordTypeType(ctx: SnapiParser.RecordTypeTypeContext): SourceNode = visit(ctx.record_type)

  override def visitIterableTypeType(ctx: SnapiParser.IterableTypeTypeContext): SourceNode = visit(ctx.iterable_type)

  override def visitTypeWithParenType(ctx: SnapiParser.TypeWithParenTypeContext): SourceNode = visit(ctx.tipe)

  override def visitListTypeType(ctx: SnapiParser.ListTypeTypeContext): SourceNode = visit(ctx.list_type)

  override def visitPrimitiveTypeType(ctx: SnapiParser.PrimitiveTypeTypeContext): SourceNode =
    visit(ctx.primitive_types)

  override def visitPrimitive_types(ctx: SnapiParser.Primitive_typesContext): SourceNode = {
    val result =
      if (ctx.BOOL_TOKEN != null) Rql2BoolType(defaultProps)
      else if (ctx.STRING_TOKEN != null) Rql2StringType(defaultProps)
      else if (ctx.LOCATION_TOKEN != null) Rql2LocationType(defaultProps)
      else if (ctx.BINARY_TOKEN != null) Rql2BinaryType(defaultProps)
      else if (ctx.DATE_TOKEN != null) Rql2DateType(defaultProps)
      else if (ctx.TIME_TOKEN != null) Rql2TimeType(defaultProps)
      else if (ctx.INTERVAL_TOKEN != null) Rql2IntervalType(defaultProps)
      else if (ctx.TIMESTAMP_TOKEN != null) Rql2TimestampType(defaultProps)
      else if (ctx.BYTE_TOKEN != null) Rql2ByteType(defaultProps)
      else if (ctx.SHORT_TOKEN != null) Rql2ShortType(defaultProps)
      else if (ctx.INT_TOKEN != null) Rql2IntType(defaultProps)
      else if (ctx.LONG_TOKEN != null) Rql2LongType(defaultProps)
      else if (ctx.FLOAT_TOKEN != null) Rql2FloatType(defaultProps)
      else if (ctx.DOUBLE_TOKEN != null) Rql2DoubleType(defaultProps)
      else if (ctx.DECIMAL_TOKEN != null) Rql2DecimalType(defaultProps)
      else throw new AssertionError("Unknown primitive type")
    setPosition(ctx, result)
    result
  }

  override def visitTypeAliasType(ctx: SnapiParser.TypeAliasTypeContext): SourceNode = {
    val idnUse = IdnUse(ctx.ident.getValue)
    val result = TypeAliasType(idnUse)
    setPosition(ctx, idnUse)
    setPosition(ctx, result)
    result
  }

  override def visitFunTypeType(ctx: SnapiParser.FunTypeTypeContext): SourceNode = {
    val funType = FunType(
      Vector(visit(ctx.tipe(0)).asInstanceOf[Type]),
      Vector.empty,
      visit(ctx.tipe(1)).asInstanceOf[Type],
      defaultProps
    )
    setPosition(ctx, funType)
    funType
  }

  override def visitExprTypeExpr(ctx: SnapiParser.ExprTypeExprContext): SourceNode = visit(ctx.expr_type)

  override def visitRecord_type(ctx: SnapiParser.Record_typeContext): SourceNode = {
    val atts = ctx.type_attr.asScala.map(a => visit(a).asInstanceOf[Rql2AttrType]).toVector
    val result = Rql2RecordType(atts, defaultProps)
    setPosition(ctx, result)
    result
  }

  override def visitIterable_type(ctx: SnapiParser.Iterable_typeContext): SourceNode = {
    val result = Rql2IterableType(visit(ctx.tipe).asInstanceOf[Type], defaultProps)
    setPosition(ctx, result)
    result
  }

  override def visitList_type(ctx: SnapiParser.List_typeContext): SourceNode = {
    val result = Rql2ListType(visit(ctx.tipe).asInstanceOf[Type], defaultProps)
    setPosition(ctx, result)
    result
  }

  override def visitExpr_type(ctx: SnapiParser.Expr_typeContext): SourceNode = {
    val result = TypeExp(visit(ctx.tipe).asInstanceOf[Type])
    setPosition(ctx, result)
    result
  }

  override def visitIdentExpr(ctx: SnapiParser.IdentExprContext): SourceNode = {
    val idnUse = IdnUse(ctx.ident.getValue)
    val result = IdnExp(idnUse)
    setPosition(ctx, idnUse)
    setPosition(ctx, result)
    result
  }

  override def visitProjectionExpr(ctx: SnapiParser.ProjectionExprContext): SourceNode = {
    val proj = Proj(visit(ctx.expr).asInstanceOf[Exp], ctx.ident.getValue)
    val result =
      if (ctx.fun_ar != null) {
        // The projection with the function call
        val args =
          Option(ctx.fun_ar.fun_args).map(ar => ar.fun_arg.asScala.map(a => visit(a).asInstanceOf[FunAppArg]).toVector)
        setPosition(ctx.getStart, ctx.fun_ar.getStart, proj)
        FunApp(proj, args.getOrElse(Vector.empty))
      } else proj
    setPosition(ctx, result)
    result
  }

  override def visitLetExpr(ctx: SnapiParser.LetExprContext): SourceNode = visit(ctx.let)

  override def visitFunAbsExpr(ctx: SnapiParser.FunAbsExprContext): SourceNode = visit(ctx.fun_abs)

  override def visitFunAppExpr(ctx: SnapiParser.FunAppExprContext): SourceNode = {
    val args = ctx.fun_ar.fun_args.fun_arg.asScala.map(a => visit(a).asInstanceOf[FunAppArg]).toVector
    val result = FunApp(visit(ctx.expr).asInstanceOf[Exp], args)
    setPosition(ctx, result)
    result
  }

  override def visitIfThenElseExpr(ctx: SnapiParser.IfThenElseExprContext): SourceNode = visit(ctx.if_then_else)

  override def visitExprTypeType(ctx: SnapiParser.ExprTypeTypeContext): SourceNode = visit(ctx.expr_type)

  override def visitNumberExpr(ctx: SnapiParser.NumberExprContext): SourceNode = visit(ctx.number)

  override def visitListExpr(ctx: SnapiParser.ListExprContext): SourceNode = visit(ctx.lists)

  // Unary expressions
  override def visitNotExpr(ctx: SnapiParser.NotExprContext): SourceNode = {
    val not = Not()
    setPosition(ctx.NOT_TOKEN.getSymbol, not)
    val result = UnaryExp(not, visit(ctx.expr).asInstanceOf[Exp])
    setPosition(ctx, result)
    result
  }

  override def visitMinusUnaryExpr(ctx: SnapiParser.MinusUnaryExprContext): SourceNode = {
    val neg = Neg()
    setPosition(ctx.MINUS_TOKEN.getSymbol, neg)
    val result = UnaryExp(neg, visit(ctx.expr).asInstanceOf[Exp])
    setPosition(ctx, result)
    result
  }

  override def visitPlusUnaryExpr(ctx: SnapiParser.PlusUnaryExprContext): SourceNode = visit(ctx.expr)

  // Binary expressions
  override def visitCompareExpr(ctx: SnapiParser.CompareExprContext): SourceNode = {
    val result = BinaryExp(
      visit(ctx.compare_tokens).asInstanceOf[ComparableOp],
      visit(ctx.expr(0)).asInstanceOf[Exp],
      visit(ctx.expr(1)).asInstanceOf[Exp]
    )
    setPosition(ctx, result)
    result
  }

  override def visitCompare_tokens(ctx: SnapiParser.Compare_tokensContext): SourceNode = {
    val result =
      if (ctx.EQ_TOKEN != null) Eq()
      else if (ctx.NEQ_TOKEN != null) Neq()
      else if (ctx.GT_TOKEN != null) Gt()
      else if (ctx.GE_TOKEN != null) Ge()
      else if (ctx.LT_TOKEN != null) Lt()
      else if (ctx.LE_TOKEN != null) Le()
      else throw new AssertionError("Unknown comparable operator")
    setPosition(ctx, result)
    result
  }

  override def visitOrExpr(ctx: SnapiParser.OrExprContext): SourceNode = {
    val or = Or()
    setPosition(ctx.OR_TOKEN.getSymbol, or)
    val result = BinaryExp(or, visit(ctx.expr(0)).asInstanceOf[Exp], visit(ctx.expr(1)).asInstanceOf[Exp])
    setPosition(ctx, result)
    result
  }

  override def visitAndExpr(ctx: SnapiParser.AndExprContext): SourceNode = {
    val and = And()
    setPosition(ctx.AND_TOKEN.getSymbol, and)
    val result = BinaryExp(and, visit(ctx.expr(0)).asInstanceOf[Exp], visit(ctx.expr(1)).asInstanceOf[Exp])
    setPosition(ctx, result)
    result
  }

  override def visitMulExpr(ctx: SnapiParser.MulExprContext): SourceNode = {
    val mult = Mult()
    setPosition(ctx.MUL_TOKEN.getSymbol, mult)
    val result = BinaryExp(mult, visit(ctx.expr(0)).asInstanceOf[Exp], visit(ctx.expr(1)).asInstanceOf[Exp])
    setPosition(ctx, result)
    result
  }

  override def visitDivExpr(ctx: SnapiParser.DivExprContext): SourceNode = {
    val div: Div = Div()
    setPosition(ctx.DIV_TOKEN.getSymbol, div)
    val result = BinaryExp(div, visit(ctx.expr(0)).asInstanceOf[Exp], visit(ctx.expr(1)).asInstanceOf[Exp])
    setPosition(ctx, result)
    result
  }

  override def visitModExpr(ctx: SnapiParser.ModExprContext): SourceNode = {
    val mod: Mod = Mod()
    setPosition(ctx.MOD_TOKEN.getSymbol, mod)
    val result = BinaryExp(mod, visit(ctx.expr(0)).asInstanceOf[Exp], visit(ctx.expr(1)).asInstanceOf[Exp])
    setPosition(ctx, result)
    result
  }

  override def visitPlusExpr(ctx: SnapiParser.PlusExprContext): SourceNode = {
    val plus: Plus = Plus()
    setPosition(ctx.PLUS_TOKEN.getSymbol, plus)
    val result = BinaryExp(plus, visit(ctx.expr(0)).asInstanceOf[Exp], visit(ctx.expr(1)).asInstanceOf[Exp])
    setPosition(ctx, result)
    result
  }

  override def visitMinusExpr(ctx: SnapiParser.MinusExprContext): SourceNode = {
    val sub: Sub = Sub()
    setPosition(ctx.MINUS_TOKEN.getSymbol, sub)
    val result = BinaryExp(sub, visit(ctx.expr(0)).asInstanceOf[Exp], visit(ctx.expr(1)).asInstanceOf[Exp])
    setPosition(ctx, result)
    result
  }

  override def visitParenExpr(ctx: SnapiParser.ParenExprContext): SourceNode = visit(ctx.expr)

  override def visitRecordExpr(ctx: SnapiParser.RecordExprContext): SourceNode = visit(ctx.records)

  override def visitLet(ctx: SnapiParser.LetContext): SourceNode = {
    val decls = ctx.let_left.let_decl.asScala.map(d => visit(d).asInstanceOf[LetDecl]).toVector
    val result = Let(decls, visit(ctx.expr).asInstanceOf[Exp])
    setPosition(ctx, result)
    result
  }

  override def visitLet_decl(ctx: SnapiParser.Let_declContext): SourceNode =
    if (ctx.fun_dec == null) visit(ctx.let_bind)
    else visit(ctx.fun_dec)

  override def visitLet_bind(ctx: SnapiParser.Let_bindContext): SourceNode = {
    val tipe = Option(ctx.tipe).map(visit(_).asInstanceOf[Type])
    val idnDef = IdnDef(ctx.ident.getValue)
    setPosition(ctx.ident, idnDef)
    val result = LetBind(visit(ctx.expr).asInstanceOf[Exp], idnDef, tipe)
    setPosition(ctx, result)
    result
  }

  override def visitIf_then_else(ctx: SnapiParser.If_then_elseContext): SourceNode = {
    val result = IfThenElse(
      visit(ctx.expr(0)).asInstanceOf[Exp],
      visit(ctx.expr(1)).asInstanceOf[Exp],
      visit(ctx.expr(2)).asInstanceOf[Exp]
    )
    setPosition(ctx, result)
    result
  }

  override def visitLists(ctx: SnapiParser.ListsContext): SourceNode = visit(ctx.lists_element)

  override def visitLists_element(ctx: SnapiParser.Lists_elementContext): SourceNode = {
    val exps = ctx.expr.asScala.map(e => visit(e).asInstanceOf[Exp])
    val result: Exp = ListPackageBuilder.Build(exps: _*)
    setPosition(ctx.parent.asInstanceOf[ParserRuleContext], result)
    result
  }

  override def visitRecords(ctx: SnapiParser.RecordsContext): SourceNode = visit(ctx.record_elements)

  override def visitRecord_elements(ctx: SnapiParser.Record_elementsContext): SourceNode = {
    val tuples = ctx.record_element.asScala.zipWithIndex.map {
      case (e, idx) =>
        if (e.ident() != null) {
          (e.ident().getText, visit(e.expr()).asInstanceOf[Exp])
        } else {
          ("_" + (idx + 1), visit(e.expr()).asInstanceOf[Exp])
        }
    }.toVector
    val result: Exp = RecordPackageBuilder.Build(tuples)
    setPosition(ctx.parent.asInstanceOf[ParserRuleContext], result)
    result
  }

  // Constants
  override def visitTrippleStringExpr(ctx: SnapiParser.TrippleStringExprContext): SourceNode = {
    val result = StringConst(ctx.TRIPPLE_STRING.getText.substring(3, ctx.TRIPPLE_STRING.getText.length - 3))
    setPosition(ctx, result)
    result
  }

  override def visitStringExpr(ctx: SnapiParser.StringExprContext): SourceNode = {
    val result = StringConst(ctx.STRING.getText.substring(1, ctx.STRING.getText.length - 1))
    setPosition(ctx, result)
    result
  }

  override def visitBoolConstExpr(ctx: SnapiParser.BoolConstExprContext): SourceNode = {
    val result = BoolConst(ctx.bool_const.FALSE_TOKEN == null)
    setPosition(ctx, result)
    result
  }

  override def visitNullExpr(ctx: SnapiParser.NullExprContext): SourceNode = {
    val result = NullConst()
    setPosition(ctx, result)
    result
  }

  override def visitNumber(ctx: SnapiParser.NumberContext): SourceNode = {
    val result = {
      if (ctx.BYTE != null) ByteConst(ctx.BYTE.getText.toLowerCase.replace("b", ""))
      else if (ctx.SHORT != null) ShortConst(ctx.SHORT.getText.toLowerCase.replace("s", ""))
      else if (ctx.INTEGER != null) IntConst(ctx.INTEGER.getText.toLowerCase)
      else if (ctx.LONG != null) LongConst(ctx.LONG.getText.toLowerCase.replace("l", ""))
      else if (ctx.FLOAT != null) FloatConst(ctx.FLOAT.getText.toLowerCase.replace("f", ""))
      else if (ctx.DOUBLE != null) DoubleConst(ctx.DOUBLE.getText.toLowerCase.replace("d", ""))
      else if (ctx.DECIMAL != null) DecimalConst(ctx.DECIMAL.getText.toLowerCase.replace("q", ""))
      else throw new AssertionError("Unknown number type")
    }
    setPosition(ctx, result)
    result
  }

  override def visitIdent(ctx: SnapiParser.IdentContext): SourceNode =
    if (ctx.ESC_IDENTIFIER != null) {
      // Escaped identifier
      StringConst(ctx.getText.drop(1).dropRight(1))
    } else {
      // todo (az) throw error if reserved keyword

      //      if (isReserved(ctx.getText())) {
      //        ANTLRErrorListener listener = getErrorListenerDispatch();
      //        int line = ctx.getStart().getLine();
      //        int charPositionInLine = ctx.getStart().getCharPositionInLine();
      //        listener.syntaxError(this, null, line, charPositionInLine, "reserved keyword",
      // null);
      //      }
      StringConst(ctx.getText)
    }

  // Nodes to ignore, they are not part of the AST and should never be visited
  override def visitBool_const(ctx: SnapiParser.Bool_constContext): SourceNode =
    throw new AssertionError(assertionMessage)

  override def visitRecord_element(ctx: SnapiParser.Record_elementContext): SourceNode =
    throw new AssertionError(assertionMessage)

  override def visitLet_left(ctx: SnapiParser.Let_leftContext): SourceNode = throw new AssertionError(assertionMessage)

  override def visitAttr(ctx: SnapiParser.AttrContext): SourceNode = throw new AssertionError(assertionMessage)

  override def visitFun_ar(ctx: SnapiParser.Fun_arContext): SourceNode = throw new AssertionError(assertionMessage)

  override def visitFun_args(ctx: SnapiParser.Fun_argsContext): SourceNode = throw new AssertionError(assertionMessage)

}
