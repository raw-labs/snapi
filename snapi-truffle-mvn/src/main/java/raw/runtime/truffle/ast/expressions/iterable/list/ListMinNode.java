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
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.aggregation.AggregationNodes;
import raw.runtime.truffle.runtime.aggregation.SingleAggregation;
import raw.runtime.truffle.runtime.aggregation.aggregator.Aggregators;
import raw.runtime.truffle.runtime.list.ListNodes;

@NodeInfo(shortName = "List.Min")
@NodeChild("list")
public abstract class ListMinNode extends ExpressionNode {

  private final SingleAggregation aggregation = new SingleAggregation(Aggregators.MIN);

  @Specialization
  protected Object doCollection(
      Object list,
      @Cached(inline = true) AggregationNodes.Aggregate aggregate,
      @Cached(inline = true) ListNodes.ToIterableNode toIterableNode) {
    Object iterable = toIterableNode.execute(this, list);
    return aggregate.execute(this, aggregation, iterable);
  }
}
