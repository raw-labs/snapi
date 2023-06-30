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

package raw.runtime.truffle.ast.expressions.unary;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import raw.runtime.truffle.ExpressionNode;

import java.math.BigDecimal;

@NodeChild("value")
public abstract class NegNode extends ExpressionNode {

    @Specialization
    protected byte negByte(byte value) {
        return (byte) -value;
    }

    @Specialization
    protected short negShort(short value) {
        return (short) -value;
    }

    @Specialization
    protected int negInt(int value) {
        return -value;
    }

    @Specialization
    protected long negLong(long value) {
        return -value;
    }

    @Specialization
    protected float negFloat(float value) {
        return -value;
    }

    @Specialization
    protected double negDouble(double value) {
        return -value;
    }

    @Specialization
    protected BigDecimal negDecimal(BigDecimal value) {
        return value.negate();
    }
}
