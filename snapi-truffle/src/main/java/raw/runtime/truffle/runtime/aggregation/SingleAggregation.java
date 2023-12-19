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
public final class SingleAggregation {

  final Object aggregator;

  public SingleAggregation(Object aggregator) {
    this.aggregator = aggregator;
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
      @CachedLibrary("this.aggregator") AggregatorLibrary aggregators) {
    Object generator = getGenerator.execute(iterable);
    try {
      generatorInitNode.execute(generator);
      if (!generatorHasNextNode.execute(generator)) {
        return aggregators.zero(aggregator);
      }
      Object result = aggregators.zero(aggregator);
      while (generatorHasNextNode.execute(generator)) {
        Object next = generatorNextNode.execute(generator);
        result = aggregators.merge(aggregator, result, next);
      }
      return result;
    } catch (RawTruffleRuntimeException ex) {
      throw new RawTruffleRuntimeException(ex.getMessage());
    } finally {
      generatorCloseNode.execute(generator);
    }
  }
}
