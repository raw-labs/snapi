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

package com.rawlabs.snapi.truffle.ast.io.json.reader.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.runtime.list.ListNodes;
import com.rawlabs.snapi.truffle.runtime.list.ListNodesFactory;

@NodeInfo(shortName = "IterableParseJson")
public class IterableParseJsonNode extends ExpressionNode {

  @Child private DirectCallNode childDirectCall;

  @Child
  private ListNodes.ToIterableNode toIterableNode = ListNodesFactory.ToIterableNodeGen.create();

  public IterableParseJsonNode(ProgramExpressionNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];
    Object list = this.childDirectCall.call(parser);
    return toIterableNode.execute(this, list);
  }
}
