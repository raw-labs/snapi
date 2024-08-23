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
import com.rawlabs.snapi.frontend.rql2.source.Rql2TypeWithProperties;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;

@ExportLibrary(InteropLibrary.class)
public class OrderByCollection implements TruffleObject {
  final Object parentIterable;
  final Object[] keyFunctions;
  final int[] keyOrderings;
  final Rql2TypeWithProperties[] keyTypes;
  final Rql2TypeWithProperties rowType;
  private final MaterializedFrame frame;
  private final int generatorSlot;
  private final int collectionSlot;
  private final int offHeapGroupByKeysSlot;

  public OrderByCollection(
      Object iterable,
      Object[] keyFunctions,
      int[] keyOrderings,
      Rql2TypeWithProperties[] keyTypes,
      Rql2TypeWithProperties rowType,
      MaterializedFrame frame,
      int generatorSlot,
      int collectionSlot,
      int offHeapGroupByKeysSlot) {
    this.parentIterable = iterable;
    this.keyFunctions = keyFunctions;
    this.keyOrderings = keyOrderings;
    this.keyTypes = keyTypes;
    this.rowType = rowType;
    this.frame = frame;
    this.generatorSlot = generatorSlot;
    this.collectionSlot = collectionSlot;
    this.offHeapGroupByKeysSlot = offHeapGroupByKeysSlot;
  }

  public Object getParentIterable() {
    return parentIterable;
  }

  public Object[] getKeyFunctions() {
    return keyFunctions;
  }

  public int[] getKeyOrderings() {
    return keyOrderings;
  }

  public Rql2TypeWithProperties[] getKeyTypes() {
    return keyTypes;
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

  public int getCollectionSlot() {
    return collectionSlot;
  }

  public int getOffHeapGroupByKeysSlot() {
    return offHeapGroupByKeysSlot;
  }

  public boolean hasSameSlots(OrderByCollection other) {
    return this.generatorSlot == other.generatorSlot
        && this.collectionSlot == other.collectionSlot
        && this.offHeapGroupByKeysSlot == other.offHeapGroupByKeysSlot;
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
