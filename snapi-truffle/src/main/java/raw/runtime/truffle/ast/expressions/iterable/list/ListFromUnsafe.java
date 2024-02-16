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
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.ast.expressions.iterable.list.osr.OSRListFromNode;
import raw.runtime.truffle.ast.expressions.iterable.list.osr.OSRToArrayNode;
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

  public static LoopNode getFromLoopNode() {
    return Truffle.getRuntime().createLoopNode(new OSRListFromNode());
  }

  public static LoopNode getToArrayLoopNode(Rql2Type resultType) {
    return Truffle.getRuntime().createLoopNode(new OSRToArrayNode(resultType));
  }

  @Specialization(guards = {"isByteKind(getResultType())"})
  protected ByteList doByte(
      VirtualFrame frame,
      Object iterable,
      @Cached(value = "getFromLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      OSRListFromNode osrNode = (OSRListFromNode) loopNode.getRepeatingNode();
      osrNode.init(generator);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new ByteList((byte[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isShortKind(getResultType())"})
  protected ShortList doShort(
      VirtualFrame frame,
      Object iterable,
      @Cached(value = "getFromLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      OSRListFromNode osrNode = (OSRListFromNode) loopNode.getRepeatingNode();
      osrNode.init(generator);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new ShortList((short[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isIntKind(getResultType())"})
  protected IntList doInt(
      VirtualFrame frame,
      Object iterable,
      @Cached(value = "getFromLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      OSRListFromNode osrNode = (OSRListFromNode) loopNode.getRepeatingNode();
      osrNode.init(generator);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new IntList((int[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isLongKind(getResultType())"})
  protected LongList doLong(
      VirtualFrame frame,
      Object iterable,
      @Cached(value = "getFromLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      OSRListFromNode osrNode = (OSRListFromNode) loopNode.getRepeatingNode();
      osrNode.init(generator);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new LongList((long[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isFloatKind(getResultType())"})
  protected FloatList doFloat(
      VirtualFrame frame,
      Object iterable,
      @Cached(value = "getFromLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      OSRListFromNode osrNode = (OSRListFromNode) loopNode.getRepeatingNode();
      osrNode.init(generator);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new FloatList((float[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isDoubleKind(getResultType())"})
  protected DoubleList doDouble(
      VirtualFrame frame,
      Object iterable,
      @Cached(value = "getFromLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      OSRListFromNode osrNode = (OSRListFromNode) loopNode.getRepeatingNode();
      osrNode.init(generator);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new DoubleList((double[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isBooleanKind(getResultType())"})
  protected BooleanList doBoolean(
      VirtualFrame frame,
      Object iterable,
      @Cached(value = "getFromLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      OSRListFromNode osrNode = (OSRListFromNode) loopNode.getRepeatingNode();
      osrNode.init(generator);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new BooleanList((boolean[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization(guards = {"isStringKind(getResultType())"})
  protected StringList doString(
      VirtualFrame frame,
      Object iterable,
      @Cached(value = "getFromLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      OSRListFromNode osrNode = (OSRListFromNode) loopNode.getRepeatingNode();
      osrNode.init(generator);
      loopNode.execute(frame);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(osrNode.getResult());
      toArrayLoopNode.execute(frame);
      return new StringList((String[]) osrToArrayNode.getResult());
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }

  @Specialization
  protected RawArrayList doObject(
      VirtualFrame frame,
      Object iterable,
      @Cached(value = "getFromLoopNode()", allowUncached = true, neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(inline = true) @Cached.Shared("getGenerator")
          IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) @Cached.Shared("initNode")
          GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) @Cached.Shared("closeNode")
          GeneratorNodes.GeneratorCloseNode generatorCloseNode) {
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      OSRListFromNode osrNode = (OSRListFromNode) loopNode.getRepeatingNode();
      osrNode.init(generator);
      loopNode.execute(frame);
      return new RawArrayList(osrNode.getResult());
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }
}
