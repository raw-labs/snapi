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

package com.rawlabs.snapi.truffle.runtime.ast.io.csv.reader.parser;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.frontend.rql2.source.Rql2AttrType;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.record.RecordNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.record.RecordNodesFactory;
import java.util.Arrays;
import java.util.List;

@NodeInfo(shortName = "RecordParseCsv")
public class RecordParseCsvNode extends ExpressionNode {

  @Children private final RecordNodes.AddPropNode[] addPropNode;

  @Children private final DirectCallNode[] childDirectCalls;

  private final Rql2AttrType[] columns;

  private final boolean hasDuplicateKeys;
  private final RawLanguage language = RawLanguage.get(this);

  public RecordParseCsvNode(ProgramExpressionNode[] columnParsers, Rql2AttrType[] columns) {
    this.columns = columns;
    this.childDirectCalls = new DirectCallNode[columnParsers.length];
    for (int i = 0; i < columnParsers.length; i++) {
      this.childDirectCalls[i] = DirectCallNode.create(columnParsers[i].getCallTarget());
    }
    addPropNode = new RecordNodes.AddPropNode[columns.length];
    for (int i = 0; i < columns.length; i++) {
      addPropNode[i] = RecordNodesFactory.AddPropNodeGen.create();
    }

    List<String> keys = Arrays.stream(columns).map(Rql2AttrType::idn).toList();

    hasDuplicateKeys = keys.size() != keys.stream().distinct().count();
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    RawTruffleCsvParser parser = (RawTruffleCsvParser) args[0];
    assert (parser.startingNewLine(this));
    Object record;
    if (hasDuplicateKeys) {
      record = language.createDuplicateKeyRecord();
    } else {
      record = language.createPureRecord();
    }
    for (int i = 0; i < columns.length; i++) {
      String fieldName = columns[i].idn();
      parser.getNextField();
      Object value = childDirectCalls[i].call(parser);
      addPropNode[i].execute(this, record, fieldName, value, hasDuplicateKeys);
    }
    parser.finishLine(this);
    return record;
  }
}
