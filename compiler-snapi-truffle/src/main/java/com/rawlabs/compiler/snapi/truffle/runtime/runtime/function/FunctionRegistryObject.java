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

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.function;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.util.HashMap;
import java.util.Map;
import com.rawlabs.compiler.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.list.StringList;

@ExportLibrary(InteropLibrary.class)
public final class FunctionRegistryObject implements TruffleObject {

  private final Map<String, Object> functions = new HashMap<>();

  @TruffleBoundary
  public Object get(String name) {
    return functions.get(name);
  }

  @TruffleBoundary
  public void put(String name, Object closure) {
    functions.put(name, closure);
  }

  @ExportMessage
  boolean hasMembers() {
    return true;
  }

  @ExportMessage
  final Object getMembers(boolean includeInternal) {
    return new StringList(functions.keySet().toArray(String[]::new));
  }

  @ExportMessage
  final boolean isMemberReadable(String member) {
    return functions.containsKey(member);
  }

  @ExportMessage
  final Object readMember(String member) {
    return functions.get(member);
  }

  @ExportMessage
  final boolean hasLanguage() {
    return true;
  }

  @ExportMessage
  final Class<? extends TruffleLanguage<?>> getLanguage() {
    return RawLanguage.class;
  }

  @ExportMessage
  final Object toDisplayString(boolean allowSideEffects) {
    return "RawScope";
  }

  @ExportMessage
  boolean isScope() {
    return true;
  }
}
