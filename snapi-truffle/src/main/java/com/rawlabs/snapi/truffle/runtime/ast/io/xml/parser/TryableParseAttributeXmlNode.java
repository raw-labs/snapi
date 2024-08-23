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

package com.rawlabs.snapi.truffle.runtime.ast.io.xml.parser;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "TryableParseAttributeXml")
public class TryableParseAttributeXmlNode extends ExpressionNode {

  @Child private DirectCallNode childDirectCall;

  public TryableParseAttributeXmlNode(ProgramExpressionNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    try {
      return childDirectCall.call(args);
    } catch (TruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    }
  }
}
