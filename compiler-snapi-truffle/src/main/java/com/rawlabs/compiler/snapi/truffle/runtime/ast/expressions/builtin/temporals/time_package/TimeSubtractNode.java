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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals.time_package;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.LocalTime;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals.interval_package.IntervalNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.IntervalObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.TimeObject;

@NodeInfo(shortName = "Time.Subtract")
@NodeChild("time1")
@NodeChild("time2")
public abstract class TimeSubtractNode extends ExpressionNode {
  @Specialization
  protected IntervalObject getYear(
      TimeObject time1,
      TimeObject time2,
      @Cached(inline = true) IntervalNodes.IntervalNormalizeNode normalizeNode) {
    LocalTime localTime1 = time1.getTime();
    LocalTime localTime2 = time2.getTime();

    return normalizeNode.execute(
        this,
        0,
        0,
        0,
        0,
        localTime1.getHour() - localTime2.getHour(),
        localTime1.getMinute() - localTime2.getMinute(),
        localTime1.getSecond() - localTime2.getSecond(),
        (localTime1.getNano() - localTime2.getNano()) / 1000000);
  }
}
