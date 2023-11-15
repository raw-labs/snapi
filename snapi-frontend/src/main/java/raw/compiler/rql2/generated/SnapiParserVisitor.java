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

// Generated from SnapiParser.g4 by ANTLR 4.13.1
package raw.compiler.rql2.generated;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced by {@link
 * SnapiParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for operations with no return
 *     type.
 */
public interface SnapiParserVisitor<T> extends ParseTreeVisitor<T> {
  /**
   * Visit a parse tree produced by {@link SnapiParser#prog}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitProg(SnapiParser.ProgContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunDecStat} labeled alternative in {@link
   * SnapiParser#stat}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunDecStat(SnapiParser.FunDecStatContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunDecExprStat} labeled alternative in {@link
   * SnapiParser#stat}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunDecExprStat(SnapiParser.FunDecExprStatContext ctx);
  /**
   * Visit a parse tree produced by the {@code MethodDec} labeled alternative in {@link
   * SnapiParser#method_dec}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMethodDec(SnapiParser.MethodDecContext ctx);
  /**
   * Visit a parse tree produced by the {@code NormalFun} labeled alternative in {@link
   * SnapiParser#fun_dec}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNormalFun(SnapiParser.NormalFunContext ctx);
  /**
   * Visit a parse tree produced by the {@code RecFun} labeled alternative in {@link
   * SnapiParser#fun_dec}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRecFun(SnapiParser.RecFunContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#fun_proto}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFun_proto(SnapiParser.Fun_protoContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunParamAttr} labeled alternative in {@link
   * SnapiParser#fun_param}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunParamAttr(SnapiParser.FunParamAttrContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunParamAttrExpr} labeled alternative in {@link
   * SnapiParser#fun_param}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunParamAttrExpr(SnapiParser.FunParamAttrExprContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#attr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAttr(SnapiParser.AttrContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#type_attr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitType_attr(SnapiParser.Type_attrContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#fun_ar}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFun_ar(SnapiParser.Fun_arContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#fun_args}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFun_args(SnapiParser.Fun_argsContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunArgExpr} labeled alternative in {@link
   * SnapiParser#fun_arg}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunArgExpr(SnapiParser.FunArgExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code NamedFunArgExpr} labeled alternative in {@link
   * SnapiParser#fun_arg}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNamedFunArgExpr(SnapiParser.NamedFunArgExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunAbs} labeled alternative in {@link
   * SnapiParser#fun_abs}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunAbs(SnapiParser.FunAbsContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunAbsUnnamed} labeled alternative in {@link
   * SnapiParser#fun_abs}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunAbsUnnamed(SnapiParser.FunAbsUnnamedContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunProtoLambdaMultiParam} labeled alternative in
   * {@link SnapiParser#fun_proto_lambda}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunProtoLambdaMultiParam(SnapiParser.FunProtoLambdaMultiParamContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunProtoLambdaSingleParam} labeled alternative in
   * {@link SnapiParser#fun_proto_lambda}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunProtoLambdaSingleParam(SnapiParser.FunProtoLambdaSingleParamContext ctx);
  /**
   * Visit a parse tree produced by the {@code IterableTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIterableTypeType(SnapiParser.IterableTypeTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code ListTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitListTypeType(SnapiParser.ListTypeTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunTypeWithParamsType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunTypeWithParamsType(SnapiParser.FunTypeWithParamsTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code NullableTryableType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNullableTryableType(SnapiParser.NullableTryableTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code ExprTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExprTypeType(SnapiParser.ExprTypeTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code RecordTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRecordTypeType(SnapiParser.RecordTypeTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code TypeWithParenType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTypeWithParenType(SnapiParser.TypeWithParenTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code PackageEntryTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPackageEntryTypeType(SnapiParser.PackageEntryTypeTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code OrTypeFunType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitOrTypeFunType(SnapiParser.OrTypeFunTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code OrTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitOrTypeType(SnapiParser.OrTypeTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code PrimitiveTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPrimitiveTypeType(SnapiParser.PrimitiveTypeTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code TypeAliasType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTypeAliasType(SnapiParser.TypeAliasTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code PackageTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPackageTypeType(SnapiParser.PackageTypeTypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunTypeType(SnapiParser.FunTypeTypeContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#or_type}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitOr_type(SnapiParser.Or_typeContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#param_list}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitParam_list(SnapiParser.Param_listContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#record_type}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRecord_type(SnapiParser.Record_typeContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#record_attr_list}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRecord_attr_list(SnapiParser.Record_attr_listContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#iterable_type}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIterable_type(SnapiParser.Iterable_typeContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#list_type}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitList_type(SnapiParser.List_typeContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#expr_type}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExpr_type(SnapiParser.Expr_typeContext ctx);
  /**
   * Visit a parse tree produced by the {@code MulExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMulExpr(SnapiParser.MulExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code AndExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAndExpr(SnapiParser.AndExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code MinusUnaryExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMinusUnaryExpr(SnapiParser.MinusUnaryExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code StringLiteralExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitStringLiteralExpr(SnapiParser.StringLiteralExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code NullExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNullExpr(SnapiParser.NullExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code SignedNumberExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSignedNumberExpr(SnapiParser.SignedNumberExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code PlusExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPlusExpr(SnapiParser.PlusExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code CompareExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCompareExpr(SnapiParser.CompareExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code PlusUnaryExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPlusUnaryExpr(SnapiParser.PlusUnaryExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code ListExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitListExpr(SnapiParser.ListExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code NotExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNotExpr(SnapiParser.NotExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code RecordExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRecordExpr(SnapiParser.RecordExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code MinusExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMinusExpr(SnapiParser.MinusExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code IdentExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIdentExpr(SnapiParser.IdentExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code BoolConstExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBoolConstExpr(SnapiParser.BoolConstExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code ProjectionExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitProjectionExpr(SnapiParser.ProjectionExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code LetExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLetExpr(SnapiParser.LetExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunAbsExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunAbsExpr(SnapiParser.FunAbsExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunAppExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunAppExpr(SnapiParser.FunAppExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code OrExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitOrExpr(SnapiParser.OrExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code IfThenElseExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIfThenElseExpr(SnapiParser.IfThenElseExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code ExprTypeExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExprTypeExpr(SnapiParser.ExprTypeExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code DivExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDivExpr(SnapiParser.DivExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code PackageIdnExp} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPackageIdnExp(SnapiParser.PackageIdnExpContext ctx);
  /**
   * Visit a parse tree produced by the {@code ModExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitModExpr(SnapiParser.ModExprContext ctx);
  /**
   * Visit a parse tree produced by the {@code ParenExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitParenExpr(SnapiParser.ParenExprContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#let}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLet(SnapiParser.LetContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#let_left}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLet_left(SnapiParser.Let_leftContext ctx);
  /**
   * Visit a parse tree produced by the {@code LetBind} labeled alternative in {@link
   * SnapiParser#let_decl}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLetBind(SnapiParser.LetBindContext ctx);
  /**
   * Visit a parse tree produced by the {@code LetFunDec} labeled alternative in {@link
   * SnapiParser#let_decl}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLetFunDec(SnapiParser.LetFunDecContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#let_bind}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLet_bind(SnapiParser.Let_bindContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#if_then_else}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIf_then_else(SnapiParser.If_then_elseContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#lists}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLists(SnapiParser.ListsContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#lists_element}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLists_element(SnapiParser.Lists_elementContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#records}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRecords(SnapiParser.RecordsContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#record_elements}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRecord_elements(SnapiParser.Record_elementsContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#record_element}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRecord_element(SnapiParser.Record_elementContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#signed_number}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSigned_number(SnapiParser.Signed_numberContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#number}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNumber(SnapiParser.NumberContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#primitive_types}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPrimitive_types(SnapiParser.Primitive_typesContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#string_literal}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitString_literal(SnapiParser.String_literalContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#triple_string_literal}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTriple_string_literal(SnapiParser.Triple_string_literalContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#compare_tokens}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCompare_tokens(SnapiParser.Compare_tokensContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#bool_const}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBool_const(SnapiParser.Bool_constContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#ident}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIdent(SnapiParser.IdentContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#package_idn_exp}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPackage_idn_exp(SnapiParser.Package_idn_expContext ctx);
  /**
   * Visit a parse tree produced by {@link SnapiParser#nullable_tryable}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNullable_tryable(SnapiParser.Nullable_tryableContext ctx);
}
