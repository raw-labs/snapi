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

package raw.runtime.truffle.ast.expressions.builtin.numeric.int_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

@NodeInfo(shortName = "Int.From")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class IntFromNode extends ExpressionNode {

  @Specialization
  protected int fromByte(byte argument) {
    return argument;
  }

  @Specialization
  protected int fromShort(short argument) {
    return argument;
  }

  @Specialization
  protected int fromInt(int argument) {
    return argument;
  }

  @Specialization
  protected int fromLong(long argument) {
    return (int) argument;
  }

  @Specialization
  protected int fromFloat(float argument) {
    return (int) argument;
  }

  @Specialization
  protected int fromDouble(double argument) {
    return (int) argument;
  }

  @Specialization
  protected int fromDecimal(BigDecimal argument) {
    return argument.intValue();
  }

  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected ObjectTryable fromString(String argument) {
    try {
      return ObjectTryable.BuildSuccess(Integer.parseInt(argument));
    } catch (RuntimeException ex) {
      return ObjectTryable.BuildFailure("cannot cast '" + argument + "' to int");
    }
  }
}
