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

package com.rawlabs.snapi.truffle.runtime.ast.io.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.StatementNode;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.runtime.ast.io.json.writer.JsonWriteNodes;
import com.rawlabs.snapi.truffle.runtime.ast.io.json.writer.JsonWriteNodesFactory;
import com.rawlabs.snapi.truffle.runtime.tryable_nullable.TryableNullableNodes;
import com.rawlabs.snapi.truffle.runtime.tryable_nullable.TryableNullableNodesFactory;

@NodeInfo(shortName = "NullableWriteJson")
public class NullableWriteJsonNode extends StatementNode {

  @Child private DirectCallNode childDirectCall;

  @Child
  JsonWriteNodes.WriteNullJsonWriterNode writeNullNode =
      JsonWriteNodesFactory.WriteNullJsonWriterNodeGen.create();

  @Child
  private TryableNullableNodes.IsNullNode isNullNode =
      TryableNullableNodesFactory.IsNullNodeGen.create();

  public NullableWriteJsonNode(ProgramStatementNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    Object option = args[0];
    JsonGenerator gen = (JsonGenerator) args[1];
    if (!isNullNode.execute(this, option)) {
      childDirectCall.call(option, gen);
    } else {
      writeNullNode.execute(this, gen);
    }
  }
}
