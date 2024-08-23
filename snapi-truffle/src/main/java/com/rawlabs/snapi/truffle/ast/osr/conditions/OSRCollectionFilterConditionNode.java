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

package com.rawlabs.snapi.truffle.ast.osr.conditions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodesFactory;

public class OSRCollectionFilterConditionNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  private final int generatorSlot;

  private final int resultSlot;

  public OSRCollectionFilterConditionNode(int generatorSlot, int resultSlot) {
    this.generatorSlot = generatorSlot;
    this.resultSlot = resultSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object generator = frame.getObject(generatorSlot);
    Object result = frame.getObject(resultSlot);
    return result == null && hasNextNode.execute(this, generator);
  }

  @Override
  public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return (boolean) executeGeneric(virtualFrame);
  }
}
