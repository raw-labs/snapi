/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.off_heap_generator.off_heap.distinct;

import com.esotericsoftware.kryo.io.Input;
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
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.list.StringList;

@ExportLibrary(InteropLibrary.class)
public class DistinctSpilledFilesGenerator implements TruffleObject {
  private final OffHeapDistinct offHeapDistinct;
  private ArrayList<Input> kryoBuffers; // list of Kryo buffers that contain the spilled data.
  private ArrayList<Object> headKeys; // list of keys that are currently at the head of each buffer.

  public DistinctSpilledFilesGenerator(OffHeapDistinct offHeapDistinct) {
    this.offHeapDistinct = offHeapDistinct;
  }

  public ArrayList<Input> getKryoBuffers() {
    return kryoBuffers;
  }

  public void setKryoBuffers(ArrayList<Input> kryoBuffers) {
    this.kryoBuffers = kryoBuffers;
  }

  public ArrayList<Object> getHeadKeys() {
    return headKeys;
  }

  public void setHeadKeys(ArrayList<Object> headKeys) {
    this.headKeys = headKeys;
  }

  public OffHeapDistinct getOffHeapDistinct() {
    return offHeapDistinct;
  }

  // InteropLibrary: Iterator

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
