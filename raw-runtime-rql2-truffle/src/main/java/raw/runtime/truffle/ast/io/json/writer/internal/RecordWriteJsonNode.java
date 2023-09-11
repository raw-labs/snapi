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

package raw.runtime.truffle.ast.io.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.ast.io.json.writer.JsonWriteNodes;
import raw.runtime.truffle.ast.io.json.writer.JsonWriteNodesFactory;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.record.RecordObject;

public class RecordWriteJsonNode extends StatementNode {

  @Children private DirectCallNode[] childDirectCalls;

  @Child private InteropLibrary interops = InteropLibrary.getFactory().createDispatched(2);

  @Child
  private JsonWriteNodes.WriteStartObjectJsonWriterNode writeStartObjectNode =
      JsonWriteNodesFactory.WriteStartObjectJsonWriterNodeGen.getUncached();

  @Child
  private JsonWriteNodes.WriteEndObjectJsonWriterNode writeEndObjectNode =
      JsonWriteNodesFactory.WriteEndObjectJsonWriterNodeGen.getUncached();

  @Child
  private JsonWriteNodes.WriteFieldNameJsonWriterNode writeFieldNameNode =
      JsonWriteNodesFactory.WriteFieldNameJsonWriterNodeGen.getUncached();

  public RecordWriteJsonNode(ProgramStatementNode[] childProgramStatementNode) {
    this.childDirectCalls = new DirectCallNode[childProgramStatementNode.length];
    for (int i = 0; i < childProgramStatementNode.length; i++) {
      this.childDirectCalls[i] =
          DirectCallNode.create(childProgramStatementNode[i].getCallTarget());
    }
  }

  @Override
  @ExplodeLoop
  public void executeVoid(VirtualFrame frame) {
    try {
      Object[] args = frame.getArguments();
      RecordObject record = (RecordObject) args[0];
      JsonGenerator gen = (JsonGenerator) args[1];
      Object keys = interops.getMembers(record);
      Object item;
      writeStartObjectNode.execute(gen);
      for (int i = 0; i < childDirectCalls.length; i++) {
        String member = (String) interops.readArrayElement(keys, i);
        item = record.readIdx(i);
        writeFieldNameNode.execute(member, gen);
        childDirectCalls[i].call(item, gen);
      }
      writeEndObjectNode.execute(gen);

    } catch (RuntimeException | UnsupportedMessageException | InvalidArrayIndexException e) {
      throw new RawTruffleInternalErrorException(e, this);
    }
  }
}
