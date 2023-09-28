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
import raw.runtime.truffle.runtime.iterable.IterableLibrary;

@ExportLibrary(AggregationLibrary.class)
public class MultiAggregation {
  Object[] aggregators;

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
      @CachedLibrary("iterable") IterableLibrary iterables,
      @CachedLibrary(limit = "5") AggregatorLibrary aggregatorLibs,
      @CachedLibrary(limit = "1") GeneratorLibrary generators) {
    Object generator = iterables.getGenerator(iterable);
    try {
      generators.init(generator);
      Object[] results = new Object[aggregators.length];
      for (int i = 0; i < aggregators.length; i++) {
        results[i] = aggregatorLibs.zero(aggregators[i]);
      }
      if (!generators.hasNext(generator)) {
        return results;
      }
      while (generators.hasNext(generator)) {
        Object next = generators.next(generator);
        for (int i = 0; i < aggregators.length; i++) {
          results[i] = aggregatorLibs.merge(aggregators[i], results[i], next);
        }
      }
      return results;
    } catch (RawTruffleRuntimeException ex) {
      throw new RawTruffleRuntimeException(ex.getMessage());
    } finally {
      generators.close(generator);
    }
  }
}
