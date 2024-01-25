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

package raw.runtime.truffle.runtime.aggregation;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.aggregation.aggregator.AggregatorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;

public class AggregationNodes {
  @NodeInfo(shortName = "Aggregation.Aggregate")
  @GenerateUncached
  @GenerateInline
  public abstract static class Aggregate extends Node {

    public abstract Object execute(Node node, Object aggregation, Object iterable);

    @Specialization
    static Object aggregate(
        Node node,
        SingleAggregation aggregation,
        Object iterable,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGenerator,
        @Cached @Cached.Shared("init") GeneratorNodes.GeneratorInitNode generatorInitNode,
        @Cached @Cached.Shared("hasNext") GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
        @Cached @Cached.Shared("next") GeneratorNodes.GeneratorNextNode generatorNextNode,
        @Cached @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode generatorCloseNode,
        @Cached @Cached.Shared("merge") AggregatorNodes.Merge mergeNode,
        @Cached @Cached.Shared("zero") AggregatorNodes.Zero zeroNode) {
      Object generator = getGenerator.execute(thisNode, iterable);
      try {
        generatorInitNode.execute(thisNode, generator);
        if (!generatorHasNextNode.execute(thisNode, generator)) {
          return zeroNode.execute(thisNode, aggregation.getAggregationType());
        }
        Object result = zeroNode.execute(thisNode, aggregation.getAggregationType());
        while (generatorHasNextNode.execute(thisNode, generator)) {
          Object next = generatorNextNode.execute(thisNode, generator);
          result = mergeNode.execute(thisNode, aggregation.getAggregationType(), result, next);
        }
        return result;
      } finally {
        generatorCloseNode.execute(thisNode, generator);
      }
    }

    @Specialization
    static Object aggregate(
        Node node,
        MultiAggregation aggregation,
        Object iterable,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGenerator,
        @Cached @Cached.Shared("init") GeneratorNodes.GeneratorInitNode generatorInitNode,
        @Cached @Cached.Shared("hasNext") GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
        @Cached @Cached.Shared("next") GeneratorNodes.GeneratorNextNode generatorNextNode,
        @Cached @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode generatorCloseNode,
        @Cached @Cached.Shared("merge") AggregatorNodes.Merge mergeNode,
        @Cached @Cached.Shared("zero") AggregatorNodes.Zero zeroNode) {
      Object generator = getGenerator.execute(thisNode, iterable);
      try {
        generatorInitNode.execute(thisNode, generator);
        Object[] results = new Object[aggregation.getAggregationTypes().length];
        for (int i = 0; i < aggregation.getAggregationTypes().length; i++) {
          results[i] = zeroNode.execute(thisNode, aggregation.getAggregationTypes()[i]);
        }
        if (!generatorHasNextNode.execute(thisNode, generator)) {
          return results;
        }
        while (generatorHasNextNode.execute(thisNode, generator)) {
          Object next = generatorNextNode.execute(thisNode, generator);
          for (int i = 0; i < aggregation.getAggregationTypes().length; i++) {
            results[i] =
                mergeNode.execute(thisNode, aggregation.getAggregationTypes()[i], results[i], next);
          }
        }
        return results;
      } finally {
        generatorCloseNode.execute(thisNode, generator);
      }
    }
  }
}
