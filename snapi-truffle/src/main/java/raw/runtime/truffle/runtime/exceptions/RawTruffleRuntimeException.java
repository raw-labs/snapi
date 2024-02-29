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

package raw.runtime.truffle.runtime.exceptions;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.interop.ExceptionType;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;

@ExportLibrary(InteropLibrary.class)
public class RawTruffleRuntimeException extends AbstractTruffleException {

  @TruffleBoundary
  public RawTruffleRuntimeException(String message) {
    super(message);
  }

  @TruffleBoundary
  public RawTruffleRuntimeException(String message, Throwable cause, Node location) {
    super(message, cause, UNLIMITED_STACK_TRACE, location);
  }

  @TruffleBoundary
  public RawTruffleRuntimeException(String message, Node location) {
    super(message, location);
  }

  @TruffleBoundary
  public RawTruffleRuntimeException(String message, Throwable cause) {
    super(message, cause, UNLIMITED_STACK_TRACE, null);
  }

  @TruffleBoundary
  public RawTruffleRuntimeException(Throwable cause, Node location) {
    super(cause.toString(), cause, UNLIMITED_STACK_TRACE, location);
  }

  @ExportMessage
  public ExceptionType getExceptionType() {
    return ExceptionType.RUNTIME_ERROR;
  }
}
