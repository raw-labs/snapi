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

package com.rawlabs.snapi.truffle.runtime.primitives;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import java.math.BigDecimal;
import java.math.BigInteger;

@ExportLibrary(InteropLibrary.class)
public final class DecimalObject implements TruffleObject {
  private final BigDecimal bigDecimal;

  public DecimalObject(BigDecimal bigDecimal) {
    this.bigDecimal = bigDecimal;
  }

  public BigDecimal getBigDecimal() {
    return bigDecimal;
  }

  @ExportMessage
  final boolean hasLanguage() {
    return true;
  }

  @ExportMessage
  final Class<? extends TruffleLanguage<?>> getLanguage() {
    return SnapiLanguage.class;
  }

  @ExportMessage
  final Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
    return "Decimal";
  }

  @ExportMessage
  final boolean isString() {
    return true;
  }

  @ExportMessage
  @TruffleBoundary
  final String asString() {
    return bigDecimal.toString();
  }

  @ExportMessage
  boolean isNumber() {
    // Yes, we want to treat it as a numeric type.
    return true;
  }
  //
  // --- fitsInByte / asByte ---
  //
  @ExportMessage
  boolean fitsInByte() {
    // Must be integral (scale=0) and within Byte range
    return scaleIsZero() && withinRange(bigDecimal, Byte.MIN_VALUE, Byte.MAX_VALUE);
  }

  @ExportMessage
  byte asByte() throws UnsupportedMessageException {
    if (!fitsInByte()) {
      throw UnsupportedMessageException.create();
    }
    // This should not throw if fitsInByte() is true.
    return bigDecimal.byteValueExact();
  }

  //
  // --- fitsInShort / asShort ---
  //
  @ExportMessage
  boolean fitsInShort() {
    return scaleIsZero() && withinRange(bigDecimal, Short.MIN_VALUE, Short.MAX_VALUE);
  }

  @ExportMessage
  short asShort() throws UnsupportedMessageException {
    if (!fitsInShort()) {
      throw UnsupportedMessageException.create();
    }
    return bigDecimal.shortValueExact();
  }

  //
  // --- fitsInInt / asInt ---
  //
  @ExportMessage
  boolean fitsInInt() {
    return scaleIsZero() && withinRange(bigDecimal, Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  @ExportMessage
  int asInt() throws UnsupportedMessageException {
    if (!fitsInInt()) {
      throw UnsupportedMessageException.create();
    }
    return bigDecimal.intValueExact();
  }

  //
  // --- fitsInLong / asLong ---
  //
  @ExportMessage
  boolean fitsInLong() {
    try {
      bigDecimal.longValueExact();
      return true;
    } catch (ArithmeticException ex) {
      return false;
    }
  }

  @ExportMessage
  long asLong() throws UnsupportedMessageException {
    try {
      return bigDecimal.longValueExact();
    } catch (ArithmeticException ex) {
      throw UnsupportedMessageException.create();
    }
  }

  //
  // --- fitsInBigInteger / asBigInteger ---
  //
  @ExportMessage
  boolean fitsInBigInteger() {
    // Must be integral for exact BigInteger
    return scaleIsZero();
  }

  @ExportMessage
  BigInteger asBigInteger() throws UnsupportedMessageException {
    if (!fitsInBigInteger()) {
      throw UnsupportedMessageException.create();
    }
    // Safe for integral decimals
    return bigDecimal.toBigIntegerExact();
  }

  //
  // --- fitsInFloat / asFloat ---
  //
  @ExportMessage
  boolean fitsInFloat() {
    // We'll consider it "fits" if it's finite;
    // you could do more precise range checks if you prefer.
    float f = bigDecimal.floatValue();
    return !Float.isInfinite(f) && !Float.isNaN(f);
  }

  @ExportMessage
  float asFloat() throws UnsupportedMessageException {
    float f = bigDecimal.floatValue();
    if (Float.isInfinite(f) || Float.isNaN(f)) {
      throw UnsupportedMessageException.create();
    }
    return f;
  }

  //
  // --- fitsInDouble / asDouble ---
  //
  @ExportMessage
  boolean fitsInDouble() {
    double d = bigDecimal.doubleValue();
    return !Double.isInfinite(d) && !Double.isNaN(d);
  }

  @ExportMessage
  double asDouble() throws UnsupportedMessageException {
    double d = bigDecimal.doubleValue();
    if (Double.isInfinite(d) || Double.isNaN(d)) {
      throw UnsupportedMessageException.create();
    }
    return d;
  }

  //
  // Helper methods
  //
  @TruffleBoundary
  private static boolean withinRange(BigDecimal bd, long minVal, long maxVal) {
    // Check bd >= minVal AND bd <= maxVal
    return bd.compareTo(BigDecimal.valueOf(minVal)) >= 0 &&
            bd.compareTo(BigDecimal.valueOf(maxVal)) <= 0;
  }

  @TruffleBoundary
  private boolean scaleIsZero() {
    return bigDecimal.scale() == 0;
  }
}
