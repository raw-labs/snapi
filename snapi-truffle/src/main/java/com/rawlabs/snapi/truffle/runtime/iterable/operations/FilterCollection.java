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
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;

@ExportLibrary(InteropLibrary.class)
public class FilterCollection implements TruffleObject {
  private final Object parentIterable;
  private final Object predicate;
  private final MaterializedFrame frame;

  private final int generatorSlot;

  private final int functionSlot;

  private final int resultSlot;

  public FilterCollection(
      Object iterable,
      Object predicate,
      MaterializedFrame frame,
      int generatorSlot,
      int functionSlot,
      int resultSlot) {
    this.parentIterable = iterable;
    this.predicate = predicate;
    this.frame = frame;
    this.generatorSlot = generatorSlot;
    this.functionSlot = functionSlot;
    this.resultSlot = resultSlot;
  }

  public Object getParentIterable() {
    return parentIterable;
  }

  public Object getPredicate() {
    return predicate;
  }

  public MaterializedFrame getFrame() {
    return frame;
  }

  public int getGeneratorSlot() {
    return generatorSlot;
  }

  public int getFunctionSlot() {
    return functionSlot;
  }

  public int getResultSlot() {
    return resultSlot;
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
