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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.io.xml.parser;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.ProgramExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.xml.XmlParserRawTruffleException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "TryableParseXml")
public class TryableParseXmlNode extends ExpressionNode {

  @Child private DirectCallNode childDirectCall;

  public TryableParseXmlNode(ProgramExpressionNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    RawTruffleXmlParser parser = (RawTruffleXmlParser) args[0];
    try {
      return childDirectCall.call(args);
    } catch (XmlParserRawTruffleException ex) {
      Object failure = new ErrorObject(ex.getMessage());
      try {
        parser.finishConsuming();
      } catch (XmlParserRawTruffleException e) {
        return failure;
      }
      return failure;
    }
  }
}
