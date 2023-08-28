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

package raw.runtime.truffle.runtime.nullable_tryable;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.interpreter.ShortValue;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.option.*;
import raw.runtime.truffle.runtime.tryable.*;

// (az) this one is to be deleted and anywhere it is used shuld be replaced with the
// TryableNullableHandler
@ExportLibrary(NullableTryableLibrary.class)
public class RuntimeNullableTryableHandler {
    @ExportMessage
    public Object unboxUnsafe(
            Object maybeOptionTryable,
            @CachedLibrary(limit = "1") TryableLibrary triables,
            @CachedLibrary("this") NullableTryableLibrary nullableTryables) {
        if (maybeOptionTryable != null) {
            if (triables.isTryable(maybeOptionTryable)) {
                if (triables.isSuccess(maybeOptionTryable)) {
                    Object success = triables.success(maybeOptionTryable);
                    return nullableTryables.unboxOption(this, success);
                } else {
                    throw new RawTruffleRuntimeException(triables.failure(maybeOptionTryable));
                }
            } else {
                return nullableTryables.unboxOption(this, maybeOptionTryable);
            }
        }
        return null;
    }

    @ExportMessage
    public Object unbox(
            Object maybeOptionTryable,
            @CachedLibrary("this") NullableTryableLibrary nullableTryables,
            @CachedLibrary(limit = "1") TryableLibrary triables) {
        if (maybeOptionTryable != null) {
            if (triables.isTryable(maybeOptionTryable)) {
                if (triables.isSuccess(maybeOptionTryable)) {
                    Object success = triables.success(maybeOptionTryable);
                    return nullableTryables.unboxOption(this, success);
                } else {
                    return triables.failure(maybeOptionTryable);
                }
            } else {
                return nullableTryables.unboxOption(this, maybeOptionTryable);
            }
        }
        return null;
    }

    @ExportMessage
    Object unboxOption(
            Object maybeOptionTryable, @CachedLibrary(limit = "1") OptionLibrary options) {
        if (maybeOptionTryable != null) {
            if (options.isOption(maybeOptionTryable)) {
                if (options.isDefined(maybeOptionTryable)) {
                    return options.get(maybeOptionTryable);
                } else {
                    return null;
                }
            } else {
                return maybeOptionTryable;
            }
        }
        return null;
    }

    @ExportMessage
    Boolean handleOptionTriablePredicate(
            Object maybeOptionTryable,
            Boolean defaultValue,
            @CachedLibrary("this") NullableTryableLibrary nullableTryables,
            @CachedLibrary(limit = "1") TryableLibrary tryables) {
        if (maybeOptionTryable != null) {
            if (tryables.isTryable(maybeOptionTryable)) {
                TryableLibrary triables = TryableLibrary.getFactory().create(maybeOptionTryable);
                if (triables.isSuccess(maybeOptionTryable)) {
                    Object success = triables.success(maybeOptionTryable);
                    return nullableTryables.handleOptionPredicate(this, success, defaultValue);
                } else if (defaultValue != null) {
                    return defaultValue;
                } else {
                    throw new RawTruffleRuntimeException(triables.failure(maybeOptionTryable));
                }
            } else {
                return nullableTryables.handleOptionPredicate(
                        this, maybeOptionTryable, defaultValue);
            }
        } else {
            return defaultValue;
        }
    }

    @ExportMessage
    Object getOrElse(
            Object value,
            Object defaultValue,
            @CachedLibrary(limit = "1") TryableLibrary tryables,
            @CachedLibrary(limit = "1") OptionLibrary nullables) {
        if (tryables.isTryable(value)) {
            TryableLibrary tries = TryableLibrary.getFactory().create(value);
            if (tries.isSuccess(value)) {
                Object innerValue = tries.success(value);
                return getOrElseOption(innerValue, defaultValue, nullables);
            } else {
                return defaultValue;
            }
        } else {
            return getOrElseOption(value, defaultValue, nullables);
        }
    }

    private Object getOrElseOption(Object value, Object defaultValue, OptionLibrary nullables) {
        if (nullables.isOption(value)) {
            OptionLibrary nulls = OptionLibrary.getFactory().create(value);
            if (nulls.isDefined(value)) {
                return nulls.get(value);
            } else {
                return defaultValue;
            }
        } else {
            return value;
        }
    }

    @ExportMessage
    Boolean handleOptionPredicate(
            Object value, Boolean defaultValue, @CachedLibrary(limit = "1") OptionLibrary options) {
        if (value != null) {
            if (options.isOption(value)) {
                if (options.isDefined(value)) {
                    return (Boolean) options.get(value);
                } else {
                    return defaultValue;
                }
            } else {
                return (Boolean) value;
            }
        }
        return defaultValue;
    }

    @ExportMessage
    static class BoxOption {
        @Specialization
        static Object doByte(RuntimeNullableTryableHandler nullableTryables, byte value) {
            return new ByteOption(value);
        }

        @Specialization
        static Object doShort(RuntimeNullableTryableHandler nullableTryables, short value) {
            return new ShortValue(value);
        }

        @Specialization
        static Object doInt(RuntimeNullableTryableHandler nullableTryables, int value) {
            return new IntOption(value);
        }

        @Specialization
        static Object doLong(RuntimeNullableTryableHandler nullableTryables, long value) {
            return new LongOption(value);
        }

        @Specialization
        static Object doFloat(RuntimeNullableTryableHandler nullableTryables, float value) {
            return new FloatOption(value);
        }

        @Specialization
        static Object doDouble(RuntimeNullableTryableHandler nullableTryables, double value) {
            return new DoubleOption(value);
        }

        @Specialization
        static Object doBoolean(RuntimeNullableTryableHandler nullableTryables, boolean value) {
            return new BooleanOption(value);
        }

        @Specialization
        static Object doString(RuntimeNullableTryableHandler nullableTryables, String value) {
            return new StringOption(value);
        }

        @Specialization(guards = "options.isOption(value)", limit = "3")
        static Object doOption(
                RuntimeNullableTryableHandler nullableTryables,
                Object value,
                @CachedLibrary("value") OptionLibrary options) {
            return value;
        }

        @Specialization
        static Object doObject(RuntimeNullableTryableHandler nullableTryables, Object value) {
            return new ObjectOption(value);
        }
    }

    @ExportMessage
    static class BoxTryable {
        @Specialization
        static Object doByte(RuntimeNullableTryableHandler nullableTryables, byte value) {
            return ByteTryable.BuildSuccess(value);
        }

        @Specialization
        static Object doShort(RuntimeNullableTryableHandler nullableTryables, short value) {
            return ShortTryable.BuildSuccess(value);
        }

        @Specialization
        static Object doInt(RuntimeNullableTryableHandler nullableTryables, int value) {
            return IntTryable.BuildSuccess(value);
        }

        @Specialization
        static Object doLong(RuntimeNullableTryableHandler nullableTryables, long value) {
            return LongTryable.BuildSuccess(value);
        }

        @Specialization
        static Object doFloat(RuntimeNullableTryableHandler nullableTryables, float value) {
            return FloatTryable.BuildSuccess(value);
        }

        @Specialization
        static Object doDouble(RuntimeNullableTryableHandler nullableTryables, double value) {
            return DoubleTryable.BuildSuccess(value);
        }

        @Specialization
        static Object doBoolean(RuntimeNullableTryableHandler nullableTryables, boolean value) {
            return BooleanTryable.BuildSuccess(value);
        }

        @Specialization
        static Object doString(RuntimeNullableTryableHandler nullableTryables, String value) {
            return StringTryable.BuildSuccess(value);
        }

        @Specialization(guards = "triables.isTryable(value)", limit = "3")
        static Object doTryable(
                RuntimeNullableTryableHandler nullableTryables,
                Object value,
                @CachedLibrary("value") TryableLibrary triables) {
            return value;
        }

        @Specialization
        static Object doObject(RuntimeNullableTryableHandler nullableTryables, Object value) {
            return ObjectTryable.BuildSuccess(value);
        }
    }
}
