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
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.aggregation.*;
import raw.runtime.truffle.runtime.primitives.DecimalObject;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.runtime.truffle.runtime.record.RecordNodesFactory;

@NodeInfo(shortName = "Collection.TupleAvg")
public class CollectionTupleAvgNode extends ExpressionNode {
  @Child
  private RecordNodes.AddPropNode addPropNode = RecordNodesFactory.AddPropNodeGen.getUncached();

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

    Object[] results = (Object[]) aggregate.executeGeneric(virtualFrame);
    Object record = RawLanguage.get(this).createPureRecord();
    if ((long) results[1] == (long) zeroNode.execute(this, Aggregations.COUNT)) {
      addPropNode.execute(this, record, "sum", zeroNode.execute(this, Aggregations.SUM), false);
    } else {

      addPropNode.execute(
          this, record, "sum", new DecimalObject(new BigDecimal(results[0].toString())), false);
    }
    addPropNode.execute(this, record, "count", results[1], false);
    return record;
  }
}
