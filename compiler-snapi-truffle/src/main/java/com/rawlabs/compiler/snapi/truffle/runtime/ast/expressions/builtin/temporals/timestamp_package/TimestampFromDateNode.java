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
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.DateObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.TimestampObject;

@NodeInfo(shortName = "Timestamp.FromDate")
@NodeChild("date")
public abstract class TimestampFromDateNode extends ExpressionNode {
  @Specialization
  @TruffleBoundary
  protected TimestampObject from(DateObject dateObj) {
    LocalDate date = dateObj.getDate();
    return new TimestampObject(
        LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0));
  }
}
