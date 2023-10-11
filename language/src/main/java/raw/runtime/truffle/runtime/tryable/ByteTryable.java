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
public final class ByteTryable implements TruffleObject {

  private final byte successValue;
  private final String failureValue;

  public ByteTryable(byte successValue, String failureValue) {
    this.successValue = successValue;
    this.failureValue = failureValue;
  }

  public static ByteTryable BuildSuccess(byte successValue) {
    return new ByteTryable(successValue, null);
  }

  public static ByteTryable BuildFailure(String failureValue) {
    return new ByteTryable((byte) 0, failureValue);
  }

  @ExportMessage
  boolean isTryable() {
    return true;
  }

  @ExportMessage
  public byte success() {
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
    return "ByteTryable";
  }

  @ExportMessage boolean isNumber() {return isSuccess(); }

  @ExportMessage boolean fitsInByte() {
    return true;
  }
  @ExportMessage boolean fitsInShort() {
    return false;
  }
  @ExportMessage boolean fitsInInt() {
    return false;
  }
  @ExportMessage boolean fitsInLong() {
    return false;
  }
  @ExportMessage boolean fitsInFloat() {
    return false;
  }
  @ExportMessage boolean fitsInDouble() {
    return false;
  }
  @ExportMessage boolean fitsInBigInteger() {
    return false;
  }

  @ExportMessage
  byte asByte() {
    return successValue;
  }

  @ExportMessage short asShort() throws UnsupportedMessageException { throw UnsupportedMessageException.create(); }
  @ExportMessage int asInt() throws UnsupportedMessageException { throw UnsupportedMessageException.create(); }
  @ExportMessage long asLong() throws UnsupportedMessageException { throw UnsupportedMessageException.create(); }
  @ExportMessage float asFloat() throws UnsupportedMessageException { throw UnsupportedMessageException.create(); }
  @ExportMessage double asDouble() throws UnsupportedMessageException { throw UnsupportedMessageException.create(); }
  @ExportMessage BigInteger asBigInteger() throws UnsupportedMessageException { throw UnsupportedMessageException.create(); }

  @ExportMessage
  public boolean isException() {
    return isFailure();
  }

  @ExportMessage
  public RuntimeException throwException() {
    return new RuntimeException(failureValue);
  }

}
