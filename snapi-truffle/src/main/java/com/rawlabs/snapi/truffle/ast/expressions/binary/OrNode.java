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

package com.rawlabs.snapi.truffle.ast.expressions.binary;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodes;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodesFactory;
import com.rawlabs.snapi.truffle.runtime.primitives.NullObject;

public final class OrNode extends ExpressionNode {

  @Child private ExpressionNode leftNode;
  @Child private ExpressionNode rightNode;

  @Child
  private TryableNullableNodes.IsNullNode isNullNode =
      TryableNullableNodesFactory.IsNullNodeGen.create();

  public OrNode(ExpressionNode leftNode, ExpressionNode rightNode) {
    this.leftNode = leftNode;
    this.rightNode = rightNode;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    Boolean left = getOperand(leftNode, virtualFrame);
    if (left != null && left) {
      // if left is false, OR evaluates to true
      return true;
    } else {
      // left is either null or false (in which cases we need to check right)
      Boolean right = getOperand(rightNode, virtualFrame);
      if (left == null) {
        // if left is null, we need to check if right is true or not
        if (right != null && right) return true;
        else return NullObject.INSTANCE;
      } else {
        // left is false, the result of OR is right
        if (right != null) return right;
        else return NullObject.INSTANCE;
      }
    }
  }

  private Boolean getOperand(ExpressionNode node, VirtualFrame frame) {
    Object value = node.executeGeneric(frame);
    if (!isNullNode.execute(this, value)) {
      return (Boolean) value;
    } else {
      return null;
    }
  }
}
