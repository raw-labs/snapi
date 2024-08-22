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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.timestamp_package;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.interval_package.IntervalNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.IntervalObject;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.TimestampObject;
import java.time.LocalDateTime;

@NodeInfo(shortName = "Timestamp.Subtract")
@NodeChild("timestamp1")
@NodeChild("timestamp2")
public abstract class TimestampSubtractNode extends ExpressionNode {
  @Specialization
  protected IntervalObject subtract(
      TimestampObject timestampObj1,
      TimestampObject timestampObj2,
      @Cached(inline = false) IntervalNodes.IntervalNormalizeNode normalizeNode) {
    LocalDateTime timestamp1 = timestampObj1.getTimestamp();
    LocalDateTime timestamp2 = timestampObj2.getTimestamp();

    return normalizeNode.execute(
        this,
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
