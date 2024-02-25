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

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodesFactory;

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

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    FrameDescriptor frameDescriptor = frame.getFrameDescriptor();
    int generatorSlot =
        frameDescriptor.findOrAddAuxiliarySlot(
            raw.runtime.truffle.ast.osr.AuxiliarySlots.GENERATOR_SLOT);
    int keyFunctionSlot =
        frameDescriptor.findOrAddAuxiliarySlot(
            raw.runtime.truffle.ast.osr.AuxiliarySlots.FUNCTION_SLOT);
    int mapSlot =
        frameDescriptor.findOrAddAuxiliarySlot(raw.runtime.truffle.ast.osr.AuxiliarySlots.MAP_SLOT);

    Object generator = frame.getAuxiliarySlot(generatorSlot);
    Object keyFunc = frame.getAuxiliarySlot(keyFunctionSlot);
    Object map = frame.getAuxiliarySlot(mapSlot);
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
