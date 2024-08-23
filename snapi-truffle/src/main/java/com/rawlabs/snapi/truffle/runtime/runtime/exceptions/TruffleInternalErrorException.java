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

package com.rawlabs.snapi.truffle.runtime.runtime.exceptions;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;

// This exception is thrown when an internal error occurs in the Truffle runtime.
public class TruffleInternalErrorException extends RuntimeException {

  @CompilerDirectives.TruffleBoundary
  public TruffleInternalErrorException() {
    super("internal error");
  }

  @CompilerDirectives.TruffleBoundary
  public TruffleInternalErrorException(String message) {
    super(message);
  }

  @CompilerDirectives.TruffleBoundary
  public TruffleInternalErrorException(String message, Throwable cause) {
    super(message, cause);
  }

  @CompilerDirectives.TruffleBoundary
  public TruffleInternalErrorException(Throwable cause, Node location) {
    super(location.getDescription(), cause);
  }

  @CompilerDirectives.TruffleBoundary
  public TruffleInternalErrorException(Throwable cause) {
    super(cause);
  }
}
