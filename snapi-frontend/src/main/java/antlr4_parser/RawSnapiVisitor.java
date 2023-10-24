package antlr4_parser;

import antlr4_parser.generated.SnapiBaseVisitor;
import antlr4_parser.generated.SnapiParser;
import java.util.List;
import java.util.Vector;

import org.antlr.v4.runtime.ParserRuleContext;
import org.bitbucket.inkytonik.kiama.util.Position;
import org.bitbucket.inkytonik.kiama.util.Positions;
import org.bitbucket.inkytonik.kiama.util.Source;
import raw.compiler.base.source.Type;
import raw.compiler.common.source.*;
import raw.compiler.rql2.builtin.ListPackageBuilder;
import raw.compiler.rql2.builtin.RecordPackageBuilder;
import raw.compiler.rql2.source.*;
import scala.Option;
import scala.Tuple2;
import scala.collection.immutable.HashSet;
import scala.collection.immutable.Set;
import scala.collection.immutable.VectorBuilder;

import static antlr4_parser.generated.SnapiParser.EQ_TOKEN;
import static antlr4_parser.generated.SnapiParser.TRUE_TOKEN;

public class RawSnapiVisitor extends SnapiBaseVisitor<SourceNode> {
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
    return super.visitProg(ctx);
  }

  @Override
  public SourceNode visitFunDecStat(SnapiParser.FunDecStatContext ctx) {
    return super.visitFunDecStat(ctx);
  }

  @Override
  public SourceNode visitFunDecExprStat(SnapiParser.FunDecExprStatContext ctx) {
    return super.visitFunDecExprStat(ctx);
  }

  @Override
  public SourceNode visitFunDec(SnapiParser.FunDecContext ctx) {
    return super.visitFunDec(ctx);
  }

  @Override
  public SourceNode visitNormalFun(SnapiParser.NormalFunContext ctx) {
    return super.visitNormalFun(ctx);
  }

  @Override
  public SourceNode visitRecFun(SnapiParser.RecFunContext ctx) {
    return super.visitRecFun(ctx);
  }

  @Override
  public SourceNode visitNormalFunProto(SnapiParser.NormalFunProtoContext ctx) {
    return super.visitNormalFunProto(ctx);
  }

  @Override
  public SourceNode visitRecFunProto(SnapiParser.RecFunProtoContext ctx) {
    return super.visitRecFunProto(ctx);
  }

  @Override
  public SourceNode visitFunProtoWithoutType(SnapiParser.FunProtoWithoutTypeContext ctx) {
    return super.visitFunProtoWithoutType(ctx);
  }

  @Override
  public SourceNode visitFunProtoWithType(SnapiParser.FunProtoWithTypeContext ctx) {
    return super.visitFunProtoWithType(ctx);
  }

  @Override
  public SourceNode visitFunParams(SnapiParser.FunParamsContext ctx) {
    return super.visitFunParams(ctx);
  }

  @Override
  public SourceNode visitFunParamAttr(SnapiParser.FunParamAttrContext ctx) {
    return super.visitFunParamAttr(ctx);
  }

  @Override
  public SourceNode visitFunParamAttrExpr(SnapiParser.FunParamAttrExprContext ctx) {
    return super.visitFunParamAttrExpr(ctx);
  }

  @Override
  public SourceNode visitAttrWithType(SnapiParser.AttrWithTypeContext ctx) {
    return super.visitAttrWithType(ctx);
  }

  @Override
  public SourceNode visitFun_app(SnapiParser.Fun_appContext ctx) {
    return super.visitFun_app(ctx);
  }

  @Override
  public SourceNode visitFun_ar(SnapiParser.Fun_arContext ctx) {
    return super.visitFun_ar(ctx);
  }

  @Override
  public SourceNode visitFun_args(SnapiParser.Fun_argsContext ctx) {
    return super.visitFun_args(ctx);
  }

  @Override
  public SourceNode visitFunArgExpr(SnapiParser.FunArgExprContext ctx) {
    return super.visitFunArgExpr(ctx);
  }

  @Override
  public SourceNode visitNamedFunArgExpr(SnapiParser.NamedFunArgExprContext ctx) {
    return super.visitNamedFunArgExpr(ctx);
  }

  @Override
  public SourceNode visitFunAbs(SnapiParser.FunAbsContext ctx) {
    return super.visitFunAbs(ctx);
  }

  @Override
  public SourceNode visitFunAbsUnnamed(SnapiParser.FunAbsUnnamedContext ctx) {
    return super.visitFunAbsUnnamed(ctx);
  }

  @Override
  public SourceNode visitFunTypeWithParamsType(SnapiParser.FunTypeWithParamsTypeContext ctx) {
//    VectorBuilder<Type> vb = new VectorBuilder<>();
//    VectorBuilder<FunOptTypeParam> vbe = new VectorBuilder<>();
//    ctx.fun_opt_params().attr();
//    for(int i = 0; i < ctx.fun_params(); i++) {
//
//    }
//    vb.$plus$eq((Type) visit(ctx.type(0)));
//    return new FunType(vb.result(), vbe.result(), (Type) visit(ctx.type(1)), defaultProps);
    return super.visitFunTypeWithParamsType(ctx);
  }

  @Override
  public SourceNode visitFunOptParams(SnapiParser.FunOptParamsContext ctx) {
    return super.visitFunOptParams(ctx);
  }

  @Override
  public SourceNode visitUndefinedTypeType(SnapiParser.UndefinedTypeTypeContext ctx) {
    return new Rql2UndefinedType(defaultProps);
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
    switch (ctx.PRIMITIVE_TYPES().getSymbol().getType()) {
      case SnapiParser.BOOL_TOKEN -> {
        return new BoolType();
      }
      case SnapiParser.STRING_TOKEN -> {
        return new StringType();
      }
      case SnapiParser.LOCATION_TOKEN -> {
        return new LocationType();
      }
      case SnapiParser.BINARY_TOKEN -> {
        return new BinaryType();
      }
      case SnapiParser.DATE_TOKEN -> {
        return new DateType();
      }
      case SnapiParser.TIME_TOKEN -> {
        return new TimeType();
      }
      case SnapiParser.INTERVAL_TOKEN -> {
        return new IntervalType();
      }
      case SnapiParser.TIMESTAMP_TOKEN -> {
        return new TimestampType();
      }
      case SnapiParser.BYTE_TOKEN -> {
        return new ByteType();
      }
      case SnapiParser.SHORT_TOKEN -> {
        return new ShortType();
      }
      case SnapiParser.INT_TOKEN -> {
        return new IntType();
      }
      case SnapiParser.LONG_TOKEN -> {
        return new LongType();
      }
      case SnapiParser.FLOAT_TOKEN -> {
        return new FloatType();
      }
      case SnapiParser.DOUBLE_TOKEN -> {
        return new DoubleType();
      }
      case SnapiParser.DECIMAL_TOKEN -> {
        return new DecimalType();
      }
    }
    return super.visitPrimitiveTypeType(ctx);
  }

  @Override
  public SourceNode visitTypeAliasType(SnapiParser.TypeAliasTypeContext ctx) {
    return new TypeAliasType(new IdnUse(ctx.IDENT().getText()));
  }

  @Override
  public SourceNode visitFunTypeType(SnapiParser.FunTypeTypeContext ctx) {
    VectorBuilder<Type> vb = new VectorBuilder<>();
    VectorBuilder<FunOptTypeParam> vbe = new VectorBuilder<>();
    vb.$plus$eq((Type) visit(ctx.type(0)));
    return new FunType(vb.result(), vbe.result(), (Type) visit(ctx.type(1)), defaultProps);
  }

  @Override
  public SourceNode visitExprTypeExpr(SnapiParser.ExprTypeExprContext ctx) {
    return visit(ctx.expr_type());
  }

  @Override
  public SourceNode visitRecord_type(SnapiParser.Record_typeContext ctx) {
    VectorBuilder<AttrType> vb = new VectorBuilder<>();
    List<SnapiParser.AttrContext> attrs = ctx.attr();
    for (SnapiParser.AttrContext attr : attrs) {
      vb.$plus$eq((AttrType) visit(attr));
    }
    return new RecordType(vb.result());
  }

  @Override
  public SourceNode visitIterable_type(SnapiParser.Iterable_typeContext ctx) {
    return new IterableType((Type) visit(ctx.type()));
  }

  @Override
  public SourceNode visitList_type(SnapiParser.List_typeContext ctx) {
    return new ListType((Type) visit(ctx.type()));
  }

  @Override
  public SourceNode visitExpr_type(SnapiParser.Expr_typeContext ctx) {
    return new ExpType((Type) visit(ctx.type()));
  }

  @Override
  public SourceNode visitAndExpr(SnapiParser.AndExprContext ctx) {
    return new BinaryExp(And.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
  }

  @Override
  public SourceNode visitMulExpr(SnapiParser.MulExprContext ctx) {
    return new BinaryExp(Mult.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
  }

  @Override
  public SourceNode visitStringExpr(SnapiParser.StringExprContext ctx) {
    return new StringConst(
        ctx.STRING().getText().substring(1, ctx.STRING().getText().length() - 1));
  }

  // Probably will be deleted
  //  @Override
  //  public SourceNode visitIdentExpr(SnapiParser.IdentExprContext ctx) {
  //    return super.visitIdentExpr(ctx);
  //  }

  @Override
  public SourceNode visitBoolConstExpr(SnapiParser.BoolConstExprContext ctx) {
    return new BoolConst(ctx.BOOL_CONST().getSymbol().getType() == TRUE_TOKEN);
  }

  @Override
  public SourceNode visitProjectionExpr(SnapiParser.ProjectionExprContext ctx) {
    return new Proj((Exp) visit(ctx.expr()), ctx.IDENT().getText());
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
    return visit(ctx.fun_app());
  }

  @Override
  public SourceNode visitOrExpr(SnapiParser.OrExprContext ctx) {
    return new BinaryExp(Or.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
  }

  @Override
  public SourceNode visitIfThenElseExpr(SnapiParser.IfThenElseExprContext ctx) {
    return visit(ctx.if_then_else());
  }

  @Override
  public SourceNode visitNullExpr(SnapiParser.NullExprContext ctx) {
    return new NullConst();
  }

  @Override
  public SourceNode visitExprTypeType(SnapiParser.ExprTypeTypeContext ctx) {
    return visit(ctx.expr_type());
  }

  @Override
  public SourceNode visitDivExpr(SnapiParser.DivExprContext ctx) {
    return new BinaryExp(Div.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
  }

  @Override
  public SourceNode visitPlusExpr(SnapiParser.PlusExprContext ctx) {
    return new BinaryExp(Plus.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
  }

  @Override
  public SourceNode visitNumberExpr(SnapiParser.NumberExprContext ctx) {
    return visit(ctx.number());
  }

  @Override
  public SourceNode visitTrippleStringExpr(SnapiParser.TrippleStringExprContext ctx) {
    return new StringConst(
        ctx.TRIPPLE_STRING().getText().substring(3, ctx.TRIPPLE_STRING().getText().length() - 3));
  }

  @Override
  public SourceNode visitCompareExpr(SnapiParser.CompareExprContext ctx) {
    return switch (ctx.COMPARE_TOKENS().getSymbol().getType()) {
      case EQ_TOKEN -> new BinaryExp(
          Eq.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
      case SnapiParser.NEQ_TOKEN -> new BinaryExp(
          Neq.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
      case SnapiParser.GT_TOKEN -> new BinaryExp(
          Gt.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
      case SnapiParser.GE_TOKEN -> new BinaryExp(
          Ge.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
      case SnapiParser.LT_TOKEN -> new BinaryExp(
          Lt.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
      case SnapiParser.LE_TOKEN -> new BinaryExp(
          Le.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
      default -> throw new AssertionError("Unknown compare token");
    };
  }

  @Override
  public SourceNode visitListExpr(SnapiParser.ListExprContext ctx) {
    return visit(ctx.lists());
  }

  @Override
  public SourceNode visitNotExpr(SnapiParser.NotExprContext ctx) {
    return UnaryExp.apply(Not.apply(), (Exp) visit(ctx.expr()));
  }

  @Override
  public SourceNode visitModExpr(SnapiParser.ModExprContext ctx) {
    return new BinaryExp(Mod.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
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
  public SourceNode visitMinusExpr(SnapiParser.MinusExprContext ctx) {
    return new BinaryExp(Sub.apply(), (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)));
  }

  @Override
  public SourceNode visitLet(SnapiParser.LetContext ctx) {
    VectorBuilder<LetDecl> vb = new VectorBuilder<>();
    List<SnapiParser.Let_declContext> decls = ctx.let_left().let_decl();
    for (SnapiParser.Let_declContext decl : decls) {
      vb.$plus$eq((LetDecl) visit(decl));
    }
    return new Let(vb.result(), (Exp) visit(ctx.expr()));
  }

  @Override
  public SourceNode visitLet_left(SnapiParser.Let_leftContext ctx) {
    // skipping helper node
    return null;
  }

  @Override
  public SourceNode visitLet_decl(SnapiParser.Let_declContext ctx) {
    return ctx.fun_dec().isEmpty() ? visit(ctx.let_bind()) : visit(ctx.fun_dec());
  }

  @Override
  public SourceNode visitLet_bind(SnapiParser.Let_bindContext ctx) {
    Option<Type> type =
        ctx.type().isEmpty() ? Option.empty() : Option.apply((Type) visit(ctx.type()));
    return new LetBind((Exp) visit(ctx.expr()), new IdnDef(ctx.IDENT().getText()), type);
  }

  @Override
  public SourceNode visitIf_then_else(SnapiParser.If_then_elseContext ctx) {
    return new IfThenElse(
        (Exp) visit(ctx.expr(0)), (Exp) visit(ctx.expr(1)), (Exp) visit(ctx.expr(2)));
  }

  @Override
  public SourceNode visitLists(SnapiParser.ListsContext ctx) {
    return visit(ctx.lists_element());
  }

  @Override
  public SourceNode visitLists_element(SnapiParser.Lists_elementContext ctx) {
    VectorBuilder<Exp> vb = new VectorBuilder<>();
    List<SnapiParser.ExprContext> elements = ctx.expr();
    for (SnapiParser.ExprContext element : elements) {
      vb.$plus$eq((Exp) visit(element));
    }
    return ListPackageBuilder.Build.apply(vb.result());
  }

  @Override
  public SourceNode visitRecords(SnapiParser.RecordsContext ctx) {
    return visit(ctx.record_elements());
  }

  @Override
  public SourceNode visitRecord_elements(SnapiParser.Record_elementsContext ctx) {
    VectorBuilder<Tuple2<String, Exp>> vb = new VectorBuilder<>();
    List<SnapiParser.Record_elementContext> elements = ctx.record_element();
    for (int i = 0; i < elements.size(); i++) {
      if (elements.get(i).IDENT() != null) {
        vb.$plus$eq(
            Tuple2.apply(elements.get(i).IDENT().getText(), (Exp) visit(elements.get(i).expr())));
      } else {
        vb.$plus$eq(Tuple2.apply("_" + (i + 1), (Exp) visit(elements.get(i).expr())));
      }
    }
    return RecordPackageBuilder.Build.apply(vb.result());
  }

  @Override
  public SourceNode visitRecord_element(SnapiParser.Record_elementContext ctx) {
    // skipping helper node
    return null;
  }

  @Override
  public SourceNode visitNumber(SnapiParser.NumberContext ctx) {
    SourceNode result = null;
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
      result = new LongConst(ctx.FLOAT().getText().toLowerCase().replace("f", ""));
    }
    if (ctx.DOUBLE() != null) {
      result = new LongConst(ctx.DOUBLE().getText().toLowerCase().replace("d", ""));
    }
    if (ctx.DECIMAL() != null) {
      result = new LongConst(ctx.DECIMAL().getText().toLowerCase().replace("q", ""));
    }
    setPosition(ctx, result);
    return result;
  }
}
