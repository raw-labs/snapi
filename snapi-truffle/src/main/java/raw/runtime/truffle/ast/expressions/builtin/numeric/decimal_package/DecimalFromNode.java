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

package raw.runtime.truffle.ast.expressions.builtin.numeric.decimal_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.DecimalObject;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "Decimal.From")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class DecimalFromNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected DecimalObject fromByte(byte argument) {
    return new DecimalObject(new BigDecimal(argument));
  }

  @Specialization
  @TruffleBoundary
  protected DecimalObject fromShort(short argument) {
    return new DecimalObject(new BigDecimal(argument));
  }

  @Specialization
  @TruffleBoundary
  protected DecimalObject fromInt(int argument) {
    return new DecimalObject(new BigDecimal(argument));
  }

  @Specialization
  @TruffleBoundary
  protected DecimalObject fromLong(long argument) {
    return new DecimalObject(new BigDecimal(argument));
  }

  @Specialization
  @TruffleBoundary
  protected DecimalObject fromFloat(float argument) {
    return new DecimalObject(new BigDecimal(argument));
  }

  @Specialization
  @TruffleBoundary
  protected DecimalObject fromDouble(double argument) {
    return new DecimalObject(new BigDecimal(argument));
  }

  @Specialization
  protected DecimalObject fromDecimal(DecimalObject argument) {
    return argument;
  }

  @Specialization
  @TruffleBoundary
  protected Object fromString(String argument) {
    try {
      return new DecimalObject(new BigDecimal(argument));
    } catch (RuntimeException ex) {
      return new ErrorObject("cannot cast '" + argument + "' to decimal");
    }
  }
}
