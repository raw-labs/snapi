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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.numeric.byte_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.DecimalObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.ErrorObject;

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
  protected byte fromDecimal(DecimalObject argument) {
    return argument.getBigDecimal().byteValue();
  }

  @Specialization
  @TruffleBoundary
  protected Object fromString(String argument) {
    try {
      return Byte.parseByte(argument);
    } catch (RuntimeException ex) {
      return new ErrorObject("cannot cast '" + argument + "' to byte");
    }
  }
}
