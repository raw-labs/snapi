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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.temporals.timestamp_package;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.temporals.interval_package.IntervalNodes;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.iterable.sources.TimestampRangeCollection;
import com.rawlabs.snapi.truffle.runtime.primitives.IntervalObject;
import com.rawlabs.snapi.truffle.runtime.primitives.TimestampObject;

@NodeInfo(shortName = "Timestamp.Range")
@NodeChild("start")
@NodeChild("end")
@NodeChild("step")
public abstract class TimestampRangeNode extends ExpressionNode {

  private static final IntervalObject zero = new IntervalObject(0, 0, 0, 0, 0, 0, 0, 0);

  @Specialization
  protected Object doRange(
      TimestampObject start,
      TimestampObject end,
      IntervalObject step,
      @Cached(inline = true) IntervalNodes.IntervalCompareNode compareNode) {
    if (compareNode.execute(this, step, zero) <= 0)
      throw new TruffleRuntimeException("range step has to be strictly positive", this);
    return new TimestampRangeCollection(start, end, step);
  }
}
