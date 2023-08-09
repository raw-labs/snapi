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

// before anything else, looks like final tree is "too simple"
// and misses the cast phase

@ExportLibrary(TryableLibrary.class)
public final class LongTryable {

  private final long successValue;
  private final String failureValue;

  public LongTryable(long successValue, String failureValue) {
    this.successValue = successValue;
    this.failureValue = failureValue;
  }

  public static LongTryable BuildSuccess(long successValue) {
    return new LongTryable(successValue, null);
  }

  public static LongTryable BuildFailure(String failureValue) {
    return new LongTryable(0, failureValue);
  }

  @ExportMessage
  boolean isTryable() {
    return true;
  }

  @ExportMessage
  long success() {
    // assert(isSuccess());
    return successValue;
  }

  @ExportMessage
  String failure() {
    // assert(isFailure());
    return failureValue;
  }

  @ExportMessage
  boolean isSuccess() {
    return failureValue == null;
  }

  @ExportMessage
  boolean isFailure() {
    return failureValue != null;
  }
}
