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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.StatementNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.ProgramStatementNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.writer.JsonWriteNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.writer.JsonWriteNodesFactory;
import com.rawlabs.compiler.snapi.truffle.runtime.tryable_nullable.TryableNullableNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.tryable_nullable.TryableNullableNodesFactory;

@NodeInfo(shortName = "TryableWriteJson")
public class TryableWriteJsonNode extends StatementNode {

  @Child private DirectCallNode childDirectCall;

  @Child
  JsonWriteNodes.WriteStringJsonWriterNode writeString =
      JsonWriteNodesFactory.WriteStringJsonWriterNodeGen.create();

  @Child
  private TryableNullableNodes.IsErrorNode isErrorNode =
      TryableNullableNodesFactory.IsErrorNodeGen.create();

  @Child
  private TryableNullableNodes.GetErrorNode getErrorNode =
      TryableNullableNodesFactory.GetErrorNodeGen.create();

  public TryableWriteJsonNode(ProgramStatementNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    Object tryable = args[0];
    JsonGenerator gen = (JsonGenerator) args[1];
    if (!isErrorNode.execute(this, tryable)) {
      childDirectCall.call(tryable, gen);
    } else {
      writeString.execute(this, getErrorNode.execute(this, tryable), gen);
    }
  }
}
