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

package antlr4_parser;

import antlr4_parser.builders.ListPackageBuilder;
import antlr4_parser.builders.RecordPackageBuilder;
import antlr4_parser.generated.SnapiBaseVisitor;
import antlr4_parser.generated.SnapiParser;
import java.util.LinkedList;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import org.bitbucket.inkytonik.kiama.util.Position;
import org.bitbucket.inkytonik.kiama.util.Positions;
import org.bitbucket.inkytonik.kiama.util.Source;
import raw.compiler.base.source.Type;
import raw.compiler.common.source.*;
import raw.compiler.rql2.source.*;
import scala.Option;
import scala.Tuple2;
import scala.collection.immutable.HashSet;
import scala.collection.immutable.Set;
import scala.collection.immutable.VectorBuilder;

public class RawSnapiVisitor extends SnapiBaseVisitor<SourceNode> {
  private final String assertionMessage =
      "This is a helper (better grammar readability)  node, should never visit it";
  private final Positions positions = new Positions();
  private final Source source;
  private final Set<Rql2TypeProperty> defaultProps =
      new HashSet<Rql2TypeProperty>()
          .$plus(Rql2IsNullableTypeProperty.apply())
          .$plus(Rql2IsTryableTypeProperty.apply());

  public RawSnapiVisitor(Source source) {
    this.source = source;
  }

  public Positions getPositions() {
    return positions;
  }

  private void setPosition(ParserRuleContext ctx, SourceNode node) {
    positions.setStart(
        node,
        new Position(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), source));

    positions.setFinish(
        node, new Position(ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine(), source));
  }

  @Override
  public SourceNode visitProg(SnapiParser.ProgContext ctx) {
    return visit(ctx.stat());
  }

  @Override
  public SourceNode visitFunDecStat(SnapiParser.FunDecStatContext ctx) {
    VectorBuilder<Rql2Method> vb = new VectorBuilder<>();
    for (int i = 0; i < ctx.method_dec().size(); i++) {
      vb.$plus$eq((Rql2Method) visit(ctx.method_dec(i)));
    }
    Rql2Program result = new Rql2Program(vb.result(), Option.<Exp>empty());
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitFunDecExprStat(SnapiParser.FunDecExprStatContext ctx) {
    VectorBuilder<Rql2Method> vb = new VectorBuilder<>();
    for (int i = 0; i < ctx.method_dec().size(); i++) {
      vb.$plus$eq((Rql2Method) visit(ctx.method_dec(i)));
    }
    Rql2Program result = new Rql2Program(vb.result(), Option.<Exp>apply((Exp) visit(ctx.expr())));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitMethodDec(SnapiParser.MethodDecContext ctx) {
    FunProto funProto = (FunProto) visit(ctx.fun_proto());
    FunProto newFunProto =
        funProto.copy(funProto.ps(), funProto.r(), new FunBody((Exp) visit(ctx.expr())));
    Rql2Method result = new Rql2Method(newFunProto, new IdnDef(ctx.IDENT().getText()));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitNormalFun(SnapiParser.NormalFunContext ctx) {
    FunProto funProto = (FunProto) visit(ctx.fun_proto());
    FunProto newFunProto =
        funProto.copy(funProto.ps(), funProto.r(), new FunBody((Exp) visit(ctx.expr())));
    LetFun result = new LetFun(newFunProto, new IdnDef(ctx.IDENT().getText()));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitRecFun(SnapiParser.RecFunContext ctx) {
    FunProto funProto = (FunProto) visit(ctx.fun_proto());
    FunProto newFunProto =
        funProto.copy(funProto.ps(), funProto.r(), new FunBody((Exp) visit(ctx.expr())));
    LetFunRec result = new LetFunRec(new IdnDef(ctx.IDENT().getText()), newFunProto);
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitFunProtoWithoutType(SnapiParser.FunProtoWithoutTypeContext ctx) {
    VectorBuilder<FunParam> vb = new VectorBuilder<>();
    for (int i = 0; i < ctx.fun_param().size(); i++) {
      vb.$plus$eq((FunParam) visit(ctx.fun_param(i)));
    }
    FunProto result = new FunProto(vb.result(), Option.<Type>empty(), null);
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitFunParamAttr(SnapiParser.FunParamAttrContext ctx) {
    FunParam result =
        new FunParam(
            new IdnDef(ctx.attr().IDENT().getText()),
            ctx.attr().type() == null
                ? Option.<Type>empty()
                : Option.<Type>apply((Type) visit(ctx.attr().type())),
            Option.<Exp>empty());
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitFunParamAttrExpr(SnapiParser.FunParamAttrExprContext ctx) {
    FunParam result =
        new FunParam(
            new IdnDef(ctx.attr().IDENT().getText()),
            ctx.attr().type() == null
                ? Option.<Type>empty()
                : Option.<Type>apply((Type) visit(ctx.attr().type())),
            Option.<Exp>apply((Exp) visit(ctx.expr())));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitType_attr(SnapiParser.Type_attrContext ctx) {
    Rql2AttrType result = new Rql2AttrType(ctx.IDENT().getText(), (Type) visit(ctx.type()));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitFunProtoWithType(SnapiParser.FunProtoWithTypeContext ctx) {
    VectorBuilder<FunParam> vb = new VectorBuilder<>();
    for (int i = 0; i < ctx.fun_param().size(); i++) {
      vb.$plus$eq((FunParam) visit(ctx.fun_param(i)));
    }
    FunProto result = new FunProto(vb.result(), Option.<Type>apply((Type) visit(ctx.type())), null);
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitFunArgExpr(SnapiParser.FunArgExprContext ctx) {
    FunAppArg result = new FunAppArg((Exp) visit(ctx.expr()), Option.<String>empty());
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitNamedFunArgExpr(SnapiParser.NamedFunArgExprContext ctx) {
    FunAppArg result =
        new FunAppArg((Exp) visit(ctx.expr()), Option.<String>apply(ctx.IDENT().getText()));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitFunAbs(SnapiParser.FunAbsContext ctx) {
    FunProto funProto = (FunProto) visit(ctx.fun_proto());
    FunProto newFunProto =
        funProto.copy(funProto.ps(), funProto.r(), new FunBody((Exp) visit(ctx.expr())));
    FunAbs result = new FunAbs(newFunProto);
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitFunAbsUnnamed(SnapiParser.FunAbsUnnamedContext ctx) {
    VectorBuilder<FunParam> vb = new VectorBuilder<>();
    vb.$plus$eq(
        new FunParam(new IdnDef(ctx.IDENT().getText()), Option.<Type>empty(), Option.<Exp>empty()));
    FunAbs result =
        new FunAbs(
            new FunProto(vb.result(), Option.<Type>empty(), new FunBody((Exp) visit(ctx.expr()))));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitFunTypeWithParamsType(SnapiParser.FunTypeWithParamsTypeContext ctx) {
    VectorBuilder<Type> vb = new VectorBuilder<>();
    VectorBuilder<FunOptTypeParam> vbo = new VectorBuilder<>();
    for (int i = 0; i < ctx.attr().size(); i++) {
      vbo.$plus$eq(
          new FunOptTypeParam(
              ctx.attr().get(i).IDENT().getText(), (Type) visit(ctx.attr().get(i).type())));
    }
    for (int i = 0; i < ctx.type().size() - 1; i++) {
      vb.$plus$eq((Type) visit(ctx.type().get(i)));
    }
    FunType result =
        new FunType(vb.result(), vbo.result(), (Type) visit(ctx.type().getLast()), defaultProps);
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitOrTypeType(SnapiParser.OrTypeTypeContext ctx) {
    VectorBuilder<Type> vb = new VectorBuilder<>();
    vb.$plus$eq((Type) visit(ctx.type()));
    Rql2OrType orType = (Rql2OrType) visit(ctx.or_type());
    vb.$plus$plus$eq(orType.tipes());
    Rql2OrType result = new Rql2OrType(vb.result(), defaultProps);
    setPosition(ctx, result);
    return result;
  }

  // this one is helper, it doesn't need to set position
  @Override
  public SourceNode visitOr_type(SnapiParser.Or_typeContext ctx) {
    VectorBuilder<Type> vb = new VectorBuilder<>();
    vb.$plus$eq((Type) visit(ctx.type()));
    if (ctx.or_type() != null) {
      Rql2OrType orType = (Rql2OrType) visit(ctx.or_type());
      vb.$plus$plus$eq(orType.tipes());
    }
    return new Rql2OrType(vb.result(), defaultProps);
  }

  @Override
  public SourceNode visitOrTypeFunType(SnapiParser.OrTypeFunTypeContext ctx) {
    VectorBuilder<Type> types = new VectorBuilder<>();
    types.$plus$eq((Type) visit(ctx.type(0)));
    Rql2OrType orType = (Rql2OrType) visit(ctx.or_type());
    types.$plus$plus$eq(orType.tipes());
    Rql2OrType domainOrType = new Rql2OrType(types.result(), defaultProps);

    VectorBuilder<Type> vb = new VectorBuilder<>();
    VectorBuilder<FunOptTypeParam> vbe = new VectorBuilder<>();
    vb.$plus$eq(domainOrType);
    FunType funType =
        new FunType(vb.result(), vbe.result(), (Type) visit(ctx.type(1)), defaultProps);
    setPosition(ctx, funType);
    return funType;
  }

  @Override
  public SourceNode visitUndefinedTypeType(SnapiParser.UndefinedTypeTypeContext ctx) {
    Rql2UndefinedType result = new Rql2UndefinedType(defaultProps);
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitRecordTypeType(SnapiParser.RecordTypeTypeContext ctx) {
    return visit(ctx.record_type());
  }

  @Override
  public SourceNode visitIterableTypeType(SnapiParser.IterableTypeTypeContext ctx) {
    return visit(ctx.iterable_type());
  }

  @Override
  public SourceNode visitTypeWithParenType(SnapiParser.TypeWithParenTypeContext ctx) {
    return visit(ctx.type());
  }

  @Override
  public SourceNode visitListTypeType(SnapiParser.ListTypeTypeContext ctx) {
    return visit(ctx.list_type());
  }

  @Override
  public SourceNode visitPrimitiveTypeType(SnapiParser.PrimitiveTypeTypeContext ctx) {
    return visit(ctx.primitive_types());
  }

  @Override
  public SourceNode visitPrimitive_types(SnapiParser.Primitive_typesContext ctx) {
    Rql2TypeWithProperties result;
    if (ctx.BOOL_TOKEN() != null) {
      result = new Rql2BoolType(defaultProps);
    } else if (ctx.STRING_TOKEN() != null) {
      result = new Rql2StringType(defaultProps);
    } else if (ctx.LOCATION_TOKEN() != null) {
      result = new Rql2LocationType(defaultProps);
    } else if (ctx.BINARY_TOKEN() != null) {
      result = new Rql2BinaryType(defaultProps);
    } else if (ctx.DATE_TOKEN() != null) {
      result = new Rql2DateType(defaultProps);
    } else if (ctx.TIME_TOKEN() != null) {
      result = new Rql2TimeType(defaultProps);
    } else if (ctx.INTERVAL_TOKEN() != null) {
      result = new Rql2IntervalType(defaultProps);
    } else if (ctx.TIMESTAMP_TOKEN() != null) {
      result = new Rql2TimestampType(defaultProps);
    } else if (ctx.BYTE_TOKEN() != null) {
      result = new Rql2ByteType(defaultProps);
    } else if (ctx.SHORT_TOKEN() != null) {
      result = new Rql2ShortType(defaultProps);
    } else if (ctx.INT_TOKEN() != null) {
      result = new Rql2IntType(defaultProps);
    } else if (ctx.LONG_TOKEN() != null) {
      result = new Rql2LongType(defaultProps);
    } else if (ctx.FLOAT_TOKEN() != null) {
      result = new Rql2FloatType(defaultProps);
    } else if (ctx.DOUBLE_TOKEN() != null) {
      result = new Rql2DoubleType(defaultProps);
    } else if (ctx.DECIMAL_TOKEN() != null) {
      result = new Rql2DecimalType(defaultProps);
    } else {
      throw new AssertionError("Unknown primitive type");
    }
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitTypeAliasType(SnapiParser.TypeAliasTypeContext ctx) {
    TypeAliasType result = new TypeAliasType(new IdnUse(ctx.IDENT().getText()));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitFunTypeType(SnapiParser.FunTypeTypeContext ctx) {
    VectorBuilder<Type> vb = new VectorBuilder<>();
    VectorBuilder<FunOptTypeParam> vbe = new VectorBuilder<>();
    vb.$plus$eq((Type) visit(ctx.type(0)));
    FunType funType =
        new FunType(vb.result(), vbe.result(), (Type) visit(ctx.type(1)), defaultProps);
    setPosition(ctx, funType);
    return funType;
  }

  @Override
  public SourceNode visitExprTypeExpr(SnapiParser.ExprTypeExprContext ctx) {
    return visit(ctx.expr_type());
  }

  @Override
  public SourceNode visitRecord_type(SnapiParser.Record_typeContext ctx) {
    VectorBuilder<Rql2AttrType> vb = new VectorBuilder<>();
    List<SnapiParser.Type_attrContext> attrs = ctx.type_attr();
    for (SnapiParser.Type_attrContext attr : attrs) {
      vb.$plus$eq((Rql2AttrType) visit(attr));
    }
    Rql2RecordType result = new Rql2RecordType(vb.result(), defaultProps);
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitIterable_type(SnapiParser.Iterable_typeContext ctx) {
    Rql2IterableType result = new Rql2IterableType((Type) visit(ctx.type()), defaultProps);
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitList_type(SnapiParser.List_typeContext ctx) {
    Rql2ListType result = new Rql2ListType((Type) visit(ctx.type()), defaultProps);
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitExpr_type(SnapiParser.Expr_typeContext ctx) {
    TypeExp result = new TypeExp((Type) visit(ctx.type()));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitIdentExpr(SnapiParser.IdentExprContext ctx) {
    IdnExp result = new IdnExp(new IdnUse(ctx.IDENT().getText()));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitProjectionExpr(SnapiParser.ProjectionExprContext ctx) {
    SourceNode result;
    VectorBuilder<FunAppArg> vb = new VectorBuilder<>();
    Proj proj = new Proj((Exp) visit(ctx.expr()), ctx.IDENT().getText());

    if (ctx.fun_ar() != null) {
      if (ctx.fun_ar().fun_args() != null) {
        List<SnapiParser.Fun_argContext> args = ctx.fun_ar().fun_args().fun_arg();
        for (SnapiParser.Fun_argContext arg : args) {
          vb.$plus$eq((FunAppArg) visit(arg));
        }
      }
      result = new FunApp(proj, vb.result());
    } else {
      result = proj;
    }

    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitLetExpr(SnapiParser.LetExprContext ctx) {
    return visit(ctx.let());
  }

  @Override
  public SourceNode visitFunAbsExpr(SnapiParser.FunAbsExprContext ctx) {
    return visit(ctx.fun_abs());
  }

  @Override
  public SourceNode visitFunAppExpr(SnapiParser.FunAppExprContext ctx) {
    VectorBuilder<FunAppArg> vb = new VectorBuilder<>();
    List<SnapiParser.Fun_argContext> funArgs = ctx.fun_ar().fun_args().fun_arg();

    for (SnapiParser.Fun_argContext funArg : funArgs) {
      vb.$plus$eq((FunAppArg) visit(funArg));
    }
    FunApp result = new FunApp((Exp) visit(ctx.expr()), vb.result());
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitIfThenElseExpr(SnapiParser.IfThenElseExprContext ctx) {
    return visit(ctx.if_then_else());
  }

  @Override
  public SourceNode visitExprTypeType(SnapiParser.ExprTypeTypeContext ctx) {
    return visit(ctx.expr_type());
  }

  @Override
  public SourceNode visitNumberExpr(SnapiParser.NumberExprContext ctx) {
    return visit(ctx.number());
  }

  @Override
  public SourceNode visitListExpr(SnapiParser.ListExprContext ctx) {
    return visit(ctx.lists());
  }

  // Unary expressions
  @Override
  public SourceNode visitNotExpr(SnapiParser.NotExprContext ctx) {
    UnaryExp result = UnaryExp.apply(Not.apply(), (Exp) visit(ctx.expr()));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitMinusUnaryExpr(SnapiParser.MinusUnaryExprContext ctx) {
    UnaryExp result = new UnaryExp(Neg.apply(), (Exp) visit(ctx.expr()));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitPlusUnaryExpr(SnapiParser.PlusUnaryExprContext ctx) {
    return visit(ctx.expr());
  }

  // Binary expressions
  @Override
  public SourceNode visitCompareExpr(SnapiParser.CompareExprContext ctx) {
    BinaryExp result =
        new BinaryExp(
            (ComparableOp) visit(ctx.compare_tokens()),
            (Exp) visit(ctx.expr(0)),
            (Exp) visit(ctx.expr(1)));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitCompare_tokens(SnapiParser.Compare_tokensContext ctx) {
    ComparableOp result;
    if (ctx.EQ_TOKEN() != null) {
      result = Eq.apply();
    } else if (ctx.NEQ_TOKEN() != null) {
      result = Neq.apply();
    } else if (ctx.GT_TOKEN() != null) {
      result = Gt.apply();
    } else if (ctx.GE_TOKEN() != null) {
      result = Ge.apply();
    } else if (ctx.LT_TOKEN() != null) {
      result = Lt.apply();
    } else if (ctx.LE_TOKEN() != null) {
      result = Le.apply();
    } else {
      throw new AssertionError("Unknown comparable operator");
    }
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitOrExpr(SnapiParser.OrExprContext ctx) {
    BinaryExp result =
        new BinaryExp(Or.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitAndExpr(SnapiParser.AndExprContext ctx) {
    BinaryExp result =
        new BinaryExp(And.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitMulExpr(SnapiParser.MulExprContext ctx) {
    BinaryExp result =
        new BinaryExp(Mult.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitDivExpr(SnapiParser.DivExprContext ctx) {
    BinaryExp result =
        new BinaryExp(Div.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitModExpr(SnapiParser.ModExprContext ctx) {
    BinaryExp result =
        new BinaryExp(Mod.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitPlusExpr(SnapiParser.PlusExprContext ctx) {
    BinaryExp result =
        new BinaryExp(Plus.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitMinusExpr(SnapiParser.MinusExprContext ctx) {
    BinaryExp result =
        new BinaryExp(Sub.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitParenExpr(SnapiParser.ParenExprContext ctx) {
    return visit(ctx.expr());
  }

  @Override
  public SourceNode visitRecordExpr(SnapiParser.RecordExprContext ctx) {
    return visit(ctx.records());
  }

  @Override
  public SourceNode visitLet(SnapiParser.LetContext ctx) {
    VectorBuilder<LetDecl> vb = new VectorBuilder<>();
    List<SnapiParser.Let_declContext> decls = ctx.let_left().let_decl();
    for (SnapiParser.Let_declContext decl : decls) {
      vb.$plus$eq((LetDecl) visit(decl));
    }
    Let result = new Let(vb.result(), (Exp) visit(ctx.expr()));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitLet_decl(SnapiParser.Let_declContext ctx) {
    return ctx.fun_dec() == null ? visit(ctx.let_bind()) : visit(ctx.fun_dec());
  }

  @Override
  public SourceNode visitLet_bind(SnapiParser.Let_bindContext ctx) {
    Option<Type> type =
        ctx.type() == null ? Option.<Type>empty() : Option.<Type>apply((Type) visit(ctx.type()));
    LetBind result = new LetBind((Exp) visit(ctx.expr()), new IdnDef(ctx.IDENT().getText()), type);
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitIf_then_else(SnapiParser.If_then_elseContext ctx) {
    IfThenElse result =
        new IfThenElse(
            (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)), (Exp) visit(ctx.expr(2)));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitLists(SnapiParser.ListsContext ctx) {
    return visit(ctx.lists_element());
  }

  @Override
  public SourceNode visitLists_element(SnapiParser.Lists_elementContext ctx) {
    LinkedList<Exp> exps = new LinkedList<>();
    List<SnapiParser.ExprContext> elements = ctx.expr();
    for (SnapiParser.ExprContext element : elements) {
      exps.add((Exp) visit(element));
    }
    Exp result = ListPackageBuilder.build(exps);
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitRecords(SnapiParser.RecordsContext ctx) {
    return visit(ctx.record_elements());
  }

  @Override
  public SourceNode visitRecord_elements(SnapiParser.Record_elementsContext ctx) {
    LinkedList<Tuple2<String, Exp>> tuples = new LinkedList<>();
    List<SnapiParser.Record_elementContext> elements = ctx.record_element();
    for (int i = 0; i < elements.size(); i++) {
      if (elements.get(i).IDENT() != null) {
        tuples.add(
            Tuple2.<String, Exp>apply(
                elements.get(i).IDENT().getText(), (Exp) visit(elements.get(i).expr())));
      } else {
        tuples.add(Tuple2.<String, Exp>apply("_" + (i + 1), (Exp) visit(elements.get(i).expr())));
      }
    }
    Exp result = RecordPackageBuilder.build(tuples);
    setPosition(ctx, result);
    return result;
  }

  // Constants
  @Override
  public SourceNode visitTrippleStringExpr(SnapiParser.TrippleStringExprContext ctx) {
    StringConst result =
        new StringConst(
            ctx.TRIPPLE_STRING()
                .getText()
                .substring(3, ctx.TRIPPLE_STRING().getText().length() - 3));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitStringExpr(SnapiParser.StringExprContext ctx) {
    StringConst result =
        new StringConst(ctx.STRING().getText().substring(1, ctx.STRING().getText().length() - 1));
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitBoolConstExpr(SnapiParser.BoolConstExprContext ctx) {
    BoolConst result = new BoolConst(ctx.bool_const().FALSE_TOKEN() == null);
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitNullExpr(SnapiParser.NullExprContext ctx) {
    NullConst result = new NullConst();
    setPosition(ctx, result);
    return result;
  }

  @Override
  public SourceNode visitNumber(SnapiParser.NumberContext ctx) {
    NumberConst result = null;
    if (ctx.BYTE() != null) {
      result = new ByteConst(ctx.BYTE().getText().toLowerCase().replace("b", ""));
    }
    if (ctx.SHORT() != null) {
      result = new ShortConst(ctx.SHORT().getText().toLowerCase().replace("s", ""));
    }
    if (ctx.INTEGER() != null) {
      result = new IntConst(ctx.INTEGER().getText().toLowerCase());
    }
    if (ctx.LONG() != null) {
      result = new LongConst(ctx.LONG().getText().toLowerCase().replace("l", ""));
    }
    if (ctx.FLOAT() != null) {
      result = new FloatConst(ctx.FLOAT().getText().toLowerCase().replace("f", ""));
    }
    if (ctx.DOUBLE() != null) {
      result = new DoubleConst(ctx.DOUBLE().getText().toLowerCase().replace("d", ""));
    }
    if (ctx.DECIMAL() != null) {
      result = new DecimalConst(ctx.DECIMAL().getText().toLowerCase().replace("q", ""));
    }
    setPosition(ctx, result);
    return result;
  }

  // Nodes to ignore, they are not part of the AST and should never be visited
  @Override
  public SourceNode visitBool_const(SnapiParser.Bool_constContext ctx) {
    throw new AssertionError(assertionMessage);
  }

  @Override
  public SourceNode visitRecord_element(SnapiParser.Record_elementContext ctx) {
    throw new AssertionError(assertionMessage);
  }

  @Override
  public SourceNode visitLet_left(SnapiParser.Let_leftContext ctx) {
    throw new AssertionError(assertionMessage);
  }

  @Override
  public SourceNode visitAttr(SnapiParser.AttrContext ctx) {
    throw new AssertionError(assertionMessage);
  }

  @Override
  public SourceNode visitFun_ar(SnapiParser.Fun_arContext ctx) {
    throw new AssertionError(assertionMessage);
  }

  @Override
  public SourceNode visitFun_args(SnapiParser.Fun_argsContext ctx) {
    throw new AssertionError(assertionMessage);
  }
}
