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

package raw.runtime.truffle.ast.osr.conditions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import raw.runtime.truffle.ExpressionNode;

public class OSRFromBodyConditionNode extends ExpressionNode {

  private final int shouldContinueSlot;

  public OSRFromBodyConditionNode(int shouldContinueSlot) {
    this.shouldContinueSlot = shouldContinueSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    return frame.getBoolean(shouldContinueSlot);
  }

  @Override
  public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return (boolean) executeGeneric(virtualFrame);
  }
}
