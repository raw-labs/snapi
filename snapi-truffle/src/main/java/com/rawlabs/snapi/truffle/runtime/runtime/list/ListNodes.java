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

package com.rawlabs.snapi.truffle.runtime.runtime.list;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateInline;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.runtime.iterable.list.ListIterable;
import com.rawlabs.snapi.truffle.runtime.runtime.operators.OperatorNodes;
import java.util.ArrayList;
import java.util.Arrays;

public class ListNodes {

  @NodeInfo(shortName = "List.IsElementReadable")
  @GenerateUncached
  @GenerateInline
  public abstract static class IsElementReadableNode extends Node {

    public abstract boolean execute(Node node, Object list, int index);

    @Specialization
    static boolean isElementReadable(Node node, BooleanList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(Node node, ByteList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(Node node, DoubleList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(Node node, FloatList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(Node node, IntList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(Node node, LongList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(Node node, ObjectList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(Node node, ShortList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(Node node, StringList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(Node node, TruffleArrayList list, int index) {
      return list.isElementReadable(index);
    }
  }

  @NodeInfo(shortName = "List.Get")
  @GenerateUncached
  @GenerateInline
  public abstract static class GetNode extends Node {

    public abstract Object execute(Node node, Object list, long index);

    @Specialization
    static boolean get(Node node, BooleanList list, long index) {
      return list.get(index);
    }

    @Specialization
    static byte get(Node node, ByteList list, long index) {
      return list.get(index);
    }

    @Specialization
    static double get(Node node, DoubleList list, long index) {
      return list.get(index);
    }

    @Specialization
    static float get(Node node, FloatList list, long index) {
      return list.get(index);
    }

    @Specialization
    static int get(Node node, IntList list, long index) {
      return list.get(index);
    }

    @Specialization
    static long get(Node node, LongList list, long index) {
      return list.get(index);
    }

    @Specialization
    static Object get(Node node, ObjectList list, long index) {
      return list.get(index);
    }

    @Specialization
    static short get(Node node, ShortList list, long index) {
      return list.get(index);
    }

    @Specialization
    static String get(Node node, StringList list, long index) {
      return list.get(index);
    }

    @Specialization
    static Object get(Node node, TruffleArrayList list, long index) {
      return list.get(index);
    }
  }

  @NodeInfo(shortName = "List.Size")
  @GenerateUncached
  @GenerateInline
  public abstract static class SizeNode extends Node {

    public abstract long execute(Node node, Object list);

    @Specialization
    static long size(Node node, BooleanList list) {
      return list.size();
    }

    @Specialization
    static long size(Node node, ByteList list) {
      return list.size();
    }

    @Specialization
    static long size(Node node, DoubleList list) {
      return list.size();
    }

    @Specialization
    static long size(Node node, FloatList list) {
      return list.size();
    }

    @Specialization
    static long size(Node node, IntList list) {
      return list.size();
    }

    @Specialization
    static long size(Node node, LongList list) {
      return list.size();
    }

    @Specialization
    static long size(Node node, ObjectList list) {
      return list.size();
    }

    @Specialization
    static long size(Node node, ShortList list) {
      return list.size();
    }

    @Specialization
    static long size(Node node, StringList list) {
      return list.size();
    }

    @Specialization
    static long size(Node node, TruffleArrayList list) {
      return list.size();
    }
  }

  @NodeInfo(shortName = "List.ToIterable")
  @GenerateUncached
  @GenerateInline
  public abstract static class ToIterableNode extends Node {

    public abstract Object execute(Node node, Object list);

    @Specialization
    static ListIterable toIterable(Node node, BooleanList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(Node node, ByteList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(Node node, DoubleList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(Node node, FloatList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(Node node, IntList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(Node node, LongList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(Node node, ObjectList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(Node node, ShortList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(Node node, StringList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(Node node, TruffleArrayList list) {
      return list.toIterable();
    }
  }

  @NodeInfo(shortName = "List.Sort")
  @GenerateUncached
  @GenerateInline
  public abstract static class SortNode extends Node {

    public abstract Object execute(Node node, Object list);

    @Specialization
    static BooleanList sort(Node node, BooleanList list) {
      return list.sort();
    }

    @Specialization
    static ByteList sort(Node node, ByteList list) {
      return list.sort();
    }

    @Specialization
    static DoubleList sort(Node node, DoubleList list) {
      return list.sort();
    }

    @Specialization
    static FloatList sort(Node node, FloatList list) {
      return list.sort();
    }

    @Specialization
    static IntList sort(Node node, IntList list) {
      return list.sort();
    }

    @Specialization
    static LongList sort(Node node, LongList list) {
      return list.sort();
    }

    @Specialization
    static ObjectList sort(
        Node node,
        ObjectList list,
        @Cached(inline = false) @Cached.Shared("compareUninlined")
            OperatorNodes.CompareUninlinedNode compare) {
      Object[] result = list.getInnerList().clone();
      Arrays.sort(result, compare::execute);
      return new ObjectList(result);
    }

    @Specialization
    static ShortList sort(Node node, ShortList list) {
      return list.sort();
    }

    @Specialization
    static StringList sort(Node node, StringList list) {
      return list.sort();
    }

    @Specialization
    static TruffleArrayList sort(
        Node node,
        TruffleArrayList list,
        @Cached(inline = false) @Cached.Shared("compareUninlined")
            OperatorNodes.CompareUninlinedNode compare) {
      ArrayList<Object> aList = new ArrayList<>(list.getInnerList());
      aList.sort(compare::execute);
      return new TruffleArrayList(aList);
    }
  }

  @NodeInfo(shortName = "List.Take")
  @GenerateUncached
  @GenerateInline
  public abstract static class TakeNode extends Node {

    public abstract Object execute(Node node, Object list, int num);

    @Specialization
    static BooleanList take(Node node, BooleanList list, int num) {
      return list.take(num);
    }

    @Specialization
    static ByteList take(Node node, ByteList list, int num) {
      return list.take(num);
    }

    @Specialization
    static DoubleList take(Node node, DoubleList list, int num) {
      return list.take(num);
    }

    @Specialization
    static FloatList take(Node node, FloatList list, int num) {
      return list.take(num);
    }

    @Specialization
    static IntList take(Node node, IntList list, int num) {
      return list.take(num);
    }

    @Specialization
    static LongList take(Node node, LongList list, int num) {
      return list.take(num);
    }

    @Specialization
    static ObjectList take(Node node, ObjectList list, int num) {
      return list.take(num);
    }

    @Specialization
    static ShortList take(Node node, ShortList list, int num) {
      return list.take(num);
    }

    @Specialization
    static StringList take(Node node, StringList list, int num) {
      return list.take(num);
    }

    @Specialization
    static TruffleArrayList take(Node node, TruffleArrayList list, int num) {
      return list.take(num);
    }
  }
}
