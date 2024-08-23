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

package com.rawlabs.snapi.truffle.ast.io.xml.parser;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.ProgramExpressionNode;

@NodeInfo(shortName = "ElementParseXml")
public class ElementParseXmlPrimitiveNode extends ExpressionNode {

  @Child private DirectCallNode childDirectCall;

  public ElementParseXmlPrimitiveNode(ProgramExpressionNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    TruffleXmlParser parser = (TruffleXmlParser) args[0];
    String text = parser.getAsString();
    Object value = childDirectCall.call(parser, text);
    parser.nextToken(); // Skip the end element
    return value;
  }
}
