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

package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by;

import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.StopIterationException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import java.util.ArrayList;
import java.util.Objects;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer.OrderByInputBuffer;
import raw.runtime.truffle.runtime.list.StringList;

@ExportLibrary(InteropLibrary.class)
public class OrderBySpilledFilesGenerator implements TruffleObject {
  private final OffHeapGroupByKeys offHeapGroupByKeys;

  private ArrayList<OrderByInputBuffer>
      inputBuffers; // list of Kryo buffers that contain the spilled data.
  private OrderByInputBuffer currentKryoBuffer; // the current buffer being read.

  public OrderBySpilledFilesGenerator(OffHeapGroupByKeys offHeapGroupByKeys) {
    this.offHeapGroupByKeys = offHeapGroupByKeys;
  }

  public void setInputBuffers(ArrayList<OrderByInputBuffer> inputBuffers) {
    this.inputBuffers = inputBuffers;
  }

  public void setCurrentKryoBuffer(OrderByInputBuffer currentKryoBuffer) {
    this.currentKryoBuffer = currentKryoBuffer;
  }

  public OffHeapGroupByKeys getOffHeapGroupByKeys() {
    return offHeapGroupByKeys;
  }

  public ArrayList<OrderByInputBuffer> getInputBuffers() {
    return inputBuffers;
  }

  public OrderByInputBuffer getCurrentKryoBuffer() {
    return currentKryoBuffer;
  }

  public void addInputBuffer(OrderByInputBuffer inputBuffer) {
    this.inputBuffers.add(inputBuffer);
  }

  @ExportMessage
  final boolean isIterator() {
    return true;
  }

  @ExportMessage
  final boolean hasIteratorNextElement(
      @Bind("$node") Node thisNode,
      @Cached(inline = true) GeneratorNodes.GeneratorHasNextNode hasNextNode)
      throws UnsupportedMessageException {
    return hasNextNode.execute(thisNode, this);
  }

  @ExportMessage
  final Object getIteratorNextElement(
      @Bind("$node") Node thisNode,
      @Cached(inline = true) GeneratorNodes.GeneratorNextNode nextNode)
      throws UnsupportedMessageException, StopIterationException {
    return nextNode.execute(thisNode, this);
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
      String member,
      Object[] args,
      @Bind("$node") Node thisNode,
      @Cached(inline = true) GeneratorNodes.GeneratorCloseNode closeNode) {
    assert (Objects.equals(member, "close"));
    closeNode.execute(thisNode, this);
    return 0;
  }
}
