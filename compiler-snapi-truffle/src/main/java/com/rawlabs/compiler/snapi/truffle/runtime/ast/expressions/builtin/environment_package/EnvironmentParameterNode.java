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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.environment_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.rql2.source.Rql2Type;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawContext;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.TypeGuards;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.StaticInitializers;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.*;

@ImportStatic(value = {TypeGuards.class, StaticInitializers.class})
@NodeInfo(shortName = "Environment.Parameter")
@NodeChild(value = "key")
@NodeField(name = "paramType", type = Rql2Type.class)
public abstract class EnvironmentParameterNode extends ExpressionNode {

  @Idempotent
  protected abstract Rql2Type getParamType();

  @Specialization(guards = {"isByteKind(getParamType())"})
  protected byte getByte(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (byte) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isShortKind(getParamType())"})
  protected short getShort(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (short) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isIntKind(getParamType())"})
  protected int getInt(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (int) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isLongKind(getParamType())"})
  protected long getLong(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (long) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isFloatKind(getParamType())"})
  protected float getFloat(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (float) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isDoubleKind(getParamType())"})
  protected Double getDouble(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (double) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isDecimalKind(getParamType())"})
  protected DecimalObject getDecimal(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (DecimalObject) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isBooleanKind(getParamType())"})
  protected boolean getBool(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (boolean) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isStringKind(getParamType())"})
  protected String getString(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (String) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isDateKind(getParamType())"})
  protected DateObject getDate(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (DateObject) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isTimeKind(getParamType())"})
  protected TimeObject getTime(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (TimeObject) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isTimestampKind(getParamType())"})
  protected TimestampObject getTimestamp(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (TimestampObject) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }

  @Specialization(guards = {"isIntervalKind(getParamType())"})
  @TruffleBoundary
  protected IntervalObject getInterval(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRawContext(thisNode)", neverDefault = true) RawContext context,
      @CachedLibrary(limit = "3") @Cached.Shared("interop") InteropLibrary bindings) {
    TruffleObject polyglotBindings = context.getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return (IntervalObject) bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }
}
