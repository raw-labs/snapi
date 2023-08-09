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
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.BinaryNode;
import raw.runtime.truffle.runtime.operators.CompareOperator;
import raw.runtime.truffle.runtime.operators.OperatorLibrary;

@NodeInfo(shortName = "==")
public class EqNode extends BinaryNode {

  CompareOperator comparator;
  OperatorLibrary comparatorLibrary;

  @Child ExpressionNode left;

  @Child ExpressionNode right;

  public EqNode(ExpressionNode left, ExpressionNode right) {
    this.left = left;
    this.right = right;
    comparator = new CompareOperator();
    comparatorLibrary = OperatorLibrary.getFactory().create(comparator);
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    Object leftValue = left.executeGeneric(virtualFrame);
    Object rightValue = right.executeGeneric(virtualFrame);
    return (int) (comparatorLibrary.doOperation(comparator, leftValue, rightValue)) == 0;
  }
}
