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

package raw.runtime.truffle.ast.expressions.iterable.collection;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.runtime.aggregation.AggregationLibrary;
import raw.runtime.truffle.runtime.aggregation.SingleAggregation;
import raw.runtime.truffle.runtime.aggregation.aggregator.MaxAggregator;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

@NodeInfo(shortName = "Collection.Max")
@NodeChild("iterable")
public abstract class CollectionMaxNode extends ExpressionNode {
  @Specialization
  protected ObjectTryable doCollection(
      Object iterable,
      @Cached("create()") TryableNullableNodes.BoxOptionNode boxOption,
      @CachedLibrary(limit = "1") AggregationLibrary aggregations) {
    try {
      Object aggregation = new SingleAggregation(new MaxAggregator());
      Object result = aggregations.aggregate(aggregation, iterable);
      return ObjectTryable.BuildSuccess(boxOption.execute(result));
    } catch (RawTruffleRuntimeException ex) {
      return ObjectTryable.BuildFailure(ex.getMessage());
    }
  }
}
