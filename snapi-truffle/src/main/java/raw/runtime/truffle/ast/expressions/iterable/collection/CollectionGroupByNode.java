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

package raw.runtime.truffle.ast.expressions.iterable.collection;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.ast.osr.AuxiliarySlots;
import raw.runtime.truffle.runtime.iterable.operations.GroupByCollection;

@NodeInfo(shortName = "Collection.GroupBy")
@NodeChild("iterable")
@NodeChild("keyFun")
@NodeField(name = "keyType", type = Rql2TypeWithProperties.class)
@NodeField(name = "rowType", type = Rql2TypeWithProperties.class)
public abstract class CollectionGroupByNode extends ExpressionNode {

  @Idempotent
  protected abstract Rql2TypeWithProperties getKeyType();

  @Idempotent
  protected abstract Rql2TypeWithProperties getRowType();

  protected int getGeneratorSlot(VirtualFrame frame) {
    return AuxiliarySlots.getGeneratorSlot(frame.getFrameDescriptor());
  }

  protected int getFunctionSlot(VirtualFrame frame) {
    return AuxiliarySlots.getFunctionSlot(frame.getFrameDescriptor());
  }

  protected int getMapSlot(VirtualFrame frame) {
    return AuxiliarySlots.getMapSlot(frame.getFrameDescriptor());
  }

  protected int getOffHeapFlushSlot(VirtualFrame frame) {
    return AuxiliarySlots.getOffHeapFlushSlot(frame.getFrameDescriptor());
  }

  protected int getKryoOutputSlot(VirtualFrame frame) {
    return AuxiliarySlots.getKryoOutputSlot(frame.getFrameDescriptor());
  }

  protected int getIteratorSlot(VirtualFrame frame) {
    return AuxiliarySlots.getIteratorSlot(frame.getFrameDescriptor());
  }

  @Specialization
  protected Object doGroup(
      VirtualFrame frame,
      Object iterable,
      Object keyFun,
      @Cached(value = "getGeneratorSlot(frame)", neverDefault = false) int generatorSlot,
      @Cached(value = "getFunctionSlot(frame)", neverDefault = true) int keyFunctionSlot,
      @Cached(value = "getMapSlot(frame)", neverDefault = true) int mapSlot,
      @Cached(value = "getOffHeapFlushSlot(frame)", neverDefault = true) int offHeapFlushSlot,
      @Cached(value = "getKryoOutputSlot(frame)", neverDefault = true) int kryoOutputSlot,
      @Cached(value = "getIteratorSlot(frame)", neverDefault = true) int iteratorSlot) {
    return new GroupByCollection(
        iterable,
        keyFun,
        getKeyType(),
        getRowType(),
        RawContext.get(this).getSourceContext(),
        frame.materialize(),
        generatorSlot,
        keyFunctionSlot,
        mapSlot,
        kryoOutputSlot,
        iteratorSlot,
        offHeapFlushSlot);
  }
}
