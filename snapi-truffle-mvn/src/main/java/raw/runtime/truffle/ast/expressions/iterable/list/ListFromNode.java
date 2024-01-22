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
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.ArrayList;
import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.*;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.From")
@NodeChild("list")
@NodeField(name = "resultType", type = Rql2Type.class)
public abstract class ListFromNode extends ExpressionNode {

  @Idempotent
  protected abstract Rql2Type getResultType();

  @Specialization(guards = {"isByteKind(getResultType())"})
  protected Object doByte(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("nextNode")
          GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      initGeneratorNode.execute(this, generator);
      ArrayList<Byte> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(this, generator)) {
        llist.add((byte) nextGeneratorNode.execute(this, generator));
      }
      byte[] list = new byte[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new ByteList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isShortKind(getResultType())"})
  protected Object doShort(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("nextNode")
          GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      initGeneratorNode.execute(this, generator);
      ArrayList<Short> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(this, generator)) {
        llist.add((short) nextGeneratorNode.execute(this, generator));
      }
      short[] list = new short[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new ShortList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isIntKind(getResultType())"})
  protected Object doInt(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("nextNode")
          GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      initGeneratorNode.execute(this, generator);
      ArrayList<Integer> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(this, generator)) {
        llist.add((int) nextGeneratorNode.execute(this, generator));
      }
      int[] list = new int[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new IntList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isLongKind(getResultType())"})
  protected Object doLong(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("nextNode")
          GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      initGeneratorNode.execute(this, generator);
      ArrayList<Long> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(this, generator)) {
        llist.add((long) nextGeneratorNode.execute(this, generator));
      }
      long[] list = new long[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new LongList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isFloatKind(getResultType())"})
  protected Object doFloat(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("nextNode")
          GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      initGeneratorNode.execute(this, generator);
      ArrayList<Float> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(this, generator)) {
        llist.add((float) nextGeneratorNode.execute(this, generator));
      }
      float[] list = new float[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new FloatList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isDoubleKind(getResultType())"})
  protected Object doDouble(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("nextNode")
          GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      initGeneratorNode.execute(this, generator);
      ArrayList<Double> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(this, generator)) {
        llist.add((double) nextGeneratorNode.execute(this, generator));
      }
      double[] list = new double[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new DoubleList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isBooleanKind(getResultType())"})
  protected Object doBoolean(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("nextNode")
          GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      initGeneratorNode.execute(this, generator);
      ArrayList<Boolean> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(this, generator)) {
        llist.add((boolean) nextGeneratorNode.execute(this, generator));
      }
      boolean[] list = new boolean[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new BooleanList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isStringKind(getResultType())"})
  protected Object doString(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("nextNode")
          GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      initGeneratorNode.execute(this, generator);
      ArrayList<String> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(this, generator)) {
        llist.add((String) nextGeneratorNode.execute(this, generator));
      }
      String[] list = new String[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new StringList(list);
    } catch (RawTruffleRuntimeException e) {
      return new ErrorObject(e.getMessage());
    } finally {
      closeGeneratorNode.execute(this, generator);
    }
  }

  @Specialization
  protected Object doObject(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("nextNode")
          GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      initGeneratorNode.execute(this, generator);
      ArrayList<Object> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(this, generator)) {
        llist.add(nextGeneratorNode.execute(this, generator));
      }
      return new RawArrayList(llist);
    } catch (RawTruffleRuntimeException e) {
      return new ErrorObject(e.getMessage());
    } finally {
      closeGeneratorNode.execute(this, generator);
    }
  }
}
