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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.iterable.collection;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.aggregation.*;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.DecimalObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.record.RecordNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.record.RecordNodesFactory;

@NodeInfo(shortName = "Collection.TupleAvg")
public class CollectionTupleAvgNode extends ExpressionNode {
  @Child private RecordNodes.AddPropNode addPropNode = RecordNodesFactory.AddPropNodeGen.create();

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
