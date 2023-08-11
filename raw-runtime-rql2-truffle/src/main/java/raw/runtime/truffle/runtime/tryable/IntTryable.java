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
public final class IntTryable {

    private final int successValue;
    private final String failureValue;

    public IntTryable(int successValue, String failureValue) {
        this.successValue = successValue;
        this.failureValue = failureValue;
    }

    public static IntTryable BuildSuccess(int successValue) {
        return new IntTryable(successValue, null);
    }

    public static IntTryable BuildFailure(String failureValue) {
        return new IntTryable(0, failureValue);
    }

    @ExportMessage
    boolean isTryable() {
        return true;
    }

    @ExportMessage
    public int success() {
        //assert(isSuccess());
        return successValue;
    }

    @ExportMessage
    public String failure() {
        //assert(isFailure());
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
