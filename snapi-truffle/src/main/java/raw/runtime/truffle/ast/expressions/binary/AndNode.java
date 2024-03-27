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

package raw.runtime.truffle.ast.expressions.binary;

import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.NullObject;
import raw.runtime.truffle.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.tryable_nullable.TryableNullableNodesFactory;

public final class AndNode extends ExpressionNode {

  @Child private ExpressionNode leftNode;
  @Child private ExpressionNode rightNode;

  @Child
  private TryableNullableNodes.IsNullNode isNullNode =
      TryableNullableNodesFactory.IsNullNodeGen.create();

  public AndNode(ExpressionNode leftNode, ExpressionNode rightNode) {
    this.leftNode = leftNode;
    this.rightNode = rightNode;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    Boolean left = getOperand(leftNode, virtualFrame);
    if (left != null && !left) {
      // if left is false, and evaluates to false
      return false;
    } else {
      // left is either null or true (in which case we need to check right)
      Boolean right = getOperand(rightNode, virtualFrame);
      if (left == null) {
        // if left is null, we need to check if right is false or not
        if (right != null && !right) return false;
        else return NullObject.INSTANCE;
      } else {
        // left is true, the result of and is right
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
