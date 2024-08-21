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
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.aggregation.AggregatorNodes;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.aggregation.AggregatorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodesFactory;

public class OSRSingleAggregationBodyNode extends ExpressionNode {

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child AggregatorNodes.Merge mergeNode = AggregatorNodesFactory.MergeNodeGen.create();

  private final byte aggregationType;
  private final int resultSlot;
  private final int generatorSlot;

  public OSRSingleAggregationBodyNode(byte aggregationType, int generatorSlot, int resultSlot) {
    this.resultSlot = resultSlot;
    this.generatorSlot = generatorSlot;
    this.aggregationType = aggregationType;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object generator = frame.getObject(generatorSlot);
    Object next = nextNode.execute(this, generator);
    Object currentResult = frame.getObject(resultSlot);
    Object newResult = mergeNode.execute(this, aggregationType, currentResult, next);
    frame.setObject(resultSlot, newResult);
    return null;
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }

  @Override
  public String toString() {
    return "OSRSingleAggregationBodyNode-Type" + aggregationType;
  }
}
