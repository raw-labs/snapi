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
public final class ObjectOption implements TruffleObject {

  private Object value;

  private boolean isDefined;

  private InteropLibrary interops = InteropLibrary.getFactory().createDispatched(3);

  public ObjectOption() {
    this.isDefined = false;
  }

  public ObjectOption(Object value) {
    this.isDefined = true;
    this.value = value;
  }

  @ExportMessage
  boolean isOption() {
    return true;
  }

  @ExportMessage
  public Object get() {
    return value;
  }

  @ExportMessage
  public void set(Object value) {
    this.value = value;
    this.isDefined = true;
  }

  @ExportMessage
  public boolean isDefined() {
    return isDefined;
  }

  //    /* Generator interface */
  //
  //    @ExportMessage
  //    boolean isGenerator() {
  //        return true;
  //    }
  //
  //    @ExportMessage
  //    void init() {
  //    }
  //
  //    @ExportMessage
  //    void close() {
  //    }
  //
  //    @ExportMessage
  //    Object next() {
  //        if (value == null) {
  //            throw new BreakException();
  //        }
  //        return value;
  //    }
  //
  //    @ExportMessage
  //    boolean hasNext() {
  //        return value != null;
  //    }

  /* Option interface */

  //    @ExportMessage
  //    public boolean isOption() {
  //        return true;
  //    }
  //
  //    @ExportMessage
  //    public Object get() {
  //        return value;
  //    }
  //
  //    @ExportMessage
  //    public void set(Object value) {
  //        this.value = value;
  //    }
  //
  //    @ExportMessage
  //    public boolean isDefined() {
  //        return value != null;
  //    }


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
    return "ObjectOption";
  }

  @ExportMessage
  boolean isNull() {
    return !isDefined;
  }

  @ExportMessage
  boolean isString() {
    return interops.isString(value);
  }

  @ExportMessage
  String asString() throws UnsupportedMessageException {
    return interops.asString(value);
  }

  @ExportMessage
  boolean isNumber() {
    return interops.isNumber(value);
  }

  @ExportMessage
  boolean fitsInByte() {
    return interops.fitsInByte(value);
  }

  @ExportMessage
  boolean fitsInShort() {
    return interops.fitsInShort(value);
  }

  @ExportMessage
  boolean fitsInInt() {
    return interops.fitsInInt(value);
  }

  @ExportMessage
  boolean fitsInLong() {
    return interops.fitsInLong(value);
  }

  @ExportMessage
  boolean fitsInFloat() {
    return interops.fitsInFloat(value);
  }


  @ExportMessage
  boolean fitsInDouble() {
    return interops.fitsInDouble(value);
  }


  @ExportMessage
  boolean fitsInBigInteger() {
    return interops.fitsInBigInteger(value);
  }

  @ExportMessage
  byte asByte() throws UnsupportedMessageException {
    return interops.asByte(value);
  }


  @ExportMessage
  short asShort() throws UnsupportedMessageException {
    return interops.asShort(value);
  }


  @ExportMessage
  int asInt() throws UnsupportedMessageException {
    return interops.asInt(value);
  }


  @ExportMessage
  long asLong() throws UnsupportedMessageException {
    return interops.asLong(value);
  }


  @ExportMessage
  float asFloat() throws UnsupportedMessageException {
    return interops.asFloat(value);
  }


  @ExportMessage
  double asDouble() throws UnsupportedMessageException {
    return interops.asDouble(value);
  }


  @ExportMessage
  BigInteger asBigInteger() throws UnsupportedMessageException {
    return interops.asBigInteger(value);
  }


}
