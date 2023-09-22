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

package raw.runtime.truffle.ast.io.csv.writer.internal;

import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.IOException;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvWriterRawTruffleException;
import raw.runtime.truffle.runtime.record.RecordObject;

@NodeInfo(shortName = "RecordWriteCsv")
public class RecordWriteCsvNode extends StatementNode {

  @Children private DirectCallNode[] valueWriter;

  @Child private InteropLibrary records = InteropLibrary.getFactory().createDispatched(3);

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
    RecordObject record = (RecordObject) args[0];
    CsvGenerator generator = (CsvGenerator) args[1];
    try {
      Object keys = keysArrays.getMembers(record);
      int nFields = valueWriter.length;
      doStartRow(generator);
      for (int idx = 0; idx < nFields; idx++) {
        String fieldName = (String) keysArrays.readArrayElement(keys, idx);
        Object value = records.readMember(record, fieldName);
        valueWriter[idx].call(value, generator);
      }
      doEndRow(generator);
    } catch (UnsupportedMessageException
        | InvalidArrayIndexException
        | UnknownIdentifierException e) {
      throw new RawTruffleInternalErrorException(e, this);
    }
  }

  @CompilerDirectives.TruffleBoundary
  private void doStartRow(CsvGenerator gen) {
    try {
      gen.writeStartArray();
    } catch (IOException e) {
      throw new CsvWriterRawTruffleException(e.getMessage(), e, this);
    }
  }

  @CompilerDirectives.TruffleBoundary
  private void doEndRow(CsvGenerator gen) {
    try {
      gen.writeEndArray();
    } catch (IOException e) {
      throw new CsvWriterRawTruffleException(e.getMessage(), e, this);
    }
  }
}
