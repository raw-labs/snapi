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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.aggregation.*;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.primitives.DecimalObject;
import raw.runtime.truffle.runtime.record.RecordObject;

@NodeInfo(shortName = "Collection.TupleAvg")
public class CollectionTupleAvgNode extends ExpressionNode {
  @Child InteropLibrary records = InteropLibrary.getFactory().createDispatched(2);
  @Child AggregateMultipleNode aggregate;
  @Child AggregatorNodes.Zero zeroNode = AggregatorNodesFactory.ZeroNodeGen.create();

  public CollectionTupleAvgNode(ExpressionNode iterableNode, int generatorSlot, int resultSlot) {
    aggregate =
        new AggregateMultipleNode(
            iterableNode,
            new byte[] {Aggregations.SUM, Aggregations.COUNT},
            generatorSlot,
            resultSlot);
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    try {
      Object[] results = (Object[]) aggregate.executeGeneric(virtualFrame);
      RecordObject record = RawLanguage.get(this).createRecord();
      if ((long) results[1] == (long) zeroNode.execute(this, Aggregations.COUNT)) {
        records.writeMember(record, "sum", zeroNode.execute(this, Aggregations.SUM));
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
