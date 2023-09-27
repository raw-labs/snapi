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

package raw.runtime.truffle.ast.expressions.builtin.temporals.timestamp_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.LocalDateTime;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

@NodeInfo(shortName = "Timestamp.Subtract")
@NodeChild("timestamp1")
@NodeChild("timestamp2")
public abstract class TimestampSubtractNode extends ExpressionNode {
  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected IntervalObject subtract(TimestampObject timestampObj1, TimestampObject timestampObj2) {
    LocalDateTime timestamp1 = timestampObj1.getTimestamp();
    LocalDateTime timestamp2 = timestampObj2.getTimestamp();

    return IntervalObject.normalize(
        timestamp1.getYear() - timestamp2.getYear(),
        timestamp1.getMonthValue() - timestamp2.getMonthValue(),
        0,
        timestamp1.getDayOfMonth() - timestamp2.getDayOfMonth(),
        timestamp1.getHour() - timestamp2.getHour(),
        timestamp1.getMinute() - timestamp2.getMinute(),
        timestamp1.getSecond() - timestamp2.getSecond(),
        (timestamp1.getNano() - timestamp2.getNano()) / 1000000);
  }
}
