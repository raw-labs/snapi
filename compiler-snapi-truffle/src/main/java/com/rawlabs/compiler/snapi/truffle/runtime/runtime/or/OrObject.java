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

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.or;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.time.*;
import com.rawlabs.compiler.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.list.StringList;

@ExportLibrary(InteropLibrary.class)
public final class OrObject implements TruffleObject {
  private final int index;
  public final Object value;

  public OrObject(int index, Object value) {
    this.index = index;
    this.value = value;
  }

  public int getIndex() {
    return index;
  }

  public Object getValue() {
    return value;
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
  Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
    return "OrObject";
  }

  @ExportMessage
  boolean hasMembers() {
    return true;
  }

  @ExportMessage
  Object readMember(String name, @CachedLibrary("this.value") InteropLibrary interops)
      throws UnsupportedMessageException, UnknownIdentifierException {
    return interops.readMember(value, name);
  }

  @ExportMessage
  Object getMembers(boolean includeInternal) throws UnsupportedMessageException {
    return new StringList(new String[] {"getIndex", "getValue"});
  }

  @ExportMessage
  boolean isMemberInvocable(String member) {
    return true;
  }

  @ExportMessage
  Object invokeMember(String member, Object[] arguments) throws UnsupportedMessageException {
    return switch (member) {
      case "getIndex" -> getIndex();
      case "getValue" -> getValue();
      default -> throw UnsupportedMessageException.create();
    };
  }

  @ExportMessage
  final boolean isMemberReadable(String member) {
    return false;
  }
}
