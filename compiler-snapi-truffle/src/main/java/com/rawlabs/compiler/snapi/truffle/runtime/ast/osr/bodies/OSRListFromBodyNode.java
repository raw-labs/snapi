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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodesFactory;
import java.util.ArrayList;

public class OSRListFromBodyNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  private final int generatorSlot;
  private final int llistSlot;

  public OSRListFromBodyNode(int generatorSlot, int llistSlot) {
    this.generatorSlot = generatorSlot;
    this.llistSlot = llistSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object generator = frame.getObject(generatorSlot);
    @SuppressWarnings("unchecked")
    ArrayList<Object> llist = (ArrayList<Object>) frame.getObject(llistSlot);
    llist.add(nextNode.execute(this, generator));
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
