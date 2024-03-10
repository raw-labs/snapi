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

import com.oracle.truffle.api.dsl.Idempotent;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
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

  @Specialization
  protected Object doGroup(VirtualFrame frame, Object iterable, Object keyFun) {
    int generatorSlot =
        frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.GENERATOR_SLOT);
    int keyFunctionSlot =
        frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.FUNCTION_SLOT);
    int mapSlot = frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.MAP_SLOT);
    return new GroupByCollection(
        iterable,
        keyFun,
        getKeyType(),
        getRowType(),
        RawContext.get(this).getSourceContext(),
        frame.materialize(),
        generatorSlot,
        keyFunctionSlot,
        mapSlot);
  }
}
