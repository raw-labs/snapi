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

package raw.runtime.truffle.handlers;

import raw.compiler.rql2.source.Rql2IsNullableTypeProperty;
import raw.compiler.rql2.source.Rql2IsTryableTypeProperty;
import raw.compiler.rql2.source.Rql2Type;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.option.OptionLibrary;
import raw.runtime.truffle.runtime.tryable.TryableLibrary;

public class NullableTryableHandler {

    public static TryableLibrary triables = TryableLibrary.getFactory().createDispatched(5);
    public static OptionLibrary options = OptionLibrary.getFactory().createDispatched(5);

    public static Object handleTryableNullableUnsafe(Object optionTryable) {
        if (triables.isSuccess(optionTryable)) {
            Object success = triables.success(optionTryable);
            if (options.isDefined(success)) {
                return options.get(success);
            } else {
                return null;
            }
        } else {
            throw new RawTruffleRuntimeException(triables.failure(optionTryable));
        }
    }

    public static Object handleTryableUnsafe(Object tryable) {
        if (triables.isSuccess(tryable)) {
            return triables.success(tryable);
        } else {
            throw new RawTruffleRuntimeException(triables.failure(tryable));
        }
    }

    public static Object handleNullable(Object option) {
        if (options.isDefined(option)) {
            return options.get(option);
        } else {
            return null;
        }
    }

    public static Object handleTryableNullable(Object optionTryable) {
        if (triables.isSuccess(optionTryable)) {
            Object success = triables.success(optionTryable);
            if (options.isDefined(success)) {
                return options.get(success);
            } else {
                return null;
            }
        } else {
            return triables.failure(optionTryable);
        }
    }

    public static Object handleTryable(Object tryable) {
        if (triables.isSuccess(tryable)) {
            return triables.success(tryable);
        } else {
            return triables.failure(tryable);
        }
    }

    /**
     * Handles both tryable and option. If the tryable is failure, it throws an error.
     *
     * @param maybeOptionTryable An object value that can be either Option, Tryable or Both
     * @param typeWithProps Rql2Type to extract nullable or tryable properties
     * @return either returns a value extracted from nullable - tryable, or null for null option, or
     *     throws an error
     */
    public static Object handleOptionTriableUnsafe(
            Object maybeOptionTryable, Rql2TypeWithProperties typeWithProps) {
        if (typeWithProps.props().contains(Rql2IsTryableTypeProperty.apply())) {
            TryableLibrary triables = TryableLibrary.getFactory().create(maybeOptionTryable);
            if (triables.isSuccess(maybeOptionTryable)) {
                Object success = triables.success(maybeOptionTryable);
                return handleOption(success, typeWithProps);
            } else {
                throw new RawTruffleRuntimeException(triables.failure(maybeOptionTryable));
            }
        } else {
            return handleOption(maybeOptionTryable, typeWithProps);
        }
    }

    /**
     * Handles both tryable and option predicates. If the tryable is failure, or the option is null,
     * it returns the default value (something similar to getOrElse in scala)
     *
     * @param maybeOptionTryable An object value that can be either Option, Tryable or Both
     * @param type Rql2Type to extract nullable or tryable properties
     * @param defaultValue The value to be returned in case of error or null
     * @return either a value or default value
     */
    public static Boolean handleOptionTriablePredicate(
            Object maybeOptionTryable, Rql2Type type, Boolean defaultValue) {
        Rql2TypeWithProperties typeWithProps = (Rql2TypeWithProperties) type;
        if (typeWithProps.props().contains(Rql2IsTryableTypeProperty.apply())) {
            TryableLibrary triables = TryableLibrary.getFactory().create(maybeOptionTryable);
            if (triables.isSuccess(maybeOptionTryable)) {
                Object success = triables.success(maybeOptionTryable);
                return handleOptionPredicate(success, typeWithProps, defaultValue);
            } else if (defaultValue != null) {
                return defaultValue;
            } else {
                throw new RawTruffleRuntimeException(triables.failure(maybeOptionTryable));
            }
        } else {
            return handleOptionPredicate(maybeOptionTryable, typeWithProps, defaultValue);
        }
    }

    /**
     * Handles both tryable and option. If the tryable is failure, it returns the error message as
     * string value
     *
     * @param maybeOptionTryable An object value that can be either Option, Tryable or Both
     * @param typeWithProps Rql2Type to extract nullable or tryable properties
     * @return Either the result value or null or the error message string
     */
    public static Object handleOptionTriable(
            Object maybeOptionTryable, Rql2TypeWithProperties typeWithProps) {
        if (typeWithProps.props().contains(Rql2IsTryableTypeProperty.apply())) {
            TryableLibrary triables = TryableLibrary.getFactory().create(maybeOptionTryable);
            if (triables.isSuccess(maybeOptionTryable)) {
                Object success = triables.success(maybeOptionTryable);
                return handleOption(success, typeWithProps);
            } else {
                return triables.failure(maybeOptionTryable);
            }
        } else {
            return handleOption(maybeOptionTryable, typeWithProps);
        }
    }

    private static Object handleOption(Object value, Rql2TypeWithProperties typeWithProps) {
        if (typeWithProps.props().contains(Rql2IsNullableTypeProperty.apply())) {
            OptionLibrary options = OptionLibrary.getFactory().create(value);
            if (options.isDefined(value)) {
                return options.get(value);
            } else {
                return null;
            }
        } else {
            return value;
        }
    }

    private static Boolean handleOptionPredicate(
            Object value, Rql2TypeWithProperties typeWithProps, Boolean defaultValue) {
        if (typeWithProps.props().contains(Rql2IsNullableTypeProperty.apply())) {
            OptionLibrary options = OptionLibrary.getFactory().create(value);
            if (options.isDefined(value)) {
                return (Boolean) options.get(value);
            } else return defaultValue;
        } else {
            return (Boolean) value;
        }
    }
}
