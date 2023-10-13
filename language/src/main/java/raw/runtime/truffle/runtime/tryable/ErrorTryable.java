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

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

@ExportLibrary(TryableLibrary.class)
@ExportLibrary(InteropLibrary.class)
public final class ErrorTryable implements TruffleObject {
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
    throw new RawTruffleRuntimeException(failureValue);
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
    return false;
  }

  @ExportMessage
  public boolean isFailure() {
    return true;
  }

  @ExportMessage
  public boolean isException() {
    return true;
  }

  @ExportMessage
  public RuntimeException throwException() {
    return new RawTruffleRuntimeException(failureValue);
  }
}
