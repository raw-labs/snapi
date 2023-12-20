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

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.aggregation.aggregator.AggregatorNodes;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;

public class AggregationNodes {
  @NodeInfo(shortName = "Aggregation.Aggregate")
  @GenerateUncached
  public abstract static class Aggregate extends Node {

    public abstract Object execute(Object aggregation, Object iterable);

    @Specialization
    static Object aggregate(
        SingleAggregation aggregation,
        Object iterable,
        @Cached IterableNodes.GetGeneratorNode getGenerator,
        @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
        @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
        @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
        @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode,
        @Cached AggregatorNodes.Merge mergeNode,
        @Cached AggregatorNodes.Zero zeroNode) {
      Object generator = getGenerator.execute(iterable);
      try {
        generatorInitNode.execute(generator);
        if (!generatorHasNextNode.execute(generator)) {
          return zeroNode.execute(aggregation.getAggregationType());
        }
        Object result = zeroNode.execute(aggregation.getAggregationType());
        while (generatorHasNextNode.execute(generator)) {
          Object next = generatorNextNode.execute(generator);
          result = mergeNode.execute(aggregation.getAggregationType(), result, next);
        }
        return result;
      } catch (RawTruffleRuntimeException ex) {
        throw new RawTruffleRuntimeException(ex.getMessage());
      } finally {
        generatorCloseNode.execute(generator);
      }
    }

    @Specialization
    static Object aggregate(
        MultiAggregation aggregation,
        Object iterable,
        @Cached IterableNodes.GetGeneratorNode getGenerator,
        @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
        @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
        @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
        @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode,
        @Cached AggregatorNodes.Merge mergeNode,
        @Cached AggregatorNodes.Zero zeroNode) {
      Object generator = getGenerator.execute(iterable);
      try {
        generatorInitNode.execute(generator);
        Object[] results = new Object[aggregation.getAggregationTypes().length];
        for (int i = 0; i < aggregation.getAggregationTypes().length; i++) {
          results[i] = zeroNode.execute(aggregation.getAggregationTypes()[i]);
        }
        if (!generatorHasNextNode.execute(generator)) {
          return results;
        }
        while (generatorHasNextNode.execute(generator)) {
          Object next = generatorNextNode.execute(generator);
          for (int i = 0; i < aggregation.getAggregationTypes().length; i++) {
            results[i] = mergeNode.execute(aggregation.getAggregationTypes()[i], results[i], next);
          }
        }
        return results;
      } catch (RawTruffleRuntimeException ex) {
        throw new RawTruffleRuntimeException(ex.getMessage());
      } finally {
        generatorCloseNode.execute(generator);
      }
    }
  }
}
