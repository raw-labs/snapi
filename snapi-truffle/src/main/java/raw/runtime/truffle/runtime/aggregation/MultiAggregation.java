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
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.aggregation.aggregator.AggregatorLibrary;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;

@ExportLibrary(AggregationLibrary.class)
public final class MultiAggregation {
  final Object[] aggregators;

  public MultiAggregation(Object[] aggregators) {
    this.aggregators = aggregators;
  }

  @ExportMessage
  public boolean isAggregation() {
    return true;
  }

  @ExportMessage(limit = "3")
  public Object aggregate(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGenerator,
      @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @CachedLibrary(limit = "5") AggregatorLibrary aggregatorLibs) {
    Object generator = getGenerator.execute(iterable);
    try {
      generatorInitNode.execute(generator);
      Object[] results = new Object[aggregators.length];
      for (int i = 0; i < aggregators.length; i++) {
        results[i] = aggregatorLibs.zero(aggregators[i]);
      }
      if (!generatorHasNextNode.execute(generator)) {
        return results;
      }
      while (generatorHasNextNode.execute(generator)) {
        Object next = generatorNextNode.execute(generator);
        for (int i = 0; i < aggregators.length; i++) {
          results[i] = aggregatorLibs.merge(aggregators[i], results[i], next);
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
