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
import raw.runtime.truffle.runtime.operators.CompareOperator;
import raw.runtime.truffle.runtime.operators.OperatorLibrary;

public class GeNode extends BinaryNode {

  CompareOperator comparator;
  OperatorLibrary comparatorLibrary;

  @Child ExpressionNode left;

  @Child ExpressionNode right;

  public GeNode(ExpressionNode left, ExpressionNode right) {
    this.left = left;
    this.right = right;
    comparator = new CompareOperator();
    comparatorLibrary = OperatorLibrary.getFactory().create(comparator);
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    Object leftValue = left.executeGeneric(virtualFrame);
    Object rightValue = right.executeGeneric(virtualFrame);
    return (int) (comparatorLibrary.doOperation(comparator, leftValue, rightValue)) >= 0;
  }

  //
  //    @Specialization
  //    protected boolean greaterOrEqual(byte left, byte right) {
  //        return left >= right;
  //    }
  //
  //    @Specialization
  //    protected boolean greaterOrEqual(short left, short right) {
  //        return left >= right;
  //    }
  //
  //    @Specialization
  //    protected boolean greaterOrEqual(int left, int right) {
  //        return left >= right;
  //    }
  //
  //    @Specialization
  //    protected boolean greaterOrEqual(float left, float right) {
  //        return Float.compare(left, right) >= 0;
  //    }
  //
  //    @Specialization
  //    protected boolean greaterOrEqual(double left, double right) {
  //        return Double.compare(left, right) >= 0;
  //    }
  //
  //    @Specialization
  //    protected boolean greaterOrEqual(long left, long right) {
  //        return left >= right;
  //    }
  //
  //    @Specialization
  //    protected boolean greaterOrEqual(String left, String right) {
  //        return left.compareTo(right) >= 0;
  //    }
  //
  //    @Specialization
  //    protected boolean greaterOrEqual(BigDecimal left, BigDecimal right) {
  //        return left.compareTo(right) >= 0;
  //    }
  //
  //    @Specialization
  //    protected boolean greaterOrEqual(LocalDate left, LocalDate right) {
  //        return left.isAfter(right) || left.equals(right);
  //    }
  //
  //    @Specialization
  //    protected boolean greaterOrEqual(DateObject leftObj, DateObject rightObj) {
  //        LocalDate left = leftObj.getDate();
  //        LocalDate right = rightObj.getDate();
  //        return !left.isBefore(right);
  //    }
  //
  //    @Specialization
  //    protected boolean greaterOrEqual(TimeObject leftObj, TimeObject rightObj) {
  //        LocalTime left = leftObj.getTime();
  //        LocalTime right = rightObj.getTime();
  //        return !left.isBefore(right);
  //    }
  //
  //    @Specialization
  //    protected boolean greaterOrEqual(TimestampObject leftObj, TimestampObject rightObj) {
  //        LocalDateTime left = leftObj.getTimestamp();
  //        LocalDateTime right = rightObj.getTimestamp();
  //        return !left.isBefore(right);
  //    }
  //
  //    @Specialization
  //    protected boolean greaterOrEqual(IntervalObject left, IntervalObject right) {
  //        return left.compareTo(right) >= 0;
  //    }

}
