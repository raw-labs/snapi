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
import com.rawlabs.snapi.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import com.rawlabs.snapi.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.EquiJoinComputeNext;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;

@ExportLibrary(InteropLibrary.class)
public class EquiJoinCollection implements TruffleObject {
  final Object leftIterable, rightIterable;
  final Object leftKeyF, rightKeyF, reshapeFun;
  final SnapiTypeWithProperties leftRowType, rightRowType;
  final SnapiTypeWithProperties keyType;
  private final MaterializedFrame frame;
  private final int computeNextSlot;
  private final int shouldContinueSlot;
  private final int generatorSlot;
  private final int keyFunctionSlot;
  private final int mapSlot;

  public EquiJoinCollection(
      Object leftIterable,
      Object leftKeyF,
      SnapiTypeWithProperties leftRowType,
      Object rightIterable,
      Object rightKeyF,
      SnapiTypeWithProperties rightRowType,
      SnapiTypeWithProperties keyType,
      Object reshapeFun,
      MaterializedFrame frame,
      int computeNextSlot,
      int shouldContinueSlot,
      int generatorSlot,
      int keyFunctionSlot,
      int mapSlot) {
    this.leftIterable = leftIterable;
    this.leftKeyF = leftKeyF;
    this.leftRowType = leftRowType;
    this.rightIterable = rightIterable;
    this.rightKeyF = rightKeyF;
    this.rightRowType = rightRowType;
    this.keyType = keyType;
    this.reshapeFun = reshapeFun;
    this.frame = frame;
    this.computeNextSlot = computeNextSlot;
    this.shouldContinueSlot = shouldContinueSlot;
    this.generatorSlot = generatorSlot;
    this.keyFunctionSlot = keyFunctionSlot;
    this.mapSlot = mapSlot;
  }

  public Object getGenerator() {
    return new AbstractGenerator(
        new EquiJoinComputeNext(
            leftIterable,
            leftKeyF,
            leftRowType,
            rightIterable,
            rightKeyF,
            rightRowType,
            keyType,
            reshapeFun,
            frame,
            computeNextSlot,
            shouldContinueSlot,
            generatorSlot,
            keyFunctionSlot,
            mapSlot));
  }

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
