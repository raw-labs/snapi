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

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ast.BinaryNode;
import raw.runtime.truffle.runtime.tryable.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

// TODO: further optimization could be done by creating permutations of types?
// if we divide 500.0 by 500.0 the result could fit into int, should we specialize that case?

@NodeInfo(shortName = "/")
public abstract class DivNode extends BinaryNode {

    @Specialization
    protected ByteTryable divByte(byte a, byte b) {
        return b != 0
                ? ByteTryable.BuildSuccess((byte) (a / b))
                : ByteTryable.BuildFailure("/ by zero");
    }

    @Specialization
    protected ShortTryable divShort(short a, short b) {
        return b != 0
                ? ShortTryable.BuildSuccess((short) (a / b))
                : ShortTryable.BuildFailure("/ by zero");
    }

    @Specialization
    protected IntTryable divInt(int a, int b) {
        return b != 0 ? IntTryable.BuildSuccess(a / b) : IntTryable.BuildFailure("/ by zero");
    }

    @Specialization
    protected LongTryable divLong(long a, long b) {
        return b != 0 ? LongTryable.BuildSuccess(a / b) : LongTryable.BuildFailure("/ by zero");
    }

    @Specialization
    protected FloatTryable divFloat(float a, float b) {
        return b != 0 ? FloatTryable.BuildSuccess(a / b) : FloatTryable.BuildFailure("/ by zero");
    }

    @Specialization
    protected DoubleTryable divDouble(double a, double b) {
        return b != 0 ? DoubleTryable.BuildSuccess(a / b) : DoubleTryable.BuildFailure("/ by zero");
    }

    @Specialization
    protected ObjectTryable divDecimal(BigDecimal a, BigDecimal b) {
        // Without the MathContext.DECIMAL128, we would get a:
        // java.lang.ArithmeticException: Non-terminating decimal expansion; no exact representable
        // decimal result.
        // MathContext DECIMAL128 = new MathContext(34, RoundingMode.HALF_EVEN);
        // This means 34 digits before rounding
        // TODO: Check if this the rounding mode we want.
        return b.doubleValue() != 0
                ? ObjectTryable.BuildSuccess(a.divide(b, MathContext.DECIMAL128))
                : ObjectTryable.BuildFailure("/ by zero");
    }
}
