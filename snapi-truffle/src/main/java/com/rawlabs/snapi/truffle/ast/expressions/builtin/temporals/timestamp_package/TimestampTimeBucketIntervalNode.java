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

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.temporals.interval_package.IntervalNodes;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.primitives.IntervalObject;
import com.rawlabs.snapi.truffle.runtime.primitives.TimestampObject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@NodeInfo(shortName = "Timestamp.TimeBucketInterval")
@NodeChild("value")
@NodeChild("timestamp")
public abstract class TimestampTimeBucketIntervalNode extends ExpressionNode {
  @Specialization
  @TruffleBoundary
  protected TimestampObject fromUnixTimestamp(
      IntervalObject intervalObj,
      TimestampObject timestampObj,
      @Cached(inline = true) IntervalNodes.IntervalToMillisStaticNode toMillisNode) {

    LocalDateTime timestamp = timestampObj.getTimestamp();
    LocalDateTime result;

    long millis = toMillisNode.execute(this, intervalObj);

    if (millis == 0)
      throw new TruffleRuntimeException("interval cannot be empty in time_bucket", this);

    if (intervalObj.getWeeks() == 0
        && intervalObj.getDays() == 0
        && intervalObj.getHours() == 0
        && intervalObj.getMinutes() == 0
        && intervalObj.getSeconds() == 0
        && intervalObj.getMillis() == 0) {
      // the interval is only months and years we can perform the calculation on months, no
      // need for
      // approximation
      int months1 = 12 * timestamp.getYear() + (timestamp.getMonthValue() - 1);
      int months2 = 12 * intervalObj.getYears() + intervalObj.getMonths();
      int truncated = (months1 / months2) * months2;
      int year = truncated / 12;
      int month = truncated % 12 + 1;

      result = LocalDateTime.of(year, month, 1, 0, 0, 0);
    } else if (intervalObj.getYears() == 0 && intervalObj.getMonths() == 0) {
      // 01-01-1973 starts on a sunday, so we give an offset so that week calculations are
      // correct
      long offset = LocalDateTime.of(1973, 1, 1, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli();
      long millis1 = timestamp.toInstant(ZoneOffset.UTC).toEpochMilli();
      long truncated = ((millis1 + offset) / millis) * millis - offset;

      result = LocalDateTime.ofInstant(Instant.ofEpochMilli(truncated), ZoneId.of("UTC"));
    } else {
      throw new TruffleRuntimeException(
          "intervals in time_bucket cannot have years or months mixed with other fields.", this);
    }

    return new TimestampObject(result);
  }
}
