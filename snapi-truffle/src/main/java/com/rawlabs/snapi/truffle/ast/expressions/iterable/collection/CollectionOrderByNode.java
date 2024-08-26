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

package com.rawlabs.snapi.truffle.ast.expressions.iterable.collection;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.rawlabs.snapi.frontend.snapi.source.SnapiTypeWithProperties;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.iterable.operations.OrderByCollection;

@NodeInfo(shortName = "Collection.OrderBy")
public class CollectionOrderByNode extends ExpressionNode {
  @Child private ExpressionNode input;
  @Children private final ExpressionNode[] keyFuns;
  @Children private final ExpressionNode[] orderings;
  private final SnapiTypeWithProperties[] keyTypes;
  private final SnapiTypeWithProperties valueType;
  private final int generatorSlot;
  private final int collectionSlot;
  private final int offHeapGroupByKeysSlot;

  public CollectionOrderByNode(
      ExpressionNode input,
      ExpressionNode[] keyFuns,
      ExpressionNode[] orderings,
      SnapiTypeWithProperties[] keyTypes,
      SnapiTypeWithProperties valueType,
      int generatorSlot,
      int collectionSlot,
      int offHeapGroupByKeysSlot) {
    this.input = input;
    this.keyFuns = keyFuns;
    this.orderings = orderings;
    this.keyTypes = keyTypes;
    this.valueType = valueType;
    this.generatorSlot = generatorSlot;
    this.collectionSlot = collectionSlot;
    this.offHeapGroupByKeysSlot = offHeapGroupByKeysSlot;
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    Object iterable = input.executeGeneric(frame);
    int[] orders = new int[orderings.length];
    for (int i = 0; i < orderings.length; i++) {
      try {
        String order = orderings[i].executeString(frame);
        if (order.equalsIgnoreCase("ASC")) orders[i] = 1;
        else if (order.equalsIgnoreCase("DESC")) orders[i] = -1;
        else throw new TruffleRuntimeException("Invalid ordering: " + order);
      } catch (UnexpectedResultException e) {
        throw new TruffleRuntimeException(e.getMessage(), e, this);
      }
    }
    Object[] keyFunctions = new Object[this.keyFuns.length];
    for (int i = 0; i < this.keyFuns.length; i++) {
      keyFunctions[i] = this.keyFuns[i].executeGeneric(frame);
    }

    return new OrderByCollection(
        iterable,
        keyFunctions,
        orders,
        keyTypes,
        valueType,
        frame.materialize(),
        generatorSlot,
        collectionSlot,
        offHeapGroupByKeysSlot);
  }
}
