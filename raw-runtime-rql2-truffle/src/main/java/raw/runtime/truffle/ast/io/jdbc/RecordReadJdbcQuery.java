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

package raw.runtime.truffle.ast.io.jdbc;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2AttrType;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.record.RecordObject;

@NodeInfo(shortName = "Jdbc.RecordRead")
public class RecordReadJdbcQuery extends ExpressionNode {

  @Children private DirectCallNode[] childDirectCalls;

  @Child private InteropLibrary records = InteropLibrary.getFactory().createDispatched(2);

  private final Rql2AttrType[] columns;

  public RecordReadJdbcQuery(ProgramExpressionNode[] columnParsers, Rql2AttrType[] columns) {
    this.columns = columns;
    this.childDirectCalls = new DirectCallNode[columnParsers.length];
    for (int i = 0; i < columnParsers.length; i++) {
      this.childDirectCalls[i] = DirectCallNode.create(columnParsers[i].getCallTarget());
    }
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return this.executeRecord(frame);
  }

  @Override
  public final RecordObject executeRecord(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JdbcQuery rs = (JdbcQuery) args[0];
    RecordObject record = RawLanguage.get(this).createRecord();
    for (int i = 0; i < columns.length; i++) {
      String fieldName = columns[i].idn();
      Object value = childDirectCalls[i].call(rs);
      try {
        records.writeMember(record, fieldName, value);
      } catch (UnsupportedMessageException
          | UnknownIdentifierException
          | UnsupportedTypeException ex) {
        throw new RawTruffleInternalErrorException(ex, this);
      }
    }
    return record;
  }
}
