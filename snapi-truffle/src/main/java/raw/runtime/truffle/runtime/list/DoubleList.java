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

package raw.runtime.truffle.runtime.list;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.iterable.list.ListIterable;

import java.util.Arrays;

@ExportLibrary(ListLibrary.class)
@ExportLibrary(InteropLibrary.class)
public final class DoubleList implements TruffleObject {
  private final double[] list;

  public DoubleList(double[] list) {
    this.list = list;
  }

  @ExportMessage
  boolean isList() {
    return true;
  }

  @ExportMessage
  public double[] getInnerList() {
    return list;
  }

  @ExportMessage
  boolean isElementReadable(int index) {
    return index >= 0 && index < list.length;
  }

  @ExportMessage
  public double get(long index) {
    int idx = (int) index;
    if (!isElementReadable(idx)) {
      throw new IndexOutOfBoundsException("index out of bounds");
    }
    return list[idx];
  }

  @ExportMessage
  public int size() {
    return list.length;
  }

  @ExportMessage
  public Object toIterable() {
    return new ListIterable(this);
  }

  @ExportMessage
  public Object sort() {
    double[] result = this.list.clone();
    Arrays.sort(result);
    return new DoubleList(result);
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
  final double readArrayElement(long index) throws ArrayIndexOutOfBoundsException {
    int idx = (int) index;
    if (!isElementReadable(idx)) {
      throw new ArrayIndexOutOfBoundsException(idx);
    }
    return list[idx];
  }
}
