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

public class OSRIsLessThanSizeConditionNode extends ExpressionNode {

  private final int currentIdxSlot;
  private final int sizeSlot;

  public OSRIsLessThanSizeConditionNode(int currentIdxSlot, int listSizeSlot) {
    this.currentIdxSlot = currentIdxSlot;
    this.sizeSlot = listSizeSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    int currentIdx = frame.getInt(currentIdxSlot);
    int listSize = frame.getInt(sizeSlot);
    return currentIdx < listSize;
  }

  @Override
  public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return (boolean) executeGeneric(virtualFrame);
  }
}
