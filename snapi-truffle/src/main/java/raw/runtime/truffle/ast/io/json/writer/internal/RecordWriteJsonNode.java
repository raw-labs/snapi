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
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.ast.io.json.writer.JsonWriteNodes;
import raw.runtime.truffle.ast.io.json.writer.JsonWriteNodesFactory;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.runtime.truffle.runtime.record.RecordNodesFactory;

public class RecordWriteJsonNode extends StatementNode {

  @Children private DirectCallNode[] childDirectCalls;

  @Child
  private JsonWriteNodes.WriteStartObjectJsonWriterNode writeStartObjectNode =
      JsonWriteNodesFactory.WriteStartObjectJsonWriterNodeGen.getUncached();

  @Child
  private JsonWriteNodes.WriteEndObjectJsonWriterNode writeEndObjectNode =
      JsonWriteNodesFactory.WriteEndObjectJsonWriterNodeGen.getUncached();

  @Child
  private JsonWriteNodes.WriteFieldNameJsonWriterNode writeFieldNameNode =
      JsonWriteNodesFactory.WriteFieldNameJsonWriterNodeGen.getUncached();

  @Child private RecordNodes.GetKeysNode getKeysNode = RecordNodesFactory.GetKeysNodeGen.create();

  @Child
  RecordNodes.GetValueByIndexNode getValueByIndexNode =
      RecordNodesFactory.GetValueByIndexNodeGen.create();

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
      Object record = args[0];
      JsonGenerator gen = (JsonGenerator) args[1];
      Object[] keys = getKeysNode.execute(this, record);
      Object item;

      writeStartObjectNode.execute(this, gen);
      for (int i = 0; i < childDirectCalls.length; i++) {
        item = getValueByIndexNode.execute(this, record, i);
        writeFieldNameNode.execute(this, (String) keys[i], gen);
        childDirectCalls[i].call(item, gen);
      }
      writeEndObjectNode.execute(this, gen);

    } catch (RuntimeException e) {
      throw new RawTruffleInternalErrorException(e, this);
    }
  }
}
