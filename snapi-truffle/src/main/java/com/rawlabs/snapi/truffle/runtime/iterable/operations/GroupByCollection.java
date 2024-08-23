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
public class GroupByCollection implements TruffleObject {
  final Object iterable;
  final Object keyFun;
  final Rql2TypeWithProperties keyType;
  final Rql2TypeWithProperties rowType;
  private final MaterializedFrame frame;
  private final int generatorSlot;
  private final int keyFunctionSlot;
  private final int mapSlot;

  public GroupByCollection(
      Object iterable,
      Object keyFun,
      Rql2TypeWithProperties kType,
      Rql2TypeWithProperties rowType,
      MaterializedFrame frame,
      int generatorSlot,
      int keyFunctionSlot,
      int mapSlot) {
    this.iterable = iterable;
    this.keyFun = keyFun;
    this.keyType = kType;
    this.rowType = rowType;
    this.frame = frame;
    this.generatorSlot = generatorSlot;
    this.keyFunctionSlot = keyFunctionSlot;
    this.mapSlot = mapSlot;
  }

  public Object getIterable() {
    return iterable;
  }

  public Object getKeyFun() {
    return keyFun;
  }

  public Rql2TypeWithProperties getKeyType() {
    return keyType;
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

  public int getKeyFunctionSlot() {
    return keyFunctionSlot;
  }

  public int getMapSlot() {
    return mapSlot;
  }

  public boolean hasSameSlots(GroupByCollection other) {
    return this.generatorSlot == other.generatorSlot
        && this.keyFunctionSlot == other.keyFunctionSlot
        && this.mapSlot == other.mapSlot;
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
