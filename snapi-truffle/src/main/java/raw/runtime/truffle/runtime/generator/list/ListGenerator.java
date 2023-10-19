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

package raw.runtime.truffle.runtime.generator.list;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.StopIterationException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.util.Objects;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.list.ListLibrary;
import raw.runtime.truffle.runtime.list.StringList;

@ExportLibrary(GeneratorLibrary.class)
@ExportLibrary(InteropLibrary.class)
public class ListGenerator implements TruffleObject {

  final Object list;
  private int position = 0;

  public ListGenerator(Object list) {
    this.list = list;
  }

  @ExportMessage
  boolean isGenerator() {
    return true;
  }

  @ExportMessage
  void init() {}

  @ExportMessage
  void close() {}

  @ExportMessage
  public boolean hasNext(@CachedLibrary("this.list") ListLibrary lists) {
    return this.position < lists.size(list);
  }

  @ExportMessage
  public Object next(@CachedLibrary("this.list") ListLibrary lists) {
    Object item = lists.get(list, position);
    this.position++;
    return item;
  }

  // InteropLibrary: Iterator

  @ExportMessage
  final boolean isIterator() {
    return true;
  }

  @ExportMessage
  final boolean hasIteratorNextElement(@CachedLibrary("this") GeneratorLibrary generatorLibrary)
      throws UnsupportedMessageException {
    return generatorLibrary.hasNext(this);
  }

  @ExportMessage
  final Object getIteratorNextElement(@CachedLibrary("this") GeneratorLibrary generatorLibrary)
      throws UnsupportedMessageException, StopIterationException {
    return generatorLibrary.next(this);
  }

  @ExportMessage
  final boolean hasMembers() {
    return true;
  }

  @ExportMessage
  final Object getMembers(boolean includeInternal) {
    return new StringList(new String[] {"close"});
  }

  @ExportMessage
  final boolean isMemberInvocable(String member) {
    return Objects.equals(member, "close");
  }

  @ExportMessage
  final Object invokeMember(
      String member, Object[] args, @CachedLibrary("this") GeneratorLibrary generatorLibrary) {
    assert (Objects.equals(member, "close"));
    generatorLibrary.close(this);
    return 0;
  }
}
