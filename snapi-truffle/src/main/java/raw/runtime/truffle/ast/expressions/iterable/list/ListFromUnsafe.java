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
import com.oracle.truffle.api.nodes.Node;
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

  @Idempotent
  protected abstract Rql2Type getResultType();

  @Specialization(guards = {"isByteKind(getResultType())"})
  protected ByteList doByte(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      ArrayList<Byte> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(this, generator)) {
        llist.add((byte) generatorNextNode.execute(this, generator));
      }
      byte[] list = new byte[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new ByteList(list);
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isShortKind(getResultType())"})
  protected ShortList doShort(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      ArrayList<Short> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(this, generator)) {
        llist.add((short) generatorNextNode.execute(this, generator));
      }
      short[] list = new short[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new ShortList(list);
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isIntKind(getResultType())"})
  protected IntList doInt(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      ArrayList<Integer> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(this, generator)) {
        llist.add((int) generatorNextNode.execute(this, generator));
      }
      int[] list = new int[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new IntList(list);
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isLongKind(getResultType())"})
  protected LongList doLong(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      ArrayList<Long> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(this, generator)) {
        llist.add((long) generatorNextNode.execute(this, generator));
      }
      long[] list = new long[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new LongList(list);
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isFloatKind(getResultType())"})
  protected FloatList doFloat(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      ArrayList<Float> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(this, generator)) {
        llist.add((float) generatorNextNode.execute(this, generator));
      }
      float[] list = new float[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new FloatList(list);
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isDoubleKind(getResultType())"})
  protected DoubleList doDouble(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      ArrayList<Double> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(this, generator)) {
        llist.add((double) generatorNextNode.execute(this, generator));
      }
      double[] list = new double[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new DoubleList(list);
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isBooleanKind(getResultType())"})
  protected BooleanList doBoolean(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      ArrayList<Boolean> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(this, generator)) {
        llist.add((boolean) generatorNextNode.execute(this, generator));
      }
      boolean[] list = new boolean[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new BooleanList(list);
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isStringKind(getResultType())"})
  protected StringList doString(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      ArrayList<String> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(this, generator)) {
        llist.add((String) generatorNextNode.execute(this, generator));
      }
      String[] list = new String[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new StringList(list);
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization
  protected ObjectList doObject(
      Object iterable,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("hasNextNode")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      ArrayList<Object> llist = new ArrayList<>();
      while (generatorHasNextNode.execute(this, generator)) {
        llist.add(generatorNextNode.execute(this, generator));
      }
      Object[] list = new Object[llist.size()];
      for (int i = 0; i < list.length; i++) {
        list[i] = llist.get(i);
      }
      return new ObjectList(list);
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }
}
