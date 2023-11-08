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

import org.antlr.v4.runtime.ParserRuleContext
import org.bitbucket.inkytonik.kiama.util.{Positions, Source}
import raw.compiler.base.source.Type
import raw.compiler.common.source._
import raw.compiler.rql2.Keywords
import raw.compiler.rql2.builtin.{ListPackageBuilder, RecordPackageBuilder}
import raw.compiler.rql2.generated.{SnapiParser, SnapiParserBaseVisitor}
import raw.compiler.rql2.source._

import scala.collection.JavaConverters._
import scala.util.Try

class RawSnapiVisitor(positions: Positions, private val source: Source, isFrontend: Boolean)
    extends SnapiParserBaseVisitor[SourceNode] {

  private val positionsWrapper = new RawPositions(positions, source)

  private val assertionMessage = "This is a helper (better grammar readability)  node, should never visit it"

  private val defaultProps: Set[Rql2TypeProperty] =
    if (isFrontend) Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())
    else Set.empty

  // TO BE DELETED!!!!!!!!!!!!!!!!!!!!!!!!!
  private def visitWithNullCheck = (ctx: ParserRuleContext) => {
    if (ctx != null) {
      visit(ctx)
    } else null
  }

  // An extension method to extract the identifier from a token (removes the backticks)
  implicit class IdentExtension(ctx: SnapiParser.IdentContext) {
    def getValue: String = Option(ctx).map(visit(_).asInstanceOf[StringConst].value).getOrElse("")
  }

  override def visitProg(ctx: SnapiParser.ProgContext): SourceNode = Option(ctx)
    .flatMap(c => Option(c.stat))
    .map(visit(_).asInstanceOf[Rql2Program])
    .getOrElse(Rql2Program(Vector.empty, Option.empty))

  override def visitFunDecStat(ctx: SnapiParser.FunDecStatContext): SourceNode = Option(ctx)
    .map { context =>
      val methods = Option(context.method_dec())
        .map(m =>
          m.asScala.map(md =>
            Option(md)
              .map(visit(_).asInstanceOf[Rql2Method])
              .getOrElse(Rql2Method(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())), IdnDef("")))
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val result = Rql2Program(methods, Option.empty)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(Rql2Program(Vector.empty, Option.empty))

  override def visitFunDecExprStat(ctx: SnapiParser.FunDecExprStatContext): SourceNode = Option(ctx)
    .map { context =>
      val methods = Option(context.method_dec())
        .map(m =>
          m.asScala.map(md =>
            Option(md)
              .map(visit(_).asInstanceOf[Rql2Method])
              .getOrElse(Rql2Method(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())), IdnDef("")))
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val me = Option(context.expr).map(visit(_).asInstanceOf[Exp])
      val result = Rql2Program(methods, me)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(Rql2Program(Vector.empty, Option.empty))

  override def visitFun_proto(ctx: SnapiParser.Fun_protoContext): SourceNode = Option(ctx)
    .map { context =>
      val ps = Option(context.fun_param())
        .map(p =>
          p.asScala
            .map(pr =>
              Option(pr)
                .map(visit(_).asInstanceOf[FunParam])
                .getOrElse(FunParam(IdnDef(""), Option.empty, Option.empty))
            )
            .toVector
        )
        .getOrElse(Vector.empty)

      val funBody = Option(context.expr)
        .map { expContext =>
          val exp = visit(expContext).asInstanceOf[Exp]
          val funBody = FunBody(exp)
          positionsWrapper.setPosition(expContext, funBody)
          funBody
        }
        .getOrElse(FunBody(ErrorExp()))
      val result = FunProto(ps, Option(context.tipe()).map(visit(_).asInstanceOf[Rql2TypeWithProperties]), funBody)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))

  override def visitFunProtoLambdaMultiParam(ctx: SnapiParser.FunProtoLambdaMultiParamContext): SourceNode = Option(ctx)
    .map { context =>
      val ps = Option(context.fun_param())
        .map(p =>
          p.asScala
            .map(fp =>
              Option(fp)
                .map(visit(_).asInstanceOf[FunParam])
                .getOrElse(FunParam(IdnDef(""), Option.empty, Option.empty))
            )
            .toVector
        )
        .getOrElse(Vector.empty)

      val funBody = Option(context.expr)
        .map { expContext =>
          val exp = visit(expContext).asInstanceOf[Exp]
          val funBody = FunBody(exp)
          positionsWrapper.setPosition(expContext, funBody)
          funBody
        }
        .getOrElse(FunBody(ErrorExp()))
      val result =
        FunProto(ps, Option(context.tipe()).map(visitWithNullCheck(_).asInstanceOf[Rql2TypeWithProperties]), funBody)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))

  override def visitFunProtoLambdaSingleParam(ctx: SnapiParser.FunProtoLambdaSingleParamContext): SourceNode =
    Option(ctx)
      .map { context =>
        val ps = Option(context.fun_param()).map(fp => Vector(visit(fp).asInstanceOf[FunParam])).getOrElse(Vector.empty)
        val funBody = Option(context.expr)
          .map { expContext =>
            val exp = visit(expContext).asInstanceOf[Exp]
            val funBody = FunBody(exp)
            positionsWrapper.setPosition(expContext, funBody)
            funBody
          }
          .getOrElse(FunBody(ErrorExp()))
        val result = FunProto(ps, Option(context.tipe()).map(visit(_).asInstanceOf[Rql2TypeWithProperties]), funBody)
        positionsWrapper.setPosition(context, result)
        result
      }
      .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))

  override def visitMethodDec(ctx: SnapiParser.MethodDecContext): SourceNode = Option(ctx)
    .map { context =>
      val funProto = Option(context.fun_proto())
        .map(visit(_).asInstanceOf[FunProto])
        .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))

      val idnDef = Option(context.ident)
        .map { idnContext =>
          val res = IdnDef(idnContext.getValue)
          positionsWrapper.setPosition(idnContext, res)
          res
        }
        .getOrElse(IdnDef(""))
      val result = Rql2Method(funProto, idnDef)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(Rql2Method(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())), IdnDef("")))

  override def visitNormalFun(ctx: SnapiParser.NormalFunContext): SourceNode = Option(ctx)
    .map { context =>
      val funProto = Option(context.fun_proto())
        .map(visit(_).asInstanceOf[FunProto])
        .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))

      val idnDef = Option(context.ident)
        .map { idnContext =>
          val res = IdnDef(idnContext.getValue)
          positionsWrapper.setPosition(idnContext, res)
          res
        }
        .getOrElse(IdnDef(""))
      val result: LetFun = LetFun(funProto, idnDef)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(LetFun(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())), IdnDef("")))

  override def visitRecFun(ctx: SnapiParser.RecFunContext): SourceNode = Option(ctx)
    .map { context =>
      val funProto = Option(context.fun_proto())
        .map(visit(_).asInstanceOf[FunProto])
        .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))

      val idnDef = Option(context.ident)
        .map { idnContext =>
          val res = IdnDef(idnContext.getValue)
          positionsWrapper.setPosition(idnContext, res)
          res
        }
        .getOrElse(IdnDef(""))
      val result = LetFunRec(idnDef, funProto)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(LetFunRec(IdnDef(""), FunProto(Vector.empty, Option.empty, FunBody(ErrorExp()))))

  override def visitFunParamAttr(ctx: SnapiParser.FunParamAttrContext): SourceNode = Option(ctx)
    .map { context =>
      val result = Option(context.attr)
        .map { attrContext =>
          val idnDef = Option(attrContext.ident)
            .map { idnContext =>
              val res = IdnDef(idnContext.getValue)
              positionsWrapper.setPosition(idnContext, res)
              res
            }
            .getOrElse(IdnDef(""))
          val tipe = Option(attrContext.tipe)
            .map(visit(_).asInstanceOf[Rql2TypeWithProperties])
          FunParam(
            idnDef,
            tipe,
            Option.empty
          )
        }
        .getOrElse(FunParam(IdnDef(""), Option.empty, Option.empty))
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(FunParam(IdnDef(""), Option.empty, Option.empty))

  override def visitFunParamAttrExpr(ctx: SnapiParser.FunParamAttrExprContext): SourceNode = {
    Option(ctx)
      .map { context =>
        val tupple = Option(context.attr)
          .map { attrContext =>
            val idnDef = Option(attrContext.ident)
              .map { idnContext =>
                val res = IdnDef(idnContext.getValue)
                positionsWrapper.setPosition(idnContext, res)
                res
              }
              .getOrElse(IdnDef(""))
            val tipe = Option(attrContext.tipe)
              .map(visit(_).asInstanceOf[Rql2TypeWithProperties])
            (idnDef, tipe)
          }
          .getOrElse((IdnDef(""), Option.empty))

        val exp = Option(context.expr()).map(visit(_).asInstanceOf[Exp])

        val result = FunParam(
          tupple._1,
          tupple._2,
          exp
        )
        positionsWrapper.setPosition(context, result)
        result
      }
      .getOrElse(FunParam(IdnDef(""), Option.empty, Option.empty))
  }

  override def visitType_attr(ctx: SnapiParser.Type_attrContext): SourceNode = {
    if (ctx != null) {
      val result = Rql2AttrType(ctx.ident.getValue, visitWithNullCheck(ctx.tipe).asInstanceOf[Rql2TypeWithProperties])
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
      val ms =
        ctx.tipe.asScala.dropRight(1).map(t => visitWithNullCheck(t).asInstanceOf[Rql2TypeWithProperties]).toVector

      val os = ctx.attr.asScala
        .map(a => {
          val funOptTypeParam =
            FunOptTypeParam(a.ident.getValue, visitWithNullCheck(a.tipe).asInstanceOf[Rql2TypeWithProperties])
          positionsWrapper.setPosition(a, funOptTypeParam)
          funOptTypeParam
        })
        .toVector

      val result: FunType = FunType(
        ms,
        os,
        visitWithNullCheck(ctx.tipe.getLast).asInstanceOf[Rql2TypeWithProperties],
        defaultProps
      )
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitOrTypeType(ctx: SnapiParser.OrTypeTypeContext): SourceNode = {
    if (ctx != null) {
      val tipes = Vector(visitWithNullCheck(ctx.tipe).asInstanceOf[Rql2TypeWithProperties])
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
        Vector(visitWithNullCheck(ctx.tipe).asInstanceOf[Rql2TypeWithProperties]) ++ Option(ctx.or_type())
          .map(visitWithNullCheck(_).asInstanceOf[Rql2OrType].tipes)
          .getOrElse(Vector.empty),
        defaultProps
      )
    } else null
  }

  override def visitOrTypeFunType(ctx: SnapiParser.OrTypeFunTypeContext): SourceNode = {
    if (ctx != null) {
      val types = Vector(visitWithNullCheck(ctx.tipe(0)).asInstanceOf[Rql2TypeWithProperties])
      val orType = visitWithNullCheck(ctx.or_type).asInstanceOf[Rql2OrType]
      val combinedTypes = types ++ orType.tipes
      val domainOrType = Rql2OrType(combinedTypes, defaultProps)
      val funType = FunType(
        Vector(domainOrType),
        Vector.empty,
        visitWithNullCheck(ctx.tipe(1)).asInstanceOf[Rql2TypeWithProperties],
        defaultProps
      )
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
        Vector(visitWithNullCheck(ctx.tipe(0)).asInstanceOf[Rql2TypeWithProperties]),
        Vector.empty,
        visitWithNullCheck(ctx.tipe(1)).asInstanceOf[Rql2TypeWithProperties],
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
      val result = Rql2IterableType(visitWithNullCheck(ctx.tipe).asInstanceOf[Rql2TypeWithProperties], defaultProps)
      positionsWrapper.setPosition(ctx, result)
      result
    } else null
  }

  override def visitList_type(ctx: SnapiParser.List_typeContext): SourceNode = {
    if (ctx != null) {
      val result = Rql2ListType(visitWithNullCheck(ctx.tipe).asInstanceOf[Rql2TypeWithProperties], defaultProps)
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
      val reslut = ExpType(visitWithNullCheck(ctx.expr_type()).asInstanceOf[Rql2TypeWithProperties])
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

  override def visitStringLiteralExpr(ctx: SnapiParser.StringLiteralExprContext): SourceNode =
    visitWithNullCheck(ctx.string_literal)

  override def visitString_literal(ctx: SnapiParser.String_literalContext): SourceNode = {
    if (ctx != null) {
      if (ctx.STRING() != null) {
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
      } else {
        visitWithNullCheck(ctx.triple_string_literal())
      }
    } else null
  }

  override def visitTriple_string_literal(ctx: SnapiParser.Triple_string_literalContext): SourceNode = {
    if (ctx != null) {
      val result = TripleQuotedStringConst(ctx.getText.drop(3).dropRight(3))
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
  override def visitSigned_number(ctx: SnapiParser.Signed_numberContext): SourceNode = Option(ctx)
    .map { context =>
      val sign = Option(context.MINUS_TOKEN()).map(_ => "-").getOrElse("")
      val result =
        if (context.number.BYTE != null) ByteConst(sign + context.number.BYTE.getText.toLowerCase.replace("b", ""))
        else if (context.number.SHORT != null)
          ShortConst(sign + context.number.SHORT.getText.toLowerCase.replace("s", ""))
        else if (context.number.INTEGER != null) {
          val intText = sign + context.number.INTEGER.getText.toLowerCase
          val intTry = Try(intText.toInt)
          if (intTry.isSuccess) IntConst(intText)
          else {
            val longTry = Try(intText.toLong)
            if (longTry.isSuccess) LongConst(intText)
            else DoubleConst(intText)
          }
        } else if (context.number.LONG != null)
          LongConst(sign + context.number.LONG.getText.toLowerCase.replace("l", ""))
        else if (context.number.FLOAT != null)
          FloatConst(sign + context.number.FLOAT.getText.toLowerCase.replace("f", ""))
        else if (context.number.DOUBLE != null)
          DoubleConst(sign + context.number.DOUBLE.getText.toLowerCase.replace("d", ""))
        else if (context.number.DECIMAL != null)
          DecimalConst(sign + context.number.DECIMAL.getText.toLowerCase.replace("q", ""))
        else throw new AssertionError("Unknown number type")
      val posContext = if (context.PLUS_TOKEN() != null) context.number() else context
      positionsWrapper.setPosition(posContext, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitNumber(ctx: SnapiParser.NumberContext): SourceNode = Option(ctx)
    .map(context => {
      if (context.BYTE != null) ByteConst(context.BYTE.getText.toLowerCase.replace("b", ""))
      else if (context.SHORT != null) ShortConst(context.SHORT.getText.toLowerCase.replace("s", ""))
      else if (context.INTEGER != null) {
        val intText = context.INTEGER.getText.toLowerCase
        val intTry = Try(intText.toInt)
        if (intTry.isSuccess) IntConst(intText)
        else {
          val longTry = Try(intText.toLong)
          if (longTry.isSuccess) LongConst(intText)
          else DoubleConst(intText)
        }
      } else if (context.LONG != null) LongConst(context.LONG.getText.toLowerCase.replace("l", ""))
      else if (context.FLOAT != null) FloatConst(context.FLOAT.getText.toLowerCase.replace("f", ""))
      else if (context.DOUBLE != null) DoubleConst(context.DOUBLE.getText.toLowerCase.replace("d", ""))
      else if (context.DECIMAL != null) DecimalConst(context.DECIMAL.getText.toLowerCase.replace("q", ""))
      else throw new AssertionError("Unknown number type")
    })
    .getOrElse(ErrorExp())

  override def visitIdent(ctx: SnapiParser.IdentContext): SourceNode = Option(ctx)
    .map { context =>
      Option(context.ESC_IDENTIFIER)
        .map(_ => StringConst(context.getText.drop(1).dropRight(1))) // Escaped identifier
        .getOrElse(StringConst(context.getText)) // Regular identifier
    }
    .getOrElse(ErrorExp())

  // Nullable tryable
  override def visitNullableTryableType(ctx: SnapiParser.NullableTryableTypeContext): SourceNode = {
    if (isFrontend) throw new RuntimeException("Nullable tryable types are not supported in frontend")

    Option(ctx)
      .map { context =>
        val tipe = Option(context.tipe)
          .map(visit(_).asInstanceOf[Rql2TypeWithProperties])
          .getOrElse(ErrorType().asInstanceOf[Rql2TypeWithProperties])
        // this is needed for the case of parenthesis around nullable_tryable rule
        Option(context.nullable_tryable())
          .flatMap(c => Option(c.nullable_tryable()).orElse(Some(c)))
          .map { nullableTryable =>
            val withoutNullable = Option(nullableTryable.NULLABLE_TOKEN())
              .map(_ => tipe.cloneAndAddProp(Rql2IsNullableTypeProperty()).asInstanceOf[Rql2TypeWithProperties])
              .getOrElse(tipe)
            Option(nullableTryable.TRYABLE_TOKEN())
              .map(_ =>
                withoutNullable.cloneAndAddProp(Rql2IsTryableTypeProperty()).asInstanceOf[Rql2TypeWithProperties]
              )
              .getOrElse(withoutNullable)
          }
          .getOrElse(tipe)
      }
      .getOrElse(ErrorType())
  }

  override def visitPackage_idn_exp(ctx: SnapiParser.Package_idn_expContext): SourceNode = {
    if (isFrontend) throw new RuntimeException("Package syntax is not supported in frontend")
    Option(ctx)
      .map { context =>
        val stringLiteral = Option(context.string_literal()).map(visit(_)).getOrElse(ErrorExp())
        val str = stringLiteral match {
          case StringConst(s) => s
          case TripleQuotedStringConst(s) => s
          case ErrorExp() => ""
          case _ => throw new AssertionError("Unexpected string literal")
        }
        val result = PackageIdnExp(str)
        positionsWrapper.setPosition(context, result)
        result
      }
      .getOrElse(ErrorExp())
  }

  override def visitPackageIdnExp(ctx: SnapiParser.PackageIdnExpContext): SourceNode =
    Option(ctx).flatMap(context => Option(context.package_idn_exp())).map(visit(_)).getOrElse(ErrorExp())

  // Nodes to ignore, they are not part of the AST and should never be visited
  override def visitBool_const(ctx: SnapiParser.Bool_constContext): SourceNode =
    throw new AssertionError(assertionMessage)

  override def visitRecord_element(ctx: SnapiParser.Record_elementContext): SourceNode =
    throw new AssertionError(assertionMessage)

  override def visitLet_left(ctx: SnapiParser.Let_leftContext): SourceNode = throw new AssertionError(assertionMessage)

  override def visitAttr(ctx: SnapiParser.AttrContext): SourceNode = throw new AssertionError(assertionMessage)

  override def visitFun_ar(ctx: SnapiParser.Fun_arContext): SourceNode = throw new AssertionError(assertionMessage)

  override def visitFun_args(ctx: SnapiParser.Fun_argsContext): SourceNode = throw new AssertionError(assertionMessage)

  override def visitNullable_tryable(ctx: SnapiParser.Nullable_tryableContext): SourceNode =
    throw new AssertionError(assertionMessage)
}
