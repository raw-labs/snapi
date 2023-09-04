// Generated from Snapi.g4 by ANTLR 4.13.0
package raw.compiler.rql2.antlr4.generated;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SnapiParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SnapiVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SnapiParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(SnapiParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunDec}
	 * labeled alternative in {@link SnapiParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunDec(SnapiParser.FunDecContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunDecExpr}
	 * labeled alternative in {@link SnapiParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunDecExpr(SnapiParser.FunDecExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#fun_dec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun_dec(SnapiParser.Fun_decContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#fun}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun(SnapiParser.FunContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#normal_fun}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormal_fun(SnapiParser.Normal_funContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#rec_fun}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRec_fun(SnapiParser.Rec_funContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#fun_proto}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun_proto(SnapiParser.Fun_protoContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#fun_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun_params(SnapiParser.Fun_paramsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#fun_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun_param(SnapiParser.Fun_paramContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#attr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttr(SnapiParser.AttrContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#fun_app}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun_app(SnapiParser.Fun_appContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#fun_ar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun_ar(SnapiParser.Fun_arContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#fun_args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun_args(SnapiParser.Fun_argsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#fun_arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun_arg(SnapiParser.Fun_argContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#fun_abs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun_abs(SnapiParser.Fun_absContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(SnapiParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#pure_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPure_type(SnapiParser.Pure_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#record_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecord_type(SnapiParser.Record_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#iterable_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIterable_type(SnapiParser.Iterable_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#list_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_type(SnapiParser.List_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#expr_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr_type(SnapiParser.Expr_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#typealias_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypealias_type(SnapiParser.Typealias_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#fun_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFun_type(SnapiParser.Fun_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(SnapiParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#let}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLet(SnapiParser.LetContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#let_left}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLet_left(SnapiParser.Let_leftContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#let_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLet_decl(SnapiParser.Let_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#let_bind}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLet_bind(SnapiParser.Let_bindContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#if_then_else}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_then_else(SnapiParser.If_then_elseContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#lists}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLists(SnapiParser.ListsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#lists_element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLists_element(SnapiParser.Lists_elementContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#records}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecords(SnapiParser.RecordsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#record_elements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecord_elements(SnapiParser.Record_elementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#record_element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecord_element(SnapiParser.Record_elementContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#ident}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdent(SnapiParser.IdentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(SnapiParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#number_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber_type(SnapiParser.Number_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#premetive_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPremetive_type(SnapiParser.Premetive_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#temporal_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemporal_type(SnapiParser.Temporal_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#compare_tokens}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompare_tokens(SnapiParser.Compare_tokensContext ctx);
	/**
	 * Visit a parse tree produced by {@link SnapiParser#bool_const}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool_const(SnapiParser.Bool_constContext ctx);
}