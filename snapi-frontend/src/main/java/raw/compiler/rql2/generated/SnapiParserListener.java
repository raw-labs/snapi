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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/** This interface defines a complete listener for a parse tree produced by {@link SnapiParser}. */
public interface SnapiParserListener extends ParseTreeListener {
  /**
   * Enter a parse tree produced by {@link SnapiParser#prog}.
   *
   * @param ctx the parse tree
   */
  void enterProg(SnapiParser.ProgContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#prog}.
   *
   * @param ctx the parse tree
   */
  void exitProg(SnapiParser.ProgContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunDecStat} labeled alternative in {@link
   * SnapiParser#stat}.
   *
   * @param ctx the parse tree
   */
  void enterFunDecStat(SnapiParser.FunDecStatContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunDecStat} labeled alternative in {@link
   * SnapiParser#stat}.
   *
   * @param ctx the parse tree
   */
  void exitFunDecStat(SnapiParser.FunDecStatContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunDecExprStat} labeled alternative in {@link
   * SnapiParser#stat}.
   *
   * @param ctx the parse tree
   */
  void enterFunDecExprStat(SnapiParser.FunDecExprStatContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunDecExprStat} labeled alternative in {@link
   * SnapiParser#stat}.
   *
   * @param ctx the parse tree
   */
  void exitFunDecExprStat(SnapiParser.FunDecExprStatContext ctx);
  /**
   * Enter a parse tree produced by the {@code MethodDec} labeled alternative in {@link
   * SnapiParser#method_dec}.
   *
   * @param ctx the parse tree
   */
  void enterMethodDec(SnapiParser.MethodDecContext ctx);
  /**
   * Exit a parse tree produced by the {@code MethodDec} labeled alternative in {@link
   * SnapiParser#method_dec}.
   *
   * @param ctx the parse tree
   */
  void exitMethodDec(SnapiParser.MethodDecContext ctx);
  /**
   * Enter a parse tree produced by the {@code NormalFun} labeled alternative in {@link
   * SnapiParser#fun_dec}.
   *
   * @param ctx the parse tree
   */
  void enterNormalFun(SnapiParser.NormalFunContext ctx);
  /**
   * Exit a parse tree produced by the {@code NormalFun} labeled alternative in {@link
   * SnapiParser#fun_dec}.
   *
   * @param ctx the parse tree
   */
  void exitNormalFun(SnapiParser.NormalFunContext ctx);
  /**
   * Enter a parse tree produced by the {@code RecFun} labeled alternative in {@link
   * SnapiParser#fun_dec}.
   *
   * @param ctx the parse tree
   */
  void enterRecFun(SnapiParser.RecFunContext ctx);
  /**
   * Exit a parse tree produced by the {@code RecFun} labeled alternative in {@link
   * SnapiParser#fun_dec}.
   *
   * @param ctx the parse tree
   */
  void exitRecFun(SnapiParser.RecFunContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#fun_proto}.
   *
   * @param ctx the parse tree
   */
  void enterFun_proto(SnapiParser.Fun_protoContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#fun_proto}.
   *
   * @param ctx the parse tree
   */
  void exitFun_proto(SnapiParser.Fun_protoContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunParamAttr} labeled alternative in {@link
   * SnapiParser#fun_param}.
   *
   * @param ctx the parse tree
   */
  void enterFunParamAttr(SnapiParser.FunParamAttrContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunParamAttr} labeled alternative in {@link
   * SnapiParser#fun_param}.
   *
   * @param ctx the parse tree
   */
  void exitFunParamAttr(SnapiParser.FunParamAttrContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunParamAttrExpr} labeled alternative in {@link
   * SnapiParser#fun_param}.
   *
   * @param ctx the parse tree
   */
  void enterFunParamAttrExpr(SnapiParser.FunParamAttrExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunParamAttrExpr} labeled alternative in {@link
   * SnapiParser#fun_param}.
   *
   * @param ctx the parse tree
   */
  void exitFunParamAttrExpr(SnapiParser.FunParamAttrExprContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#attr}.
   *
   * @param ctx the parse tree
   */
  void enterAttr(SnapiParser.AttrContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#attr}.
   *
   * @param ctx the parse tree
   */
  void exitAttr(SnapiParser.AttrContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#type_attr}.
   *
   * @param ctx the parse tree
   */
  void enterType_attr(SnapiParser.Type_attrContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#type_attr}.
   *
   * @param ctx the parse tree
   */
  void exitType_attr(SnapiParser.Type_attrContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#fun_ar}.
   *
   * @param ctx the parse tree
   */
  void enterFun_ar(SnapiParser.Fun_arContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#fun_ar}.
   *
   * @param ctx the parse tree
   */
  void exitFun_ar(SnapiParser.Fun_arContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#fun_args}.
   *
   * @param ctx the parse tree
   */
  void enterFun_args(SnapiParser.Fun_argsContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#fun_args}.
   *
   * @param ctx the parse tree
   */
  void exitFun_args(SnapiParser.Fun_argsContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunArgExpr} labeled alternative in {@link
   * SnapiParser#fun_arg}.
   *
   * @param ctx the parse tree
   */
  void enterFunArgExpr(SnapiParser.FunArgExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunArgExpr} labeled alternative in {@link
   * SnapiParser#fun_arg}.
   *
   * @param ctx the parse tree
   */
  void exitFunArgExpr(SnapiParser.FunArgExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code NamedFunArgExpr} labeled alternative in {@link
   * SnapiParser#fun_arg}.
   *
   * @param ctx the parse tree
   */
  void enterNamedFunArgExpr(SnapiParser.NamedFunArgExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code NamedFunArgExpr} labeled alternative in {@link
   * SnapiParser#fun_arg}.
   *
   * @param ctx the parse tree
   */
  void exitNamedFunArgExpr(SnapiParser.NamedFunArgExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunAbs} labeled alternative in {@link
   * SnapiParser#fun_abs}.
   *
   * @param ctx the parse tree
   */
  void enterFunAbs(SnapiParser.FunAbsContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunAbs} labeled alternative in {@link
   * SnapiParser#fun_abs}.
   *
   * @param ctx the parse tree
   */
  void exitFunAbs(SnapiParser.FunAbsContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunAbsUnnamed} labeled alternative in {@link
   * SnapiParser#fun_abs}.
   *
   * @param ctx the parse tree
   */
  void enterFunAbsUnnamed(SnapiParser.FunAbsUnnamedContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunAbsUnnamed} labeled alternative in {@link
   * SnapiParser#fun_abs}.
   *
   * @param ctx the parse tree
   */
  void exitFunAbsUnnamed(SnapiParser.FunAbsUnnamedContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunProtoLambdaMultiParam} labeled alternative in
   * {@link SnapiParser#fun_proto_lambda}.
   *
   * @param ctx the parse tree
   */
  void enterFunProtoLambdaMultiParam(SnapiParser.FunProtoLambdaMultiParamContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunProtoLambdaMultiParam} labeled alternative in
   * {@link SnapiParser#fun_proto_lambda}.
   *
   * @param ctx the parse tree
   */
  void exitFunProtoLambdaMultiParam(SnapiParser.FunProtoLambdaMultiParamContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunProtoLambdaSingleParam} labeled alternative in
   * {@link SnapiParser#fun_proto_lambda}.
   *
   * @param ctx the parse tree
   */
  void enterFunProtoLambdaSingleParam(SnapiParser.FunProtoLambdaSingleParamContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunProtoLambdaSingleParam} labeled alternative in
   * {@link SnapiParser#fun_proto_lambda}.
   *
   * @param ctx the parse tree
   */
  void exitFunProtoLambdaSingleParam(SnapiParser.FunProtoLambdaSingleParamContext ctx);
  /**
   * Enter a parse tree produced by the {@code IterableTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterIterableTypeType(SnapiParser.IterableTypeTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code IterableTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitIterableTypeType(SnapiParser.IterableTypeTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code ListTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterListTypeType(SnapiParser.ListTypeTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code ListTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitListTypeType(SnapiParser.ListTypeTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunTypeWithParamsType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterFunTypeWithParamsType(SnapiParser.FunTypeWithParamsTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunTypeWithParamsType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitFunTypeWithParamsType(SnapiParser.FunTypeWithParamsTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code NullableTryableType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterNullableTryableType(SnapiParser.NullableTryableTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code NullableTryableType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitNullableTryableType(SnapiParser.NullableTryableTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code ExprTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterExprTypeType(SnapiParser.ExprTypeTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code ExprTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitExprTypeType(SnapiParser.ExprTypeTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code RecordTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterRecordTypeType(SnapiParser.RecordTypeTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code RecordTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitRecordTypeType(SnapiParser.RecordTypeTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code TypeWithParenType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterTypeWithParenType(SnapiParser.TypeWithParenTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code TypeWithParenType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitTypeWithParenType(SnapiParser.TypeWithParenTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code PackageEntryTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterPackageEntryTypeType(SnapiParser.PackageEntryTypeTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code PackageEntryTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitPackageEntryTypeType(SnapiParser.PackageEntryTypeTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code OrTypeFunType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterOrTypeFunType(SnapiParser.OrTypeFunTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code OrTypeFunType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitOrTypeFunType(SnapiParser.OrTypeFunTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code OrTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterOrTypeType(SnapiParser.OrTypeTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code OrTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitOrTypeType(SnapiParser.OrTypeTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code PrimitiveTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterPrimitiveTypeType(SnapiParser.PrimitiveTypeTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code PrimitiveTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitPrimitiveTypeType(SnapiParser.PrimitiveTypeTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code TypeAliasType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterTypeAliasType(SnapiParser.TypeAliasTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code TypeAliasType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitTypeAliasType(SnapiParser.TypeAliasTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code PackageTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterPackageTypeType(SnapiParser.PackageTypeTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code PackageTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitPackageTypeType(SnapiParser.PackageTypeTypeContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void enterFunTypeType(SnapiParser.FunTypeTypeContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunTypeType} labeled alternative in {@link
   * SnapiParser#tipe}.
   *
   * @param ctx the parse tree
   */
  void exitFunTypeType(SnapiParser.FunTypeTypeContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#or_type}.
   *
   * @param ctx the parse tree
   */
  void enterOr_type(SnapiParser.Or_typeContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#or_type}.
   *
   * @param ctx the parse tree
   */
  void exitOr_type(SnapiParser.Or_typeContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#param_list}.
   *
   * @param ctx the parse tree
   */
  void enterParam_list(SnapiParser.Param_listContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#param_list}.
   *
   * @param ctx the parse tree
   */
  void exitParam_list(SnapiParser.Param_listContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#record_type}.
   *
   * @param ctx the parse tree
   */
  void enterRecord_type(SnapiParser.Record_typeContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#record_type}.
   *
   * @param ctx the parse tree
   */
  void exitRecord_type(SnapiParser.Record_typeContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#record_attr_list}.
   *
   * @param ctx the parse tree
   */
  void enterRecord_attr_list(SnapiParser.Record_attr_listContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#record_attr_list}.
   *
   * @param ctx the parse tree
   */
  void exitRecord_attr_list(SnapiParser.Record_attr_listContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#iterable_type}.
   *
   * @param ctx the parse tree
   */
  void enterIterable_type(SnapiParser.Iterable_typeContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#iterable_type}.
   *
   * @param ctx the parse tree
   */
  void exitIterable_type(SnapiParser.Iterable_typeContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#list_type}.
   *
   * @param ctx the parse tree
   */
  void enterList_type(SnapiParser.List_typeContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#list_type}.
   *
   * @param ctx the parse tree
   */
  void exitList_type(SnapiParser.List_typeContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#expr_type}.
   *
   * @param ctx the parse tree
   */
  void enterExpr_type(SnapiParser.Expr_typeContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#expr_type}.
   *
   * @param ctx the parse tree
   */
  void exitExpr_type(SnapiParser.Expr_typeContext ctx);
  /**
   * Enter a parse tree produced by the {@code MulExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterMulExpr(SnapiParser.MulExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code MulExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitMulExpr(SnapiParser.MulExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code AndExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterAndExpr(SnapiParser.AndExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code AndExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitAndExpr(SnapiParser.AndExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code MinusUnaryExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterMinusUnaryExpr(SnapiParser.MinusUnaryExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code MinusUnaryExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitMinusUnaryExpr(SnapiParser.MinusUnaryExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code StringLiteralExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterStringLiteralExpr(SnapiParser.StringLiteralExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code StringLiteralExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitStringLiteralExpr(SnapiParser.StringLiteralExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code NullExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterNullExpr(SnapiParser.NullExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code NullExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitNullExpr(SnapiParser.NullExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code SignedNumberExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterSignedNumberExpr(SnapiParser.SignedNumberExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code SignedNumberExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitSignedNumberExpr(SnapiParser.SignedNumberExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code PlusExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterPlusExpr(SnapiParser.PlusExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code PlusExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitPlusExpr(SnapiParser.PlusExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code CompareExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterCompareExpr(SnapiParser.CompareExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code CompareExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitCompareExpr(SnapiParser.CompareExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code PlusUnaryExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterPlusUnaryExpr(SnapiParser.PlusUnaryExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code PlusUnaryExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitPlusUnaryExpr(SnapiParser.PlusUnaryExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code ListExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterListExpr(SnapiParser.ListExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code ListExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitListExpr(SnapiParser.ListExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code NotExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterNotExpr(SnapiParser.NotExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code NotExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitNotExpr(SnapiParser.NotExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code RecordExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterRecordExpr(SnapiParser.RecordExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code RecordExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitRecordExpr(SnapiParser.RecordExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code MinusExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterMinusExpr(SnapiParser.MinusExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code MinusExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitMinusExpr(SnapiParser.MinusExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code IdentExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterIdentExpr(SnapiParser.IdentExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code IdentExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitIdentExpr(SnapiParser.IdentExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code BoolConstExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterBoolConstExpr(SnapiParser.BoolConstExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code BoolConstExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitBoolConstExpr(SnapiParser.BoolConstExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code ProjectionExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterProjectionExpr(SnapiParser.ProjectionExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code ProjectionExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitProjectionExpr(SnapiParser.ProjectionExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code LetExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterLetExpr(SnapiParser.LetExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code LetExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitLetExpr(SnapiParser.LetExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunAbsExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterFunAbsExpr(SnapiParser.FunAbsExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunAbsExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitFunAbsExpr(SnapiParser.FunAbsExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code FunAppExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterFunAppExpr(SnapiParser.FunAppExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code FunAppExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitFunAppExpr(SnapiParser.FunAppExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code OrExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterOrExpr(SnapiParser.OrExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code OrExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitOrExpr(SnapiParser.OrExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code IfThenElseExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterIfThenElseExpr(SnapiParser.IfThenElseExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code IfThenElseExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitIfThenElseExpr(SnapiParser.IfThenElseExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code ExprTypeExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterExprTypeExpr(SnapiParser.ExprTypeExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code ExprTypeExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitExprTypeExpr(SnapiParser.ExprTypeExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code DivExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterDivExpr(SnapiParser.DivExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code DivExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitDivExpr(SnapiParser.DivExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code PackageIdnExp} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterPackageIdnExp(SnapiParser.PackageIdnExpContext ctx);
  /**
   * Exit a parse tree produced by the {@code PackageIdnExp} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitPackageIdnExp(SnapiParser.PackageIdnExpContext ctx);
  /**
   * Enter a parse tree produced by the {@code BinaryConstExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterBinaryConstExpr(SnapiParser.BinaryConstExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code BinaryConstExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitBinaryConstExpr(SnapiParser.BinaryConstExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code ModExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterModExpr(SnapiParser.ModExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code ModExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitModExpr(SnapiParser.ModExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code ParenExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterParenExpr(SnapiParser.ParenExprContext ctx);
  /**
   * Exit a parse tree produced by the {@code ParenExpr} labeled alternative in {@link
   * SnapiParser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitParenExpr(SnapiParser.ParenExprContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#let}.
   *
   * @param ctx the parse tree
   */
  void enterLet(SnapiParser.LetContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#let}.
   *
   * @param ctx the parse tree
   */
  void exitLet(SnapiParser.LetContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#let_left}.
   *
   * @param ctx the parse tree
   */
  void enterLet_left(SnapiParser.Let_leftContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#let_left}.
   *
   * @param ctx the parse tree
   */
  void exitLet_left(SnapiParser.Let_leftContext ctx);
  /**
   * Enter a parse tree produced by the {@code LetBind} labeled alternative in {@link
   * SnapiParser#let_decl}.
   *
   * @param ctx the parse tree
   */
  void enterLetBind(SnapiParser.LetBindContext ctx);
  /**
   * Exit a parse tree produced by the {@code LetBind} labeled alternative in {@link
   * SnapiParser#let_decl}.
   *
   * @param ctx the parse tree
   */
  void exitLetBind(SnapiParser.LetBindContext ctx);
  /**
   * Enter a parse tree produced by the {@code LetFunDec} labeled alternative in {@link
   * SnapiParser#let_decl}.
   *
   * @param ctx the parse tree
   */
  void enterLetFunDec(SnapiParser.LetFunDecContext ctx);
  /**
   * Exit a parse tree produced by the {@code LetFunDec} labeled alternative in {@link
   * SnapiParser#let_decl}.
   *
   * @param ctx the parse tree
   */
  void exitLetFunDec(SnapiParser.LetFunDecContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#let_bind}.
   *
   * @param ctx the parse tree
   */
  void enterLet_bind(SnapiParser.Let_bindContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#let_bind}.
   *
   * @param ctx the parse tree
   */
  void exitLet_bind(SnapiParser.Let_bindContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#if_then_else}.
   *
   * @param ctx the parse tree
   */
  void enterIf_then_else(SnapiParser.If_then_elseContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#if_then_else}.
   *
   * @param ctx the parse tree
   */
  void exitIf_then_else(SnapiParser.If_then_elseContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#lists}.
   *
   * @param ctx the parse tree
   */
  void enterLists(SnapiParser.ListsContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#lists}.
   *
   * @param ctx the parse tree
   */
  void exitLists(SnapiParser.ListsContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#lists_element}.
   *
   * @param ctx the parse tree
   */
  void enterLists_element(SnapiParser.Lists_elementContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#lists_element}.
   *
   * @param ctx the parse tree
   */
  void exitLists_element(SnapiParser.Lists_elementContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#records}.
   *
   * @param ctx the parse tree
   */
  void enterRecords(SnapiParser.RecordsContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#records}.
   *
   * @param ctx the parse tree
   */
  void exitRecords(SnapiParser.RecordsContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#record_elements}.
   *
   * @param ctx the parse tree
   */
  void enterRecord_elements(SnapiParser.Record_elementsContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#record_elements}.
   *
   * @param ctx the parse tree
   */
  void exitRecord_elements(SnapiParser.Record_elementsContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#record_element}.
   *
   * @param ctx the parse tree
   */
  void enterRecord_element(SnapiParser.Record_elementContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#record_element}.
   *
   * @param ctx the parse tree
   */
  void exitRecord_element(SnapiParser.Record_elementContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#signed_number}.
   *
   * @param ctx the parse tree
   */
  void enterSigned_number(SnapiParser.Signed_numberContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#signed_number}.
   *
   * @param ctx the parse tree
   */
  void exitSigned_number(SnapiParser.Signed_numberContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#number}.
   *
   * @param ctx the parse tree
   */
  void enterNumber(SnapiParser.NumberContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#number}.
   *
   * @param ctx the parse tree
   */
  void exitNumber(SnapiParser.NumberContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#primitive_types}.
   *
   * @param ctx the parse tree
   */
  void enterPrimitive_types(SnapiParser.Primitive_typesContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#primitive_types}.
   *
   * @param ctx the parse tree
   */
  void exitPrimitive_types(SnapiParser.Primitive_typesContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#string_literal}.
   *
   * @param ctx the parse tree
   */
  void enterString_literal(SnapiParser.String_literalContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#string_literal}.
   *
   * @param ctx the parse tree
   */
  void exitString_literal(SnapiParser.String_literalContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#triple_string_literal}.
   *
   * @param ctx the parse tree
   */
  void enterTriple_string_literal(SnapiParser.Triple_string_literalContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#triple_string_literal}.
   *
   * @param ctx the parse tree
   */
  void exitTriple_string_literal(SnapiParser.Triple_string_literalContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#compare_tokens}.
   *
   * @param ctx the parse tree
   */
  void enterCompare_tokens(SnapiParser.Compare_tokensContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#compare_tokens}.
   *
   * @param ctx the parse tree
   */
  void exitCompare_tokens(SnapiParser.Compare_tokensContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#bool_const}.
   *
   * @param ctx the parse tree
   */
  void enterBool_const(SnapiParser.Bool_constContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#bool_const}.
   *
   * @param ctx the parse tree
   */
  void exitBool_const(SnapiParser.Bool_constContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#ident}.
   *
   * @param ctx the parse tree
   */
  void enterIdent(SnapiParser.IdentContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#ident}.
   *
   * @param ctx the parse tree
   */
  void exitIdent(SnapiParser.IdentContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#package_idn_exp}.
   *
   * @param ctx the parse tree
   */
  void enterPackage_idn_exp(SnapiParser.Package_idn_expContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#package_idn_exp}.
   *
   * @param ctx the parse tree
   */
  void exitPackage_idn_exp(SnapiParser.Package_idn_expContext ctx);
  /**
   * Enter a parse tree produced by {@link SnapiParser#nullable_tryable}.
   *
   * @param ctx the parse tree
   */
  void enterNullable_tryable(SnapiParser.Nullable_tryableContext ctx);
  /**
   * Exit a parse tree produced by {@link SnapiParser#nullable_tryable}.
   *
   * @param ctx the parse tree
   */
  void exitNullable_tryable(SnapiParser.Nullable_tryableContext ctx);
}
