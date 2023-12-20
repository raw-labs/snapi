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
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.aggregation.AggregationNodes;
import raw.runtime.truffle.runtime.aggregation.MultiAggregation;
import raw.runtime.truffle.runtime.aggregation.aggregator.AggregatorNodes;
import raw.runtime.truffle.runtime.aggregation.aggregator.Aggregators;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.primitives.DecimalObject;
import raw.runtime.truffle.runtime.record.RecordObject;

@NodeInfo(shortName = "Collection.TupleAvg")
@NodeChild("iterable")
public abstract class CollectionTupleAvgNode extends ExpressionNode {
  @Specialization
  protected Object doCollection(
      Object iterable,
      @Cached AggregationNodes.Aggregate aggregate,
      @Cached AggregatorNodes.Zero zero,
      @Cached AggregatorNodes.Merge merge,
      @CachedLibrary(limit = "1") InteropLibrary records) {
    try {
      byte[] aggregators = new byte[] {Aggregators.SUM, Aggregators.COUNT};
      Object aggregation = new MultiAggregation(aggregators);
      Object[] results = (Object[]) aggregate.execute(aggregation, iterable);
      RecordObject record = RawLanguage.get(this).createRecord();
      if ((long) results[1] == (long) zero.execute(Aggregators.COUNT)) {
        records.writeMember(record, "sum", zero.execute(Aggregators.SUM));
      } else {
        records.writeMember(
            record, "sum", new DecimalObject(new BigDecimal(results[0].toString())));
      }
      records.writeMember(record, "count", results[1]);
      return record;
    } catch (UnsupportedMessageException
        | UnknownIdentifierException
        | UnsupportedTypeException ex) {
      throw new RawTruffleInternalErrorException(ex);
    }
  }
}
