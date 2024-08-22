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

package com.rawlabs.snapi.truffle.runtime.ast.osr.bodies;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.aggregation.AggregatorNodes;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.aggregation.AggregatorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodesFactory;

public class OSRMultiAggregationBodyNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child AggregatorNodes.Merge mergeNode = AggregatorNodesFactory.MergeNodeGen.create();
  private final byte[] aggregationTypes;
  private final int resultSlot;
  private final int generatorSlot;
  private final int aggregationLength;

  public OSRMultiAggregationBodyNode(byte[] aggregationTypes, int generatorSlot, int resultSlot) {
    this.resultSlot = resultSlot;
    this.generatorSlot = generatorSlot;
    this.aggregationTypes = aggregationTypes;
    this.aggregationLength = aggregationTypes.length;
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    Object generator = frame.getObject(generatorSlot);
    Object next = nextNode.execute(this, generator);
    Object[] currentResults = (Object[]) frame.getObject(resultSlot);
    for (int i = 0; i < aggregationLength; i++) {
      currentResults[i] = mergeNode.execute(this, aggregationTypes[i], currentResults[i], next);
    }
    frame.setObject(resultSlot, currentResults);
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }
}
