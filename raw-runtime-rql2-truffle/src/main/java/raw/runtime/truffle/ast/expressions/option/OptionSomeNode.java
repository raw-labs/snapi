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

package raw.runtime.truffle.ast.expressions.option;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.option.*;

@NodeChild("value")
public abstract class OptionSomeNode extends ExpressionNode {

    @Specialization
    protected Object someByte(byte value) {
        return new ByteOption(value);
    }

    @Specialization
    protected Object someShort(short value) {
        return new ShortOption(value);
    }

    @Specialization
    protected Object someInt(int value) {
        return new IntOption(value);
    }

    @Specialization
    protected Object someLong(long value) {
        return new LongOption(value);
    }

    @Specialization
    protected Object someFloat(float value) {
        return new FloatOption(value);
    }

    @Specialization
    protected Object someDouble(double value) {
        return new DoubleOption(value);
    }

    @Specialization
    protected Object someBoolean(boolean value) {
        return new BooleanOption(value);
    }

    @Specialization
    protected Object someString(String value) {
        return new StringOption(value);
    }

    @Specialization
    protected Object someOption(Object value) {
        return new ObjectOption(value);
    }
}
