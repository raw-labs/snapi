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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.numeric.int_package;

import static com.rawlabs.snapi.truffle.runtime.boundary.TruffleBoundaries.parseInt;

import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.boundary.TruffleBoundaries;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.DecimalObject;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "Int.From")
@NodeChild(value = "argument", type = ExpressionNode.class)
@ImportStatic(TruffleBoundaries.class)
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
  protected int fromDecimal(DecimalObject argument) {
    return argument.getBigDecimal().intValue();
  }

  @Specialization
  protected Object fromString(String argument) {
    try {
      return parseInt(argument);
    } catch (RuntimeException ex) {
      return new ErrorObject("cannot cast '" + argument + "' to int");
    }
  }
}
