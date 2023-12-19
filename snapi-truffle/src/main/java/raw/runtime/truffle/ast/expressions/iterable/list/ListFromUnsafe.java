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
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.*;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.FromUnsafe")
@NodeChild("list")
@NodeField(name = "resultType", type = Rql2Type.class)
public abstract class ListFromUnsafe extends ExpressionNode {

  protected abstract Rql2Type getResultType();

  @Specialization(
      guards = {"isByteKind(getResultType())"},
      limit = "3")
  protected ByteList doByte(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      generatorInitNode.execute(generator);
      ArrayList<Byte> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(generator)) {
        llist.add((byte) generatorNextNode.execute(generator));
      }
      byte[] list = new byte[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new ByteList(list);
    } finally {
      generatorCloseNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isShortKind(getResultType())"},
      limit = "3")
  protected ShortList doShort(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      generatorInitNode.execute(generator);
      ArrayList<Short> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(generator)) {
        llist.add((short) generatorNextNode.execute(generator));
      }
      short[] list = new short[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new ShortList(list);
    } finally {
      generatorCloseNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isIntKind(getResultType())"},
      limit = "3")
  protected IntList doInt(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      generatorInitNode.execute(generator);
      ArrayList<Integer> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(generator)) {
        llist.add((int) generatorNextNode.execute(generator));
      }
      int[] list = new int[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new IntList(list);
    } finally {
      generatorCloseNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isLongKind(getResultType())"},
      limit = "3")
  protected LongList doLong(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      generatorInitNode.execute(generator);
      ArrayList<Long> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(generator)) {
        llist.add((long) generatorNextNode.execute(generator));
      }
      long[] list = new long[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new LongList(list);
    } finally {
      generatorCloseNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isFloatKind(getResultType())"},
      limit = "3")
  protected FloatList doFloat(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      generatorInitNode.execute(generator);
      ArrayList<Float> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(generator)) {
        llist.add((float) generatorNextNode.execute(generator));
      }
      float[] list = new float[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new FloatList(list);
    } finally {
      generatorCloseNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isDoubleKind(getResultType())"},
      limit = "3")
  protected DoubleList doDouble(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      generatorInitNode.execute(generator);
      ArrayList<Double> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(generator)) {
        llist.add((double) generatorNextNode.execute(generator));
      }
      double[] list = new double[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new DoubleList(list);
    } finally {
      generatorCloseNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isBooleanKind(getResultType())"},
      limit = "3")
  protected BooleanList doBoolean(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      generatorInitNode.execute(generator);
      ArrayList<Boolean> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(generator)) {
        llist.add((boolean) generatorNextNode.execute(generator));
      }
      boolean[] list = new boolean[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new BooleanList(list);
    } finally {
      generatorCloseNode.execute(generator);
    }
  }

  @Specialization(
      guards = {"isStringKind(getResultType())"},
      limit = "3")
  protected StringList doString(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      generatorInitNode.execute(generator);
      ArrayList<String> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(generator)) {
        llist.add((String) generatorNextNode.execute(generator));
      }
      String[] list = new String[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new StringList(list);
    } finally {
      generatorCloseNode.execute(generator);
    }
  }

  @Specialization(limit = "3")
  protected ObjectList doObject(
      Object iterable,
      @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(iterable);
    try {
      generatorInitNode.execute(generator);
      ArrayList<Object> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(generator)) {
        llist.add(generatorNextNode.execute(generator));
      }
      Object[] list = new Object[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new ObjectList(list);
    } finally {
      generatorCloseNode.execute(generator);
    }
  }
}
