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

package com.rawlabs.snapi.truffle.runtime.iterable.operations;

import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.snapi.frontend.snapi.source.SnapiTypeWithProperties;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;

@ExportLibrary(InteropLibrary.class)
public class DistinctCollection implements TruffleObject {
  final Object iterable;
  final SnapiTypeWithProperties rowType;
  private final MaterializedFrame frame;
  private final int generatorSlot;
  private final int offHeapDistinctSlot;

  public DistinctCollection(
      Object iterable,
      SnapiTypeWithProperties vType,
      MaterializedFrame frame,
      int generatorSlot,
      int offHeapDistinctSlot) {
    this.iterable = iterable;
    this.rowType = vType;
    this.frame = frame;
    this.generatorSlot = generatorSlot;
    this.offHeapDistinctSlot = offHeapDistinctSlot;
  }

  public Object getIterable() {
    return iterable;
  }

  public SnapiTypeWithProperties getRowType() {
    return rowType;
  }

  public MaterializedFrame getFrame() {
    return frame;
  }

  public int getGeneratorSlot() {
    return generatorSlot;
  }

  public int getOffHeapDistinctSlot() {
    return offHeapDistinctSlot;
  }

  public boolean hasSameSlots(DistinctCollection other) {
    return this.generatorSlot == other.generatorSlot
        && this.offHeapDistinctSlot == other.offHeapDistinctSlot;
  }

  // InteropLibrary: Iterable

  @ExportMessage
  boolean hasIterator() {
    return true;
  }

  @ExportMessage
  Object getIterator(
      @Bind("$node") Node thisNode,
      @Cached(inline = true) IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) GeneratorNodes.GeneratorInitNode initNode) {
    Object generator = getGeneratorNode.execute(thisNode, this);
    initNode.execute(thisNode, generator);
    return generator;
  }
}
