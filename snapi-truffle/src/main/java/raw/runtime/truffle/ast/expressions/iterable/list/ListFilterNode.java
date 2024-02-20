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

import java.util.ArrayList;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Filter")
@NodeChild("list")
@NodeChild("function")
@NodeField(name = "resultType", type = Rql2Type.class)
public abstract class ListFilterNode extends ExpressionNode {

  @Idempotent
  protected abstract Rql2Type getResultType();

  public static String generatorKey = "generator";
  public static String functionKey = "function";
  public static String llistKey = "llist";

  public static int[] getSlots(VirtualFrame frame) {
    return new int[] {
      frame.getFrameDescriptor().findOrAddAuxiliarySlot(generatorKey),
      frame.getFrameDescriptor().findOrAddAuxiliarySlot(functionKey),
      frame.getFrameDescriptor().findOrAddAuxiliarySlot(llistKey)
    };
  }

  public static LoopNode getFilterLoopNode(int[] slots) {
    return Truffle.getRuntime().createLoopNode(new OSRListFilterNode(slots[0], slots[1], slots[2]));
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
      @Cached(allowUncached = true, neverDefault = true, value = "getSlots(frame)", dimensions = 1)
          int[] slots,
      @Cached(value = "getFilterLoopNode(slots)", allowUncached = true) LoopNode loopNode,
      @Cached(value = "getToArrayLoopNode(getResultType())", allowUncached = true)
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
      frame.setAuxiliarySlot(slots[0], generator);
      frame.setAuxiliarySlot(slots[1], function);
      frame.setAuxiliarySlot(slots[2], new ArrayList<>());
      loopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getAuxiliarySlot(slots[2]);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(llist);
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
      @Cached(allowUncached = true, neverDefault = true, value = "getSlots(frame)", dimensions = 1)
          int[] slots,
      @Cached(value = "getFilterLoopNode(slots)", allowUncached = true) LoopNode loopNode,
      @Cached(value = "getToArrayLoopNode(getResultType())", allowUncached = true)
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
      frame.setAuxiliarySlot(slots[0], generator);
      frame.setAuxiliarySlot(slots[1], function);
      frame.setAuxiliarySlot(slots[2], new ArrayList<>());
      loopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getAuxiliarySlot(slots[2]);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(llist);
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
      @Cached(allowUncached = true, neverDefault = true, value = "getSlots(frame)", dimensions = 1)
          int[] slots,
      @Cached(value = "getFilterLoopNode(slots)", allowUncached = true) LoopNode loopNode,
      @Cached(value = "getToArrayLoopNode(getResultType())", allowUncached = true)
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
      frame.setAuxiliarySlot(slots[0], generator);
      frame.setAuxiliarySlot(slots[1], function);
      frame.setAuxiliarySlot(slots[2], new ArrayList<>());
      loopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getAuxiliarySlot(slots[2]);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(llist);
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
      @Cached(allowUncached = true, neverDefault = true, value = "getSlots(frame)", dimensions = 1)
          int[] slots,
      @Cached(value = "getFilterLoopNode(slots)", allowUncached = true) LoopNode loopNode,
      @Cached(value = "getToArrayLoopNode(getResultType())", allowUncached = true)
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
      frame.setAuxiliarySlot(slots[0], generator);
      frame.setAuxiliarySlot(slots[1], function);
      frame.setAuxiliarySlot(slots[2], new ArrayList<>());
      loopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getAuxiliarySlot(slots[2]);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(llist);
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
      @Cached(allowUncached = true, neverDefault = true, value = "getSlots(frame)", dimensions = 1)
          int[] slots,
      @Cached(value = "getFilterLoopNode(slots)", allowUncached = true) LoopNode loopNode,
      @Cached(value = "getToArrayLoopNode(getResultType())", allowUncached = true)
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
      frame.setAuxiliarySlot(slots[0], generator);
      frame.setAuxiliarySlot(slots[1], function);
      frame.setAuxiliarySlot(slots[2], new ArrayList<>());
      loopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getAuxiliarySlot(slots[2]);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(llist);
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
      @Cached(allowUncached = true, neverDefault = true, value = "getSlots(frame)", dimensions = 1)
          int[] slots,
      @Cached(value = "getFilterLoopNode(slots)", allowUncached = true) LoopNode loopNode,
      @Cached(value = "getToArrayLoopNode(getResultType())", allowUncached = true)
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
      frame.setAuxiliarySlot(slots[0], generator);
      frame.setAuxiliarySlot(slots[1], function);
      frame.setAuxiliarySlot(slots[2], new ArrayList<>());
      loopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getAuxiliarySlot(slots[2]);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(llist);
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
      @Cached(allowUncached = true, neverDefault = true, value = "getSlots(frame)", dimensions = 1)
          int[] slots,
      @Cached(value = "getFilterLoopNode(slots)", allowUncached = true) LoopNode loopNode,
      @Cached(value = "getToArrayLoopNode(getResultType())", allowUncached = true)
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
      frame.setAuxiliarySlot(slots[0], generator);
      frame.setAuxiliarySlot(slots[1], function);
      frame.setAuxiliarySlot(slots[2], new ArrayList<>());
      loopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getAuxiliarySlot(slots[2]);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(llist);
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
      @Cached(allowUncached = true, neverDefault = true, value = "getSlots(frame)", dimensions = 1)
          int[] slots,
      @Cached(value = "getFilterLoopNode(slots)", allowUncached = true) LoopNode loopNode,
      @Cached(value = "getToArrayLoopNode(getResultType())", allowUncached = true)
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
      frame.setAuxiliarySlot(slots[0], generator);
      frame.setAuxiliarySlot(slots[1], function);
      frame.setAuxiliarySlot(slots[2], new ArrayList<>());
      loopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getAuxiliarySlot(slots[2]);
      OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
      osrToArrayNode.init(llist);
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
      @Cached(allowUncached = true, neverDefault = true, value = "getSlots(frame)", dimensions = 1)
          int[] slots,
      @Cached(value = "getFilterLoopNode(slots)", allowUncached = true) LoopNode loopNode,
      @Cached(value = "getToArrayLoopNode(getResultType())", allowUncached = true)
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
      frame.setAuxiliarySlot(slots[0], generator);
      frame.setAuxiliarySlot(slots[1], function);
      frame.setAuxiliarySlot(slots[2], new ArrayList<>());
      loopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getAuxiliarySlot(slots[2]);
      return new RawArrayList(llist);
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }
}
