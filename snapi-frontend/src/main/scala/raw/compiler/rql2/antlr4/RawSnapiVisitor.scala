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
import raw.compiler.rql2.builtin.{ListPackageBuilder, RecordPackageBuilder}
import raw.compiler.rql2.generated.{SnapiParser, SnapiParserBaseVisitor}
import raw.compiler.rql2.source._

import scala.collection.JavaConverters._

class RawSnapiVisitor(positions: Positions, private val source: Source)
    extends SnapiParserBaseVisitor[SourceNode]
    with Keywords {

  private val positionsWrapper = new RawPositions(positions, source)

  private val assertionMessage = "This is a helper (better grammar readability)  node, should never visit it"

  private val defaultProps: Set[Rql2TypeProperty] = Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())

  // An extension method to extract the identifier from a token (removes the backticks)
  implicit class IdentExtension(ctx: SnapiParser.IdentContext) {
    def getValue: String = {
      val identConst = visitWithNullCheck(ctx).asInstanceOf[StringConst]
      identConst.value
    }
  }

  private def visitWithNullCheck(ctx: ParserRuleContext) =
    if (ctx == null) null
    else visit(ctx)

  override def visitProg(ctx: SnapiParser.ProgContext): SourceNode = visitWithNullCheck(ctx.stat)

  override def visitFunDecStat(ctx: SnapiParser.FunDecStatContext): SourceNode = {
    val methods = ctx.method_dec().asScala.map(m => visitWithNullCheck(m).asInstanceOf[Rql2Method]).toVector
    val result = Rql2Program(methods, Option.empty)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitFunDecExprStat(ctx: SnapiParser.FunDecExprStatContext): SourceNode = {
    val methods = ctx.method_dec().asScala.map(md => visitWithNullCheck(md).asInstanceOf[Rql2Method]).toVector
    val result = Rql2Program(methods, Option(visitWithNullCheck(ctx.expr).asInstanceOf[Exp]))
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitFun_proto(ctx: SnapiParser.Fun_protoContext): SourceNode = {
    val ps = ctx.fun_param.asScala.map(fp => visitWithNullCheck(fp).asInstanceOf[FunParam]).toVector
    val funBody = FunBody(visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
    positionsWrapper.setPosition(ctx.expr, funBody)
    val result = FunProto(ps, Option(ctx.tipe()).map(visitWithNullCheck(_).asInstanceOf[Type]), funBody)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitFun_proto_lambda(ctx: SnapiParser.Fun_proto_lambdaContext): SourceNode = {
    val ps = ctx.fun_param.asScala.map(fp => visitWithNullCheck(fp).asInstanceOf[FunParam]).toVector
    val funBody = FunBody(visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
    positionsWrapper.setPosition(ctx.expr, funBody)
    val result = FunProto(ps, Option(ctx.tipe()).map(visitWithNullCheck(_).asInstanceOf[Type]), funBody)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitMethodDec(ctx: SnapiParser.MethodDecContext): SourceNode = {
    val funProto = visitWithNullCheck(ctx.fun_proto).asInstanceOf[FunProto]
    val idnDef = IdnDef(ctx.ident.getValue)
    positionsWrapper.setPosition(ctx.ident, idnDef)
    val result = Rql2Method(funProto, idnDef)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitNormalFun(ctx: SnapiParser.NormalFunContext): SourceNode = {
    val funProto = visitWithNullCheck(ctx.fun_proto).asInstanceOf[FunProto]
    val idnDef = IdnDef(ctx.ident.getValue)
    positionsWrapper.setPosition(ctx.ident, idnDef)
    val result: LetFun = LetFun(funProto, idnDef)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitRecFun(ctx: SnapiParser.RecFunContext): SourceNode = {
    val funProto = visitWithNullCheck(ctx.fun_proto).asInstanceOf[FunProto]
    val idnDef = IdnDef(ctx.ident.getValue)
    positionsWrapper.setPosition(ctx.ident, idnDef)
    val result = LetFunRec(idnDef, funProto)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitFunParamAttr(ctx: SnapiParser.FunParamAttrContext): SourceNode = {
    val idnDef = IdnDef(ctx.attr.ident.getValue)
    positionsWrapper.setPosition(ctx.attr.ident, idnDef)
    val result = FunParam(
      idnDef,
      Option(ctx.attr.tipe).map(visitWithNullCheck(_).asInstanceOf[Type]),
      Option.empty
    )
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitFunParamAttrExpr(ctx: SnapiParser.FunParamAttrExprContext): SourceNode = {
    val idnDef = IdnDef(ctx.attr.ident.getValue)
    positionsWrapper.setPosition(ctx.attr.ident, idnDef)
    val result = FunParam(
      idnDef,
      Option(ctx.attr.tipe).map(visitWithNullCheck(_).asInstanceOf[Type]),
      Option(visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
    )
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitType_attr(ctx: SnapiParser.Type_attrContext): SourceNode = {
    val result = Rql2AttrType(ctx.ident.getValue, visitWithNullCheck(ctx.tipe).asInstanceOf[Type])
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitFunArgExpr(ctx: SnapiParser.FunArgExprContext): SourceNode = {
    val result: FunAppArg = FunAppArg(visitWithNullCheck(ctx.expr).asInstanceOf[Exp], Option.empty)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitNamedFunArgExpr(ctx: SnapiParser.NamedFunArgExprContext): SourceNode = {
    val result: FunAppArg = FunAppArg(visitWithNullCheck(ctx.expr).asInstanceOf[Exp], Option(ctx.ident.getValue))
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitFunAbs(ctx: SnapiParser.FunAbsContext): SourceNode = {
    val funProto = visitWithNullCheck(ctx.fun_proto_lambda).asInstanceOf[FunProto]
    val result = FunAbs(funProto)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitFunAbsUnnamed(ctx: SnapiParser.FunAbsUnnamedContext): SourceNode = {
    val idnDef = IdnDef(ctx.ident.getValue)
    positionsWrapper.setPosition(ctx.ident, idnDef)
    val funParam = FunParam(idnDef, Option.empty, Option.empty)
    positionsWrapper.setPosition(ctx.ident, funParam)
    val funBody = FunBody(visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
    positionsWrapper.setPosition(ctx.expr, funBody)
    val funProto = FunProto(Vector(funParam), Option.empty, funBody)
    positionsWrapper.setPosition(ctx, funProto)
    val result = FunAbs(funProto)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitFunTypeWithParamsType(ctx: SnapiParser.FunTypeWithParamsTypeContext): SourceNode = {
    val ms = ctx.tipe.asScala.dropRight(1).map(t => visitWithNullCheck(t).asInstanceOf[Type]).toVector

    val os = ctx.attr.asScala
      .map(a => {
        val funOptTypeParam = FunOptTypeParam(a.ident.getValue, visitWithNullCheck(a.tipe).asInstanceOf[Type])
        positionsWrapper.setPosition(a, funOptTypeParam)
        funOptTypeParam
      })
      .toVector

    val result: FunType = FunType(
      ms,
      os,
      visitWithNullCheck(ctx.tipe.getLast).asInstanceOf[Type],
      defaultProps
    )
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitOrTypeType(ctx: SnapiParser.OrTypeTypeContext): SourceNode = {
    val tipes = Vector(visitWithNullCheck(ctx.tipe).asInstanceOf[Type])
    val orType: Rql2OrType = visitWithNullCheck(ctx.or_type).asInstanceOf[Rql2OrType]
    val combinedTypes = tipes ++ orType.tipes
    val result = Rql2OrType(combinedTypes, defaultProps)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  // this one is helper, it doesn't need to set position (basically an accumulator for or_type)
  override def visitOr_type(ctx: SnapiParser.Or_typeContext): SourceNode = {
    Rql2OrType(
      Vector(visitWithNullCheck(ctx.tipe).asInstanceOf[Type]) ++ Option(ctx.or_type())
        .map(visitWithNullCheck(_).asInstanceOf[Rql2OrType].tipes)
        .getOrElse(Vector.empty),
      defaultProps
    )
  }

  override def visitOrTypeFunType(ctx: SnapiParser.OrTypeFunTypeContext): SourceNode = {
    val types = Vector(visitWithNullCheck(ctx.tipe(0)).asInstanceOf[Type])
    val orType = visitWithNullCheck(ctx.or_type).asInstanceOf[Rql2OrType]
    val combinedTypes = types ++ orType.tipes
    val domainOrType = Rql2OrType(combinedTypes, defaultProps)
    val funType =
      FunType(Vector(domainOrType), Vector.empty, visitWithNullCheck(ctx.tipe(1)).asInstanceOf[Type], defaultProps)
    positionsWrapper.setPosition(ctx, funType)
    funType
  }

  override def visitRecordTypeType(ctx: SnapiParser.RecordTypeTypeContext): SourceNode =
    visitWithNullCheck(ctx.record_type)

  override def visitIterableTypeType(ctx: SnapiParser.IterableTypeTypeContext): SourceNode =
    visitWithNullCheck(ctx.iterable_type)

  override def visitTypeWithParenType(ctx: SnapiParser.TypeWithParenTypeContext): SourceNode =
    visitWithNullCheck(ctx.tipe)

  override def visitListTypeType(ctx: SnapiParser.ListTypeTypeContext): SourceNode = visitWithNullCheck(ctx.list_type)

  override def visitPrimitiveTypeType(ctx: SnapiParser.PrimitiveTypeTypeContext): SourceNode =
    visitWithNullCheck(ctx.primitive_types)

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
      else if (ctx.UNDEFINED_TOKEN != null) Rql2UndefinedType(defaultProps)
      else throw new AssertionError("Unknown primitive type")
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitTypeAliasType(ctx: SnapiParser.TypeAliasTypeContext): SourceNode = {
    val idnUse = IdnUse(ctx.ident.getValue)
    val result = TypeAliasType(idnUse)
    positionsWrapper.setPosition(ctx, idnUse)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitFunTypeType(ctx: SnapiParser.FunTypeTypeContext): SourceNode = {
    val funType = FunType(
      Vector(visitWithNullCheck(ctx.tipe(0)).asInstanceOf[Type]),
      Vector.empty,
      visitWithNullCheck(ctx.tipe(1)).asInstanceOf[Type],
      defaultProps
    )
    positionsWrapper.setPosition(ctx, funType)
    funType
  }

  override def visitExprTypeExpr(ctx: SnapiParser.ExprTypeExprContext): SourceNode = visitWithNullCheck(ctx.expr_type)

  override def visitRecord_type(ctx: SnapiParser.Record_typeContext): SourceNode = {
    val atts = ctx.type_attr.asScala.map(a => visitWithNullCheck(a).asInstanceOf[Rql2AttrType]).toVector
    val result = Rql2RecordType(atts, defaultProps)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitIterable_type(ctx: SnapiParser.Iterable_typeContext): SourceNode = {
    val result = Rql2IterableType(visitWithNullCheck(ctx.tipe).asInstanceOf[Type], defaultProps)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitList_type(ctx: SnapiParser.List_typeContext): SourceNode = {
    val result = Rql2ListType(visitWithNullCheck(ctx.tipe).asInstanceOf[Type], defaultProps)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitExpr_type(ctx: SnapiParser.Expr_typeContext): SourceNode = {
    val result = TypeExp(visitWithNullCheck(ctx.tipe).asInstanceOf[Type])
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitIdentExpr(ctx: SnapiParser.IdentExprContext): SourceNode = {
    val idnUse = IdnUse(ctx.ident.getValue)
    val result = IdnExp(idnUse)
    positionsWrapper.setPosition(ctx, idnUse)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitProjectionExpr(ctx: SnapiParser.ProjectionExprContext): SourceNode = {
    val proj = Proj(visitWithNullCheck(ctx.expr).asInstanceOf[Exp], ctx.ident.getValue)
    val result =
      if (ctx.fun_ar != null) {
        // The projection with the function call
        val args = Option(ctx.fun_ar.fun_args).map(ar =>
          ar.fun_arg.asScala.map(a => visitWithNullCheck(a).asInstanceOf[FunAppArg]).toVector
        )
        positionsWrapper.setPosition(ctx.getStart, ctx.fun_ar.getStart, proj)
        FunApp(proj, args.getOrElse(Vector.empty))
      } else proj
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitLetExpr(ctx: SnapiParser.LetExprContext): SourceNode = visitWithNullCheck(ctx.let)

  override def visitFunAbsExpr(ctx: SnapiParser.FunAbsExprContext): SourceNode = visitWithNullCheck(ctx.fun_abs)

  override def visitFunAppExpr(ctx: SnapiParser.FunAppExprContext): SourceNode = {
    val args = ctx.fun_ar.fun_args.fun_arg.asScala.map(a => visitWithNullCheck(a).asInstanceOf[FunAppArg]).toVector
    val result = FunApp(visitWithNullCheck(ctx.expr).asInstanceOf[Exp], args)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitIfThenElseExpr(ctx: SnapiParser.IfThenElseExprContext): SourceNode =
    visitWithNullCheck(ctx.if_then_else)

  override def visitExprTypeType(ctx: SnapiParser.ExprTypeTypeContext): SourceNode = visitWithNullCheck(ctx.expr_type)

  override def visitNumberExpr(ctx: SnapiParser.NumberExprContext): SourceNode = visitWithNullCheck(ctx.number)

  override def visitListExpr(ctx: SnapiParser.ListExprContext): SourceNode = visitWithNullCheck(ctx.lists)

  // Unary expressions
  override def visitNotExpr(ctx: SnapiParser.NotExprContext): SourceNode = {
    val not = Not()
    positionsWrapper.setPosition(ctx.NOT_TOKEN.getSymbol, not)
    val result = UnaryExp(not, visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitMinusUnaryExpr(ctx: SnapiParser.MinusUnaryExprContext): SourceNode = {
    val neg = Neg()
    positionsWrapper.setPosition(ctx.MINUS_TOKEN.getSymbol, neg)
    val result = UnaryExp(neg, visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitPlusUnaryExpr(ctx: SnapiParser.PlusUnaryExprContext): SourceNode = visitWithNullCheck(ctx.expr)

  // Binary expressions
  override def visitCompareExpr(ctx: SnapiParser.CompareExprContext): SourceNode = {
    val result = BinaryExp(
      visitWithNullCheck(ctx.compare_tokens).asInstanceOf[ComparableOp],
      visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
      visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
    )
    positionsWrapper.setPosition(ctx, result)
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
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitOrExpr(ctx: SnapiParser.OrExprContext): SourceNode = {
    val or = Or()
    positionsWrapper.setPosition(ctx.OR_TOKEN.getSymbol, or)
    val result = BinaryExp(
      or,
      visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
      visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
    )
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitAndExpr(ctx: SnapiParser.AndExprContext): SourceNode = {
    val and = And()
    positionsWrapper.setPosition(ctx.AND_TOKEN.getSymbol, and)
    val result = BinaryExp(
      and,
      visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
      visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
    )
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitMulExpr(ctx: SnapiParser.MulExprContext): SourceNode = {
    val mult = Mult()
    positionsWrapper.setPosition(ctx.MUL_TOKEN.getSymbol, mult)
    val result = BinaryExp(
      mult,
      visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
      visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
    )
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitDivExpr(ctx: SnapiParser.DivExprContext): SourceNode = {
    val div: Div = Div()
    positionsWrapper.setPosition(ctx.DIV_TOKEN.getSymbol, div)
    val result = BinaryExp(
      div,
      visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
      visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
    )
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitModExpr(ctx: SnapiParser.ModExprContext): SourceNode = {
    val mod: Mod = Mod()
    positionsWrapper.setPosition(ctx.MOD_TOKEN.getSymbol, mod)
    val result = BinaryExp(
      mod,
      visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
      visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
    )
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitPlusExpr(ctx: SnapiParser.PlusExprContext): SourceNode = {
    val plus: Plus = Plus()
    positionsWrapper.setPosition(ctx.PLUS_TOKEN.getSymbol, plus)
    val result = BinaryExp(
      plus,
      visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
      visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
    )
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitMinusExpr(ctx: SnapiParser.MinusExprContext): SourceNode = {
    val sub: Sub = Sub()
    positionsWrapper.setPosition(ctx.MINUS_TOKEN.getSymbol, sub)
    val result = BinaryExp(
      sub,
      visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
      visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
    )
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitParenExpr(ctx: SnapiParser.ParenExprContext): SourceNode = visitWithNullCheck(ctx.expr)

  override def visitRecordExpr(ctx: SnapiParser.RecordExprContext): SourceNode = visitWithNullCheck(ctx.records)

  override def visitLet(ctx: SnapiParser.LetContext): SourceNode = {
    val decls = ctx.let_left.let_decl.asScala.map(d => visitWithNullCheck(d).asInstanceOf[LetDecl]).toVector
    val result = Let(decls, visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitLet_decl(ctx: SnapiParser.Let_declContext): SourceNode =
    if (ctx.fun_dec == null) visitWithNullCheck(ctx.let_bind)
    else visitWithNullCheck(ctx.fun_dec)

  override def visitLet_bind(ctx: SnapiParser.Let_bindContext): SourceNode = {
    val tipe = Option(ctx.tipe).map(visitWithNullCheck(_).asInstanceOf[Type])
    val idnDef = IdnDef(ctx.ident.getValue)
    positionsWrapper.setPosition(ctx.ident, idnDef)
    val result = LetBind(visitWithNullCheck(ctx.expr).asInstanceOf[Exp], idnDef, tipe)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitIf_then_else(ctx: SnapiParser.If_then_elseContext): SourceNode = {
    val result = IfThenElse(
      visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
      visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp],
      visitWithNullCheck(ctx.expr(2)).asInstanceOf[Exp]
    )
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitLists(ctx: SnapiParser.ListsContext): SourceNode = {
    if (ctx.lists_element == null) {
      val result = ListPackageBuilder.Build()
      positionsWrapper.setPosition(ctx, result)
      result
    } else visitWithNullCheck(ctx.lists_element)
  }

  override def visitLists_element(ctx: SnapiParser.Lists_elementContext): SourceNode = {
    val exps = ctx.expr.asScala.map(e => visitWithNullCheck(e).asInstanceOf[Exp])
    val result: Exp = ListPackageBuilder.Build(exps: _*)
    positionsWrapper.setPosition(ctx.parent.asInstanceOf[ParserRuleContext], result)
    result
  }

  override def visitRecords(ctx: SnapiParser.RecordsContext): SourceNode = {
    if (ctx.record_elements == null) {
      val result = RecordPackageBuilder.Build()
      positionsWrapper.setPosition(ctx, result)
      result
    } else visitWithNullCheck(ctx.record_elements)
  }

  override def visitRecord_elements(ctx: SnapiParser.Record_elementsContext): SourceNode = {
    val tuples = ctx.record_element.asScala.zipWithIndex.map {
      case (e, idx) =>
        val exp = visitWithNullCheck(e.expr()).asInstanceOf[Exp]
        if (e.ident() != null) {
          (e.ident().getValue, exp)
        } else exp match {
          case proj: Proj => (proj.i, exp)
          case _ => ("_" + (idx + 1), exp)
        }
    }.toVector
    val result: Exp = RecordPackageBuilder.Build(tuples)
    positionsWrapper.setPosition(ctx.parent.asInstanceOf[ParserRuleContext], result)
    result
  }

  // Constants

  override def visitTrippleStringExpr(ctx: SnapiParser.TrippleStringExprContext): SourceNode = {
    val result = TripleQuotedStringConst(ctx.getText.drop(3).dropRight(3))
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitStringExpr(ctx: SnapiParser.StringExprContext): SourceNode = {
    val result = StringConst(
      ctx.STRING.getText
        .substring(1, ctx.STRING.getText.length - 1)
        .replace("\\b", "\b")
        .replace("\\n", "\n")
        .replace("\\f", "\f")
        .replace("\\r", "\r")
        .replace("\\t", "\t")
        .replace("\\\\", "\\")
        .replace("\\\"", "\"")
    )
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitBoolConstExpr(ctx: SnapiParser.BoolConstExprContext): SourceNode = {
    val result = BoolConst(ctx.bool_const.FALSE_TOKEN == null)
    positionsWrapper.setPosition(ctx, result)
    result
  }

  override def visitNullExpr(ctx: SnapiParser.NullExprContext): SourceNode = {
    val result = NullConst()
    positionsWrapper.setPosition(ctx, result)
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
    positionsWrapper.setPosition(ctx, result)
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
