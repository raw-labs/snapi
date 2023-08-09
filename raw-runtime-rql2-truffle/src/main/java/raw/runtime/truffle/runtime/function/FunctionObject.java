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

package raw.runtime.truffle.runtime.function;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import java.util.HashMap;
import java.util.Map;

@ExportLibrary(InteropLibrary.class)
@SuppressWarnings("static-method")
final class FunctionObject implements TruffleObject {

  final Map<String, Function> functions = new HashMap<>();

  FunctionObject() {}

  @ExportMessage
  boolean hasMembers() {
    return true;
  }

  @ExportMessage
  @TruffleBoundary
  Object readMember(String member) throws UnknownIdentifierException {
    Object value = functions.get(member);
    if (value != null) {
      return value;
    }
    throw UnknownIdentifierException.create(member);
  }

  @ExportMessage
  @TruffleBoundary
  boolean isMemberReadable(String member) {
    return functions.containsKey(member);
  }

  @ExportMessage
  @TruffleBoundary
  Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
    return new FunctionNamesObject(functions.keySet().toArray());
  }

  @ExportLibrary(InteropLibrary.class)
  static final class FunctionNamesObject implements TruffleObject {

    private final Object[] names;

    FunctionNamesObject(Object[] names) {
      this.names = names;
    }

    @ExportMessage
    boolean hasArrayElements() {
      return true;
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
      return index >= 0 && index < names.length;
    }

    @ExportMessage
    long getArraySize() {
      return names.length;
    }

    @ExportMessage
    Object readArrayElement(long index) throws InvalidArrayIndexException {
      if (!isArrayElementReadable(index)) {
        throw InvalidArrayIndexException.create(index);
      }
      return names[(int) index];
    }
  }
}
