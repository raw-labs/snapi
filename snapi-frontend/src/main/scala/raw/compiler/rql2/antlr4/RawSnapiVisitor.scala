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
import org.bitbucket.inkytonik.kiama.parsing.{Failure, Success}
import org.bitbucket.inkytonik.kiama.util.{Position, Positions, Source}
import raw.compiler.base.source.Type
import raw.compiler.common.source._
import raw.compiler.rql2.Keywords
import raw.compiler.rql2.builtin.{ListPackageBuilder, RecordPackageBuilder}
import raw.compiler.rql2.generated.{SnapiParser, SnapiParserBaseVisitor}
import raw.compiler.rql2.source._

import scala.collection.JavaConverters._
import scala.util.Try

class RawSnapiVisitor(positions: Positions, private val source: Source)
    extends SnapiParserBaseVisitor[SourceNode]
    with Keywords {

  private val positionsWrapper = new RawPositions(positions, source)

  private val assertionMessage = "This is a helper (better grammar readability)  node, should never visit it"

  private val defaultProps: Set[Rql2TypeProperty] = Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())

  // An extension method to extract the identifier from a token (removes the backticks)
  implicit class IdentExtension(ctx: SnapiParser.IdentContext) {
    def getValue: String = {
      if (ctx != null) {
        val identConst = visitWithNullCheck(ctx).asInstanceOf[StringConst]
        identConst.value
      } else null
    }
  }

  private def visitWithNullCheck = (ctx: ParserRuleContext) => {
    if (ctx != null) { visit(ctx) }
    else null
  }

  override def visitProg(ctx: SnapiParser.ProgContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.stat) }
    else null

  override def visitFunDecStat(ctx: SnapiParser.FunDecStatContext): SourceNode = {
    if (ctx != null) {
      val methods = ctx.method_dec().asScala.map(m => visitWithNullCheck(m).asInstanceOf[Rql2Method]).toVector
      val result = Rql2Program(methods, Option.empty)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitFunDecExprStat(ctx: SnapiParser.FunDecExprStatContext): SourceNode = {
    if (ctx != null) {
      val methods = ctx.method_dec().asScala.map(md => visitWithNullCheck(md).asInstanceOf[Rql2Method]).toVector
      val result = Rql2Program(methods, Option(visitWithNullCheck(ctx.expr).asInstanceOf[Exp]))
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitFun_proto(ctx: SnapiParser.Fun_protoContext): SourceNode = {
    if (ctx != null) {
      val ps = ctx.fun_param.asScala.map(fp => visitWithNullCheck(fp).asInstanceOf[FunParam]).toVector
      val funBody = FunBody(visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
      positionsWrapper.setPosition(ctx.expr, funBody)
      val result = FunProto(ps, Option(ctx.tipe()).map(visitWithNullCheck(_).asInstanceOf[Type]), funBody)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitFunProtoLambdaMultiParam(ctx: SnapiParser.FunProtoLambdaMultiParamContext): SourceNode = {
    if (ctx != null) {
      val ps = ctx.fun_param.asScala.map(fp => visitWithNullCheck(fp).asInstanceOf[FunParam]).toVector
      val funBody = FunBody(visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
      positionsWrapper.setPosition(ctx.expr, funBody)
      val result = FunProto(ps, Option(ctx.tipe()).map(visitWithNullCheck(_).asInstanceOf[Type]), funBody)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }
  override def visitFunProtoLambdaSingleParam(ctx: SnapiParser.FunProtoLambdaSingleParamContext): SourceNode = {
    if (ctx != null) {
      val ps = visitWithNullCheck(ctx.fun_param).asInstanceOf[FunParam] +: Vector.empty
      val funBody = FunBody(visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
      positionsWrapper.setPosition(ctx.expr, funBody)
      val result = FunProto(ps, Option(ctx.tipe()).map(visitWithNullCheck(_).asInstanceOf[Type]), funBody)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitMethodDec(ctx: SnapiParser.MethodDecContext): SourceNode = {
    if (ctx != null) {
      val funProto = visitWithNullCheck(ctx.fun_proto).asInstanceOf[FunProto]
      val idnDef = IdnDef(ctx.ident.getValue)
      positionsWrapper.setPosition(ctx.ident, idnDef)
      val result = Rql2Method(funProto, idnDef)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitNormalFun(ctx: SnapiParser.NormalFunContext): SourceNode = {
    if (ctx != null) {
      val funProto = visitWithNullCheck(ctx.fun_proto).asInstanceOf[FunProto]
      val idnDef = IdnDef(ctx.ident.getValue)
      positionsWrapper.setPosition(ctx.ident, idnDef)
      val result: LetFun = LetFun(funProto, idnDef)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitRecFun(ctx: SnapiParser.RecFunContext): SourceNode = {
    if (ctx != null) {
      val funProto = visitWithNullCheck(ctx.fun_proto).asInstanceOf[FunProto]
      val idnDef = IdnDef(ctx.ident.getValue)
      positionsWrapper.setPosition(ctx.ident, idnDef)
      val result = LetFunRec(idnDef, funProto)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitFunParamAttr(ctx: SnapiParser.FunParamAttrContext): SourceNode = {
    if (ctx != null) {
      val idnDef = IdnDef(ctx.attr.ident.getValue)
      positionsWrapper.setPosition(ctx.attr.ident, idnDef)
      val result = FunParam(
        idnDef,
        Option(ctx.attr.tipe).map(visitWithNullCheck(_).asInstanceOf[Type]),
        Option.empty
      )
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitFunParamAttrExpr(ctx: SnapiParser.FunParamAttrExprContext): SourceNode = {
    if (ctx != null) {
      val idnDef = IdnDef(ctx.attr.ident.getValue)
      positionsWrapper.setPosition(ctx.attr.ident, idnDef)
      val result = FunParam(
        idnDef,
        Option(ctx.attr.tipe).map(visitWithNullCheck(_).asInstanceOf[Type]),
        Option(visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
      )
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitType_attr(ctx: SnapiParser.Type_attrContext): SourceNode = {
    if (ctx != null) {
      val result = Rql2AttrType(ctx.ident.getValue, visitWithNullCheck(ctx.tipe).asInstanceOf[Type])
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitFunArgExpr(ctx: SnapiParser.FunArgExprContext): SourceNode = {
    if (ctx != null) {
      val result: FunAppArg = FunAppArg(visitWithNullCheck(ctx.expr).asInstanceOf[Exp], Option.empty)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitNamedFunArgExpr(ctx: SnapiParser.NamedFunArgExprContext): SourceNode = {
    if (ctx != null) {
      val result: FunAppArg = FunAppArg(visitWithNullCheck(ctx.expr).asInstanceOf[Exp], Option(ctx.ident.getValue))
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitFunAbs(ctx: SnapiParser.FunAbsContext): SourceNode = {
    if (ctx != null) {
      val funProto = visitWithNullCheck(ctx.fun_proto_lambda).asInstanceOf[FunProto]
      val result = FunAbs(funProto)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitFunAbsUnnamed(ctx: SnapiParser.FunAbsUnnamedContext): SourceNode = {
    if (ctx != null) {
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
    } else null
  }

  override def visitFunTypeWithParamsType(ctx: SnapiParser.FunTypeWithParamsTypeContext): SourceNode = {
    if (ctx != null) {
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
    } else null
  }

  override def visitOrTypeType(ctx: SnapiParser.OrTypeTypeContext): SourceNode = {
    if (ctx != null) {
      val tipes = Vector(visitWithNullCheck(ctx.tipe).asInstanceOf[Type])
      val orType: Rql2OrType = visitWithNullCheck(ctx.or_type).asInstanceOf[Rql2OrType]
      val combinedTypes = tipes ++ orType.tipes
      val result = Rql2OrType(combinedTypes, defaultProps)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  // this one is helper, it doesn't need to set position (basically an accumulator for or_type)
  override def visitOr_type(ctx: SnapiParser.Or_typeContext): SourceNode = {
    if (ctx != null) {
      Rql2OrType(
        Vector(visitWithNullCheck(ctx.tipe).asInstanceOf[Type]) ++ Option(ctx.or_type())
          .map(visitWithNullCheck(_).asInstanceOf[Rql2OrType].tipes)
          .getOrElse(Vector.empty),
        defaultProps
      )
    } else null
  }

  override def visitOrTypeFunType(ctx: SnapiParser.OrTypeFunTypeContext): SourceNode = {
    if (ctx != null) {
      val types = Vector(visitWithNullCheck(ctx.tipe(0)).asInstanceOf[Type])
      val orType = visitWithNullCheck(ctx.or_type).asInstanceOf[Rql2OrType]
      val combinedTypes = types ++ orType.tipes
      val domainOrType = Rql2OrType(combinedTypes, defaultProps)
      val funType =
        FunType(Vector(domainOrType), Vector.empty, visitWithNullCheck(ctx.tipe(1)).asInstanceOf[Type], defaultProps)
      positionsWrapper.setPosition(ctx, funType)
      funType
    } else null
  }

  override def visitRecordTypeType(ctx: SnapiParser.RecordTypeTypeContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.record_type) }
    else null

  override def visitIterableTypeType(ctx: SnapiParser.IterableTypeTypeContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.iterable_type) }
    else null

  override def visitTypeWithParenType(ctx: SnapiParser.TypeWithParenTypeContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.tipe) }
    else null

  override def visitListTypeType(ctx: SnapiParser.ListTypeTypeContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.list_type) }
    else null

  override def visitPrimitiveTypeType(ctx: SnapiParser.PrimitiveTypeTypeContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.primitive_types) }
    else null

  override def visitPrimitive_types(ctx: SnapiParser.Primitive_typesContext): SourceNode = {
    if (ctx != null) {
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
    } else null
  }

  override def visitTypeAliasType(ctx: SnapiParser.TypeAliasTypeContext): SourceNode = {
    if (ctx != null) {
      val idnUse = IdnUse(ctx.ident.getValue)
      val result = TypeAliasType(idnUse)
      positionsWrapper.setPosition(ctx, idnUse)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitFunTypeType(ctx: SnapiParser.FunTypeTypeContext): SourceNode = {
    if (ctx != null) {
      val funType = FunType(
        Vector(visitWithNullCheck(ctx.tipe(0)).asInstanceOf[Type]),
        Vector.empty,
        visitWithNullCheck(ctx.tipe(1)).asInstanceOf[Type],
        defaultProps
      )
      positionsWrapper.setPosition(ctx, funType)
      funType
    } else null
  }

  override def visitExprTypeExpr(ctx: SnapiParser.ExprTypeExprContext): SourceNode =
    if (ctx != null) {
      val exp = visitWithNullCheck(ctx.expr_type).asInstanceOf[Type]
      val result = TypeExp(exp)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null

  override def visitRecord_type(ctx: SnapiParser.Record_typeContext): SourceNode = {
    if (ctx != null) {
      val atts = ctx.type_attr.asScala.map(a => visitWithNullCheck(a).asInstanceOf[Rql2AttrType]).toVector
      val result = Rql2RecordType(atts, defaultProps)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitIterable_type(ctx: SnapiParser.Iterable_typeContext): SourceNode = {
    if (ctx != null) {
      val result = Rql2IterableType(visitWithNullCheck(ctx.tipe).asInstanceOf[Type], defaultProps)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitList_type(ctx: SnapiParser.List_typeContext): SourceNode = {
    if (ctx != null) {
      val result = Rql2ListType(visitWithNullCheck(ctx.tipe).asInstanceOf[Type], defaultProps)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitExpr_type(ctx: SnapiParser.Expr_typeContext): SourceNode = {
    if (ctx != null) {
      visitWithNullCheck(ctx.tipe)
    } else null
  }

  override def visitIdentExpr(ctx: SnapiParser.IdentExprContext): SourceNode = {
    if (ctx != null) {
      val idnUse = IdnUse(ctx.ident.getValue)
      val result = IdnExp(idnUse)
      positionsWrapper.setPosition(ctx, idnUse)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitProjectionExpr(ctx: SnapiParser.ProjectionExprContext): SourceNode = {
    if (ctx != null) {
      val proj = Proj(visitWithNullCheck(ctx.expr).asInstanceOf[Exp], ctx.ident.getValue)
      val result =
        if (ctx.fun_ar != null) {
          // The projection with the function call
          val args = Option(ctx.fun_ar.fun_args).map(ar =>
            ar.fun_arg.asScala.map(a => visitWithNullCheck(a).asInstanceOf[FunAppArg]).toVector
          )
          positionsWrapper.setPosition(ctx.getStart, ctx.ident().getStop, proj)
          FunApp(proj, args.getOrElse(Vector.empty))
        } else proj
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitLetExpr(ctx: SnapiParser.LetExprContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.let) }
    else null

  override def visitFunAbsExpr(ctx: SnapiParser.FunAbsExprContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.fun_abs) }
    else null

  override def visitFunAppExpr(ctx: SnapiParser.FunAppExprContext): SourceNode = {
    if (ctx != null && ctx.fun_ar != null) {
      val args =
        if (ctx.fun_ar.fun_args != null)
          ctx.fun_ar.fun_args.fun_arg.asScala.map(a => visitWithNullCheck(a).asInstanceOf[FunAppArg]).toVector
        else Vector.empty
      val result = FunApp(visitWithNullCheck(ctx.expr).asInstanceOf[Exp], args)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitIfThenElseExpr(ctx: SnapiParser.IfThenElseExprContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.if_then_else) }
    else null

  override def visitExprTypeType(ctx: SnapiParser.ExprTypeTypeContext): SourceNode =
    if (ctx != null) {
      val reslut = ExpType(visitWithNullCheck(ctx.expr_type()).asInstanceOf[Type])
      positionsWrapper.setPosition(ctx, reslut)
      reslut
    } else null

  override def visitListExpr(ctx: SnapiParser.ListExprContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.lists) }
    else null

  // Unary expressions
  override def visitNotExpr(ctx: SnapiParser.NotExprContext): SourceNode = {
    if (ctx != null) {
      val not = Not()
      positionsWrapper.setPosition(ctx.NOT_TOKEN.getSymbol, not)
      val result = UnaryExp(not, visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitMinusUnaryExpr(ctx: SnapiParser.MinusUnaryExprContext): SourceNode = {
    if (ctx != null) {
      val neg = Neg()
      positionsWrapper.setPosition(ctx.MINUS_TOKEN.getSymbol, neg)
      val result = UnaryExp(neg, visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitPlusUnaryExpr(ctx: SnapiParser.PlusUnaryExprContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.expr) }
    else null

  // Binary expressions
  override def visitCompareExpr(ctx: SnapiParser.CompareExprContext): SourceNode = {
    if (ctx != null) {
      val result = BinaryExp(
        visitWithNullCheck(ctx.compare_tokens).asInstanceOf[ComparableOp],
        visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
        visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
      )
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitCompare_tokens(ctx: SnapiParser.Compare_tokensContext): SourceNode = {
    if (ctx != null) {
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
    } else null
  }

  override def visitOrExpr(ctx: SnapiParser.OrExprContext): SourceNode = {
    if (ctx != null) {
      val or = Or()
      positionsWrapper.setPosition(ctx.OR_TOKEN.getSymbol, or)
      val result = BinaryExp(
        or,
        visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
        visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
      )
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitAndExpr(ctx: SnapiParser.AndExprContext): SourceNode = {
    if (ctx != null) {
      val and = And()
      positionsWrapper.setPosition(ctx.AND_TOKEN.getSymbol, and)
      val result = BinaryExp(
        and,
        visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
        visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
      )
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitMulExpr(ctx: SnapiParser.MulExprContext): SourceNode = {
    if (ctx != null) {
      val mult = Mult()
      positionsWrapper.setPosition(ctx.MUL_TOKEN.getSymbol, mult)
      val result = BinaryExp(
        mult,
        visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
        visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
      )
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitDivExpr(ctx: SnapiParser.DivExprContext): SourceNode = {
    if (ctx != null) {
      val div: Div = Div()
      positionsWrapper.setPosition(ctx.DIV_TOKEN.getSymbol, div)
      val result = BinaryExp(
        div,
        visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
        visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
      )
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitModExpr(ctx: SnapiParser.ModExprContext): SourceNode = {
    if (ctx != null) {
      val mod: Mod = Mod()
      positionsWrapper.setPosition(ctx.MOD_TOKEN.getSymbol, mod)
      val result = BinaryExp(
        mod,
        visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
        visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
      )
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitPlusExpr(ctx: SnapiParser.PlusExprContext): SourceNode = {
    if (ctx != null) {
      val plus: Plus = Plus()
      positionsWrapper.setPosition(ctx.PLUS_TOKEN.getSymbol, plus)
      val result = BinaryExp(
        plus,
        visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
        visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
      )
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitMinusExpr(ctx: SnapiParser.MinusExprContext): SourceNode = {
    if (ctx != null) {
      val sub: Sub = Sub()
      positionsWrapper.setPosition(ctx.MINUS_TOKEN.getSymbol, sub)
      val result = BinaryExp(
        sub,
        visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
        visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp]
      )
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitParenExpr(ctx: SnapiParser.ParenExprContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.expr) }
    else null

  override def visitRecordExpr(ctx: SnapiParser.RecordExprContext): SourceNode =
    if (ctx != null) { visitWithNullCheck(ctx.records) }
    else null

  override def visitLet(ctx: SnapiParser.LetContext): SourceNode = {
    if (ctx != null) {
      val decls = ctx.let_left.let_decl.asScala.map(d => visitWithNullCheck(d).asInstanceOf[LetDecl]).toVector
      val result = Let(decls, visitWithNullCheck(ctx.expr).asInstanceOf[Exp])
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitLet_decl(ctx: SnapiParser.Let_declContext): SourceNode =
    if (ctx != null) {
      if (ctx.fun_dec == null) visitWithNullCheck(ctx.let_bind)
      else visitWithNullCheck(ctx.fun_dec)
    } else null

  override def visitLet_bind(ctx: SnapiParser.Let_bindContext): SourceNode = {
    if (ctx != null) {
      val tipe = Option(ctx.tipe).map(visitWithNullCheck(_).asInstanceOf[Type])
      val idnDef = IdnDef(ctx.ident.getValue)
      positionsWrapper.setPosition(ctx.ident, idnDef)
      val result = LetBind(visitWithNullCheck(ctx.expr).asInstanceOf[Exp], idnDef, tipe)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitIf_then_else(ctx: SnapiParser.If_then_elseContext): SourceNode = {
    if (ctx != null) {
      val result = IfThenElse(
        visitWithNullCheck(ctx.expr(0)).asInstanceOf[Exp],
        visitWithNullCheck(ctx.expr(1)).asInstanceOf[Exp],
        visitWithNullCheck(ctx.expr(2)).asInstanceOf[Exp]
      )
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitLists(ctx: SnapiParser.ListsContext): SourceNode = {
    if (ctx != null) {
      if (ctx.lists_element == null) {
        val result = ListPackageBuilder.Build()
        positionsWrapper.setPosition(ctx, result)
        result
      } else visitWithNullCheck(ctx.lists_element)
    } else null
  }

  override def visitLists_element(ctx: SnapiParser.Lists_elementContext): SourceNode = {
    if (ctx != null) {
      val exps = ctx.expr.asScala.map(e => visitWithNullCheck(e).asInstanceOf[Exp])
      val result: Exp = ListPackageBuilder.Build(exps: _*)
      positionsWrapper.setPosition(ctx.parent.asInstanceOf[ParserRuleContext], result)
      result
    } else null
  }

  override def visitRecords(ctx: SnapiParser.RecordsContext): SourceNode = {
    if (ctx != null) {
      if (ctx.record_elements == null) {
        val result = RecordPackageBuilder.Build()
        positionsWrapper.setPosition(ctx, result)
        result
      } else visitWithNullCheck(ctx.record_elements)
    } else null
  }

  override def visitRecord_elements(ctx: SnapiParser.Record_elementsContext): SourceNode = {
    if (ctx != null) {
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
    } else null
  }

  // Constants

  override def visitTrippleStringExpr(ctx: SnapiParser.TrippleStringExprContext): SourceNode = {
    if (ctx != null) {
      val result = TripleQuotedStringConst(ctx.getText.drop(3).dropRight(3))
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitStringExpr(ctx: SnapiParser.StringExprContext): SourceNode = {
    if (ctx != null) {
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
    } else null
  }

  override def visitBoolConstExpr(ctx: SnapiParser.BoolConstExprContext): SourceNode = {
    if (ctx != null) {
      val result = BoolConst(ctx.bool_const.FALSE_TOKEN == null)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitNullExpr(ctx: SnapiParser.NullExprContext): SourceNode = {
    if (ctx != null) {
      val result = NullConst()
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }
  override def visitSignedNumberExpr(ctx: SnapiParser.SignedNumberExprContext): SourceNode = {
    if (ctx != null) visitWithNullCheck(ctx.signed_number)
    else null
  }
  override def visitSigned_number(ctx: SnapiParser.Signed_numberContext): SourceNode = {
    val sign = if (ctx.MINUS_TOKEN != null) "-" else ""
    if (ctx != null && ctx.number != null) {
      val result =
        if (ctx.number.BYTE != null) ByteConst(sign + ctx.number.BYTE.getText.toLowerCase.replace("b", ""))
        else if (ctx.number.SHORT != null) ShortConst(sign + ctx.number.SHORT.getText.toLowerCase.replace("s", ""))
        else if (ctx.number.INTEGER != null) {
          val intText = sign + ctx.number.INTEGER.getText.toLowerCase
          val intTry = Try(intText.toInt)
          if (intTry.isSuccess) IntConst(intText)
          else {
            val longTry = Try(intText.toLong)
            if (longTry.isSuccess) LongConst(intText)
            else DoubleConst(intText)
          }
        } else if (ctx.number.LONG != null) LongConst(sign + ctx.number.LONG.getText.toLowerCase.replace("l", ""))
        else if (ctx.number.FLOAT != null) FloatConst(sign + ctx.number.FLOAT.getText.toLowerCase.replace("f", ""))
        else if (ctx.number.DOUBLE != null) DoubleConst(sign + ctx.number.DOUBLE.getText.toLowerCase.replace("d", ""))
        else if (ctx.number.DECIMAL != null)
          DecimalConst(sign + ctx.number.DECIMAL.getText.toLowerCase.replace("q", ""))
        else throw new AssertionError("Unknown number type")
      val context = if (ctx.PLUS_TOKEN() != null) ctx.number() else ctx
      positionsWrapper.setPosition(context, result)
      result
    } else null
  }

  override def visitNumber(ctx: SnapiParser.NumberContext): SourceNode = {
    if (ctx != null) {
      if (ctx.BYTE != null) ByteConst(ctx.BYTE.getText.toLowerCase.replace("b", ""))
      else if (ctx.SHORT != null) ShortConst(ctx.SHORT.getText.toLowerCase.replace("s", ""))
      else if (ctx.INTEGER != null) {
        val intText = ctx.INTEGER.getText.toLowerCase
        val intTry = Try(intText.toInt)
        if (intTry.isSuccess) IntConst(intText)
        else {
          val longTry = Try(intText.toLong)
          if (longTry.isSuccess) LongConst(intText)
          else DoubleConst(intText)
        }
      } else if (ctx.LONG != null) LongConst(ctx.LONG.getText.toLowerCase.replace("l", ""))
      else if (ctx.FLOAT != null) FloatConst(ctx.FLOAT.getText.toLowerCase.replace("f", ""))
      else if (ctx.DOUBLE != null) DoubleConst(ctx.DOUBLE.getText.toLowerCase.replace("d", ""))
      else if (ctx.DECIMAL != null) DecimalConst(ctx.DECIMAL.getText.toLowerCase.replace("q", ""))
      else throw new AssertionError("Unknown number type")
    } else null
  }

  override def visitIdent(ctx: SnapiParser.IdentContext): SourceNode =
    if (ctx != null) {
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
    } else null

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
