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

package com.rawlabs.snapi.truffle.ast.osr.bodies;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodesFactory;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.OffHeapDistinct;

public class OSRDistinctGetGeneratorNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child
  OffHeapNodes.OffHeapGroupByPutNode putNode =
      OffHeapNodesFactory.OffHeapGroupByPutNodeGen.create();

  private final int generatorSlot;

  private final int offHeapDistinctSlot;

  public OSRDistinctGetGeneratorNode(int generatorSlot, int offHeapDistinctSlot) {
    this.generatorSlot = generatorSlot;
    this.offHeapDistinctSlot = offHeapDistinctSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object generator = frame.getObject(generatorSlot);
    OffHeapDistinct index = (OffHeapDistinct) frame.getObject(offHeapDistinctSlot);

    Object next = nextNode.execute(this, generator);
    putNode.execute(this, index, next, null);
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
