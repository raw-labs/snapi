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

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2Type;
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

  @Child private InteropLibrary bindings = insert(InteropLibrary.getFactory().createDispatched(1));

  private Object getParam(String key) {
    TruffleObject polyglotBindings = RawContext.get(this).getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isByteKind(getParamType())"})
  protected byte getByte(String key) {
    return (byte) getParam(key);
  }

  @Specialization(guards = {"isShortKind(getParamType())"})
  protected short getShort(String key) {
    return (short) getParam(key);
  }

  @Specialization(guards = {"isIntKind(getParamType())"})
  protected int getInt(String key) {
    return (int) getParam(key);
  }

  @Specialization(guards = {"isLongKind(getParamType())"})
  protected long getLong(String key) {
    return (long) getParam(key);
  }

  @Specialization(guards = {"isFloatKind(getParamType())"})
  protected float getFloat(String key) {
    return (float) getParam(key);
  }

  @Specialization(guards = {"isDoubleKind(getParamType())"})
  protected Double getDouble(String key) {
    return (double) getParam(key);
  }

  @Specialization(guards = {"isDecimalKind(getParamType())"})
  protected DecimalObject getDecimal(String key) {
    return (DecimalObject) getParam(key);
  }

  @Specialization(guards = {"isBooleanKind(getParamType())"})
  protected boolean getBool(String key) {
    return (boolean) getParam(key);
  }

  @Specialization(guards = {"isStringKind(getParamType())"})
  protected String getString(String key) {
    return (String) getParam(key);
  }

  @Specialization(guards = {"isDateKind(getParamType())"})
  protected DateObject getDate(String key) {
    return (DateObject) getParam(key);
  }

  @Specialization(guards = {"isTimeKind(getParamType())"})
  protected TimeObject getTime(String key) {
    return (TimeObject) getParam(key);
  }

  @Specialization(guards = {"isTimestampKind(getParamType())"})
  protected TimestampObject getTimestamp(String key) {
    return (TimestampObject) getParam(key);
  }

  @Specialization(guards = {"isIntervalKind(getParamType())"})
  @TruffleBoundary
  protected IntervalObject getInterval(String key) {
    return (IntervalObject) getParam(key);
  }
}
