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
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.*;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Transform")
@NodeChild("list")
@NodeChild("function")
@NodeField(name = "resultType", type = Rql2Type.class)
public abstract class ListTransformNode extends ExpressionNode {

  static final int LIB_LIMIT = 2;

  @Idempotent
  protected abstract Rql2Type getResultType();

  @Specialization(
      guards = {"isByteKind(getResultType())"},
      limit = "3")
  protected static ByteList doByte(
      Object list,
      Object closure,
      @Bind("this") Node thisNode,
      @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = toIterableNode.execute(list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    byte[] values = new byte[(int) sizeNode.execute(list)];
    int cnt = 0;
    while (generatorHasNextNode.execute(thisNode, generator)) {
      Object v = generatorNextNode.execute(thisNode, generator);
      try {
        values[cnt] = (byte) interops.execute(closure, v);
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
  protected static ShortList doShort(
      Object list,
      Object closure,
      @Bind("this") Node thisNode,
      @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = toIterableNode.execute(list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    short[] values = new short[(int) sizeNode.execute(list)];
    int cnt = 0;
    while (generatorHasNextNode.execute(thisNode, generator)) {
      Object v = generatorNextNode.execute(thisNode, generator);
      try {
        values[cnt] = (short) interops.execute(closure, v);
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
  protected static IntList doInt(
      Object list,
      Object closure,
      @Bind("this") Node thisNode,
      @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = toIterableNode.execute(list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    int[] values = new int[(int) sizeNode.execute(list)];
    int cnt = 0;
    while (generatorHasNextNode.execute(thisNode, generator)) {
      Object v = generatorNextNode.execute(thisNode, generator);
      try {
        values[cnt] = (int) interops.execute(closure, v);
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
  protected static LongList doLong(
      Object list,
      Object closure,
      @Bind("this") Node thisNode,
      @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = toIterableNode.execute(list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    long[] values = new long[(int) sizeNode.execute(list)];
    int cnt = 0;
    while (generatorHasNextNode.execute(thisNode, generator)) {
      Object v = generatorNextNode.execute(thisNode, generator);
      try {
        values[cnt] = (long) interops.execute(closure, v);
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
  protected static FloatList doFloat(
      Object list,
      Object closure,
      @Bind("this") Node thisNode,
      @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = toIterableNode.execute(list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    float[] values = new float[(int) sizeNode.execute(list)];
    int cnt = 0;
    while (generatorHasNextNode.execute(thisNode, generator)) {
      Object v = generatorNextNode.execute(thisNode, generator);
      try {
        values[cnt] = (float) interops.execute(closure, v);
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
  protected static DoubleList doDouble(
      Object list,
      Object closure,
      @Bind("this") Node thisNode,
      @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = toIterableNode.execute(list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    double[] values = new double[(int) sizeNode.execute(list)];
    int cnt = 0;
    while (generatorHasNextNode.execute(thisNode, generator)) {
      Object v = generatorNextNode.execute(thisNode, generator);
      try {
        values[cnt] = (double) interops.execute(closure, v);
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
      @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = toIterableNode.execute(list);
    Object generator = getGeneratorNode.execute(this, iterable);
    boolean[] values = new boolean[(int) sizeNode.execute(list)];
    int cnt = 0;
    while (generatorHasNextNode.execute(this, generator)) {
      Object v = generatorNextNode.execute(this, generator);
      try {
        values[cnt] = (boolean) interops.execute(closure, v);
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
  protected static StringList doString(
      Object list,
      Object closure,
      @Bind("this") Node thisNode,
      @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = toIterableNode.execute(list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    String[] values = new String[(int) sizeNode.execute(list)];
    int cnt = 0;
    while (generatorHasNextNode.execute(thisNode, generator)) {
      Object v = generatorNextNode.execute(thisNode, generator);
      try {
        values[cnt] = (String) interops.execute(closure, v);
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
      @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @CachedLibrary("closure") InteropLibrary interops) {
    Object iterable = toIterableNode.execute(list);
    Object generator = getGeneratorNode.execute(this, iterable);
    Object[] values = new Object[(int) sizeNode.execute(list)];
    int cnt = 0;
    while (generatorHasNextNode.execute(this, generator)) {
      Object v = generatorNextNode.execute(this, generator);
      try {
        values[cnt] = interops.execute(closure, v);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
      cnt++;
    }
    return new ObjectList(values);
  }
}
