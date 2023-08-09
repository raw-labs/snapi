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

package raw.runtime.truffle.ast.expressions.builtin.temporals.date_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.IntervalObject;

import java.time.LocalDate;

@NodeInfo(shortName = "Date.Subtract")
@NodeChild("date1")
@NodeChild("date2")
public abstract class DateSubtractNode extends ExpressionNode {
  @Specialization
  protected IntervalObject getYear(DateObject date1, DateObject date2) {
    LocalDate localDate1 = date1.getDate();
    LocalDate localDate2 = date2.getDate();

    return IntervalObject.normalize(
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
