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

package raw.runtime.truffle.ast.osr.conditions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;

public class OSRExistsConditionNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  private final int generatorSlot;

  private final int predicateResultSlot;

  public OSRExistsConditionNode(int generatorSlot, int predicateResultSlot) {
    this.generatorSlot = generatorSlot;
    this.predicateResultSlot = predicateResultSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return !frame.getBoolean(predicateResultSlot)
        && hasNextNode.execute(this, frame.getObject(generatorSlot));
  }

  @Override
  public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return (boolean) executeGeneric(virtualFrame);
  }
}
