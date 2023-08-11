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

@ExportLibrary(TryableLibrary.class)
public final class ErrorTryable {
    private final String failureValue;

    private ErrorTryable(String failureValue) {
        this.failureValue = failureValue;
    }

    public static ErrorTryable BuildFailure(String failureValue) {
        return new ErrorTryable(failureValue);
    }

    @ExportMessage
    boolean isTryable() {
        return true;
    }

    @ExportMessage
    public boolean success() {
        throw new AssertionError("Calling success get on ErrorTryable");
    }

    @ExportMessage
    public String failure() {
        //assert(isFailure());
        return failureValue;
    }

    @ExportMessage
    public boolean isSuccess() {
        return false;
    }

    @ExportMessage
    public boolean isFailure() {
        return true;
    }
}

