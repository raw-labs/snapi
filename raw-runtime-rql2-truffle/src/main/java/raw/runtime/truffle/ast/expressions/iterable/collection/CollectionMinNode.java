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

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.aggregation.AggregationLibrary;
import raw.runtime.truffle.runtime.aggregation.SingleAggregation;
import raw.runtime.truffle.runtime.aggregation.aggregator.MinAggregator;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.nullable_tryable.NullableTryableLibrary;
import raw.runtime.truffle.runtime.nullable_tryable.RuntimeNullableTryableHandler;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

@NodeInfo(shortName = "Collection.Min")
@NodeChild("iterable")
public abstract class CollectionMinNode extends ExpressionNode {

    @Specialization
    protected ObjectTryable doCollection(Object iterable,
                                         @CachedLibrary(limit = "1") AggregationLibrary aggregations,
                                         @CachedLibrary(limit = "1") NullableTryableLibrary nullableTryables) {
        try {
            Object aggregation = new SingleAggregation(new MinAggregator());
            Object result = aggregations.aggregate(aggregation, iterable);
            RuntimeNullableTryableHandler handler = new RuntimeNullableTryableHandler();
            return ObjectTryable.BuildSuccess(nullableTryables.boxOption(handler, result));
        } catch (RawTruffleRuntimeException ex) {
            return ObjectTryable.BuildFailure(ex.getMessage());
        }
    }
}
