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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.ast.io.json.reader.JsonParserNodes;
import com.rawlabs.snapi.truffle.ast.io.json.reader.JsonParserNodesFactory;
import com.rawlabs.snapi.truffle.runtime.exceptions.json.JsonExpectedNothingException;
import com.rawlabs.snapi.truffle.runtime.exceptions.json.JsonParserTruffleException;
import com.rawlabs.snapi.truffle.runtime.primitives.NullObject;

public class NullableParseXmlNode extends ExpressionNode {

  @Child private DirectCallNode childDirectCall;

  @Child
  private JsonParserNodes.NextTokenJsonParserNode nextTokenNode =
      JsonParserNodesFactory.NextTokenJsonParserNodeGen.create();

  public NullableParseXmlNode(ProgramExpressionNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];
    if (parser.getCurrentToken() == JsonToken.VALUE_NULL) {
      nextTokenNode.execute(this, parser);
      return NullObject.INSTANCE;
    } else {
      try {
        return childDirectCall.call(parser);
      } catch (JsonExpectedNothingException ex) {
        throw new JsonParserTruffleException("expected null but got non-null", ex, this);
      }
    }
  }
}
