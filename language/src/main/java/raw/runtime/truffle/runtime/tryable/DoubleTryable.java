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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

import java.math.BigInteger;

@ExportLibrary(TryableLibrary.class)
@ExportLibrary(InteropLibrary.class)
public final class DoubleTryable implements TruffleObject {

  private final double successValue;
  private final String failureValue;

  public DoubleTryable(double successValue, String failureValue) {
    this.successValue = successValue;
    this.failureValue = failureValue;
  }

  public static DoubleTryable BuildSuccess(double successValue) {
    return new DoubleTryable(successValue, null);
  }

  public static DoubleTryable BuildFailure(String failureValue) {
    return new DoubleTryable(0, failureValue);
  }

  @ExportMessage
  boolean isTryable() {
    return true;
  }

  @ExportMessage
  public double success() {
    if (!isSuccess()) {
      throw new RawTruffleRuntimeException(failureValue);
    }
    return successValue;
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
    return failureValue == null;
  }

  @ExportMessage
  public boolean isFailure() {
    return failureValue != null;
  }

  @ExportMessage boolean hasLanguage() { return true; }

  @ExportMessage
  Class<? extends TruffleLanguage<?>> getLanguage() {
    return RawLanguage.class;
  }

  @ExportMessage
  @CompilerDirectives.TruffleBoundary
  Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
    return "DoubleTryable";
  }

  @ExportMessage
  boolean isNumber() {
    return true;
  }

  @ExportMessage
  boolean fitsInByte() {
    return false;
  }

  @ExportMessage
  boolean fitsInShort() {
    return false;
  }

  @ExportMessage
  boolean fitsInInt() {
    return false;
  }

  @ExportMessage
  boolean fitsInLong() {
    return false;
  }

  @ExportMessage
  boolean fitsInFloat() {
    return false;
  }

  @ExportMessage
  boolean fitsInDouble() {
    return true;
  }


  @ExportMessage
  boolean fitsInBigInteger() {
    return false;
  }

  @ExportMessage
  double asDouble() throws UnsupportedMessageException {
    return successValue;
  }

  @ExportMessage byte asByte() throws UnsupportedMessageException { return (byte) 0; }
  @ExportMessage short asShort() throws UnsupportedMessageException { return (short) 0; }
  @ExportMessage int asInt() throws UnsupportedMessageException { return 0; }
  @ExportMessage long asLong() throws UnsupportedMessageException { return 0L; }
  @ExportMessage float asFloat() throws UnsupportedMessageException { return 0.0F; }
  @ExportMessage
  BigInteger asBigInteger() throws UnsupportedMessageException { return null; }

  @ExportMessage
  public boolean isException() {
    return isFailure();
  }

  @ExportMessage
  public RuntimeException throwException() {
    return new RuntimeException(failureValue);
  }

}
