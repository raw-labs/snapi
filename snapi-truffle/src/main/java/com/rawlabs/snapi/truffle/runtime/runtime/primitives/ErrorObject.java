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

package com.rawlabs.snapi.truffle.runtime.runtime.primitives;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleRuntimeException;

@ExportLibrary(InteropLibrary.class)
public class ErrorObject implements TruffleObject {
  private final String message;

  public ErrorObject(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @ExportMessage
  public boolean isException() {
    return true;
  }

  @ExportMessage
  public RuntimeException throwException() {
    return new TruffleRuntimeException(message);
  }
}
