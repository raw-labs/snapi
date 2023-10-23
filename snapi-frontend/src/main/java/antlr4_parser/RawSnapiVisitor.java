package antlr4_parser;

import antlr4_parser.generated.SnapiBaseVisitor;
import antlr4_parser.generated.SnapiParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.bitbucket.inkytonik.kiama.util.Position;
import org.bitbucket.inkytonik.kiama.util.Positions;
import org.bitbucket.inkytonik.kiama.util.Source;
import raw.compiler.rql2.Keywords;
import raw.compiler.rql2.source.*;
import scala.collection.immutable.Set;
import scala.collection.immutable.HashSet;

public class RawSnapiVisitor extends SnapiBaseVisitor<Rql2Node> implements Keywords {
  private final Positions positions = new Positions();
  private final Source source;
  private final Set<Rql2TypeProperty> defaultProps =
      new HashSet<Rql2TypeProperty>().$plus(Rql2IsNullableTypeProperty.apply()).$plus(Rql2IsTryableTypeProperty.apply());

  public RawSnapiVisitor(Source source) {
    this.source = source;
  }

  public Positions getPositions() {
    return positions;
  }

  private void setPosition(ParserRuleContext ctx, Rql2Node node) {
    positions.setStart(
        node,
        new Position(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(), source));

    positions.setFinish(
        node, new Position(ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine(), source));
  }

  @Override
  public Rql2Node visitProg(SnapiParser.ProgContext ctx) {
    return super.visitProg(ctx);
  }

  @Override
  public Rql2Node visitFunDecStat(SnapiParser.FunDecStatContext ctx) {
    return super.visitFunDecStat(ctx);
  }

  @Override
  public Rql2Node visitFunDecExprStat(SnapiParser.FunDecExprStatContext ctx) {
    return super.visitFunDecExprStat(ctx);
  }

  @Override
  public Rql2Node visitFunDec(SnapiParser.FunDecContext ctx) {
    return super.visitFunDec(ctx);
  }

  @Override
  public Rql2Node visitNormalFun(SnapiParser.NormalFunContext ctx) {
    return super.visitNormalFun(ctx);
  }

  @Override
  public Rql2Node visitRecFun(SnapiParser.RecFunContext ctx) {
    return super.visitRecFun(ctx);
  }

  @Override
  public Rql2Node visitNormalFunProto(SnapiParser.NormalFunProtoContext ctx) {
    return super.visitNormalFunProto(ctx);
  }

  @Override
  public Rql2Node visitRecFunProto(SnapiParser.RecFunProtoContext ctx) {
    return super.visitRecFunProto(ctx);
  }

  @Override
  public Rql2Node visitFunProtoWithoutType(SnapiParser.FunProtoWithoutTypeContext ctx) {
    return super.visitFunProtoWithoutType(ctx);
  }

  @Override
  public Rql2Node visitFunProtoWithType(SnapiParser.FunProtoWithTypeContext ctx) {
    return super.visitFunProtoWithType(ctx);
  }

  @Override
  public Rql2Node visitFunParams(SnapiParser.FunParamsContext ctx) {
    return super.visitFunParams(ctx);
  }

  @Override
  public Rql2Node visitFunParamAttr(SnapiParser.FunParamAttrContext ctx) {
    return super.visitFunParamAttr(ctx);
  }

  @Override
  public Rql2Node visitFunParamAttrExpr(SnapiParser.FunParamAttrExprContext ctx) {
    return super.visitFunParamAttrExpr(ctx);
  }

  @Override
  public Rql2Node visitAttrWithType(SnapiParser.AttrWithTypeContext ctx) {
    return super.visitAttrWithType(ctx);
  }

  @Override
  public Rql2Node visitFun_app(SnapiParser.Fun_appContext ctx) {
    return super.visitFun_app(ctx);
  }

  @Override
  public Rql2Node visitFun_ar(SnapiParser.Fun_arContext ctx) {
    return super.visitFun_ar(ctx);
  }

  @Override
  public Rql2Node visitFun_args(SnapiParser.Fun_argsContext ctx) {
    return super.visitFun_args(ctx);
  }

  @Override
  public Rql2Node visitFunArgExpr(SnapiParser.FunArgExprContext ctx) {
    return super.visitFunArgExpr(ctx);
  }

  @Override
  public Rql2Node visitNamedFunArgExpr(SnapiParser.NamedFunArgExprContext ctx) {
    return super.visitNamedFunArgExpr(ctx);
  }

  @Override
  public Rql2Node visitFunAbs(SnapiParser.FunAbsContext ctx) {
    return super.visitFunAbs(ctx);
  }

  @Override
  public Rql2Node visitFunAbsUnnamed(SnapiParser.FunAbsUnnamedContext ctx) {
    return super.visitFunAbsUnnamed(ctx);
  }

  @Override
  public Rql2Node visitTypeWithParen(SnapiParser.TypeWithParenContext ctx) {
    return super.visitTypeWithParen(ctx);
  }

  @Override
  public Rql2Node visitRecordType(SnapiParser.RecordTypeContext ctx) {
    return super.visitRecordType(ctx);
  }

  @Override
  public Rql2Node visitExprType(SnapiParser.ExprTypeContext ctx) {
    return super.visitExprType(ctx);
  }

  @Override
  public Rql2Node visitFunTypeWithParams(SnapiParser.FunTypeWithParamsContext ctx) {
    return super.visitFunTypeWithParams(ctx);
  }

  @Override
  public Rql2Node visitListType(SnapiParser.ListTypeContext ctx) {
    return super.visitListType(ctx);
  }

  @Override
  public Rql2Node visitFunType(SnapiParser.FunTypeContext ctx) {
    return super.visitFunType(ctx);
  }

  @Override
  public Rql2Node visitTypeAlias(SnapiParser.TypeAliasContext ctx) {
    return super.visitTypeAlias(ctx);
  }

  @Override
  public Rql2Node visitPremetiveType(SnapiParser.PremetiveTypeContext ctx) {
    return super.visitPremetiveType(ctx);
  }

  @Override
  public Rql2Node visitUndefinedType(SnapiParser.UndefinedTypeContext ctx) {
    return super.visitUndefinedType(ctx);
  }

  @Override
  public Rql2Node visitIterableType(SnapiParser.IterableTypeContext ctx) {
    return super.visitIterableType(ctx);
  }

  @Override
  public Rql2Node visitRecord_type(SnapiParser.Record_typeContext ctx) {
    return super.visitRecord_type(ctx);
  }

  @Override
  public Rql2Node visitIterable_type(SnapiParser.Iterable_typeContext ctx) {
    return super.visitIterable_type(ctx);
  }

  @Override
  public Rql2Node visitList_type(SnapiParser.List_typeContext ctx) {
    return super.visitList_type(ctx);
  }

  @Override
  public Rql2Node visitExpr_type(SnapiParser.Expr_typeContext ctx) {
    return super.visitExpr_type(ctx);
  }

  @Override
  public Rql2Node visitAndExpr(SnapiParser.AndExprContext ctx) {
    return super.visitAndExpr(ctx);
  }

  @Override
  public Rql2Node visitMulExpr(SnapiParser.MulExprContext ctx) {
    return super.visitMulExpr(ctx);
  }

  @Override
  public Rql2Node visitStringExpr(SnapiParser.StringExprContext ctx) {
    return super.visitStringExpr(ctx);
  }

  @Override
  public Rql2Node visitIdentExpr(SnapiParser.IdentExprContext ctx) {
    return super.visitIdentExpr(ctx);
  }

  @Override
  public Rql2Node visitBoolConstExpr(SnapiParser.BoolConstExprContext ctx) {
    return super.visitBoolConstExpr(ctx);
  }

  @Override
  public Rql2Node visitProjectionExpr(SnapiParser.ProjectionExprContext ctx) {
    return super.visitProjectionExpr(ctx);
  }

  @Override
  public Rql2Node visitLetExpr(SnapiParser.LetExprContext ctx) {
    return super.visitLetExpr(ctx);
  }

  @Override
  public Rql2Node visitFunAbsExpr(SnapiParser.FunAbsExprContext ctx) {
    return super.visitFunAbsExpr(ctx);
  }

  @Override
  public Rql2Node visitFunAppExpr(SnapiParser.FunAppExprContext ctx) {
    return super.visitFunAppExpr(ctx);
  }

  @Override
  public Rql2Node visitOrExpr(SnapiParser.OrExprContext ctx) {
    return super.visitOrExpr(ctx);
  }

  @Override
  public Rql2Node visitIfThenElseExpr(SnapiParser.IfThenElseExprContext ctx) {
    return super.visitIfThenElseExpr(ctx);
  }

  @Override
  public Rql2Node visitNullExpr(SnapiParser.NullExprContext ctx) {
    return super.visitNullExpr(ctx);
  }

  @Override
  public Rql2Node visitExprTypeType(SnapiParser.ExprTypeTypeContext ctx) {
    return super.visitExprTypeType(ctx);
  }

  @Override
  public Rql2Node visitDivExpr(SnapiParser.DivExprContext ctx) {
    return super.visitDivExpr(ctx);
  }

  @Override
  public Rql2Node visitPlusExpr(SnapiParser.PlusExprContext ctx) {
    return super.visitPlusExpr(ctx);
  }

  @Override
  public Rql2Node visitNumberExpr(SnapiParser.NumberExprContext ctx) {
    return super.visitNumberExpr(ctx);
  }

  @Override
  public Rql2Node visitTrippleStringExpr(SnapiParser.TrippleStringExprContext ctx) {
    return super.visitTrippleStringExpr(ctx);
  }

  @Override
  public Rql2Node visitCompareExpr(SnapiParser.CompareExprContext ctx) {
    return super.visitCompareExpr(ctx);
  }

  @Override
  public Rql2Node visitListExpr(SnapiParser.ListExprContext ctx) {
    return super.visitListExpr(ctx);
  }

  @Override
  public Rql2Node visitNotExpr(SnapiParser.NotExprContext ctx) {
    return super.visitNotExpr(ctx);
  }

  @Override
  public Rql2Node visitModExpr(SnapiParser.ModExprContext ctx) {
    return super.visitModExpr(ctx);
  }

  @Override
  public Rql2Node visitParenExpr(SnapiParser.ParenExprContext ctx) {
    return super.visitParenExpr(ctx);
  }

  @Override
  public Rql2Node visitRecordExpr(SnapiParser.RecordExprContext ctx) {
    return super.visitRecordExpr(ctx);
  }

  @Override
  public Rql2Node visitMinusExpr(SnapiParser.MinusExprContext ctx) {
    return super.visitMinusExpr(ctx);
  }

  @Override
  public Rql2Node visitLet(SnapiParser.LetContext ctx) {
    return super.visitLet(ctx);
  }

  @Override
  public Rql2Node visitLet_left(SnapiParser.Let_leftContext ctx) {
    return super.visitLet_left(ctx);
  }

  @Override
  public Rql2Node visitLetBind(SnapiParser.LetBindContext ctx) {
    return super.visitLetBind(ctx);
  }

  @Override
  public Rql2Node visitLetFunDec(SnapiParser.LetFunDecContext ctx) {
    return super.visitLetFunDec(ctx);
  }

  @Override
  public Rql2Node visitLet_bind(SnapiParser.Let_bindContext ctx) {
    return super.visitLet_bind(ctx);
  }

  @Override
  public Rql2Node visitIf_then_else(SnapiParser.If_then_elseContext ctx) {
    return super.visitIf_then_else(ctx);
  }

  @Override
  public Rql2Node visitLists(SnapiParser.ListsContext ctx) {
    return super.visitLists(ctx);
  }

  @Override
  public Rql2Node visitLists_element(SnapiParser.Lists_elementContext ctx) {
    return super.visitLists_element(ctx);
  }

  @Override
  public Rql2Node visitRecords(SnapiParser.RecordsContext ctx) {
    return super.visitRecords(ctx);
  }

  @Override
  public Rql2Node visitRecord_elements(SnapiParser.Record_elementsContext ctx) {
    return super.visitRecord_elements(ctx);
  }

  @Override
  public Rql2Node visitRecord_element(SnapiParser.Record_elementContext ctx) {
    return super.visitRecord_element(ctx);
  }

  @Override
  public Rql2Node visitNumber(SnapiParser.NumberContext ctx) {
    Rql2Node result = null;
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
