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

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.temporals.interval_package.IntervalNodes;
import com.rawlabs.snapi.truffle.runtime.primitives.DateObject;
import com.rawlabs.snapi.truffle.runtime.primitives.IntervalObject;
import java.time.LocalDate;

@NodeInfo(shortName = "Date.Subtract")
@NodeChild("date1")
@NodeChild("date2")
public abstract class DateSubtractNode extends ExpressionNode {
  @Specialization
  protected IntervalObject dateSubtract(
      DateObject date1,
      DateObject date2,
      @Cached(inline = true) IntervalNodes.IntervalNormalizeNode normalizeNode) {
    LocalDate localDate1 = date1.getDate();
    LocalDate localDate2 = date2.getDate();

    return normalizeNode.execute(
        this,
        localDate1.getYear() - localDate2.getYear(),
        localDate1.getMonthValue() - localDate2.getMonthValue(),
        0,
        localDate1.getDayOfMonth() - localDate2.getDayOfMonth(),
        0,
        0,
        0,
        0);
  }
}
