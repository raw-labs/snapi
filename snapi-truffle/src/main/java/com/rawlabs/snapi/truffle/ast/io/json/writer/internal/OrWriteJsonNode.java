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

package com.rawlabs.snapi.truffle.ast.io.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.StatementNode;
import com.rawlabs.snapi.truffle.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.runtime.or.OrObject;

@NodeInfo(shortName = "OrWriteJson")
public class OrWriteJsonNode extends StatementNode {

  @Children private DirectCallNode[] childDirectCalls;

  public OrWriteJsonNode(ProgramStatementNode[] childProgramStatementNode) {
    this.childDirectCalls = new DirectCallNode[childProgramStatementNode.length];
    for (int i = 0; i < childProgramStatementNode.length; i++) {
      this.childDirectCalls[i] =
          DirectCallNode.create(childProgramStatementNode[i].getCallTarget());
    }
  }

  @ExplodeLoop
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    OrObject or = (OrObject) args[0];
    JsonGenerator gen = (JsonGenerator) args[1];
    for (int i = 0; i < this.childDirectCalls.length; i++) {
      if (or.getIndex() == i) {
        this.childDirectCalls[i].call(or.getValue(), gen);
        return;
      }
    }
  }
}
