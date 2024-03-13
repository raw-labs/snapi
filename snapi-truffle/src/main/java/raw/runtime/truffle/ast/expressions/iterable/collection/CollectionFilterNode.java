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

import static raw.runtime.truffle.ast.osr.AuxiliarySlots.*;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.iterable.operations.FilterCollection;

@NodeInfo(shortName = "Collection.Filter")
public class CollectionFilterNode extends ExpressionNode {

  @Child private ExpressionNode iterableNode;

  @Child private ExpressionNode predicateNode;

  @CompilerDirectives.CompilationFinal private int collectionSlot = -1;
  @CompilerDirectives.CompilationFinal private int functionSlot = -1;
  @CompilerDirectives.CompilationFinal private int resultSlot = -1;

  public CollectionFilterNode(ExpressionNode iterableNode, ExpressionNode predicateNode) {
    this.iterableNode = iterableNode;
    this.predicateNode = predicateNode;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object iterable = iterableNode.executeGeneric(frame);
    Object predicate = predicateNode.executeGeneric(frame);

    if (collectionSlot == -1) {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      collectionSlot = getCollectionSlot(frame);
      functionSlot = getFunctionSlot(frame);
      resultSlot = getResultSlot(frame);
    }

    return new FilterCollection(
        iterable, predicate, frame.materialize(), collectionSlot, functionSlot, resultSlot);
  }
}
