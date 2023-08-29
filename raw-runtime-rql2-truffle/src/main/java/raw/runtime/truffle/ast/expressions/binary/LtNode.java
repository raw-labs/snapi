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
import raw.runtime.truffle.runtime.operators.OperatorLibrary;
import raw.runtime.truffle.runtime.operators.CompareOperator;

@NodeInfo(shortName = "<")
public class LtNode extends BinaryNode {

  CompareOperator comparator;
  OperatorLibrary comparatorLibrary;

  @Child ExpressionNode left;

  @Child ExpressionNode right;

  public LtNode(ExpressionNode left, ExpressionNode right) {
    this.left = left;
    this.right = right;
    comparator = new CompareOperator();
    comparatorLibrary = OperatorLibrary.getFactory().create(comparator);
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    Object leftValue = left.executeGeneric(virtualFrame);
    Object rightValue = right.executeGeneric(virtualFrame);
    return (int) (comparatorLibrary.doOperation(comparator, leftValue, rightValue)) < 0;
  }

  //
  //    @Specialization
  //    protected boolean lessThan(byte left, byte right) {
  //        return left < right;
  //    }
  //
  //    @Specialization
  //    protected boolean lessThan(short left, short right) {
  //        return left < right;
  //    }
  //
  //    @Specialization
  //    protected boolean lessThan(int left, int right) {
  //        return left < right;
  //    }
  //
  //    @Specialization
  //    protected boolean lessThan(long left, long right) {
  //        return left < right;
  //    }
  //
  //    @Specialization
  //    protected boolean lessThan(float left, float right) {
  //        return Float.compare(left, right) < 0;
  //    }
  //
  //    @Specialization
  //    protected boolean lessThan(double left, double right) {
  //        return Double.compare(left, right) < 0;
  //    }
  //
  //    @Specialization
  //    protected boolean lessThan(BigDecimal left, BigDecimal right) {
  //        return left.compareTo(right) < 0;
  //    }
  //
  //    @Specialization
  //    protected boolean lessThan(String left, String right) {
  //        return left.compareTo(right) < 0;
  //    }
  //    @Specialization
  //    protected boolean lessThan(LocalDate left, LocalDate right) {
  //        return left.isBefore(right);
  //    }
  //
  //    @Specialization
  //    protected boolean lessThan(DateObject leftObj, DateObject rightObj) {
  //        LocalDate left = leftObj.getDate();
  //        LocalDate right = rightObj.getDate();
  //        return left.isBefore(right);
  //    }
  //
  //    @Specialization
  //    protected boolean lessThan(TimeObject leftObj, TimeObject rightObj) {
  //        LocalTime left = leftObj.getTime();
  //        LocalTime right = rightObj.getTime();
  //        return left.isBefore(right);
  //    }
  //
  //    @Specialization
  //    protected boolean lessThan(TimestampObject leftObj, TimestampObject rightObj) {
  //        LocalDateTime left = leftObj.getTimestamp();
  //        LocalDateTime right = rightObj.getTimestamp();
  //        return left.isBefore(right);
  //    }
  //
  //    @Specialization
  //    protected boolean lessThan(IntervalObject left, IntervalObject right) {
  //        return left.compareTo(right) < 0;
  //    }

}
