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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.osr.AuxiliarySlots;
import raw.runtime.truffle.runtime.iterable.operations.FilterCollection;

@NodeInfo(shortName = "Collection.Filter")
public class CollectionFilterNode extends ExpressionNode {

  @Child private ExpressionNode iterableNode;

  @Child private ExpressionNode predicateNode;

  public CollectionFilterNode(ExpressionNode iterableNode, ExpressionNode predicateNode) {
    this.iterableNode = iterableNode;
    this.predicateNode = predicateNode;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object iterable = iterableNode.executeGeneric(frame);
    Object predicate = predicateNode.executeGeneric(frame);

    int collectionSLot =
        frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.COLLECTION_SLOT);
    int functionSlot =
        frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.FUNCTION_SLOT);
    int resultSlot = frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.RESULT_SLOT);

    return new FilterCollection(
        iterable, predicate, frame.materialize(), collectionSLot, functionSlot, resultSlot);
  }
}
