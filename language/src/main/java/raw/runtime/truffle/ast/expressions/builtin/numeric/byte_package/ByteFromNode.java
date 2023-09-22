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

package raw.runtime.truffle.ast.expressions.builtin.numeric.byte_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

@NodeInfo(shortName = "Byte.From")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class ByteFromNode extends ExpressionNode {

  @Specialization
  protected byte fromByte(byte argument) {
    return argument;
  }

  @Specialization
  protected byte fromShort(short argument) {
    return (byte) argument;
  }

  @Specialization
  protected byte fromInt(int argument) {
    return (byte) argument;
  }

  @Specialization
  protected byte fromLong(long argument) {
    return (byte) argument;
  }

  @Specialization
  protected byte fromFloat(float argument) {
    return (byte) argument;
  }

  @Specialization
  protected byte fromDouble(double argument) {
    return (byte) argument;
  }

  @Specialization
  protected byte fromDecimal(BigDecimal argument) {
    return argument.byteValue();
  }

  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected ObjectTryable fromString(String argument) {
    try {
      return ObjectTryable.BuildSuccess(Byte.parseByte(argument));
    } catch (RuntimeException ex) {
      return ObjectTryable.BuildFailure("cannot cast '" + argument + "' to byte");
    }
  }
}
