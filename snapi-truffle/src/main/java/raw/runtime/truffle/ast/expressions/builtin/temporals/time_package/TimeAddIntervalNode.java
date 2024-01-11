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

package raw.runtime.truffle.ast.expressions.builtin.temporals.time_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.LocalTime;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimeObject;

@NodeInfo(shortName = "Date.AddInterval")
@NodeChild("time")
@NodeChild("interval")
public abstract class TimeAddIntervalNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected TimeObject addInterval(TimeObject timeObj, IntervalObject interval) {
    LocalTime time = timeObj.getTime();
    return new TimeObject(
        time.plusHours(interval.getHours())
            .plusMinutes(interval.getMinutes())
            .plusSeconds(interval.getSeconds())
            .plusNanos(1000000L * interval.getMillis()));
  }
}
