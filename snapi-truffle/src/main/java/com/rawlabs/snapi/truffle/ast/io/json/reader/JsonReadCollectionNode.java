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

package com.rawlabs.snapi.truffle.ast.io.json.reader;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.runtime.iterable.sources.JsonReadCollection;
import com.rawlabs.snapi.truffle.runtime.primitives.LocationObject;

@NodeInfo(shortName = "Json.ReadCollection")
public class JsonReadCollectionNode extends ExpressionNode {
  @Child private ExpressionNode locationExp;

  @Child private ExpressionNode encodingExp;

  private final RootCallTarget rootCallTarget;

  public JsonReadCollectionNode(
      ExpressionNode locationExp, ExpressionNode encodingExp, ProgramExpressionNode readerNode) {
    this.locationExp = locationExp;
    this.encodingExp = encodingExp;
    this.rootCallTarget = readerNode.getCallTarget();
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    LocationObject locationObject = (LocationObject) locationExp.executeGeneric(virtualFrame);
    String encoding = (String) encodingExp.executeGeneric(virtualFrame);
    return new JsonReadCollection(locationObject, encoding, rootCallTarget);
  }
}
