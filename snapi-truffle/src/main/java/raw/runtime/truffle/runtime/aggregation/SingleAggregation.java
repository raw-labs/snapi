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

import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.aggregation.aggregator.AggregatorLibrary;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable_old.IterableLibrary;

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
      @CachedLibrary("iterable") IterableLibrary iterables,
      @CachedLibrary("this.aggregator") AggregatorLibrary aggregators,
      @CachedLibrary(limit = "1") GeneratorLibrary generators) {
    Object generator = iterables.getGenerator(iterable);
    try {
      generators.init(generator);
      if (!generators.hasNext(generator)) {
        return aggregators.zero(aggregator);
      }
      Object result = aggregators.zero(aggregator);
      while (generators.hasNext(generator)) {
        Object next = generators.next(generator);
        result = aggregators.merge(aggregator, result, next);
      }
      return result;
    } catch (RawTruffleRuntimeException ex) {
      throw new RawTruffleRuntimeException(ex.getMessage());
    } finally {
      generators.close(generator);
    }
  }
}
