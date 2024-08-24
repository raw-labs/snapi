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

package com.rawlabs.snapi.truffle.emitter.builtin.jdbc;

import static com.rawlabs.snapi.truffle.emitter.builtin.CompilerScalaConsts.*;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.jdbc.JdbcQueryNode;
import com.rawlabs.snapi.truffle.ast.io.jdbc.*;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;
import com.rawlabs.snapi.truffle.runtime.exceptions.rdbms.JdbcExceptionHandler;
import scala.collection.JavaConverters;

public class Jdbc {
  static public JdbcQueryNode query(
      ExpressionNode location,
      ExpressionNode query,
      Type t,
      JdbcExceptionHandler exceptionHandler,
      SnapiLanguage lang) {
    SnapiIterableType iterableType = (SnapiIterableType) t;
    SnapiRecordType recordType = (SnapiRecordType) iterableType.innerType();
    assert iterableType.props().isEmpty();
    assert recordType.props().isEmpty();

    FrameDescriptor frameDescriptor = new FrameDescriptor();

    ProgramExpressionNode[] columnParsers =
        JavaConverters.asJavaCollection(recordType.atts()).stream().map(a -> (SnapiAttrType) a)
            .map(att -> columnReader(att.idn(), att.tipe(), lang))
            .toArray(ProgramExpressionNode[]::new);
    RecordReadJdbcQuery recordParser =
        new RecordReadJdbcQuery(
            columnParsers,
            JavaConverters.asJavaCollection(recordType.atts()).stream().map(a -> (SnapiAttrType) a).toArray(SnapiAttrType[]::new));
    return new JdbcQueryNode(
        location,
        query,
        new ProgramExpressionNode(lang, frameDescriptor, recordParser),
        exceptionHandler);
  }

  static private ProgramExpressionNode columnReader(String colName, Type t, SnapiLanguage lang) {
    FrameDescriptor frameDescriptor = new FrameDescriptor();
    ExpressionNode node = switch (t){
      case SnapiTypeWithProperties r when r.props().contains(tryable) -> {
        ProgramExpressionNode inner = columnReader(colName,r.cloneAndRemoveProp(tryable),lang);
        yield new TryableReadJdbcQuery(inner, colName);
      }
      case SnapiTypeWithProperties r when r.props().contains(nullable) -> {
        ProgramExpressionNode inner = columnReader(colName,r.cloneAndRemoveProp(nullable),lang);
        yield new NullableReadJdbcQuery(inner, colName);
      }
      case SnapiByteType ignored ->  new ByteReadJdbcQuery(colName);
      case SnapiShortType ignored ->  new ShortReadJdbcQuery(colName);
      case SnapiIntType ignored ->  new IntReadJdbcQuery(colName);
      case SnapiLongType ignored ->  new LongReadJdbcQuery(colName);
      case SnapiFloatType ignored ->  new FloatReadJdbcQuery(colName);
      case SnapiDoubleType ignored ->  new DoubleReadJdbcQuery(colName);
      case SnapiDecimalType ignored ->  new DecimalReadJdbcQuery(colName);
      case SnapiStringType ignored ->  new StringReadJdbcQuery(colName);
      case SnapiDateType ignored ->  new DateReadJdbcQuery(colName);
      case SnapiTimeType ignored ->  new TimeReadJdbcQuery(colName);
      case SnapiTimestampType ignored ->  new TimestampReadJdbcQuery(colName);
      case SnapiBoolType ignored ->  new BoolReadJdbcQuery(colName);
      case SnapiBinaryType ignored ->  new BinaryReadJdbcQuery(colName);
      default -> throw new TruffleInternalErrorException();
    };
    return new ProgramExpressionNode(lang, frameDescriptor, node);
  }
}
