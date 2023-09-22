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

package raw.runtime.truffle.ast.expressions.builtin.numeric.float_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

@NodeInfo(shortName = "Float.From")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class FloatFromNode extends ExpressionNode {

  @Specialization
  protected float fromByte(byte argument) {
    return argument;
  }

  @Specialization
  protected float fromShort(short argument) {
    return argument;
  }

  @Specialization
  protected float fromInt(int argument) {
    return argument;
  }

  @Specialization
  protected float fromLong(long argument) {
    return argument;
  }

  @Specialization
  protected float fromFloat(float argument) {
    return argument;
  }

  @Specialization
  protected float fromDouble(double argument) {
    return (float) argument;
  }

  @Specialization
  protected float fromDecimal(BigDecimal argument) {
    return argument.floatValue();
  }

  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected ObjectTryable fromString(String argument) {
    try {
      return ObjectTryable.BuildSuccess(Float.parseFloat(argument));
    } catch (RuntimeException ex) {
      return ObjectTryable.BuildFailure("cannot cast '" + argument + "' to float");
    }
  }
}
