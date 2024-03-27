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

package raw.runtime.truffle.ast.osr.bodies;

import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.EquiJoinComputeNext;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.operators.OperatorNodesFactory;

public class OSREquiJoinNextBodyNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child OperatorNodes.CompareNode compareKey = OperatorNodesFactory.CompareNodeGen.create();

  private final int computeNextSlot;
  private final int shouldContinueSlot;

  public OSREquiJoinNextBodyNode(int computeNextSlot, int shouldContinueSlot) {
    this.computeNextSlot = computeNextSlot;
    this.shouldContinueSlot = shouldContinueSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    EquiJoinComputeNext computeNext = (EquiJoinComputeNext) frame.getAuxiliarySlot(computeNextSlot);
    if (computeNext.getLeftKey() == null || computeNext.getRightKey() == null) {
      if (computeNext.getLeftKey() == null) {
        if (hasNextNode.execute(this, computeNext.getLeftMapGenerator())) {
          computeNext.setLeftEntry(
              (Object[]) nextNode.execute(this, computeNext.getLeftMapGenerator()));
          computeNext.setLeftKey(computeNext.getLeftEntry()[0]);
        } else {
          throw new BreakException();
        }
      }

      if (computeNext.getRightKey() == null) {
        if (hasNextNode.execute(this, computeNext.getRightMapGenerator())) {
          computeNext.setRightEntry(
              (Object[]) nextNode.execute(this, computeNext.getRightMapGenerator()));
          computeNext.setRightKey(computeNext.getRightEntry()[0]);
        } else {
          throw new BreakException();
        }
      }

      int compare = compareKey.execute(this, computeNext.getLeftKey(), computeNext.getRightKey());
      // if keys aren't equal, reset the smallest of both (it will be read in the next
      // iteration and
      // will be larger)
      if (compare < 0) {
        computeNext.setLeftKey(null);
      } else if (compare > 0) {
        computeNext.setRightKey(null);
      } else {
        // keys are equal, prepare to do the cartesian product between both.
        // leftRows and rightRows are the arrays of rows with the same key.
        // We'll iterate over them to produce the cartesian product.
        computeNext.setLeftRows((Object[]) computeNext.getLeftEntry()[1]);
        computeNext.setRightRows((Object[]) computeNext.getRightEntry()[1]);
        computeNext.setLeftIndex(0);
        computeNext.setRightIndex(0);
        frame.setAuxiliarySlot(shouldContinueSlot, false);
        return null;
      }
      frame.setAuxiliarySlot(shouldContinueSlot, true);
      return null;
    }
    frame.setAuxiliarySlot(shouldContinueSlot, false);
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
