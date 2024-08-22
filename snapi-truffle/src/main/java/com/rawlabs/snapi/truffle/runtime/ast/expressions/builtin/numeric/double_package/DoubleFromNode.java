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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.numeric.double_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.DecimalObject;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.ErrorObject;

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
  protected double fromDecimal(DecimalObject argument) {
    return argument.getBigDecimal().doubleValue();
  }

  @Specialization
  @TruffleBoundary
  protected Object fromString(String argument) {
    try {
      return Double.parseDouble(argument);
    } catch (RuntimeException ex) {
      return new ErrorObject("cannot cast '" + argument + "' to double");
    }
  }
}
