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

// before anything else, looks like final tree is "too simple"
// and misses the cast phase

@ExportLibrary(TryableLibrary.class)
@ExportLibrary(InteropLibrary.class)
public final class LongTryable implements TruffleObject {

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
    if (!isSuccess()) {
      throw new RawTruffleRuntimeException(failureValue);
    }
    return successValue;
  }

  @ExportMessage
  String failure() {
    if (!isFailure()) {
      throw new RawTruffleRuntimeException("not a failure");
    }
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

  @ExportMessage boolean hasLanguage() { return true; }

  @ExportMessage
  Class<? extends TruffleLanguage<?>> getLanguage() {
    return RawLanguage.class;
  }

  @ExportMessage
  @CompilerDirectives.TruffleBoundary
  Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
    return "LongTryable";
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
    return true;
  }

  @ExportMessage
  boolean fitsInFloat() {
    return false;
  }

  @ExportMessage
  boolean fitsInDouble() {
    return false;
  }


  @ExportMessage
  boolean fitsInBigInteger() {
    return false;
  }

  @ExportMessage
  long asLong() throws UnsupportedMessageException {
    return successValue;
  }

  @ExportMessage byte asByte() throws UnsupportedMessageException { return (byte) 0; }
  @ExportMessage short asShort() throws UnsupportedMessageException { return (short) 0; }
  @ExportMessage int asInt() throws UnsupportedMessageException { return 0; }
  @ExportMessage float asFloat() throws UnsupportedMessageException { return 0.0F; }
  @ExportMessage double asDouble() throws UnsupportedMessageException { return 0.0D; }
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
