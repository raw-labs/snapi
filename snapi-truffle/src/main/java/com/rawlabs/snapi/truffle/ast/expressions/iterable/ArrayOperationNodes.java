/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.ast.expressions.iterable;

import com.oracle.truffle.api.dsl.GenerateInline;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.frontend.rql2.source.Rql2Type;
import com.rawlabs.snapi.truffle.ast.TypeGuards;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;
import com.rawlabs.snapi.truffle.runtime.list.*;
import java.util.ArrayList;

public class ArrayOperationNodes {

  @NodeInfo(shortName = "ArrayOperation.Build")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(TypeGuards.class)
  public abstract static class ArrayBuildNode extends Node {

    public abstract Object execute(Node node, Rql2Type resultType, int size);

    @Specialization(guards = "isByteKind(resultType)")
    static byte[] buildByte(Node node, Rql2Type resultType, int size) {
      return new byte[size];
    }

    @Specialization(guards = "isShortKind(resultType)")
    static short[] buildShort(Node node, Rql2Type resultType, int size) {
      return new short[size];
    }

    @Specialization(guards = "isIntKind(resultType)")
    static int[] buildInt(Node node, Rql2Type resultType, int size) {
      return new int[size];
    }

    @Specialization(guards = "isLongKind(resultType)")
    static long[] buildLong(Node node, Rql2Type resultType, int size) {
      return new long[size];
    }

    @Specialization(guards = "isFloatKind(resultType)")
    static float[] buildFloat(Node node, Rql2Type resultType, int size) {
      return new float[size];
    }

    @Specialization(guards = "isDoubleKind(resultType)")
    static double[] buildDouble(Node node, Rql2Type resultType, int size) {
      return new double[size];
    }

    @Specialization(guards = "isBooleanKind(resultType)")
    static boolean[] buildBoolean(Node node, Rql2Type resultType, int size) {
      return new boolean[size];
    }

    @Specialization(guards = "isStringKind(resultType)")
    static String[] buildString(Node node, Rql2Type resultType, int size) {
      return new String[size];
    }

    @Specialization
    static Object[] buildObject(Node node, Rql2Type resultType, int size) {
      return new Object[size];
    }
  }

  @NodeInfo(shortName = "ArrayOperation.BuildList")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(TypeGuards.class)
  public abstract static class ArrayBuildListNode extends Node {

    public abstract Object execute(Node node, Object array);

    @Specialization
    static ByteList buildByte(Node node, byte[] array) {
      return new ByteList(array);
    }

    @Specialization
    static ShortList buildShort(Node node, short[] array) {
      return new ShortList(array);
    }

    @Specialization
    static IntList buildInt(Node node, int[] array) {
      return new IntList(array);
    }

    @Specialization
    static LongList buildLong(Node node, long[] array) {
      return new LongList(array);
    }

    @Specialization
    static FloatList buildFloat(Node node, float[] array) {
      return new FloatList(array);
    }

    @Specialization
    static DoubleList buildDouble(Node node, double[] array) {
      return new DoubleList(array);
    }

    @Specialization
    static BooleanList buildBoolean(Node node, boolean[] array) {
      return new BooleanList(array);
    }

    @Specialization
    static StringList buildString(Node node, String[] array) {
      return new StringList(array);
    }

    @Specialization
    static ObjectList buildObject(Node node, Object[] array) {
      return new ObjectList(array);
    }

    @Specialization
    static TruffleArrayList buildObject(Node node, Object array) {
      try {
        @SuppressWarnings("unchecked")
        ArrayList<Object> arrayList = (ArrayList<Object>) array;
        return new TruffleArrayList(arrayList);
      } catch (ClassCastException e) {
        throw new TruffleInternalErrorException(e.getMessage(), e);
      }
    }
  }

  @NodeInfo(shortName = "ArrayOperation.BuildList")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(TypeGuards.class)
  public abstract static class ArraySetArrayItemNode extends Node {

    public abstract void execute(Node node, Object array, Object item, int idx);

    @Specialization
    static void buildByte(Node node, byte[] array, byte item, int idx) {
      array[idx] = item;
    }

    @Specialization
    static void buildShort(Node node, short[] array, short item, int idx) {
      array[idx] = item;
    }

    @Specialization
    static void buildInt(Node node, int[] array, int item, int idx) {
      array[idx] = item;
    }

    @Specialization
    static void buildLong(Node node, long[] array, long item, int idx) {
      array[idx] = item;
    }

    @Specialization
    static void buildFloat(Node node, float[] array, float item, int idx) {
      array[idx] = item;
    }

    @Specialization
    static void buildDouble(Node node, double[] array, double item, int idx) {
      array[idx] = item;
    }

    @Specialization
    static void buildBoolean(Node node, boolean[] array, boolean item, int idx) {
      array[idx] = item;
    }

    @Specialization
    static void buildString(Node node, String[] array, String item, int idx) {
      array[idx] = item;
    }

    @Specialization
    static void buildObject(Node node, Object[] array, Object item, int idx) {
      array[idx] = item;
    }
  }
}
