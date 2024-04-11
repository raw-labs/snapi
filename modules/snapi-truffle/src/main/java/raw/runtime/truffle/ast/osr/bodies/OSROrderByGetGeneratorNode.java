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

package raw.runtime.truffle.ast.osr.bodies;

import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OffHeapGroupByKeys;
import raw.runtime.truffle.runtime.iterable.operations.OrderByCollection;

public class OSROrderByGetGeneratorNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child
  OffHeapNodes.OffHeapGroupByPutNode putNode =
      OffHeapNodesFactory.OffHeapGroupByPutNodeGen.create();

  @Child
  private FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  private final int generatorSlot;
  private final int collectionSlot;
  private final int offHeapGroupByKeysSlot;

  public OSROrderByGetGeneratorNode(
      int generatorSlot, int collectionSlot, int offHeapGroupByKeysSlot) {
    this.generatorSlot = generatorSlot;
    this.collectionSlot = collectionSlot;
    this.offHeapGroupByKeysSlot = offHeapGroupByKeysSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object generator = frame.getObject(generatorSlot);

    OrderByCollection collection = (OrderByCollection) frame.getObject(collectionSlot);

    OffHeapGroupByKeys groupByKeys = (OffHeapGroupByKeys) frame.getObject(offHeapGroupByKeysSlot);

    int funLen = collection.getKeyFunctions().length;

    Object v = nextNode.execute(this, generator);
    Object[] key = new Object[funLen];
    for (int i = 0; i < funLen; i++) {
      key[i] = functionExecuteOneNode.execute(this, collection.getKeyFunctions()[i], v);
    }
    putNode.execute(this, groupByKeys, key, v);
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
