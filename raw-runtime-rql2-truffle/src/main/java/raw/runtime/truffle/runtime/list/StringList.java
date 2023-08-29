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

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.list.ListGenerator;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.iterable.list.ListIterable;

import java.util.Arrays;

@ExportLibrary(ListLibrary.class)
public class StringList {
  private final String[] list;

  public StringList(String[] list) {
    this.list = list;
  }

  @ExportMessage
  boolean isList() {
    return true;
  }

  @ExportMessage
  public String[] getInnerList() {
    return list;
  }

  @ExportMessage
  boolean isElementReadable(int index) {
    return index >= 0 && index < list.length;
  }

  @ExportMessage
  public String get(long index) {
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
    String[] result = this.list.clone();
    Arrays.sort(result);
    return new StringList(result);
  }
}
