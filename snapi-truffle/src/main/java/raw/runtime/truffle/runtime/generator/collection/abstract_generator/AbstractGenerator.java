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

package raw.runtime.truffle.runtime.generator.collection.abstract_generator;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.StopIterationException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.util.Objects;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.list.StringList;

@ExportLibrary(InteropLibrary.class)
public class AbstractGenerator implements TruffleObject {

  private final Object nextGenerator;

  private Object next = null;

  private boolean isTerminated = false;

  private RawTruffleRuntimeException exception = null;

  public AbstractGenerator(Object computeNext) {
    this.nextGenerator = computeNext;
  }

  public Object getNextGenerator() {
    return nextGenerator;
  }

  public boolean isTerminated() {
    return isTerminated;
  }

  public void setTerminated(boolean isTerminated) {
    this.isTerminated = isTerminated;
  }

  public Object getNext() {
    return next;
  }

  public void setNext(Object next) {
    this.next = next;
  }

  public RawTruffleRuntimeException getException() {
    return exception;
  }

  public void setException(RawTruffleRuntimeException exception) {
    this.exception = exception;
  }

  public boolean hasException() {
    return exception != null;
  }

  // InteropLibrary: Iterator
  @ExportMessage
  final boolean isIterator() {
    return true;
  }

  @ExportMessage
  final boolean hasIteratorNextElement(@Cached GeneratorNodes.GeneratorHasNextNode hasNextNode)
      throws UnsupportedMessageException {
    return hasNextNode.execute(this);
  }

  @ExportMessage
  final Object getIteratorNextElement(@Cached GeneratorNodes.GeneratorNextNode nextNode)
      throws UnsupportedMessageException, StopIterationException {
    return nextNode.execute(this);
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
      String member, Object[] args, @Cached GeneratorNodes.GeneratorCloseNode closeNode) {
    assert (Objects.equals(member, "close"));
    closeNode.execute(this);
    return 0;
  }
}
