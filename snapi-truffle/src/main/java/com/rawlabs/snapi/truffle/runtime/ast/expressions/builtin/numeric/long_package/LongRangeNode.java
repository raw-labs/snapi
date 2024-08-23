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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.numeric.long_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.runtime.iterable.sources.LongRangeCollection;

@NodeInfo(shortName = "Int.Range")
@NodeChild(value = "start")
@NodeChild(value = "end")
@NodeChild(value = "step")
public abstract class LongRangeNode extends ExpressionNode {
  @Specialization
  protected Object doRange(long start, long end, long step) {
    if (step <= 0)
      throw new TruffleRuntimeException("range step has to be strictly positive", this);
    return new LongRangeCollection(start, end, step);
  }
}
