/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.runtime.ast.osr.conditions;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.io.json.reader.JsonParserNodes;
import com.rawlabs.snapi.truffle.runtime.ast.io.json.reader.JsonParserNodesFactory;

public class OSRListParseJsonConditionNode extends ExpressionNode {

  @Child
  JsonParserNodes.CurrentTokenJsonParserNode currentToken =
      JsonParserNodesFactory.CurrentTokenJsonParserNodeGen.create();

  private final int parserSlot;

  public OSRListParseJsonConditionNode(int parserSlot) {
    this.parserSlot = parserSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    JsonParser parser = (JsonParser) frame.getObject(parserSlot);
    return currentToken.execute(this, parser) != JsonToken.END_ARRAY;
  }

  @Override
  public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return (boolean) executeGeneric(virtualFrame);
  }
}
