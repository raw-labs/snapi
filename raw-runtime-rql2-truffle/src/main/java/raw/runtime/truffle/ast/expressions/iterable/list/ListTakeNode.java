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
import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.boundary.BoundaryNodes;
import raw.runtime.truffle.runtime.list.*;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Take")
@NodeChild("list")
@NodeChild("num")
@NodeField(name = "resultType", type = Rql2Type.class)
public abstract class ListTakeNode extends ExpressionNode {

  protected abstract Rql2Type getResultType();

  @Specialization(
      guards = {"isByteKind(getResultType())"},
      limit = "3")
  protected ByteList doByte(
      Object list,
      long num,
      @Cached BoundaryNodes.CopyArrayNode copyArrayNode,
      @CachedLibrary("list") ListLibrary lists) {
    byte[] innerList = (byte[]) lists.getInnerList(list);
    if (num >= innerList.length) {
      return (ByteList) list;
    }
    byte[] result = new byte[(int) num];
    copyArrayNode.execute(innerList, 0, result, 0, result.length);
    return new ByteList(result);
  }

  @Specialization(
      guards = {"isShortKind(getResultType())"},
      limit = "3")
  protected ShortList doShort(
      Object list,
      long num,
      @Cached BoundaryNodes.CopyArrayNode copyArrayNode,
      @CachedLibrary("list") ListLibrary lists) {
    short[] innerList = (short[]) lists.getInnerList(list);
    if (num >= innerList.length) {
      return (ShortList) list;
    }
    short[] result = new short[(int) num];
    copyArrayNode.execute(innerList, 0, result, 0, result.length);
    return new ShortList(result);
  }

  @Specialization(
      guards = {"isIntKind(getResultType())"},
      limit = "3")
  protected IntList doInt(
      Object list,
      long num,
      @Cached BoundaryNodes.CopyArrayNode copyArrayNode,
      @CachedLibrary("list") ListLibrary lists) {
    int[] innerList = (int[]) lists.getInnerList(list);
    if (num >= innerList.length) {
      return (IntList) list;
    }
    int[] result = new int[(int) num];
    copyArrayNode.execute(innerList, 0, result, 0, result.length);
    return new IntList(result);
  }

  @Specialization(
      guards = {"isLongKind(getResultType())"},
      limit = "3")
  protected LongList doLong(
      Object list,
      int num,
      @Cached BoundaryNodes.CopyArrayNode copyArrayNode,
      @CachedLibrary("list") ListLibrary lists) {
    long[] innerList = (long[]) lists.getInnerList(list);
    if (num >= innerList.length) {
      return (LongList) list;
    }
    long[] result = new long[num];
    copyArrayNode.execute(innerList, 0, result, 0, result.length);
    return new LongList(result);
  }

  @Specialization(
      guards = {"isFloatKind(getResultType())"},
      limit = "3")
  protected FloatList doFloat(
      Object list,
      long num,
      @Cached BoundaryNodes.CopyArrayNode copyArrayNode,
      @CachedLibrary("list") ListLibrary lists) {
    float[] innerList = (float[]) lists.getInnerList(list);
    if (num >= innerList.length) {
      return (FloatList) list;
    }
    float[] result = new float[(int) num];
    copyArrayNode.execute(innerList, 0, result, 0, result.length);
    return new FloatList(result);
  }

  @Specialization(
      guards = {"isDoubleKind(getResultType())"},
      limit = "3")
  protected DoubleList doDouble(
      Object list,
      long num,
      @Cached BoundaryNodes.CopyArrayNode copyArrayNode,
      @CachedLibrary("list") ListLibrary lists) {
    double[] innerList = (double[]) lists.getInnerList(list);
    if (num >= innerList.length) {
      return (DoubleList) list;
    }
    double[] result = new double[(int) num];
    copyArrayNode.execute(innerList, 0, result, 0, result.length);
    return new DoubleList(result);
  }

  @Specialization(
      guards = {"isBooleanKind(getResultType())"},
      limit = "3")
  protected BooleanList doBoolean(
      Object list,
      long num,
      @Cached BoundaryNodes.CopyArrayNode copyArrayNode,
      @CachedLibrary("list") ListLibrary lists) {
    boolean[] innerList = (boolean[]) lists.getInnerList(list);
    if (num >= innerList.length) {
      return (BooleanList) list;
    }
    boolean[] result = new boolean[(int) num];
    copyArrayNode.execute(innerList, 0, result, 0, result.length);
    return new BooleanList(result);
  }

  @Specialization(
      guards = {"isStringKind(getResultType())"},
      limit = "3")
  protected StringList doString(
      Object list,
      long num,
      @Cached BoundaryNodes.CopyArrayNode copyArrayNode,
      @CachedLibrary("list") ListLibrary lists) {
    String[] innerList = (String[]) lists.getInnerList(list);
    if (num >= innerList.length) {
      return (StringList) list;
    }
    String[] result = new String[(int) num];
    copyArrayNode.execute(innerList, 0, result, 0, result.length);
    return new StringList(result);
  }

  @Specialization(limit = "3")
  protected ObjectList doObject(
      Object list,
      long num,
      @Cached BoundaryNodes.CopyArrayNode copyArrayNode,
      @CachedLibrary("list") ListLibrary lists) {
    Object[] innerList = (Object[]) lists.getInnerList(list);
    if (num >= innerList.length) {
      return (ObjectList) list;
    }
    Object[] result = new Object[(int) num];
    copyArrayNode.execute(innerList, 0, result, 0, result.length);
    return new ObjectList(result);
  }
}
