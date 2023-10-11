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
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import java.math.BigInteger;

@ExportLibrary(OptionLibrary.class)
@ExportLibrary(InteropLibrary.class)
public final class ShortOption implements TruffleObject {

  private short value;

  private boolean isDefined;

  public ShortOption() {
    this.isDefined = false;
  }

  public ShortOption(short value) {
    this.isDefined = true;
    this.value = value;
  }

  @ExportMessage
  boolean isOption() {
    return true;
  }

  @ExportMessage
  public short get() {
    return value;
  }

  @ExportMessage
  public void set(Object value) {
    this.value = (short) value;
    this.isDefined = true;
  }

  @ExportMessage
  public boolean isDefined() {
    return isDefined;
  }

  @ExportMessage
  @CompilerDirectives.TruffleBoundary
  Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
    return "ShortOption";
  }

  @ExportMessage
  boolean isNull() {
    return !isDefined;
  }

  @ExportMessage
  boolean isNumber() {
    return true;
  }

  @ExportMessage
  boolean fitsInByte() {
    return true;
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
    return false;
  }

  @ExportMessage
  boolean fitsInBigInteger() {
    return false;
  }

  @ExportMessage
  short asShort() throws UnsupportedMessageException {
    return value;
  }

  @ExportMessage int asInt() throws UnsupportedMessageException { return 0; }
  @ExportMessage byte asByte() throws UnsupportedMessageException { return (byte)0; }
  @ExportMessage long asLong() throws UnsupportedMessageException { return 0L; }
  @ExportMessage float asFloat() throws UnsupportedMessageException { return 0.0F; }
  @ExportMessage double asDouble() throws UnsupportedMessageException { return 0.0D; }
  @ExportMessage
  BigInteger asBigInteger() throws UnsupportedMessageException { return null; }
}
