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

package com.rawlabs.snapi.truffle.runtime.list;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.rawlabs.snapi.truffle.runtime.iterable.list.ListIterable;
import java.util.ArrayList;

@ExportLibrary(InteropLibrary.class)
public class TruffleArrayList implements TruffleObject {
  private final ArrayList<Object> list;

  public TruffleArrayList(ArrayList<Object> list) {
    this.list = list;
  }

  public ArrayList<Object> getInnerList() {
    return list;
  }

  boolean isElementReadable(int index) {
    return index >= 0 && index < list.size();
  }

  @CompilerDirectives.TruffleBoundary
  public Object get(long index) {
    int idx = (int) index;
    if (!isElementReadable(idx)) {
      throw new IndexOutOfBoundsException("index out of bounds");
    }
    return list.get(idx);
  }

  public int size() {
    return list.size();
  }

  public ListIterable toIterable() {
    return new ListIterable(this);
  }

  @CompilerDirectives.TruffleBoundary
  public TruffleArrayList take(int num) {
    if (num >= this.list.size()) {
      return this;
    } else if (num <= 0) {
      return new TruffleArrayList(new ArrayList<>());
    } else {
      return new TruffleArrayList(new ArrayList<>(list.subList(0, num)));
    }
  }

  // InteropLibrary: Array

  @ExportMessage
  final boolean hasArrayElements() {
    return true;
  }

  @ExportMessage
  final long getArraySize() {
    return list.size();
  }

  @ExportMessage
  final boolean isArrayElementReadable(long index) {
    return index >= 0 && index < list.size();
  }

  @ExportMessage
  final Object readArrayElement(long index) throws ArrayIndexOutOfBoundsException {
    int idx = (int) index;
    if (!isElementReadable(idx)) {
      throw new ArrayIndexOutOfBoundsException(idx);
    }
    return list.get(idx);
  }
}
