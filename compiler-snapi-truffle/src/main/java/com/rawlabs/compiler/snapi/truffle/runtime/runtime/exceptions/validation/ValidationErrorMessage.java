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

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.validation;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(InteropLibrary.class)
public class ValidationErrorMessage implements TruffleObject {

  private final String message;
  private final ValidationErrorRange[] positions;

  public ValidationErrorMessage(String message, ValidationErrorRange[] positions) {
    this.message = message;
    this.positions = positions;
  }

  @ExportMessage
  public final boolean isString() {
    return true;
  }

  @ExportMessage
  public final String asString() {
    return message;
  }

  @ExportMessage
  public final boolean hasArrayElements() {
    return true;
  }

  @ExportMessage
  public final Object readArrayElement(long index) {
    return positions[(int) index];
  }

  @ExportMessage
  public final long getArraySize() {
    return positions.length;
  }

  @ExportMessage
  public final boolean isArrayElementReadable(long index) {
    return index >= 0 && index < positions.length;
  }
}
