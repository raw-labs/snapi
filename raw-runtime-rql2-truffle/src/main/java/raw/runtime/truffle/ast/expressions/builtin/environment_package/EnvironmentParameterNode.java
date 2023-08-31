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

package raw.runtime.truffle.ast.expressions.builtin.environment_package;

import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import java.time.Duration;
import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimeObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "Environment.Parameter")
@NodeChild(value = "key")
@NodeField(name = "paramType", type = Rql2Type.class)
public abstract class EnvironmentParameterNode extends ExpressionNode {

  protected abstract Rql2Type getParamType();

  @Specialization(guards = {"isByteKind(getParamType())"})
  protected byte getByte(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamByte p = (ParamByte) context.params(key).get();
    return p.v();
  }

  @Specialization(guards = {"isShortKind(getParamType())"})
  protected short getShort(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamShort p = (ParamShort) context.params(key).get();
    return p.v();
  }

  @Specialization(guards = {"isIntKind(getParamType())"})
  protected int getInt(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamInt p = (ParamInt) context.params(key).get();
    return p.v();
  }

  @Specialization(guards = {"isLongKind(getParamType())"})
  protected long getLong(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamLong p = (ParamLong) context.params(key).get();
    return p.v();
  }

  @Specialization(guards = {"isFloatKind(getParamType())"})
  protected float getFloat(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamFloat p = (ParamFloat) context.params(key).get();
    return p.v();
  }

  @Specialization(guards = {"isDoubleKind(getParamType())"})
  protected Double getDouble(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamDouble p = (ParamDouble) context.params(key).get();
    return p.v();
  }

  @Specialization(guards = {"isDecimalKind(getParamType())"})
  protected BigDecimal getDecimal(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamDecimal p = (ParamDecimal) context.params(key).get();
    return p.v();
  }

  @Specialization(guards = {"isBooleanKind(getParamType())"})
  protected boolean getBool(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamBool p = (ParamBool) context.params(key).get();
    return p.v();
  }

  @Specialization(guards = {"isStringKind(getParamType())"})
  protected String getString(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamString p = (ParamString) context.params(key).get();
    return p.v();
  }

  @Specialization(guards = {"isDateKind(getParamType())"})
  protected DateObject getDate(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamDate p = (ParamDate) context.params(key).get();
    return new DateObject(p.v());
  }

  @Specialization(guards = {"isTimeKind(getParamType())"})
  protected TimeObject getTime(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamTime p = (ParamTime) context.params(key).get();
    return new TimeObject(p.v());
  }

  @Specialization(guards = {"isTimestampKind(getParamType())"})
  protected TimestampObject getTimestamp(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamTimestamp p = (ParamTimestamp) context.params(key).get();
    return new TimestampObject(p.v());
  }

  @Specialization(guards = {"isIntervalKind(getParamType())"})
  protected IntervalObject getInterval(String key) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    ParamInterval p = (ParamInterval) context.params(key).get();
    Duration duration = p.v();
    long millis = duration.getNano() / 1000000 + duration.getSeconds() * 1000;
    return new IntervalObject(0, millis);
  }
}
