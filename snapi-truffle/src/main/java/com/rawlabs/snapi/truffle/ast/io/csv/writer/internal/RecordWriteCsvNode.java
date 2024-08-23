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

package com.rawlabs.snapi.truffle.ast.io.csv.writer.internal;

import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.ast.StatementNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;
import com.rawlabs.snapi.truffle.runtime.exceptions.csv.CsvWriterTruffleException;
import com.rawlabs.snapi.truffle.runtime.record.RecordNodes;
import com.rawlabs.snapi.truffle.runtime.record.RecordNodesFactory;
import java.io.IOException;

@NodeInfo(shortName = "RecordWriteCsv")
public class RecordWriteCsvNode extends StatementNode {

  @Children private DirectCallNode[] valueWriter;

  @Child private RecordNodes.GetValueNode getValue = RecordNodesFactory.GetValueNodeGen.create();

  @Child private InteropLibrary keysArrays = InteropLibrary.getFactory().createDispatched(1);

  public RecordWriteCsvNode(ProgramStatementNode[] valueWriter) {
    this.valueWriter = new DirectCallNode[valueWriter.length];
    for (int i = 0; i < valueWriter.length; i++) {
      this.valueWriter[i] = DirectCallNode.create(valueWriter[i].getCallTarget());
    }
  }

  @ExplodeLoop
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    Object record = args[0];
    CsvGenerator generator = (CsvGenerator) args[1];
    try {
      Object keys = keysArrays.getMembers(record);
      int nFields = valueWriter.length;
      doStartRow(generator);
      for (int idx = 0; idx < nFields; idx++) {
        String fieldName = (String) keysArrays.readArrayElement(keys, idx);
        Object value = getValue.execute(this, record, fieldName);
        valueWriter[idx].call(value, generator);
      }
      doEndRow(generator);
    } catch (UnsupportedMessageException | InvalidArrayIndexException e) {
      throw new TruffleInternalErrorException(e, this);
    }
  }

  @TruffleBoundary
  private void doStartRow(CsvGenerator gen) {
    try {
      gen.writeStartArray();
    } catch (IOException e) {
      throw new CsvWriterTruffleException(e.getMessage(), e, this);
    }
  }

  @TruffleBoundary
  private void doEndRow(CsvGenerator gen) {
    try {
      gen.writeEndArray();
    } catch (IOException e) {
      throw new CsvWriterTruffleException(e.getMessage(), e, this);
    }
  }
}
