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

package com.rawlabs.snapi.truffle.ast.expressions.aggregation;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.osr.OSRGeneratorNode;
import com.rawlabs.snapi.truffle.ast.osr.bodies.OSRSingleAggregationBodyNode;
import com.rawlabs.snapi.truffle.ast.osr.conditions.OSRHasNextConditionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodesFactory;
import com.rawlabs.snapi.truffle.runtime.primitives.ErrorObject;

public class AggregateSingleNode extends ExpressionNode {

  @Child ExpressionNode iterableNode;

  @Child private LoopNode loop;

  @Child
  GeneratorNodes.GeneratorInitNode initNode = GeneratorNodesFactory.GeneratorInitNodeGen.create();

  @Child
  GeneratorNodes.GeneratorCloseNode closeNode =
      GeneratorNodesFactory.GeneratorCloseNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  IterableNodes.GetGeneratorNode getGeneratorNode =
      IterableNodesFactory.GetGeneratorNodeGen.create();

  @Child AggregatorNodes.Zero zeroNode = AggregatorNodesFactory.ZeroNodeGen.create();

  private final byte aggregationType;

  private final int generatorSlot;
  private final int resultSlot;

  public AggregateSingleNode(
      ExpressionNode iterableNode, byte aggregationType, int generatorSlot, int resultSlot) {
    this.iterableNode = iterableNode;
    loop =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRHasNextConditionNode(generatorSlot),
                    new OSRSingleAggregationBodyNode(aggregationType, generatorSlot, resultSlot)));
    this.aggregationType = aggregationType;
    this.generatorSlot = generatorSlot;
    this.resultSlot = resultSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    Object generator = getGeneratorNode.execute(this, iterableNode.executeGeneric(virtualFrame));
    try {
      initNode.execute(this, generator);
      if (!hasNextNode.execute(this, generator)) {
        return zeroNode.execute(this, aggregationType);
      }
      Object result = zeroNode.execute(this, aggregationType);

      virtualFrame.setObject(generatorSlot, generator);
      virtualFrame.setObject(resultSlot, result);
      loop.execute(virtualFrame);
      return virtualFrame.getObject(resultSlot);
    } catch (TruffleRuntimeException e) {
      return new ErrorObject(e.getMessage());
    } finally {
      closeNode.execute(this, generator);
    }
  }
}
