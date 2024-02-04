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

package raw.runtime.truffle.ast.expressions.aggregation;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodesFactory;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

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

  public AggregateMultipleNode(ExpressionNode iterableNode, byte[] aggregationTypes) {
    this.iterableNode = iterableNode;
    loop = Truffle.getRuntime().createLoopNode(new OSRMultiAggregationNode());
    this.aggregationTypes = aggregationTypes;
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
      OSRMultiAggregationNode OSRNode = (OSRMultiAggregationNode) loop.getRepeatingNode();
      OSRNode.init(generator, aggregationTypes, results);
      return loop.execute(virtualFrame);
    } catch (RawTruffleRuntimeException e) {
      return new ErrorObject(e.getMessage());
    } finally {
      closeNode.execute(this, generator);
    }
  }
}
