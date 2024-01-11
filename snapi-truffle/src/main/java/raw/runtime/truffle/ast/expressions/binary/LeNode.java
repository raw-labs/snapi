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
import raw.runtime.truffle.ast.BinaryNode;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.operators.OperatorNodesFactory;

public class LeNode extends BinaryNode {

  @Child ExpressionNode left;

  @Child ExpressionNode right;

  @Child OperatorNodes.CompareNode compare = OperatorNodesFactory.CompareNodeGen.create();

  public LeNode(ExpressionNode left, ExpressionNode right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    Object leftValue = left.executeGeneric(virtualFrame);
    Object rightValue = right.executeGeneric(virtualFrame);
    return compare.execute(this, leftValue, rightValue) <= 0;
  }
}
