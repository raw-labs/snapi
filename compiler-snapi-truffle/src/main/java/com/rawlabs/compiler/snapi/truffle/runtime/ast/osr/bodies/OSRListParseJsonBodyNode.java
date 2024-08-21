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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.osr.bodies;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import java.util.ArrayList;

public class OSRListParseJsonBodyNode extends ExpressionNode {

  @Child DirectCallNode childCallNode;

  private final int llistSlot;
  private final int parserSlot;

  public OSRListParseJsonBodyNode(
      RootCallTarget childRootCallTarget, int llistSlot, int parserSlot) {
    this.childCallNode = DirectCallNode.create(childRootCallTarget);
    this.llistSlot = llistSlot;
    this.parserSlot = parserSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object parser = frame.getObject(parserSlot);
    @SuppressWarnings("unchecked")
    ArrayList<Object> llist = (ArrayList<Object>) frame.getObject(llistSlot);
    llist.add(childCallNode.call(parser));
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
