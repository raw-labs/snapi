// Generated from Snapi.g4 by ANTLR 4.13.0
package antlr4_parser.generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SnapiParser}.
 */
public interface SnapiListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SnapiParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(SnapiParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(SnapiParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunDecStat}
	 * labeled alternative in {@link SnapiParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterFunDecStat(SnapiParser.FunDecStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunDecStat}
	 * labeled alternative in {@link SnapiParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitFunDecStat(SnapiParser.FunDecStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunDecExprStat}
	 * labeled alternative in {@link SnapiParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterFunDecExprStat(SnapiParser.FunDecExprStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunDecExprStat}
	 * labeled alternative in {@link SnapiParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitFunDecExprStat(SnapiParser.FunDecExprStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunDec}
	 * labeled alternative in {@link SnapiParser#fun_dec}.
	 * @param ctx the parse tree
	 */
	void enterFunDec(SnapiParser.FunDecContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunDec}
	 * labeled alternative in {@link SnapiParser#fun_dec}.
	 * @param ctx the parse tree
	 */
	void exitFunDec(SnapiParser.FunDecContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NormalFun}
	 * labeled alternative in {@link SnapiParser#fun}.
	 * @param ctx the parse tree
	 */
	void enterNormalFun(SnapiParser.NormalFunContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NormalFun}
	 * labeled alternative in {@link SnapiParser#fun}.
	 * @param ctx the parse tree
	 */
	void exitNormalFun(SnapiParser.NormalFunContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RecFun}
	 * labeled alternative in {@link SnapiParser#fun}.
	 * @param ctx the parse tree
	 */
	void enterRecFun(SnapiParser.RecFunContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RecFun}
	 * labeled alternative in {@link SnapiParser#fun}.
	 * @param ctx the parse tree
	 */
	void exitRecFun(SnapiParser.RecFunContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NormalFunProto}
	 * labeled alternative in {@link SnapiParser#normal_fun}.
	 * @param ctx the parse tree
	 */
	void enterNormalFunProto(SnapiParser.NormalFunProtoContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NormalFunProto}
	 * labeled alternative in {@link SnapiParser#normal_fun}.
	 * @param ctx the parse tree
	 */
	void exitNormalFunProto(SnapiParser.NormalFunProtoContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RecFunProto}
	 * labeled alternative in {@link SnapiParser#rec_fun}.
	 * @param ctx the parse tree
	 */
	void enterRecFunProto(SnapiParser.RecFunProtoContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RecFunProto}
	 * labeled alternative in {@link SnapiParser#rec_fun}.
	 * @param ctx the parse tree
	 */
	void exitRecFunProto(SnapiParser.RecFunProtoContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunProtoWithoutType}
	 * labeled alternative in {@link SnapiParser#fun_proto}.
	 * @param ctx the parse tree
	 */
	void enterFunProtoWithoutType(SnapiParser.FunProtoWithoutTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunProtoWithoutType}
	 * labeled alternative in {@link SnapiParser#fun_proto}.
	 * @param ctx the parse tree
	 */
	void exitFunProtoWithoutType(SnapiParser.FunProtoWithoutTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunProtoWithType}
	 * labeled alternative in {@link SnapiParser#fun_proto}.
	 * @param ctx the parse tree
	 */
	void enterFunProtoWithType(SnapiParser.FunProtoWithTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunProtoWithType}
	 * labeled alternative in {@link SnapiParser#fun_proto}.
	 * @param ctx the parse tree
	 */
	void exitFunProtoWithType(SnapiParser.FunProtoWithTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunParams}
	 * labeled alternative in {@link SnapiParser#fun_params}.
	 * @param ctx the parse tree
	 */
	void enterFunParams(SnapiParser.FunParamsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunParams}
	 * labeled alternative in {@link SnapiParser#fun_params}.
	 * @param ctx the parse tree
	 */
	void exitFunParams(SnapiParser.FunParamsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunParamAttr}
	 * labeled alternative in {@link SnapiParser#fun_param}.
	 * @param ctx the parse tree
	 */
	void enterFunParamAttr(SnapiParser.FunParamAttrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunParamAttr}
	 * labeled alternative in {@link SnapiParser#fun_param}.
	 * @param ctx the parse tree
	 */
	void exitFunParamAttr(SnapiParser.FunParamAttrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunParamAttrExpr}
	 * labeled alternative in {@link SnapiParser#fun_param}.
	 * @param ctx the parse tree
	 */
	void enterFunParamAttrExpr(SnapiParser.FunParamAttrExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunParamAttrExpr}
	 * labeled alternative in {@link SnapiParser#fun_param}.
	 * @param ctx the parse tree
	 */
	void exitFunParamAttrExpr(SnapiParser.FunParamAttrExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AttrWithType}
	 * labeled alternative in {@link SnapiParser#attr}.
	 * @param ctx the parse tree
	 */
	void enterAttrWithType(SnapiParser.AttrWithTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AttrWithType}
	 * labeled alternative in {@link SnapiParser#attr}.
	 * @param ctx the parse tree
	 */
	void exitAttrWithType(SnapiParser.AttrWithTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#fun_app}.
	 * @param ctx the parse tree
	 */
	void enterFun_app(SnapiParser.Fun_appContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#fun_app}.
	 * @param ctx the parse tree
	 */
	void exitFun_app(SnapiParser.Fun_appContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#fun_ar}.
	 * @param ctx the parse tree
	 */
	void enterFun_ar(SnapiParser.Fun_arContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#fun_ar}.
	 * @param ctx the parse tree
	 */
	void exitFun_ar(SnapiParser.Fun_arContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#fun_args}.
	 * @param ctx the parse tree
	 */
	void enterFun_args(SnapiParser.Fun_argsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#fun_args}.
	 * @param ctx the parse tree
	 */
	void exitFun_args(SnapiParser.Fun_argsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunArgExpr}
	 * labeled alternative in {@link SnapiParser#fun_arg}.
	 * @param ctx the parse tree
	 */
	void enterFunArgExpr(SnapiParser.FunArgExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunArgExpr}
	 * labeled alternative in {@link SnapiParser#fun_arg}.
	 * @param ctx the parse tree
	 */
	void exitFunArgExpr(SnapiParser.FunArgExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NamedFunArgExpr}
	 * labeled alternative in {@link SnapiParser#fun_arg}.
	 * @param ctx the parse tree
	 */
	void enterNamedFunArgExpr(SnapiParser.NamedFunArgExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NamedFunArgExpr}
	 * labeled alternative in {@link SnapiParser#fun_arg}.
	 * @param ctx the parse tree
	 */
	void exitNamedFunArgExpr(SnapiParser.NamedFunArgExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunAbs}
	 * labeled alternative in {@link SnapiParser#fun_abs}.
	 * @param ctx the parse tree
	 */
	void enterFunAbs(SnapiParser.FunAbsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunAbs}
	 * labeled alternative in {@link SnapiParser#fun_abs}.
	 * @param ctx the parse tree
	 */
	void exitFunAbs(SnapiParser.FunAbsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunAbsUnnamed}
	 * labeled alternative in {@link SnapiParser#fun_abs}.
	 * @param ctx the parse tree
	 */
	void enterFunAbsUnnamed(SnapiParser.FunAbsUnnamedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunAbsUnnamed}
	 * labeled alternative in {@link SnapiParser#fun_abs}.
	 * @param ctx the parse tree
	 */
	void exitFunAbsUnnamed(SnapiParser.FunAbsUnnamedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TypeWithParen}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void enterTypeWithParen(SnapiParser.TypeWithParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TypeWithParen}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void exitTypeWithParen(SnapiParser.TypeWithParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RecordType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void enterRecordType(SnapiParser.RecordTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RecordType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void exitRecordType(SnapiParser.RecordTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void enterExprType(SnapiParser.ExprTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void exitExprType(SnapiParser.ExprTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunTypeWithParams}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void enterFunTypeWithParams(SnapiParser.FunTypeWithParamsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunTypeWithParams}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void exitFunTypeWithParams(SnapiParser.FunTypeWithParamsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ListType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void enterListType(SnapiParser.ListTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ListType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void exitListType(SnapiParser.ListTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void enterFunType(SnapiParser.FunTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void exitFunType(SnapiParser.FunTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TypeAlias}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void enterTypeAlias(SnapiParser.TypeAliasContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TypeAlias}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void exitTypeAlias(SnapiParser.TypeAliasContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PremetiveType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void enterPremetiveType(SnapiParser.PremetiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PremetiveType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void exitPremetiveType(SnapiParser.PremetiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UndefinedType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void enterUndefinedType(SnapiParser.UndefinedTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UndefinedType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void exitUndefinedType(SnapiParser.UndefinedTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IterableType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void enterIterableType(SnapiParser.IterableTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IterableType}
	 * labeled alternative in {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void exitIterableType(SnapiParser.IterableTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#record_type}.
	 * @param ctx the parse tree
	 */
	void enterRecord_type(SnapiParser.Record_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#record_type}.
	 * @param ctx the parse tree
	 */
	void exitRecord_type(SnapiParser.Record_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#iterable_type}.
	 * @param ctx the parse tree
	 */
	void enterIterable_type(SnapiParser.Iterable_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#iterable_type}.
	 * @param ctx the parse tree
	 */
	void exitIterable_type(SnapiParser.Iterable_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#list_type}.
	 * @param ctx the parse tree
	 */
	void enterList_type(SnapiParser.List_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#list_type}.
	 * @param ctx the parse tree
	 */
	void exitList_type(SnapiParser.List_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#expr_type}.
	 * @param ctx the parse tree
	 */
	void enterExpr_type(SnapiParser.Expr_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#expr_type}.
	 * @param ctx the parse tree
	 */
	void exitExpr_type(SnapiParser.Expr_typeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AndExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAndExpr(SnapiParser.AndExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AndExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAndExpr(SnapiParser.AndExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulExpr(SnapiParser.MulExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulExpr(SnapiParser.MulExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterStringExpr(SnapiParser.StringExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitStringExpr(SnapiParser.StringExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IdentExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIdentExpr(SnapiParser.IdentExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IdentExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIdentExpr(SnapiParser.IdentExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BoolConstExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBoolConstExpr(SnapiParser.BoolConstExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BoolConstExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBoolConstExpr(SnapiParser.BoolConstExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ProjectionExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterProjectionExpr(SnapiParser.ProjectionExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ProjectionExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitProjectionExpr(SnapiParser.ProjectionExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LetExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLetExpr(SnapiParser.LetExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LetExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLetExpr(SnapiParser.LetExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunAbsExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFunAbsExpr(SnapiParser.FunAbsExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunAbsExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFunAbsExpr(SnapiParser.FunAbsExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunAppExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFunAppExpr(SnapiParser.FunAppExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunAppExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFunAppExpr(SnapiParser.FunAppExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OrExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterOrExpr(SnapiParser.OrExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OrExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitOrExpr(SnapiParser.OrExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfThenElseExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIfThenElseExpr(SnapiParser.IfThenElseExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfThenElseExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIfThenElseExpr(SnapiParser.IfThenElseExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NullExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNullExpr(SnapiParser.NullExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NullExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNullExpr(SnapiParser.NullExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprTypeType}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprTypeType(SnapiParser.ExprTypeTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprTypeType}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprTypeType(SnapiParser.ExprTypeTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DivExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterDivExpr(SnapiParser.DivExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DivExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitDivExpr(SnapiParser.DivExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PlusExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPlusExpr(SnapiParser.PlusExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PlusExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPlusExpr(SnapiParser.PlusExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NumberExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNumberExpr(SnapiParser.NumberExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NumberExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNumberExpr(SnapiParser.NumberExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TrippleStringExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterTrippleStringExpr(SnapiParser.TrippleStringExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TrippleStringExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitTrippleStringExpr(SnapiParser.TrippleStringExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CompareExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterCompareExpr(SnapiParser.CompareExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CompareExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitCompareExpr(SnapiParser.CompareExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ListExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterListExpr(SnapiParser.ListExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ListExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitListExpr(SnapiParser.ListExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNotExpr(SnapiParser.NotExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNotExpr(SnapiParser.NotExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ModExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterModExpr(SnapiParser.ModExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ModExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitModExpr(SnapiParser.ModExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParenExpr(SnapiParser.ParenExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParenExpr(SnapiParser.ParenExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RecordExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterRecordExpr(SnapiParser.RecordExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RecordExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitRecordExpr(SnapiParser.RecordExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MinusExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMinusExpr(SnapiParser.MinusExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MinusExpr}
	 * labeled alternative in {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMinusExpr(SnapiParser.MinusExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#let}.
	 * @param ctx the parse tree
	 */
	void enterLet(SnapiParser.LetContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#let}.
	 * @param ctx the parse tree
	 */
	void exitLet(SnapiParser.LetContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#let_left}.
	 * @param ctx the parse tree
	 */
	void enterLet_left(SnapiParser.Let_leftContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#let_left}.
	 * @param ctx the parse tree
	 */
	void exitLet_left(SnapiParser.Let_leftContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LetBind}
	 * labeled alternative in {@link SnapiParser#let_decl}.
	 * @param ctx the parse tree
	 */
	void enterLetBind(SnapiParser.LetBindContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LetBind}
	 * labeled alternative in {@link SnapiParser#let_decl}.
	 * @param ctx the parse tree
	 */
	void exitLetBind(SnapiParser.LetBindContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LetFunDec}
	 * labeled alternative in {@link SnapiParser#let_decl}.
	 * @param ctx the parse tree
	 */
	void enterLetFunDec(SnapiParser.LetFunDecContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LetFunDec}
	 * labeled alternative in {@link SnapiParser#let_decl}.
	 * @param ctx the parse tree
	 */
	void exitLetFunDec(SnapiParser.LetFunDecContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#let_bind}.
	 * @param ctx the parse tree
	 */
	void enterLet_bind(SnapiParser.Let_bindContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#let_bind}.
	 * @param ctx the parse tree
	 */
	void exitLet_bind(SnapiParser.Let_bindContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#if_then_else}.
	 * @param ctx the parse tree
	 */
	void enterIf_then_else(SnapiParser.If_then_elseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#if_then_else}.
	 * @param ctx the parse tree
	 */
	void exitIf_then_else(SnapiParser.If_then_elseContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#lists}.
	 * @param ctx the parse tree
	 */
	void enterLists(SnapiParser.ListsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#lists}.
	 * @param ctx the parse tree
	 */
	void exitLists(SnapiParser.ListsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#lists_element}.
	 * @param ctx the parse tree
	 */
	void enterLists_element(SnapiParser.Lists_elementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#lists_element}.
	 * @param ctx the parse tree
	 */
	void exitLists_element(SnapiParser.Lists_elementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#records}.
	 * @param ctx the parse tree
	 */
	void enterRecords(SnapiParser.RecordsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#records}.
	 * @param ctx the parse tree
	 */
	void exitRecords(SnapiParser.RecordsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#record_elements}.
	 * @param ctx the parse tree
	 */
	void enterRecord_elements(SnapiParser.Record_elementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#record_elements}.
	 * @param ctx the parse tree
	 */
	void exitRecord_elements(SnapiParser.Record_elementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#record_element}.
	 * @param ctx the parse tree
	 */
	void enterRecord_element(SnapiParser.Record_elementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#record_element}.
	 * @param ctx the parse tree
	 */
	void exitRecord_element(SnapiParser.Record_elementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(SnapiParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(SnapiParser.NumberContext ctx);
}