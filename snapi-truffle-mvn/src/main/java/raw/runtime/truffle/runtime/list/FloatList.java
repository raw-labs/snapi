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
import java.util.Arrays;
import raw.runtime.truffle.runtime.iterable.list.ListIterable;

@ExportLibrary(InteropLibrary.class)
public final class FloatList implements TruffleObject {

  private final float[] list;

  public FloatList(float[] list) {
    this.list = list;
  }

  public float[] getInnerList() {
    return list;
  }

  boolean isElementReadable(int index) {
    return index >= 0 && index < list.length;
  }

  public float get(long index) {
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

  public FloatList sort() {
    float[] result = this.list.clone();
    Arrays.sort(result);
    return new FloatList(result);
  }

  public FloatList take(int num) {
    if (num >= this.getInnerList().length) {
      return this;
    }
    float[] result = new float[num];
    System.arraycopy(this.list, 0, result, 0, result.length);
    return new FloatList(result);
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
  final float readArrayElement(long index) throws ArrayIndexOutOfBoundsException {
    int idx = (int) index;
    if (!isElementReadable(idx)) {
      throw new ArrayIndexOutOfBoundsException(idx);
    }
    return list[idx];
  }
}
