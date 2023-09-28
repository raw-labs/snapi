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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.Duration;
import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.primitives.*;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "Environment.Parameter")
@NodeChild(value = "key")
@NodeField(name = "paramType", type = Rql2Type.class)
public abstract class EnvironmentParameterNode extends ExpressionNode {

  protected abstract Rql2Type getParamType();

  @Specialization(guards = {"isByteKind(getParamType())"})
  protected byte getByte(String key) {
    ParamByte p = (ParamByte) RawContext.get(this).getParam(key);
    return p.v();
  }

  @Specialization(guards = {"isShortKind(getParamType())"})
  protected short getShort(String key) {
    ParamShort p = (ParamShort) RawContext.get(this).getParam(key);
    return p.v();
  }

  @Specialization(guards = {"isIntKind(getParamType())"})
  protected int getInt(String key) {
    ParamInt p = (ParamInt) RawContext.get(this).getParam(key);
    return p.v();
  }

  @Specialization(guards = {"isLongKind(getParamType())"})
  protected long getLong(String key) {
    ParamLong p = (ParamLong) RawContext.get(this).getParam(key);
    return p.v();
  }

  @Specialization(guards = {"isFloatKind(getParamType())"})
  protected float getFloat(String key) {
    ParamFloat p = (ParamFloat) RawContext.get(this).getParam(key);
    return p.v();
  }

  @Specialization(guards = {"isDoubleKind(getParamType())"})
  protected Double getDouble(String key) {
    ParamDouble p = (ParamDouble) RawContext.get(this).getParam(key);
    return p.v();
  }

  @Specialization(guards = {"isDecimalKind(getParamType())"})
  protected DecimalObject getDecimal(String key) {
    ParamDecimal p = (ParamDecimal) RawContext.get(this).getParam(key);
    return new DecimalObject(p.v());
  }

  @Specialization(guards = {"isBooleanKind(getParamType())"})
  protected boolean getBool(String key) {
    ParamBool p = (ParamBool) RawContext.get(this).getParam(key);
    return p.v();
  }

  @Specialization(guards = {"isStringKind(getParamType())"})
  protected String getString(String key) {
    ParamString p = (ParamString) RawContext.get(this).getParam(key);
    return p.v();
  }

  @Specialization(guards = {"isDateKind(getParamType())"})
  protected DateObject getDate(String key) {
    ParamDate p = (ParamDate) RawContext.get(this).getParam(key);
    return new DateObject(p.v());
  }

  @Specialization(guards = {"isTimeKind(getParamType())"})
  protected TimeObject getTime(String key) {
    ParamTime p = (ParamTime) RawContext.get(this).getParam(key);
    return new TimeObject(p.v());
  }

  @Specialization(guards = {"isTimestampKind(getParamType())"})
  protected TimestampObject getTimestamp(String key) {
    ParamTimestamp p = (ParamTimestamp) RawContext.get(this).getParam(key);
    return new TimestampObject(p.v());
  }

  @Specialization(guards = {"isIntervalKind(getParamType())"})
  @CompilerDirectives.TruffleBoundary
  protected IntervalObject getInterval(String key) {
    ParamInterval p = (ParamInterval) RawContext.get(this).getParam(key);
    Duration duration = p.v();
    long millis = duration.getNano() / 1000000 + duration.getSeconds() * 1000;
    return new IntervalObject(0, millis);
  }
}
