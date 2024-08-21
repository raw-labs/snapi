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

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.list;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.util.Arrays;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable.list.ListIterable;

@ExportLibrary(InteropLibrary.class)
public final class ShortList implements TruffleObject {
  private final short[] list;

  public ShortList(short[] list) {
    this.list = list;
  }

  boolean isList() {
    return true;
  }

  public short[] getInnerList() {
    return list;
  }

  boolean isElementReadable(int index) {
    return index >= 0 && index < list.length;
  }

  public short get(long index) {
    int idx = (int) index;
    if (!isElementReadable(idx)) {
      throw new IndexOutOfBoundsException("index out of bounds");
    }
    return list[idx];
  }

  public int size() {
    return list.length;
  }

  public ListIterable toIterable() {
    return new ListIterable(this);
  }

  public ShortList sort() {
    short[] result = this.list.clone();
    Arrays.sort(result);
    return new ShortList(result);
  }

  public ShortList take(int num) {
    if (num >= this.getInnerList().length) {
      return this;
    } else if (num <= 0) {
      return new ShortList(new short[0]);
    } else {
      short[] result = new short[num];
      System.arraycopy(this.list, 0, result, 0, result.length);
      return new ShortList(result);
    }
  }

  // InteropLibrary: Array

  @ExportMessage
  final boolean hasArrayElements() {
    return true;
  }

  @ExportMessage
  final long getArraySize() {
    return list.length;
  }

  @ExportMessage
  final boolean isArrayElementReadable(long index) {
    return index >= 0 && index < list.length;
  }

  @ExportMessage
  final short readArrayElement(long index) throws ArrayIndexOutOfBoundsException {
    int idx = (int) index;
    if (!isElementReadable(idx)) {
      throw new ArrayIndexOutOfBoundsException(idx);
    }
    return list[idx];
  }
}
