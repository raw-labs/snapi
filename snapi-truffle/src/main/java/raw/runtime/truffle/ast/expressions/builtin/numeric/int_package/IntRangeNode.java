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

package raw.runtime.truffle.ast.expressions.builtin.numeric.int_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.iterable_old.sources.IntRangeCollection;

@NodeInfo(shortName = "Int.Range")
@NodeChild(value = "start")
@NodeChild(value = "end")
@NodeChild(value = "step")
public abstract class IntRangeNode extends ExpressionNode {

  @Specialization
  protected Object doRange(int start, int end, int step) {
    if (step <= 0)
      throw new RawTruffleRuntimeException("range step has to be strictly positive", this);
    return new IntRangeCollection(start, end, step);
  }
}
