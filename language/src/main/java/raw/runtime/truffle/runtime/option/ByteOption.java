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

package raw.runtime.truffle.runtime.option;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.RawLanguage;

import java.math.BigInteger;

@ExportLibrary(OptionLibrary.class)
@ExportLibrary(InteropLibrary.class)
public final class ByteOption implements TruffleObject {

  private byte value;

  private boolean isDefined;

  public ByteOption() {
    this.isDefined = false;
  }

  public ByteOption(byte value) {
    this.isDefined = true;
    this.value = value;
  }

  @ExportMessage
  boolean isOption() {
    return true;
  }

  @ExportMessage
  public byte get() {
    return value;
  }

  @ExportMessage
  public void set(Object value) {
    this.value = (byte) value;
    this.isDefined = true;
  }

  @ExportMessage
  public boolean isDefined() {
    return isDefined;
  }

  @ExportMessage
  boolean hasLanguage() {
    return true;
  }

  @ExportMessage
  Class<? extends TruffleLanguage<?>> getLanguage() {
    return RawLanguage.class;
  }

  @ExportMessage
  @CompilerDirectives.TruffleBoundary
  Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
    return "ByteOption";
  }

  @ExportMessage
  boolean isNull() {
    return !isDefined;
  }

  @ExportMessage
  boolean isNumber() { return !isNull(); }

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
  byte asByte() throws UnsupportedMessageException {
    return value;
  }

  @ExportMessage short asShort() throws UnsupportedMessageException { throw UnsupportedMessageException.create(); }
  @ExportMessage int asInt() throws UnsupportedMessageException { throw UnsupportedMessageException.create(); }
  @ExportMessage long asLong() throws UnsupportedMessageException { throw UnsupportedMessageException.create(); }
  @ExportMessage float asFloat() throws UnsupportedMessageException { throw UnsupportedMessageException.create(); }
  @ExportMessage double asDouble() throws UnsupportedMessageException { throw UnsupportedMessageException.create(); }
  @ExportMessage BigInteger asBigInteger() throws UnsupportedMessageException { throw UnsupportedMessageException.create(); }
}
