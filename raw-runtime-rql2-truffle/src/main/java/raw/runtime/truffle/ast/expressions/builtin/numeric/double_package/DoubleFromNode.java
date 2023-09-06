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

package raw.runtime.truffle.ast.expressions.builtin.numeric.double_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

@NodeInfo(shortName = "Double.From")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class DoubleFromNode extends ExpressionNode {

  @Specialization
  protected double fromByte(byte argument) {
    return argument;
  }

  @Specialization
  protected double fromShort(short argument) {
    return argument;
  }

  @Specialization
  protected double fromInt(int argument) {
    return argument;
  }

  @Specialization
  protected double fromLong(long argument) {
    return argument;
  }

  @Specialization
  protected double fromFloat(float argument) {
    return argument;
  }

  @Specialization
  protected double fromDouble(double argument) {
    return argument;
  }

  @Specialization
  protected double fromDecimal(BigDecimal argument) {
    return argument.doubleValue();
  }

  @Specialization
  protected ObjectTryable fromString(String argument) {
    try {
      return ObjectTryable.BuildSuccess(Double.parseDouble(argument));
    } catch (RuntimeException ex) {
      return ObjectTryable.BuildFailure("cannot cast '" + argument + "' to double");
    }
  }
}
