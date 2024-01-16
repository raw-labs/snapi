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
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.function.Closure;
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

  @Specialization(guards = {"isByteKind(getResultType())"})
  protected static ByteList doByte(
      Object list,
      Closure closure,
      @Bind("this") Node thisNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @Cached(inline = true) @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
      @Cached(inline = true) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
      @Cached(inline = true) @Cached.Shared("executeOne")
          Closure.ClosureExecuteOneNode closureExecuteOneNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      initNode.execute(thisNode, generator);
      byte[] values = new byte[(int) sizeNode.execute(thisNode, list)];
      int cnt = 0;
      while (generatorHasNextNode.execute(thisNode, generator)) {
        Object v = generatorNextNode.execute(thisNode, generator);
        values[cnt] = (byte) closureExecuteOneNode.execute(thisNode, closure, v);
        cnt++;
      }
      return new ByteList(values);
    } finally {
      closeNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isShortKind(getResultType())"})
  protected static ShortList doShort(
      Object list,
      Closure closure,
      @Bind("this") Node thisNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @Cached(inline = true) @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
      @Cached(inline = true) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
      @Cached(inline = true) @Cached.Shared("executeOne")
          Closure.ClosureExecuteOneNode closureExecuteOneNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      initNode.execute(thisNode, generator);
      short[] values = new short[(int) sizeNode.execute(thisNode, list)];
      int cnt = 0;
      while (generatorHasNextNode.execute(thisNode, generator)) {
        Object v = generatorNextNode.execute(thisNode, generator);
        values[cnt] = (short) closureExecuteOneNode.execute(thisNode, closure, v);
        cnt++;
      }
      return new ShortList(values);
    } finally {
      closeNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isIntKind(getResultType())"})
  protected static IntList doInt(
      Object list,
      Closure closure,
      @Bind("this") Node thisNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @Cached(inline = true) @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
      @Cached(inline = true) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
      @Cached(inline = true) @Cached.Shared("executeOne")
          Closure.ClosureExecuteOneNode closureExecuteOneNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      initNode.execute(thisNode, generator);
      int[] values = new int[(int) sizeNode.execute(thisNode, list)];
      int cnt = 0;
      while (generatorHasNextNode.execute(thisNode, generator)) {
        Object v = generatorNextNode.execute(thisNode, generator);
        values[cnt] = (int) closureExecuteOneNode.execute(thisNode, closure, v);
        cnt++;
      }
      return new IntList(values);
    } finally {
      closeNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isLongKind(getResultType())"})
  protected static LongList doLong(
      Object list,
      Closure closure,
      @Bind("this") Node thisNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @Cached(inline = true) @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
      @Cached(inline = true) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
      @Cached(inline = true) @Cached.Shared("executeOne")
          Closure.ClosureExecuteOneNode closureExecuteOneNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      initNode.execute(thisNode, generator);
      long[] values = new long[(int) sizeNode.execute(thisNode, list)];
      int cnt = 0;
      while (generatorHasNextNode.execute(thisNode, generator)) {
        Object v = generatorNextNode.execute(thisNode, generator);
        values[cnt] = (long) closureExecuteOneNode.execute(thisNode, closure, v);
        cnt++;
      }
      return new LongList(values);
    } finally {
      closeNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isFloatKind(getResultType())"})
  protected static FloatList doFloat(
      Object list,
      Closure closure,
      @Bind("this") Node thisNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @Cached(inline = true) @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
      @Cached(inline = true) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
      @Cached(inline = true) @Cached.Shared("executeOne")
          Closure.ClosureExecuteOneNode closureExecuteOneNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      initNode.execute(thisNode, generator);
      float[] values = new float[(int) sizeNode.execute(thisNode, list)];
      int cnt = 0;
      while (generatorHasNextNode.execute(thisNode, generator)) {
        Object v = generatorNextNode.execute(thisNode, generator);
        values[cnt] = (float) closureExecuteOneNode.execute(thisNode, closure, v);
        cnt++;
      }
      return new FloatList(values);
    } finally {
      closeNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isDoubleKind(getResultType())"})
  protected static DoubleList doDouble(
      Object list,
      Closure closure,
      @Bind("this") Node thisNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @Cached(inline = true) @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
      @Cached(inline = true) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
      @Cached(inline = true) @Cached.Shared("executeOne")
          Closure.ClosureExecuteOneNode closureExecuteOneNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      initNode.execute(thisNode, generator);
      double[] values = new double[(int) sizeNode.execute(thisNode, list)];
      int cnt = 0;
      while (generatorHasNextNode.execute(thisNode, generator)) {
        Object v = generatorNextNode.execute(thisNode, generator);
        values[cnt] = (double) closureExecuteOneNode.execute(thisNode, closure, v);
        cnt++;
      }
      return new DoubleList(values);
    } finally {
      closeNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isBooleanKind(getResultType())"})
  protected BooleanList doBoolean(
      Object list,
      Closure closure,
      @Bind("$node") Node thisNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @Cached(inline = true) @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
      @Cached(inline = true) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
      @Cached(inline = true) @Cached.Shared("executeOne")
          Closure.ClosureExecuteOneNode closureExecuteOneNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      initNode.execute(thisNode, generator);
      boolean[] values = new boolean[(int) sizeNode.execute(thisNode, list)];
      int cnt = 0;
      while (generatorHasNextNode.execute(thisNode, generator)) {
        Object v = generatorNextNode.execute(thisNode, generator);
        values[cnt] = (boolean) closureExecuteOneNode.execute(thisNode, closure, v);
        cnt++;
      }
      return new BooleanList(values);
    } finally {
      closeNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isStringKind(getResultType())"})
  protected static StringList doString(
      Object list,
      Closure closure,
      @Bind("this") Node thisNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @Cached(inline = true) @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
      @Cached(inline = true) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
      @Cached(inline = true) @Cached.Shared("executeOne")
          Closure.ClosureExecuteOneNode closureExecuteOneNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      initNode.execute(thisNode, generator);
      String[] values = new String[(int) sizeNode.execute(thisNode, list)];
      int cnt = 0;
      while (generatorHasNextNode.execute(thisNode, generator)) {
        Object v = generatorNextNode.execute(thisNode, generator);
        values[cnt] = (String) closureExecuteOneNode.execute(thisNode, closure, v);
        cnt++;
      }
      return new StringList(values);
    } finally {
      closeNode.execute(thisNode, generator);
    }
  }

  @Specialization
  protected ObjectList doObject(
      Object list,
      Closure closure,
      @Bind("$node") Node thisNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("hasNext")
          GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
      @Cached(inline = true) @Cached.Shared("next")
          GeneratorNodes.GeneratorNextNode generatorNextNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("size") ListNodes.SizeNode sizeNode,
      @Cached(inline = true) @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
      @Cached(inline = true) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
      @Cached(inline = true) @Cached.Shared("executeOne")
          Closure.ClosureExecuteOneNode closureExecuteOneNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      initNode.execute(thisNode, generator);
      Object[] values = new Object[(int) sizeNode.execute(thisNode, list)];
      int cnt = 0;
      while (generatorHasNextNode.execute(thisNode, generator)) {
        Object v = generatorNextNode.execute(thisNode, generator);
        values[cnt] = closureExecuteOneNode.execute(thisNode, closure, v);
        cnt++;
      }
      return new ObjectList(values);
    } finally {
      closeNode.execute(thisNode, generator);
    }
  }
}
