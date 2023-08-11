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

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

import java.math.BigDecimal;

@NodeInfo(shortName = "Decimal.From")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class DecimalFromNode extends ExpressionNode {

    @Specialization
    protected BigDecimal fromByte(byte argument) {
        return new BigDecimal(argument);
    }

    @Specialization
    protected BigDecimal fromShort(short argument) {
        return new BigDecimal(argument);
    }

    @Specialization
    protected BigDecimal fromInt(int argument) {
        return new BigDecimal(argument);
    }

    @Specialization
    protected BigDecimal fromLong(long argument) {
        return new BigDecimal(argument);
    }

    @Specialization
    protected BigDecimal fromFloat(float argument) {
        return new BigDecimal(argument);
    }

    @Specialization
    protected BigDecimal fromDouble(double argument) {
        return new BigDecimal(argument);
    }

    @Specialization
    protected BigDecimal fromDecimal(BigDecimal argument) {
        return argument;
    }

    @Specialization
    protected ObjectTryable fromString(String argument) {
        try {
            return ObjectTryable.BuildSuccess(new BigDecimal(argument));
        } catch (RuntimeException ex) {
            return ObjectTryable.BuildFailure("cannot cast '" + argument + "' to decimal");
        }
    }

}
