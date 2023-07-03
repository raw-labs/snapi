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
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.option.OptionLibrary;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimeObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.runtime.tryable.TryableLibrary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// A.Z For now is used only in EQ node, after confirmation that this will work,
// do all the comparison node by using this library.
@ExportLibrary(OperatorLibrary.class)
public class CompareOperator {

    @ExportMessage
    public boolean isAggregator() {
        return true;
    }

    @ExportMessage
    static class DoOperation {

        @Specialization
        static int doBoolean(CompareOperator comparator, boolean left, boolean right) {
            return Boolean.compare(left, right);
        }

        @Specialization
        static int doByte(CompareOperator comparator, byte left, byte right) {
            return Byte.compare(left, right);
        }

        @Specialization
        static int doShort(CompareOperator comparator, short left, short right) {
            return Short.compare(left, right);
        }

        @Specialization
        static int doInt(CompareOperator comparator, int left, int right) {
            return Integer.compare(left, right);
        }

        @Specialization
        static int doLong(CompareOperator comparator, long left, long right) {
            return Long.compare(left, right);
        }

        @Specialization
        static int doFloat(CompareOperator comparator, float left, float right) {
            return Float.compare(left, right);
        }

        @Specialization
        static int doDouble(CompareOperator comparator, double left, double right) {
            return Double.compare(left, right);
        }

        @Specialization
        static int doDecimal(CompareOperator comparator, BigDecimal left, BigDecimal right) {
            return left.compareTo(right);
        }

        @Specialization
        static int doString(CompareOperator comparator, String left, String right) {
            return left.compareTo(right);
        }

        @Specialization
        static int doDate(CompareOperator comparator, DateObject left, DateObject right) {
            LocalDate leftDate = left.getDate();
            LocalDate rightDate = right.getDate();
            if (leftDate.isBefore(rightDate)) {
                return -1;
            } else if (leftDate.isAfter(rightDate)) {
                return 1;
            } else {
                return 0;
            }
        }

        @Specialization
        static int doTimestamp(CompareOperator comparator, TimestampObject left, TimestampObject right) {
            LocalDateTime leftDate = left.getTimestamp();
            LocalDateTime rightDate = right.getTimestamp();
            if (leftDate.isBefore(rightDate)) {
                return -1;
            } else if (leftDate.isAfter(rightDate)) {
                return 1;
            } else {
                return 0;
            }
        }

        @Specialization
        static int doTime(CompareOperator comparator, TimeObject left, TimeObject right) {
            LocalTime leftTime = left.getTime();
            LocalTime rightTime = right.getTime();
            if (leftTime.isBefore(rightTime)) {
                return -1;
            } else if (leftTime.isAfter(rightTime)) {
                return 1;
            } else {
                return 0;
            }
        }

        @Specialization
        static int doInterval(CompareOperator comparator, IntervalObject left, IntervalObject right) {
            return left.compareTo(right);
        }

        @Specialization(limit = "3")
        static int doRecord(CompareOperator comparator, RecordObject left, RecordObject right,
                            @CachedLibrary(limit = "3") OperatorLibrary aggregators,
                            @CachedLibrary("left") InteropLibrary lefts,
                            @CachedLibrary(limit = "3") InteropLibrary arrays,
                            @CachedLibrary("right") InteropLibrary rights) {
            try {
                Object leftKeys = lefts.getMembers(left);
                long leftSize = arrays.getArraySize(leftKeys);
                Object rightKeys = rights.getMembers(right);
                long rightSize = arrays.getArraySize(rightKeys);
                if (leftSize > rightSize) {
                    return 1;
                } else if (leftSize < rightSize) {
                    return -1;
                }
                for (int i = 0; i < leftSize; i++) {
                    String leftKey = (String) arrays.readArrayElement(leftKeys, i);
                    String rightKey = (String) arrays.readArrayElement(rightKeys, i);
                    int result = (int) aggregators.doOperation(comparator, leftKey, rightKey);
                    if (result != 0) {
                        return result;
                    }
                    Object leftValue = lefts.readMember(left, leftKey);
                    Object rightValue = rights.readMember(right, rightKey);
                    result = (int) aggregators.doOperation(comparator, leftValue, rightValue);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            } catch (InvalidArrayIndexException | UnsupportedMessageException | UnknownIdentifierException |
                     RawTruffleRuntimeException e) {
                throw new RawTruffleRuntimeException(e.getMessage());
            }
        }

        @Specialization(limit = "3", guards = {"left != null", "right != null", "lefts.isIterable(left)", "rights.isIterable(right)"})
        static int doIterable(CompareOperator comparator, Object left, Object right,
                              @CachedLibrary("comparator") OperatorLibrary comparators,
                              @CachedLibrary("left") IterableLibrary lefts,
                              @CachedLibrary("right") IterableLibrary rights,
                              @CachedLibrary(limit = "2") GeneratorLibrary generators) {
            Object leftGenerator = lefts.getGenerator(left);
            Object rightGenerator = rights.getGenerator(right);
            while (generators.hasNext(leftGenerator) && generators.hasNext(rightGenerator)) {
                Object leftElement = generators.next(leftGenerator);
                Object rightElement = generators.next(rightGenerator);
                int result = (int) comparators.doOperation(comparator, leftElement, rightElement);
                if (result != 0) {
                    return result;
                }
            }
            if (generators.hasNext(leftGenerator)) {
                return 1;
            } else if (generators.hasNext(rightGenerator)) {
                return -1;
            } else {
                return 0;
            }
        }


        // A.Z Here a paradox is created because for max null could be considered the least value but
        // then what happens to max. The goal is to ignore null and keep the value. That's why this
        // method is the only one that returns -2 or 2. The other methods return -1 or 1.
        @Specialization(guards = "left == null || right == null")
        static int doNull(CompareOperator comparator, Object left, Object right) {
            if (left == null && right == null) {
                return 0;
            } else if (left == null) {
                return -2;
            } else {
                return 2;
            }
        }

        @Specialization(guards = {"tryables.isTryable(left)"})
        static int doTryable(CompareOperator comparator, Object left, Object right,
                             @CachedLibrary(limit = "3") OperatorLibrary comparators,
                             @CachedLibrary(limit = "3") TryableLibrary tryables) {
            // both are tryables (maybe not nullable but that's handled recursively)
            assert (tryables.isTryable(right));
            boolean leftIsSuccess = tryables.isSuccess(left);
            boolean rightIsSuccess = tryables.isSuccess(right);
            if (leftIsSuccess) {
                if (rightIsSuccess) {
                    Object leftValue = tryables.success(left);
                    Object rightValue = tryables.success(right);
                    return (int) comparators.doOperation(comparator, leftValue, rightValue);
                } else {
                    return 1;
                }
            } else {
                if (rightIsSuccess) {
                    return -1;
                } else {
                    String leftFailure = tryables.failure(left);
                    String rightFailure = tryables.failure(right);
                    return (int) comparators.doOperation(comparator, leftFailure, rightFailure);
                }
            }
        }

        @Specialization(guards = {"options.isOption(left)"})
        static int doNullable(CompareOperator comparator, Object left, Object right,
                              @CachedLibrary(limit = "3") OperatorLibrary comparators,
                              @CachedLibrary(limit = "3") OptionLibrary options) {
            // both are options
            assert (options.isOption(right));
            boolean leftIsDefined = options.isDefined(left);
            boolean rightIsDefined = options.isDefined(right);

            if (leftIsDefined) {
                if (rightIsDefined) {
                    Object leftValue = options.get(left);
                    Object rightValue = options.get(right);
                    return (int) comparators.doOperation(comparator, leftValue, rightValue);
                } else {
                    return 1;
                }
            } else {
                if (rightIsDefined) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }

    }

// The alternative
//    @ExportMessage
//    int compareInt(int left, int right) {
//        return Integer.compare(left, right);
//    }
//
//    @ExportMessage(limit = "3")
//    int compareRecord(Object left,
//                              Object right,
//                              @CachedLibrary("left") InteropLibrary lefts,
//                              @CachedLibrary("right") InteropLibrary rights,
//                              @CachedLibrary("this") ComparatorLibrary comparators) {
//        comparators.compareInt(this, 1, 2);
//        return 0;
//    }
}
