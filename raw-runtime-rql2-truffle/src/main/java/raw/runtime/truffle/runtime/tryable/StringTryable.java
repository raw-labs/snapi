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

package raw.runtime.truffle.runtime.tryable;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

@ExportLibrary(TryableLibrary.class)
public class StringTryable {

    private final String successValue;
    private final String failureValue;

    public StringTryable(String successValue, String failureValue) {
        this.successValue = successValue;
        this.failureValue = failureValue;
    }

    public static StringTryable BuildSuccess(String successValue) {
        return new StringTryable(successValue, null);
    }

    public static StringTryable BuildFailure(String failureValue) {
        return new StringTryable(null, failureValue);
    }

    @ExportMessage
    boolean isTryable() {
        return true;
    }

    @ExportMessage
    public String success() {
        if (!isSuccess()) {
            throw new RawTruffleRuntimeException(failureValue);
        }
        return successValue;
    }

    @ExportMessage
    public String failure() {
        if (!isFailure()) {
            throw new RawTruffleRuntimeException("not a failure");
        }
        return failureValue;
    }

    @ExportMessage
    public boolean isSuccess() {
        return failureValue == null;
    }

    @ExportMessage
    public boolean isFailure() {
        return failureValue != null;
    }
}
