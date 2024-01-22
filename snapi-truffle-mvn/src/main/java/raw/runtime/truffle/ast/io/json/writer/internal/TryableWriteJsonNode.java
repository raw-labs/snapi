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
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.ast.io.json.writer.JsonWriteNodes;
import raw.runtime.truffle.ast.io.json.writer.JsonWriteNodesFactory;
import raw.runtime.truffle.tryable_nullable.Tryable;

@NodeInfo(shortName = "TryableWriteJson")
public class TryableWriteJsonNode extends StatementNode {

  @Child private DirectCallNode childDirectCall;

  @Child
  JsonWriteNodes.WriteStringJsonWriterNode writeString =
      JsonWriteNodesFactory.WriteStringJsonWriterNodeGen.create();

  public TryableWriteJsonNode(ProgramStatementNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    Object tryable = args[0];
    JsonGenerator gen = (JsonGenerator) args[1];
    if (Tryable.isSuccess(tryable)) {
      childDirectCall.call(tryable, gen);
    } else {
      writeString.execute(this, Tryable.getFailure(tryable), gen);
    }
  }
}
