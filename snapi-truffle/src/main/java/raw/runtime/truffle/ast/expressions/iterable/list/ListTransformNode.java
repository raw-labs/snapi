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
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.ast.expressions.iterable.list.osr.OSRListTransformNode;
import raw.runtime.truffle.runtime.list.*;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Transform")
@NodeChild("list")
@NodeChild("function")
@NodeField(name = "resultType", type = Rql2Type.class)
public abstract class ListTransformNode extends ExpressionNode {

  @Idempotent
  protected abstract Rql2Type getResultType();

  public static LoopNode getListTransformNode(Rql2Type resultType) {
    return Truffle.getRuntime().createLoopNode(new OSRListTransformNode(resultType));
  }

  @Specialization(guards = {"isByteKind(getResultType())"})
  protected static ByteList doByte(
      VirtualFrame frame,
      Object list,
      Object function,
      @Cached(
              value = "getListTransformNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("transformLoopNode")
          LoopNode transformLoopNode) {
    OSRListTransformNode node = (OSRListTransformNode) transformLoopNode.getRepeatingNode();
    node.init(list, function);
    transformLoopNode.execute(frame);
    byte[] values = (byte[]) node.getResult();
    return new ByteList(values);
  }

  @Specialization(guards = {"isShortKind(getResultType())"})
  protected static ShortList doShort(
      VirtualFrame frame,
      Object list,
      Object function,
      @Cached(
              value = "getListTransformNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("transformLoopNode")
          LoopNode transformLoopNode) {
    OSRListTransformNode node = (OSRListTransformNode) transformLoopNode.getRepeatingNode();
    node.init(list, function);
    transformLoopNode.execute(frame);
    short[] values = (short[]) node.getResult();
    return new ShortList(values);
  }

  @Specialization(guards = {"isIntKind(getResultType())"})
  protected static IntList doInt(
      VirtualFrame frame,
      Object list,
      Object function,
      @Cached(
              value = "getListTransformNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("transformLoopNode")
          LoopNode transformLoopNode) {
    OSRListTransformNode node = (OSRListTransformNode) transformLoopNode.getRepeatingNode();
    node.init(list, function);
    transformLoopNode.execute(frame);
    int[] values = (int[]) node.getResult();
    return new IntList(values);
  }

  @Specialization(guards = {"isLongKind(getResultType())"})
  protected static LongList doLong(
      VirtualFrame frame,
      Object list,
      Object function,
      @Cached(
              value = "getListTransformNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("transformLoopNode")
          LoopNode transformLoopNode) {
    OSRListTransformNode node = (OSRListTransformNode) transformLoopNode.getRepeatingNode();
    node.init(list, function);
    transformLoopNode.execute(frame);
    long[] values = (long[]) node.getResult();
    return new LongList(values);
  }

  @Specialization(guards = {"isFloatKind(getResultType())"})
  protected static FloatList doFloat(
      VirtualFrame frame,
      Object list,
      Object function,
      @Cached(
              value = "getListTransformNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("transformLoopNode")
          LoopNode transformLoopNode) {
    OSRListTransformNode node = (OSRListTransformNode) transformLoopNode.getRepeatingNode();
    node.init(list, function);
    transformLoopNode.execute(frame);
    float[] values = (float[]) node.getResult();
    return new FloatList(values);
  }

  @Specialization(guards = {"isDoubleKind(getResultType())"})
  protected static DoubleList doDouble(
      VirtualFrame frame,
      Object list,
      Object function,
      @Cached(
              value = "getListTransformNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("transformLoopNode")
          LoopNode transformLoopNode) {
    OSRListTransformNode node = (OSRListTransformNode) transformLoopNode.getRepeatingNode();
    node.init(list, function);
    transformLoopNode.execute(frame);
    double[] values = (double[]) node.getResult();
    return new DoubleList(values);
  }

  @Specialization(guards = {"isBooleanKind(getResultType())"})
  protected BooleanList doBoolean(
      VirtualFrame frame,
      Object list,
      Object function,
      @Cached(
              value = "getListTransformNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("transformLoopNode")
          LoopNode transformLoopNode) {
    OSRListTransformNode node = (OSRListTransformNode) transformLoopNode.getRepeatingNode();
    node.init(list, function);
    transformLoopNode.execute(frame);
    boolean[] values = (boolean[]) node.getResult();
    return new BooleanList(values);
  }

  @Specialization(guards = {"isStringKind(getResultType())"})
  protected static StringList doString(
      VirtualFrame frame,
      Object list,
      Object function,
      @Cached(
              value = "getListTransformNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("transformLoopNode")
          LoopNode transformLoopNode) {
    OSRListTransformNode node = (OSRListTransformNode) transformLoopNode.getRepeatingNode();
    node.init(list, function);
    transformLoopNode.execute(frame);
    String[] values = (String[]) node.getResult();
    return new StringList(values);
  }

  @Specialization
  protected ObjectList doObject(
      VirtualFrame frame,
      Object list,
      Object function,
      @Cached(
              value = "getListTransformNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("transformLoopNode")
          LoopNode transformLoopNode) {
    OSRListTransformNode node = (OSRListTransformNode) transformLoopNode.getRepeatingNode();
    node.init(list, function);
    transformLoopNode.execute(frame);
    Object[] values = (Object[]) node.getResult();
    return new ObjectList(values);
  }
}
