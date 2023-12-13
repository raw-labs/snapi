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
import com.oracle.truffle.api.dsl.*;
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
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.*;
import raw.runtime.truffle.tryable_nullable.TryableNullable;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Filter")
@NodeChild("list")
@NodeChild("function")
@NodeField(name = "resultType", type = Rql2Type.class)
@NodeField(name = "predicateType", type = Rql2Type.class)
public abstract class ListFilterNode extends ExpressionNode {

  static final int LIB_LIMIT = 2;

  protected abstract Rql2Type getResultType();

  @Specialization(
      guards = {"isByteKind(getResultType())"},
      limit = "3")
  @CompilerDirectives.TruffleBoundary
  protected ByteList doByte(
      Object list,
      Object closure,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary("closure") InteropLibrary interops) {
    ArrayList<Byte> llist = new ArrayList<>();
    Object iterable = lists.toIterable(list);
    Object generator = getGeneratorNode.execute(iterable);
    Object[] argumentValues = new Object[1];
    while (generatorHasNextNode.execute(generator)) {
      argumentValues[0] = generatorNextNode.execute(generator);
      Boolean predicate = null;
      try {
        predicate =
            TryableNullable.handlePredicate(interops.execute(closure, argumentValues), false);
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
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary("closure") InteropLibrary interops) {
    ArrayList<Short> llist = new ArrayList<>();
    Object iterable = lists.toIterable(list);
    Object generator = getGeneratorNode.execute(iterable);
    while (generatorHasNextNode.execute(generator)) {
      Object v = generatorNextNode.execute(generator);
      Boolean predicate = null;
      try {
        predicate = TryableNullable.handlePredicate(interops.execute(closure, v), false);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      if (predicate) {
        llist.add((Short) v);
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
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = getGeneratorNode.execute(iterable);
    int[] values =
        Arrays.stream((int[]) lists.getInnerList(list))
            .filter(
                x -> {
                  try {
                    return TryableNullable.handlePredicate(
                        interops.execute(closure, generatorNextNode.execute(generator)), false);
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
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = getGeneratorNode.execute(iterable);
    long[] values =
        Arrays.stream((long[]) lists.getInnerList(list))
            .filter(
                x -> {
                  try {
                    return TryableNullable.handlePredicate(
                        interops.execute(closure, generatorNextNode.execute(generator)), false);
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
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = getGeneratorNode.execute(iterable);
    ArrayList<Float> llist = new ArrayList<>();
    while (generatorHasNextNode.execute(generator)) {
      Object v = generatorNextNode.execute(generator);
      Boolean predicate = null;
      try {
        predicate = TryableNullable.handlePredicate(interops.execute(closure, v), false);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      if (predicate) {
        llist.add((Float) v);
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
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = getGeneratorNode.execute(iterable);
    double[] values =
        Arrays.stream((double[]) lists.getInnerList(list))
            .filter(
                x -> {
                  try {
                    return TryableNullable.handlePredicate(
                        interops.execute(closure, generatorNextNode.execute(generator)), false);
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
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = getGeneratorNode.execute(iterable);
    ArrayList<Boolean> llist = new ArrayList<>();
    while (generatorHasNextNode.execute(generator)) {
      Object v = generatorNextNode.execute(generator);
      boolean predicate = false;
      try {
        predicate = TryableNullable.handlePredicate(interops.execute(closure, v), false);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      if (predicate) {
        llist.add((Boolean) v);
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
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = getGeneratorNode.execute(iterable);
    ArrayList<String> llist = new ArrayList<>();
    while (generatorHasNextNode.execute(generator)) {
      Object v = generatorNextNode.execute(generator);
      boolean predicate = false;
      try {
        predicate = TryableNullable.handlePredicate(interops.execute(closure, v), false);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      if (predicate) {
        llist.add((String) v);
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
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = lists.toIterable(list);
    Object generator = getGeneratorNode.execute(iterable);
    Object[] values =
        Arrays.stream((Object[]) lists.getInnerList(list))
            .filter(
                x -> {
                  try {
                    return TryableNullable.handlePredicate(
                        interops.execute(closure, generatorNextNode.execute(generator)), false);
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
