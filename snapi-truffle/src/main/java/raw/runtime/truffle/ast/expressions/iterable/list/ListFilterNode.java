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

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.ast.expressions.iterable.list.osr.OSRListFilterNode;
import raw.runtime.truffle.ast.expressions.iterable.list.osr.OSRToArrayNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.*;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Filter")
@NodeChild("list")
@NodeChild("function")
@NodeField(name = "resultType", type = Rql2Type.class)
public abstract class ListFilterNode extends ExpressionNode {

  @Idempotent
  protected abstract Rql2Type getResultType();

  public static LoopNode getFilterLoopNode() {
    return Truffle.getRuntime().createLoopNode(new OSRListFilterNode());
  }

  public static LoopNode getToArrayLoopNode(Rql2Type resultType) {
    return Truffle.getRuntime().createLoopNode(new OSRToArrayNode(resultType));
  }

  @Specialization(guards = {"isByteKind(getResultType())"})
  protected static Object doByte(
      VirtualFrame frame,
      Object list,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(value = "getFilterLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFilterLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      OSRListFilterNode osrNode = (OSRListFilterNode) loopNode.getRepeatingNode();
      osrNode.init(generator, function);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new ByteList((byte[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isShortKind(getResultType())"})
  protected static Object doShort(
      VirtualFrame frame,
      Object list,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(value = "getFilterLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFilterLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      OSRListFilterNode osrNode = (OSRListFilterNode) loopNode.getRepeatingNode();
      osrNode.init(generator, function);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new ShortList((short[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isIntKind(getResultType())"})
  protected static Object doInt(
      VirtualFrame frame,
      Object list,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(value = "getFilterLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFilterLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      OSRListFilterNode osrNode = (OSRListFilterNode) loopNode.getRepeatingNode();
      osrNode.init(generator, function);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new IntList((int[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isLongKind(getResultType())"})
  protected static Object doLong(
      VirtualFrame frame,
      Object list,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(value = "getFilterLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFilterLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      OSRListFilterNode osrNode = (OSRListFilterNode) loopNode.getRepeatingNode();
      osrNode.init(generator, function);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new LongList((long[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isFloatKind(getResultType())"})
  protected static Object doFloat(
      VirtualFrame frame,
      Object list,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(value = "getFilterLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFilterLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      OSRListFilterNode osrNode = (OSRListFilterNode) loopNode.getRepeatingNode();
      osrNode.init(generator, function);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new FloatList((float[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isDoubleKind(getResultType())"})
  protected static Object doDouble(
      VirtualFrame frame,
      Object list,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(value = "getFilterLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFilterLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      OSRListFilterNode osrNode = (OSRListFilterNode) loopNode.getRepeatingNode();
      osrNode.init(generator, function);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new DoubleList((double[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isBooleanKind(getResultType())"})
  protected static Object doBoolean(
      VirtualFrame frame,
      Object list,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(value = "getFilterLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFilterLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      OSRListFilterNode osrNode = (OSRListFilterNode) loopNode.getRepeatingNode();
      osrNode.init(generator, function);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new BooleanList((boolean[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }

  @Specialization(guards = {"isStringKind(getResultType())"})
  protected static Object doString(
      VirtualFrame frame,
      Object list,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(value = "getFilterLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFilterLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      OSRListFilterNode osrNode = (OSRListFilterNode) loopNode.getRepeatingNode();
      osrNode.init(generator, function);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new StringList((String[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }

  @Specialization
  protected static Object doObject(
      VirtualFrame frame,
      Object list,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(value = "getFilterLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFilterLoopNode")
          LoopNode loopNode,
      @Cached(inline = true) @Cached.Shared("getGeneratorNode")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("toIterable") ListNodes.ToIterableNode toIterableNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      OSRListFilterNode osrNode = (OSRListFilterNode) loopNode.getRepeatingNode();
      osrNode.init(generator, function);
      loopNode.execute(frame);
      return new RawArrayList(osrNode.getResult());
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }
}
