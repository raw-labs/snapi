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

package raw.runtime.truffle.ast.expressions.iterable.list;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.runtime.aggregation.AggregationLibrary;
import raw.runtime.truffle.runtime.aggregation.SingleAggregation;
import raw.runtime.truffle.runtime.aggregation.aggregator.SumAggregator;
import raw.runtime.truffle.runtime.list.ListLibrary;

@NodeInfo(shortName = "List.Sum")
@NodeChild("list")
public abstract class ListSumNode extends ExpressionNode {
  @Specialization(limit = "3")
  protected Object doCollection(
      Object list,
      @Cached("create()") TryableNullableNodes.BoxOptionNode boxOption,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "1") AggregationLibrary aggregations) {
    Object iterable = lists.toIterable(list);
    Object aggregation = new SingleAggregation(new SumAggregator());
    Object result = aggregations.aggregate(aggregation, iterable);
    return boxOption.execute(result);
  }
}
