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

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.rawlabs.compiler.Message;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.validation.ValidationErrorObject;
import com.rawlabs.snapi.truffle.runtime.runtime.list.StringList;
import java.util.List;

@ExportLibrary(InteropLibrary.class)
public class RawTruffleValidationException extends AbstractTruffleException {

  private final ValidationErrorObject errors;

  @TruffleBoundary
  public RawTruffleValidationException(List<Message> errors) {
    super("validation failure");
    this.errors = new ValidationErrorObject(errors);
  }

  @ExportMessage
  public boolean isException() {
    return true;
  }

  @ExportMessage
  public boolean hasExceptionCause() {
    return true;
  }

  @ExportMessage
  public Object getExceptionCause() {
    return errors;
  }

  @ExportMessage
  public boolean hasExceptionMessage() {
    return this.getMessage() != null;
  }

  @ExportMessage
  public Object getExceptionMessage() {
    return this.getMessage();
  }

  @ExportMessage
  public RuntimeException throwException() {
    return this;
  }

  @ExportMessage
  public boolean hasExceptionStackTrace() {
    return false;
  }

  @ExportMessage
  final Object getExceptionStackTrace() throws UnsupportedMessageException {
    return null;
  }

  @ExportMessage
  public final boolean hasMembers() {
    return true;
  }

  @ExportMessage
  public final Object readMember(String member) {
    if (member.equals("errors")) return errors;
    else return null;
  }

  @ExportMessage
  final Object getMembers(boolean includeInternal) throws UnsupportedMessageException {
    return new StringList(new String[] {"errors"});
  }

  @ExportMessage
  final boolean isMemberReadable(String member) {
    return member.equals("errors");
  }
}
