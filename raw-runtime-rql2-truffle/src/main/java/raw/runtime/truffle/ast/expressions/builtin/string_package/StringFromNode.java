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

package raw.runtime.truffle.ast.expressions.builtin.string_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.*;

import java.math.BigDecimal;

import static raw.runtime.truffle.runtime.primitives.TruffleTemporalFormatter.*;

@NodeInfo(shortName = "String.From")
@NodeChild(value = "value")
public abstract class StringFromNode extends ExpressionNode {

  @Specialization
  protected String fromByte(byte value) {
    return String.valueOf(value);
  }

  @Specialization
  protected String fromShort(short value) {
    return String.valueOf(value);
  }

  @Specialization
  protected String fromInt(int value) {
    return String.valueOf(value);
  }

  @Specialization
  protected String fromLong(long value) {
    return String.valueOf(value);
  }

  @Specialization
  protected String fromFloat(float value) {
    return String.valueOf(value);
  }

  @Specialization
  protected String fromDouble(double value) {
    return String.valueOf(value);
  }

  @Specialization
  protected String fromDecimal(BigDecimal value) {
    return value.toString();
  }

  @Specialization
  protected String fromBoolean(boolean value) {
    return String.valueOf(value);
  }

  @Specialization
  protected String fromDate(DateObject value) {
    return value.getDate().format(DATE_FORMATTER);
  }

  @Specialization
  protected String fromTime(TimeObject value) {
    return value.getTime().format(TIME_FORMATTER);
  }

  @Specialization
  protected String fromTimestamp(TimestampObject value) {
    return value.getTimestamp().format(TIMESTAMP_FORMATTER);
  }

  @Specialization
  protected String fromInterval(IntervalObject value) {
    return value.toString();
  }
}
