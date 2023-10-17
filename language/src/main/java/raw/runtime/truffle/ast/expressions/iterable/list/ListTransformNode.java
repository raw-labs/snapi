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

package raw.runtime.truffle.ast.expressions.iterable.list;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.list.*;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Transform")
@NodeChild("list")
@NodeChild("function")
@NodeField(name = "resultType", type = Rql2Type.class)
public abstract class ListTransformNode extends ExpressionNode {

  static final int LIB_LIMIT = 2;

  protected abstract Rql2Type getResultType();

  @Specialization(
      guards = {"isByteKind(getResultType())"},
      limit = "3")
  protected ByteList doByte(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    byte[] values = new byte[(int) lists.size(list)];
    int cnt = 0;
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      try {
        values[cnt] = (byte) interops.execute(closure, argumentValues);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      cnt++;
    }
    return new ByteList(values);
  }

  @Specialization(
      guards = {"isShortKind(getResultType())"},
      limit = "3")
  protected ShortList doShort(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    short[] values = new short[(int) lists.size(list)];
    int cnt = 0;
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      try {
        values[cnt] = (short) interops.execute(closure, argumentValues);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      cnt++;
    }
    return new ShortList(values);
  }

  @Specialization(
      guards = {"isIntKind(getResultType())"},
      limit = "3")
  protected IntList doInt(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    int[] values = new int[(int) lists.size(list)];
    int cnt = 0;
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      try {
        values[cnt] = (int) interops.execute(closure, argumentValues);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      cnt++;
    }
    return new IntList(values);
  }

  @Specialization(
      guards = {"isLongKind(getResultType())"},
      limit = "3")
  protected LongList doLong(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    long[] values = new long[(int) lists.size(list)];
    int cnt = 0;
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      try {
        values[cnt] = (long) interops.execute(closure, argumentValues);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      cnt++;
    }
    return new LongList(values);
  }

  @Specialization(
      guards = {"isFloatKind(getResultType())"},
      limit = "3")
  protected FloatList doFloat(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    float[] values = new float[(int) lists.size(list)];
    int cnt = 0;
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      try {
        values[cnt] = (float) interops.execute(closure, argumentValues);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      cnt++;
    }
    return new FloatList(values);
  }

  @Specialization(
      guards = {"isDoubleKind(getResultType())"},
      limit = "3")
  protected DoubleList doDouble(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    double[] values = new double[(int) lists.size(list)];
    int cnt = 0;
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      try {
        values[cnt] = (double) interops.execute(closure, argumentValues);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      cnt++;
    }
    return new DoubleList(values);
  }

  @Specialization(
      guards = {"isBooleanKind(getResultType())"},
      limit = "3")
  protected BooleanList doBoolean(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    boolean[] values = new boolean[(int) lists.size(list)];
    int cnt = 0;
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      try {
        values[cnt] = (boolean) interops.execute(closure, argumentValues);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      cnt++;
    }
    return new BooleanList(values);
  }

  @Specialization(
      guards = {"isStringKind(getResultType())"},
      limit = "3")
  protected StringList doString(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    String[] values = new String[(int) lists.size(list)];
    int cnt = 0;
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      try {
        values[cnt] = (String) interops.execute(closure, argumentValues);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      cnt++;
    }
    return new StringList(values);
  }

  @Specialization(limit = "3")
  protected ObjectList doObject(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    Object[] values = new Object[(int) lists.size(list)];
    int cnt = 0;
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      try {
        values[cnt] = interops.execute(closure, argumentValues);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      cnt++;
    }
    return new ObjectList(values);
  }
}
