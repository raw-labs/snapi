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

package raw.runtime.truffle.ast.expressions.builtin.numeric.short_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

import java.math.BigDecimal;

@NodeInfo(shortName = "Short.From")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class ShortFromNode extends ExpressionNode {

    @Specialization
    protected short fromByte(byte argument) {
        return argument;
    }

    @Specialization
    protected short fromShort(short argument) {
        return argument;
    }

    @Specialization
    protected short fromInt(int argument) {
        return (short) argument;
    }

    @Specialization
    protected short fromLong(long argument) {
        return (short) argument;
    }

    @Specialization
    protected short fromFloat(float argument) {
        return (short) argument;
    }

    @Specialization
    protected short fromDouble(double argument) {
        return (short) argument;
    }

    @Specialization
    protected short fromDecimal(BigDecimal argument) {
        return argument.shortValue();
    }

    @Specialization
    protected ObjectTryable fromString(String argument) {
        try {
            return ObjectTryable.BuildSuccess(Short.parseShort(argument));
        } catch (RuntimeException ex) {
            return ObjectTryable.BuildFailure("cannot cast '" + argument + "' to short");
        }
    }

}
