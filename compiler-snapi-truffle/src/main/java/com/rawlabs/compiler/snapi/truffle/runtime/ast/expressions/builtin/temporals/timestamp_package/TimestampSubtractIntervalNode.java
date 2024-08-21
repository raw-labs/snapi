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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals.timestamp_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.IntervalObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.TimestampObject;
import java.time.LocalDateTime;

@NodeInfo(shortName = "Timestamp.Subtract")
@NodeChild("timestamp")
@NodeChild("interval")
public abstract class TimestampSubtractIntervalNode extends ExpressionNode {
  @Specialization
  @TruffleBoundary
  protected TimestampObject subtractInterval(
      TimestampObject timestampObj, IntervalObject interval) {
    LocalDateTime timestamp = timestampObj.getTimestamp();

    return new TimestampObject(
        timestamp
            .minusYears(interval.getYears())
            .minusMonths(interval.getMonths())
            .minusWeeks(interval.getWeeks())
            .minusDays(interval.getDays())
            .minusHours(interval.getHours())
            .minusMinutes(interval.getMinutes())
            .minusSeconds(interval.getSeconds())
            .minusNanos(1000000L * interval.getMillis()));
  }
}
