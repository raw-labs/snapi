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

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.ErrorObject;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.TimestampObject;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@NodeInfo(shortName = "Timestamp.Build")
@NodeChild("y")
@NodeChild("m")
@NodeChild("d")
@NodeChild("h")
@NodeChild("mi")
@NodeChild("s")
@NodeChild("ms")
public abstract class TimestampBuildNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  public Object buildTimestamp(int y, int m, int d, int h, int mi, int s, int ms) {
    try {
      return new TimestampObject(
          LocalDateTime.of(y, m, d, h, mi, s, (int) TimeUnit.MILLISECONDS.toNanos(ms)));
    } catch (DateTimeException e) {
      return new ErrorObject(e.getMessage());
    }
  }
}
