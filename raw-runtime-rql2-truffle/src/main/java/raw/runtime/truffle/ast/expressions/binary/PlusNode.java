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
import raw.runtime.truffle.runtime.operators.AddOperator;
import raw.runtime.truffle.runtime.operators.OperatorLibrary;

@NodeInfo(shortName = "+")
public class PlusNode extends BinaryNode {

    AddOperator sumAggregator;
    OperatorLibrary aggregatorLibrary;

    @Child
    ExpressionNode left;

    @Child
    ExpressionNode right;

    public PlusNode(ExpressionNode left, ExpressionNode right) {
        this.left = left;
        this.right = right;
        sumAggregator = new AddOperator();
        aggregatorLibrary = OperatorLibrary.getFactory().create(sumAggregator);
    }

    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        Object leftValue = left.executeGeneric(virtualFrame);
        Object rightValue = right.executeGeneric(virtualFrame);
        return aggregatorLibrary.doOperation(sumAggregator, leftValue, rightValue);
    }

//    @Specialization
//    protected byte add(byte left, byte right) {
//        return (byte) (left + right);
//    }
//
//    @Specialization
//    protected short add(short left, short right) {
//        return (short) (left + right);
//    }
//
//    @Specialization
//    protected int add(int left, int right) {
//        return left + right;
//    }
//
//    @Specialization
//    protected long add(long left, long right) {
//        return left + right;
//    }
//
//    @Specialization
//    protected float add(float left, float right) {
//        return left + right;
//    }
//
//    @Specialization
//    protected double add(double left, double right) {
//        return left + right;
//    }
//
//    @Specialization
//    protected BigDecimal add(BigDecimal left, BigDecimal right) {
//        return left.add(right);
//    }
//
//    @Specialization
//    protected String add(String left, String right) {
//        return left.concat(right);
//    }
}
