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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.aggregation;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.osr.OSRGeneratorNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.osr.bodies.OSRMultiAggregationBodyNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.osr.conditions.OSRHasNextConditionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.RawTruffleRuntimeException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodesFactory;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable.IterableNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable.IterableNodesFactory;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.ErrorObject;

public class AggregateMultipleNode extends ExpressionNode {

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

  private final byte[] aggregationTypes;

  private final int generatorSlot;
  private final int resultSlot;

  public AggregateMultipleNode(
      ExpressionNode iterableNode, byte[] aggregationTypes, int generatorSlot, int resultSlot) {
    this.iterableNode = iterableNode;
    loop =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRHasNextConditionNode(generatorSlot),
                    new OSRMultiAggregationBodyNode(aggregationTypes, generatorSlot, resultSlot)));
    this.aggregationTypes = aggregationTypes;
    this.generatorSlot = generatorSlot;
    this.resultSlot = resultSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    Object generator = getGeneratorNode.execute(this, iterableNode.executeGeneric(virtualFrame));
    try {
      initNode.execute(this, generator);
      Object[] results = new Object[aggregationTypes.length];
      for (int i = 0; i < aggregationTypes.length; i++) {
        results[i] = zeroNode.execute(this, aggregationTypes[i]);
      }
      if (!hasNextNode.execute(this, generator)) {
        return results;
      }
      virtualFrame.setObject(generatorSlot, generator);
      virtualFrame.setObject(resultSlot, results);
      loop.execute(virtualFrame);
      return virtualFrame.getObject(resultSlot);
    } catch (RawTruffleRuntimeException e) {
      return new ErrorObject(e.getMessage());
    } finally {
      closeNode.execute(this, generator);
    }
  }
}
