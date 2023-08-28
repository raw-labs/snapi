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

package raw.runtime.truffle.runtime.operators;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.nullable_tryable.NullableTryableLibrary;
import raw.runtime.truffle.runtime.nullable_tryable.RuntimeNullableTryableHandler;

import java.math.BigDecimal;
import java.util.Objects;

@ExportLibrary(OperatorLibrary.class)
public class AddOperator {

    @ExportMessage
    public boolean isAggregator() {
        return true;
    }

    @ExportMessage
    static class DoOperation {

        @Specialization
        static Object doByte(AddOperator operator, byte left, byte right) {
            return (byte) (left + right);
        }

        @Specialization
        static Object doShort(AddOperator operator, short left, short right) {
            return (short) (left + right);
        }

        @Specialization
        static Object doInt(AddOperator operator, int left, int right) {
            return left + right;
        }

        @Specialization
        static Object doLong(AddOperator operator, long left, long right) {
            return left + right;
        }

        @Specialization
        static Object doFloat(AddOperator operator, float left, float right) {
            return left + right;
        }

        @Specialization
        static Object doDouble(AddOperator operator, double left, double right) {
            return left + right;
        }

        @Specialization
        static Object doDecimal(AddOperator operator, BigDecimal left, BigDecimal right) {
            return left.add(right);
        }

        @Specialization
        static Object doString(AddOperator operator, String left, String right) {
            return left.concat(right);
        }

        // Here a paradox is created because for max null could be considered the least value but
        // then what happens to max. The goal is to ignore null and keep the value. That's why this
        // method is the only one that returns -2 or 2. The other methods return -1 or 1.
        @Specialization(guards = "left == null || right == null")
        static Object doNull(AddOperator operator, Object left, Object right) {
            if (left == null && right == null) {
                return 0;
            } else return Objects.requireNonNullElse(left, right);
        }

        // It is interesting that @CachedLibrary("comparator") does not go to int case for Object
        // calls
        // instead it was going here again, and it was needed to make it
        // @CachedLibrary(limit = "1") to make it work.
        @Specialization(guards = {"left != null", "right != null"})
        static Object doNullableTryable(
                AddOperator operator,
                Object left,
                Object right,
                @CachedLibrary(limit = "3") OperatorLibrary aggregators,
                @CachedLibrary(limit = "1") NullableTryableLibrary nullableTryables) {
            RuntimeNullableTryableHandler nullableTryableHandler =
                    new RuntimeNullableTryableHandler();
            Object unboxedLeft = nullableTryables.unboxUnsafe(nullableTryableHandler, left);
            Object unboxedRight = nullableTryables.unboxUnsafe(nullableTryableHandler, right);
            return aggregators.doOperation(operator, unboxedLeft, unboxedRight);
        }
    }
}
