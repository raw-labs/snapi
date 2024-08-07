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

package raw.runtime.truffle.ast.expressions.builtin.temporals.interval_package;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.IntervalObject;

@NodeInfo(shortName = "Interval.ToMillis")
@NodeChild("interval")
public abstract class IntervalToMillisNode extends ExpressionNode {
  @Specialization
  protected long getMillis(
      IntervalObject interval,
      @Cached(inline = true) IntervalNodes.IntervalToMillisStaticNode toMillisNode) {
    return toMillisNode.execute(this, interval);
  }
}
