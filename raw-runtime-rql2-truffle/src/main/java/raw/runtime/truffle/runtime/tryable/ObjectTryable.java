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
import com.oracle.truffle.api.library.LibraryFactory;

@ExportLibrary(TryableLibrary.class)
public final class ObjectTryable {

  private final Object successValue;
  private final String failureValue;

  private ObjectTryable(Object successValue, String failureValue) {
    this.successValue = successValue;
    this.failureValue = failureValue;
  }

  public static ObjectTryable BuildSuccess(Object successValue) {
    return new ObjectTryable(successValue, null);
  }

  public static ObjectTryable BuildFailure(String failureValue) {
    return new ObjectTryable(0, failureValue);
  }

  @ExportMessage
  boolean isTryable() {
    return true;
  }

  @ExportMessage
  public Object success() {
    // assert(isSuccess());
    return successValue;
  }

  @ExportMessage
  public String failure() {
    //  assert(isFailure());
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
