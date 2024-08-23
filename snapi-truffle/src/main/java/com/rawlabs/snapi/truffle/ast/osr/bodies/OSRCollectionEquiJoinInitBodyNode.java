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
import com.rawlabs.snapi.truffle.runtime.function.FunctionExecuteNodes;
import com.rawlabs.snapi.truffle.runtime.function.FunctionExecuteNodesFactory;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodesFactory;

public class OSRCollectionEquiJoinInitBodyNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child
  FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  @Child
  OffHeapNodes.OffHeapGroupByPutNode putNode =
      OffHeapNodesFactory.OffHeapGroupByPutNodeGen.create();

  private final int generatorSlot;
  private final int keyFunctionSlot;
  private final int mapSlot;

  public OSRCollectionEquiJoinInitBodyNode(int generatorSlot, int keyFunctionSlot, int mapSlot) {
    this.generatorSlot = generatorSlot;
    this.keyFunctionSlot = keyFunctionSlot;
    this.mapSlot = mapSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object generator = frame.getObject(generatorSlot);
    Object keyFunc = frame.getObject(keyFunctionSlot);
    Object map = frame.getObject(mapSlot);
    Object item = nextNode.execute(this, generator);
    Object key = functionExecuteOneNode.execute(this, keyFunc, item);
    putNode.execute(this, map, key, item);
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
