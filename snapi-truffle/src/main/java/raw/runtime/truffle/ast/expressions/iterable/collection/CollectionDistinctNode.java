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
import raw.runtime.truffle.runtime.iterable.operations.DistinctCollection;

@NodeInfo(shortName = "Collection.Distinct")
@NodeChild("input")
@NodeField(name = "valueType", type = Rql2TypeWithProperties.class)
public abstract class CollectionDistinctNode extends ExpressionNode {

  @Idempotent
  protected abstract Rql2TypeWithProperties getValueType();

  protected int getComputeNextSlot(VirtualFrame frame) {
    return AuxiliarySlots.getComputeNextSlot(frame);
  }

  protected int getShouldContinueSlot(VirtualFrame frame) {
    return AuxiliarySlots.getShouldContinueSlot(frame);
  }

  @Specialization
  protected Object doDistinct(
      VirtualFrame frame,
      Object iterable,
      @Cached(value = "getComputeNextSlot(frame)", neverDefault = false) int generatorSlot,
      @Cached(value = "getShouldContinueSlot(frame)", neverDefault = true)
          int offHeapDistinctSlot) {
    return new DistinctCollection(
        iterable,
        getValueType(),
        RawContext.get(this).getSourceContext(),
        frame.materialize(),
        generatorSlot,
        offHeapDistinctSlot);
  }
}
