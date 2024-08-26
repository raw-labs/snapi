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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.temporals.time_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.primitives.IntervalObject;
import com.rawlabs.snapi.truffle.runtime.primitives.TimeObject;
import java.time.LocalTime;

@NodeInfo(shortName = "Date.AddInterval")
@NodeChild("time")
@NodeChild("interval")
public abstract class TimeSubtractIntervalNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected TimeObject addInterval(TimeObject timeObj, IntervalObject interval) {
    LocalTime time = timeObj.getTime();
    return new TimeObject(
        time.minusHours(interval.getHours())
            .minusMinutes(interval.getMinutes())
            .minusSeconds(interval.getSeconds())
            .minusNanos(1000000L * interval.getMillis()));
  }
}
