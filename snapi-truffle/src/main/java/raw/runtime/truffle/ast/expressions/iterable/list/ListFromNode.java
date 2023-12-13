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
import com.oracle.truffle.api.library.CachedLibrary;
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

  protected abstract Rql2Type getResultType();

  @Specialization(
      guards = {"isByteKind(getResultType())"},
      limit = "3")
  protected Object doByte(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      initGeneratorNode.execute(generator);
      ArrayList<Byte> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(generator)) {
        llist.add((byte) nextGeneratorNode.execute(generator));
      }
      byte[] list = new byte[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new ByteList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isShortKind(getResultType())"},
      limit = "3")
  protected Object doShort(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      initGeneratorNode.execute(generator);
      ArrayList<Short> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(generator)) {
        llist.add((short) nextGeneratorNode.execute(generator));
      }
      short[] list = new short[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new ShortList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isIntKind(getResultType())"},
      limit = "3")
  protected Object doInt(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      initGeneratorNode.execute(generator);
      ArrayList<Integer> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(generator)) {
        llist.add((int) nextGeneratorNode.execute(generator));
      }
      int[] list = new int[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new IntList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isLongKind(getResultType())"},
      limit = "3")
  protected Object doLong(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      initGeneratorNode.execute(generator);
      ArrayList<Long> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(generator)) {
        llist.add((long) nextGeneratorNode.execute(generator));
      }
      long[] list = new long[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new LongList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isFloatKind(getResultType())"},
      limit = "3")
  protected Object doFloat(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      initGeneratorNode.execute(generator);
      ArrayList<Float> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(generator)) {
        llist.add((float) nextGeneratorNode.execute(generator));
      }
      float[] list = new float[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new FloatList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isDoubleKind(getResultType())"},
      limit = "3")
  protected Object doDouble(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      initGeneratorNode.execute(generator);
      ArrayList<Double> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(generator)) {
        llist.add((double) nextGeneratorNode.execute(generator));
      }
      double[] list = new double[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new DoubleList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isBooleanKind(getResultType())"},
      limit = "3")
  protected Object doBoolean(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      initGeneratorNode.execute(generator);
      ArrayList<Boolean> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(generator)) {
        llist.add((boolean) nextGeneratorNode.execute(generator));
      }
      boolean[] list = new boolean[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new BooleanList(list);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      closeGeneratorNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isStringKind(getResultType())"},
      limit = "3")
  protected Object doString(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      initGeneratorNode.execute(generator);
      ArrayList<String> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(generator)) {
        llist.add((String) nextGeneratorNode.execute(generator));
      }
      String[] list = new String[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new StringList(list);
    } catch (RawTruffleRuntimeException e) {
      return new ErrorObject(e.getMessage());
    } finally {
      closeGeneratorNode.execute(generator);
    }
  }

  @Specialization(limit = "3")
  protected Object doObject(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode initGeneratorNode,
      @Cached GeneratorNodes.GeneratorHasNextNode hasNextGeneratorNode,
      @Cached GeneratorNodes.GeneratorNextNode nextGeneratorNode,
      @Cached GeneratorNodes.GeneratorCloseNode closeGeneratorNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      initGeneratorNode.execute(generator);
      ArrayList<Object> llist = new ArrayList<>();
      while (hasNextGeneratorNode.execute(generator)) {
        llist.add(nextGeneratorNode.execute(generator));
      }
      Object[] list = new Object[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new ObjectList(list);
    } catch (RawTruffleRuntimeException e) {
      return new ErrorObject(e.getMessage());
    } finally {
      closeGeneratorNode.execute(generator);
    }
  }
}
