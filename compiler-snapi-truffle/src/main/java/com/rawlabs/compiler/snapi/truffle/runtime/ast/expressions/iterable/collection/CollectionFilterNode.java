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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.iterable.collection;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable.operations.FilterCollection;

@NodeInfo(shortName = "Collection.Filter")
public class CollectionFilterNode extends ExpressionNode {

  @Child private ExpressionNode iterableNode;

  @Child private ExpressionNode predicateNode;

  private final int collectionSlot;
  private final int functionSlot;
  private final int resultSlot;

  public CollectionFilterNode(
      ExpressionNode iterableNode,
      ExpressionNode predicateNode,
      int collectionSlot,
      int functionSlot,
      int resultSlot) {
    this.iterableNode = iterableNode;
    this.predicateNode = predicateNode;
    this.collectionSlot = collectionSlot;
    this.functionSlot = functionSlot;
    this.resultSlot = resultSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object iterable = iterableNode.executeGeneric(frame);
    Object predicate = predicateNode.executeGeneric(frame);

    return new FilterCollection(
        iterable, predicate, frame.materialize(), collectionSlot, functionSlot, resultSlot);
  }
}
