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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.temporals.date_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.primitives.DateObject;
import com.rawlabs.snapi.truffle.runtime.primitives.TimestampObject;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NodeInfo(shortName = "Date.FromTimestamp")
@NodeChild("timestamp")
public abstract class DateFromTimestampNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected DateObject fromEpochDay(TimestampObject timestamp) {
    LocalDateTime ts = timestamp.getTimestamp();
    return new DateObject(LocalDate.of(ts.getYear(), ts.getMonth(), ts.getDayOfMonth()));
  }
}
