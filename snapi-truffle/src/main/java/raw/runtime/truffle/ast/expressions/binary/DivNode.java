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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.MathContext;
import raw.runtime.truffle.ast.BinaryNode;
import raw.runtime.truffle.runtime.primitives.DecimalObject;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

// TODO: further optimization could be done by creating permutations of types?
// if we divide 500.0 by 500.0 the result could fit into int, should we specialize that case?

@NodeInfo(shortName = "/")
public abstract class DivNode extends BinaryNode {

  @Specialization
  protected Object divByte(byte a, byte b) {
    return b != 0 ? (a / b) : new ErrorObject("/ by zero");
  }

  @Specialization
  protected Object divShort(short a, short b) {
    return b != 0 ? (a / b) : new ErrorObject("/ by zero");
  }

  @Specialization
  protected Object divInt(int a, int b) {
    return b != 0 ? (a / b) : new ErrorObject("/ by zero");
  }

  @Specialization
  protected Object divLong(long a, long b) {
    return b != 0 ? a / b : new ErrorObject("/ by zero");
  }

  @Specialization
  protected Object divFloat(float a, float b) {
    return b != 0 ? a / b : new ErrorObject("/ by zero");
  }

  @Specialization
  protected Object divDouble(double a, double b) {
    return b != 0 ? a / b : new ErrorObject("/ by zero");
  }

  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected Object divDecimal(DecimalObject a, DecimalObject b) {
    // Without the MathContext.DECIMAL128, we would get a:
    // java.lang.ArithmeticException: Non-terminating decimal expansion; no exact representable
    // decimal result.
    // MathContext DECIMAL128 = new MathContext(34, RoundingMode.HALF_EVEN);
    // This means 34 digits before rounding
    // TODO: Check if this the rounding mode we want.
    return b.getBigDecimal().doubleValue() != 0
        ? new DecimalObject(a.getBigDecimal().divide(b.getBigDecimal(), MathContext.DECIMAL128))
        : new ErrorObject("/ by zero");
  }
}
