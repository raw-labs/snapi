// Generated from Snapi.g4 by ANTLR 4.13.0
package raw.compiler.rql2.antlr4.generated;
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
	 * Enter a parse tree produced by the {@code FunDec}
	 * labeled alternative in {@link SnapiParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterFunDec(SnapiParser.FunDecContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunDec}
	 * labeled alternative in {@link SnapiParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitFunDec(SnapiParser.FunDecContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunDecExpr}
	 * labeled alternative in {@link SnapiParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterFunDecExpr(SnapiParser.FunDecExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunDecExpr}
	 * labeled alternative in {@link SnapiParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitFunDecExpr(SnapiParser.FunDecExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#fun_dec}.
	 * @param ctx the parse tree
	 */
	void enterFun_dec(SnapiParser.Fun_decContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#fun_dec}.
	 * @param ctx the parse tree
	 */
	void exitFun_dec(SnapiParser.Fun_decContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#fun}.
	 * @param ctx the parse tree
	 */
	void enterFun(SnapiParser.FunContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#fun}.
	 * @param ctx the parse tree
	 */
	void exitFun(SnapiParser.FunContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#normal_fun}.
	 * @param ctx the parse tree
	 */
	void enterNormal_fun(SnapiParser.Normal_funContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#normal_fun}.
	 * @param ctx the parse tree
	 */
	void exitNormal_fun(SnapiParser.Normal_funContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#rec_fun}.
	 * @param ctx the parse tree
	 */
	void enterRec_fun(SnapiParser.Rec_funContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#rec_fun}.
	 * @param ctx the parse tree
	 */
	void exitRec_fun(SnapiParser.Rec_funContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#fun_proto}.
	 * @param ctx the parse tree
	 */
	void enterFun_proto(SnapiParser.Fun_protoContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#fun_proto}.
	 * @param ctx the parse tree
	 */
	void exitFun_proto(SnapiParser.Fun_protoContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#fun_params}.
	 * @param ctx the parse tree
	 */
	void enterFun_params(SnapiParser.Fun_paramsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#fun_params}.
	 * @param ctx the parse tree
	 */
	void exitFun_params(SnapiParser.Fun_paramsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#fun_param}.
	 * @param ctx the parse tree
	 */
	void enterFun_param(SnapiParser.Fun_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#fun_param}.
	 * @param ctx the parse tree
	 */
	void exitFun_param(SnapiParser.Fun_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#attr}.
	 * @param ctx the parse tree
	 */
	void enterAttr(SnapiParser.AttrContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#attr}.
	 * @param ctx the parse tree
	 */
	void exitAttr(SnapiParser.AttrContext ctx);
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
	 * Enter a parse tree produced by {@link SnapiParser#fun_arg}.
	 * @param ctx the parse tree
	 */
	void enterFun_arg(SnapiParser.Fun_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#fun_arg}.
	 * @param ctx the parse tree
	 */
	void exitFun_arg(SnapiParser.Fun_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#fun_abs}.
	 * @param ctx the parse tree
	 */
	void enterFun_abs(SnapiParser.Fun_absContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#fun_abs}.
	 * @param ctx the parse tree
	 */
	void exitFun_abs(SnapiParser.Fun_absContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(SnapiParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(SnapiParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#pure_type}.
	 * @param ctx the parse tree
	 */
	void enterPure_type(SnapiParser.Pure_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#pure_type}.
	 * @param ctx the parse tree
	 */
	void exitPure_type(SnapiParser.Pure_typeContext ctx);
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
	 * Enter a parse tree produced by {@link SnapiParser#typealias_type}.
	 * @param ctx the parse tree
	 */
	void enterTypealias_type(SnapiParser.Typealias_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#typealias_type}.
	 * @param ctx the parse tree
	 */
	void exitTypealias_type(SnapiParser.Typealias_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#fun_type}.
	 * @param ctx the parse tree
	 */
	void enterFun_type(SnapiParser.Fun_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#fun_type}.
	 * @param ctx the parse tree
	 */
	void exitFun_type(SnapiParser.Fun_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(SnapiParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(SnapiParser.ExprContext ctx);
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
	 * Enter a parse tree produced by {@link SnapiParser#let_decl}.
	 * @param ctx the parse tree
	 */
	void enterLet_decl(SnapiParser.Let_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#let_decl}.
	 * @param ctx the parse tree
	 */
	void exitLet_decl(SnapiParser.Let_declContext ctx);
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
	 * Enter a parse tree produced by {@link SnapiParser#ident}.
	 * @param ctx the parse tree
	 */
	void enterIdent(SnapiParser.IdentContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#ident}.
	 * @param ctx the parse tree
	 */
	void exitIdent(SnapiParser.IdentContext ctx);
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
	/**
	 * Enter a parse tree produced by {@link SnapiParser#number_type}.
	 * @param ctx the parse tree
	 */
	void enterNumber_type(SnapiParser.Number_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#number_type}.
	 * @param ctx the parse tree
	 */
	void exitNumber_type(SnapiParser.Number_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#premetive_type}.
	 * @param ctx the parse tree
	 */
	void enterPremetive_type(SnapiParser.Premetive_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#premetive_type}.
	 * @param ctx the parse tree
	 */
	void exitPremetive_type(SnapiParser.Premetive_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#temporal_type}.
	 * @param ctx the parse tree
	 */
	void enterTemporal_type(SnapiParser.Temporal_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#temporal_type}.
	 * @param ctx the parse tree
	 */
	void exitTemporal_type(SnapiParser.Temporal_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#compare_tokens}.
	 * @param ctx the parse tree
	 */
	void enterCompare_tokens(SnapiParser.Compare_tokensContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#compare_tokens}.
	 * @param ctx the parse tree
	 */
	void exitCompare_tokens(SnapiParser.Compare_tokensContext ctx);
	/**
	 * Enter a parse tree produced by {@link SnapiParser#bool_const}.
	 * @param ctx the parse tree
	 */
	void enterBool_const(SnapiParser.Bool_constContext ctx);
	/**
	 * Exit a parse tree produced by {@link SnapiParser#bool_const}.
	 * @param ctx the parse tree
	 */
	void exitBool_const(SnapiParser.Bool_constContext ctx);
}