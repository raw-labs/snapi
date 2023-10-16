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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.ArrayList;
import java.util.Arrays;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.handlers.NullableTryableHandler;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.list.*;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Filter")
@NodeChild("list")
@NodeChild("function")
@NodeField(name = "resultType", type = Rql2Type.class)
@NodeField(name = "predicateType", type = Rql2Type.class)
public abstract class ListFilterNode extends ExpressionNode {

  static final int LIB_LIMIT = 2;

  protected abstract Rql2Type getResultType();

  protected abstract Rql2Type getPredicateType();

  @Specialization(
      guards = {"isByteKind(getResultType())"},
      limit = "3")
  @CompilerDirectives.TruffleBoundary
  protected ByteList doByte(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    ArrayList<Byte> llist = new ArrayList<>();
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      Boolean predicate = null;
      try {
        predicate =
            NullableTryableHandler.handleOptionTriablePredicate(
                interops.execute(closure, argumentValues), getPredicateType(), false);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      if (predicate) {
        llist.add((Byte) argumentValues[0]);
      }
    }
    byte[] values = new byte[llist.size()];
    for (int i = 0; i < values.length; i++) {
      values[i] = llist.get(i);
    }
    return new ByteList(values);
  }

  @Specialization(
      guards = {"isShortKind(getResultType())"},
      limit = "3")
  @CompilerDirectives.TruffleBoundary
  protected ShortList doShort(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    ArrayList<Short> llist = new ArrayList<>();
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      Boolean predicate = null;
      try {
        predicate =
            NullableTryableHandler.handleOptionTriablePredicate(
                interops.execute(closure, argumentValues), getPredicateType(), false);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      if (predicate) {
        llist.add((Short) argumentValues[0]);
      }
    }
    short[] values = new short[llist.size()];
    for (int i = 0; i < values.length; i++) {
      values[i] = llist.get(i);
    }
    return new ShortList(values);
  }

  @Specialization(
      guards = {"isIntKind(getResultType())"},
      limit = "3")
  @CompilerDirectives.TruffleBoundary
  protected IntList doInt(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    Object[] argumentValues = new Object[1];
    int[] values =
        Arrays.stream((int[]) lists.getInnerList(list))
            .filter(
                x -> {
                  argumentValues[0] = generators.next(generator);
                  try {
                    return NullableTryableHandler.handleOptionTriablePredicate(
                        interops.execute(closure, argumentValues), getPredicateType(), false);
                  } catch (UnsupportedMessageException
                      | UnsupportedTypeException
                      | ArityException e) {
                    throw new RawTruffleRuntimeException("failed to execute function");
                  }
                })
            .toArray();
    return new IntList(values);
  }

  @Specialization(
      guards = {"isLongKind(getResultType())"},
      limit = "3")
  @CompilerDirectives.TruffleBoundary
  protected LongList doLong(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    Object[] argumentValues = new Object[1];
    long[] values =
        Arrays.stream((long[]) lists.getInnerList(list))
            .filter(
                x -> {
                  argumentValues[0] = generators.next(generator);
                  try {
                    return NullableTryableHandler.handleOptionTriablePredicate(
                        interops.execute(closure, argumentValues), getPredicateType(), false);
                  } catch (UnsupportedMessageException
                      | UnsupportedTypeException
                      | ArityException e) {
                    throw new RawTruffleRuntimeException("failed to execute function");
                  }
                })
            .toArray();
    return new LongList(values);
  }

  @Specialization(
      guards = {"isFloatKind(getResultType())"},
      limit = "3")
  @CompilerDirectives.TruffleBoundary
  protected FloatList doFloat(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    ArrayList<Float> llist = new ArrayList<>();
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      Boolean predicate = null;
      try {
        predicate =
            NullableTryableHandler.handleOptionTriablePredicate(
                interops.execute(closure, argumentValues), getPredicateType(), false);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      if (predicate) {
        llist.add((Float) argumentValues[0]);
      }
    }
    float[] values = new float[llist.size()];
    for (int i = 0; i < values.length; i++) {
      values[i] = llist.get(i);
    }
    return new FloatList(values);
  }

  @Specialization(
      guards = {"isDoubleKind(getResultType())"},
      limit = "3")
  @CompilerDirectives.TruffleBoundary
  protected DoubleList doDouble(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    Object[] argumentValues = new Object[1];
    double[] values =
        Arrays.stream((double[]) lists.getInnerList(list))
            .filter(
                x -> {
                  argumentValues[0] = generators.next(generator);
                  try {
                    return NullableTryableHandler.handleOptionTriablePredicate(
                        interops.execute(closure, argumentValues), getPredicateType(), false);
                  } catch (UnsupportedMessageException
                      | UnsupportedTypeException
                      | ArityException e) {
                    throw new RawTruffleRuntimeException("failed to execute function");
                  }
                })
            .toArray();
    return new DoubleList(values);
  }

  @Specialization(
      guards = {"isBooleanKind(getResultType())"},
      limit = "3")
  @CompilerDirectives.TruffleBoundary
  protected BooleanList doBoolean(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    ArrayList<Boolean> llist = new ArrayList<>();
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      boolean predicate = false;
      try {
        predicate =
            NullableTryableHandler.handleOptionTriablePredicate(
                interops.execute(closure, argumentValues), getPredicateType(), false);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      if (predicate) {
        llist.add((Boolean) argumentValues[0]);
      }
    }
    boolean[] values = new boolean[llist.size()];
    for (int i = 0; i < values.length; i++) {
      values[i] = llist.get(i);
    }
    return new BooleanList(values);
  }

  @Specialization(
      guards = {"isStringKind(getResultType())"},
      limit = "3")
  @CompilerDirectives.TruffleBoundary
  protected StringList doString(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    ArrayList<String> llist = new ArrayList<>();
    Object[] argumentValues = new Object[1];
    while (generators.hasNext(generator)) {
      argumentValues[0] = generators.next(generator);
      boolean predicate = false;
      try {
        predicate =
            NullableTryableHandler.handleOptionTriablePredicate(
                interops.execute(closure, argumentValues), getPredicateType(), false);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      if (predicate) {
        llist.add((String) argumentValues[0]);
      }
    }
    String[] values = new String[llist.size()];
    for (int i = 0; i < values.length; i++) {
      values[i] = llist.get(i);
    }
    return new StringList(values);
  }

  @Specialization(limit = "3")
  @CompilerDirectives.TruffleBoundary
  protected ObjectList doObject(
      Object list,
      Object closure,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
      @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = iterables.getGenerator(iterable);
    Object[] argumentValues = new Object[1];
    Object[] values =
        Arrays.stream((Object[]) lists.getInnerList(list))
            .filter(
                x -> {
                  argumentValues[0] = generators.next(generator);
                  try {
                    return NullableTryableHandler.handleOptionTriablePredicate(
                        interops.execute(closure, argumentValues), getPredicateType(), false);
                  } catch (UnsupportedMessageException
                      | UnsupportedTypeException
                      | ArityException e) {
                    throw new RawTruffleRuntimeException("failed to execute function");
                  }
                })
            .toArray();
    return new ObjectList(values);
  }
}
