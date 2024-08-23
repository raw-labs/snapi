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

package com.rawlabs.snapi.truffle.runtime.ast.io.jdbc;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.frontend.rql2.source.Rql2AttrType;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.Rql2Language;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.record.RecordNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.record.RecordNodesFactory;
import java.util.Arrays;
import java.util.List;

@NodeInfo(shortName = "Jdbc.RecordRead")
public class RecordReadJdbcQuery extends ExpressionNode {

  @Children private final DirectCallNode[] childDirectCalls;

  @Children private final RecordNodes.AddPropNode[] addPropNode;

  private final Rql2AttrType[] columns;

  private final Rql2Language language = Rql2Language.get(this);

  private final boolean hasDuplicateKeys;

  public RecordReadJdbcQuery(ProgramExpressionNode[] columnParsers, Rql2AttrType[] columns) {
    this.columns = columns;
    this.childDirectCalls = new DirectCallNode[columnParsers.length];
    for (int i = 0; i < columnParsers.length; i++) {
      this.childDirectCalls[i] = DirectCallNode.create(columnParsers[i].getCallTarget());
    }
    addPropNode = new RecordNodes.AddPropNode[columns.length];
    for (int i = 0; i < columns.length; i++) {
      addPropNode[i] = RecordNodesFactory.AddPropNodeGen.create();
    }
    List<String> listOfKeys = Arrays.stream(columns).map(Rql2AttrType::idn).toList();
    hasDuplicateKeys = listOfKeys.size() != listOfKeys.stream().distinct().count();
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JdbcQuery rs = (JdbcQuery) args[0];
    Object record;
    if (hasDuplicateKeys) {
      record = language.createDuplicateKeyRecord();
    } else {
      record = language.createPureRecord();
    }
    for (int i = 0; i < columns.length; i++) {
      String fieldName = columns[i].idn();
      Object value = childDirectCalls[i].call(rs);
      addPropNode[i].execute(this, record, fieldName, value, hasDuplicateKeys);
    }
    return record;
  }
}
