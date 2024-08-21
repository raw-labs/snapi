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

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable.operations;

import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.compiler.snapi.rql2.source.Rql2TypeWithProperties;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable.IterableNodes;

@ExportLibrary(InteropLibrary.class)
public class DistinctCollection implements TruffleObject {
  final Object iterable;
  final Rql2TypeWithProperties rowType;
  private final MaterializedFrame frame;
  private final int generatorSlot;
  private final int offHeapDistinctSlot;

  public DistinctCollection(
      Object iterable,
      Rql2TypeWithProperties vType,
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

  public Rql2TypeWithProperties getRowType() {
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
