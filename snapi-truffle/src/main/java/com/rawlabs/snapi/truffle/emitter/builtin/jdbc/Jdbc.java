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
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.jdbc.JdbcQueryNode;
import com.rawlabs.snapi.truffle.runtime.ast.io.csv.writer.internal.*;
import com.rawlabs.snapi.truffle.runtime.ast.io.jdbc.*;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.RawTruffleInternalErrorException;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.rdbms.JdbcExceptionHandler;
import scala.collection.JavaConverters;

public class Jdbc {
  static public JdbcQueryNode query(
      ExpressionNode location,
      ExpressionNode query,
      Type t,
      JdbcExceptionHandler exceptionHandler,
      RawLanguage lang) {
    Rql2IterableType iterableType = (Rql2IterableType) t;
    Rql2RecordType recordType = (Rql2RecordType) iterableType.innerType();
    assert iterableType.props().isEmpty();
    assert recordType.props().isEmpty();

    FrameDescriptor frameDescriptor = new FrameDescriptor();

    ProgramExpressionNode[] columnParsers =
        JavaConverters.asJavaCollection(recordType.atts()).stream().map(a -> (Rql2AttrType) a)
            .map(att -> columnReader(att.idn(), att.tipe(), lang))
            .toArray(ProgramExpressionNode[]::new);
    RecordReadJdbcQuery recordParser =
        new RecordReadJdbcQuery(
            columnParsers,
            JavaConverters.asJavaCollection(recordType.atts()).stream().map(a -> (Rql2AttrType) a).toArray(Rql2AttrType[]::new));
    return new JdbcQueryNode(
        location,
        query,
        new ProgramExpressionNode(lang, frameDescriptor, recordParser),
        exceptionHandler);
  }

  static private ProgramExpressionNode columnReader(String colName, Type t, RawLanguage lang) {
    FrameDescriptor frameDescriptor = new FrameDescriptor();
    ExpressionNode node = switch (t){
      case Rql2TypeWithProperties r when r.props().contains(tryable) -> {
        ProgramExpressionNode inner = columnReader(colName,r.cloneAndRemoveProp(tryable),lang);
        yield new TryableReadJdbcQuery(inner, colName);
      }
      case Rql2TypeWithProperties r when r.props().contains(nullable) -> {
        ProgramExpressionNode inner = columnReader(colName,r.cloneAndRemoveProp(nullable),lang);
        yield new NullableReadJdbcQuery(inner, colName);
      }
      case Rql2ByteType ignored ->  new ByteReadJdbcQuery(colName);
      case Rql2ShortType ignored ->  new ShortReadJdbcQuery(colName);
      case Rql2IntType ignored ->  new IntReadJdbcQuery(colName);
      case Rql2LongType ignored ->  new LongReadJdbcQuery(colName);
      case Rql2FloatType ignored ->  new FloatReadJdbcQuery(colName);
      case Rql2DoubleType ignored ->  new DoubleReadJdbcQuery(colName);
      case Rql2DecimalType ignored ->  new DecimalReadJdbcQuery(colName);
      case Rql2StringType ignored ->  new StringReadJdbcQuery(colName);
      case Rql2DateType ignored ->  new DateReadJdbcQuery(colName);
      case Rql2TimeType ignored ->  new TimeReadJdbcQuery(colName);
      case Rql2TimestampType ignored ->  new TimestampReadJdbcQuery(colName);
      case Rql2BoolType ignored ->  new BoolReadJdbcQuery(colName);
      case Rql2BinaryType ignored ->  new BinaryReadJdbcQuery(colName);
      default -> throw new RawTruffleInternalErrorException();
    };
    return new ProgramExpressionNode(lang, frameDescriptor, node);
  }
}
