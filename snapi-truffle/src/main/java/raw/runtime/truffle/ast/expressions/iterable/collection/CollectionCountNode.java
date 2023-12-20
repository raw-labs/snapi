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
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.aggregation.AggregationNodes;
import raw.runtime.truffle.runtime.aggregation.SingleAggregation;
import raw.runtime.truffle.runtime.aggregation.aggregator.Aggregators;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

// A.Z. Need to cache count somehow
@NodeInfo(shortName = "Collection.Count")
@NodeChild("parent")
public abstract class CollectionCountNode extends ExpressionNode {

  @Specialization
  protected Object doCount(Object iterable, @Cached AggregationNodes.Aggregate aggregate) {
    try {
      Object aggregation = new SingleAggregation(Aggregators.COUNT);
      return aggregate.execute(aggregation, iterable);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    }
  }
}
