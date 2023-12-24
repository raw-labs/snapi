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

package raw.runtime.truffle.runtime.list;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.ArrayList;
import java.util.Arrays;
import raw.runtime.truffle.runtime.iterable.list.ListIterable;
import raw.runtime.truffle.runtime.operators.OperatorNodes;

public class ListNodes {

  @NodeInfo(shortName = "List.IsElementReadable")
  @GenerateUncached
  public abstract static class IsElementReadableNode extends Node {

    public abstract boolean execute(Object list, int index);

    @Specialization
    static boolean isElementReadable(BooleanList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(ByteList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(DoubleList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(FloatList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(IntList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(LongList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(ObjectList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(ShortList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(StringList list, int index) {
      return list.isElementReadable(index);
    }

    @Specialization
    static boolean isElementReadable(RawArrayList list, int index) {
      return list.isElementReadable(index);
    }
  }

  @NodeInfo(shortName = "List.Get")
  @GenerateUncached
  public abstract static class GetNode extends Node {

    public abstract Object execute(Object list, long index);

    @Specialization
    static boolean get(BooleanList list, long index) {
      return list.get(index);
    }

    @Specialization
    static byte get(ByteList list, long index) {
      return list.get(index);
    }

    @Specialization
    static double get(DoubleList list, long index) {
      return list.get(index);
    }

    @Specialization
    static float get(FloatList list, long index) {
      return list.get(index);
    }

    @Specialization
    static int get(IntList list, long index) {
      return list.get(index);
    }

    @Specialization
    static long get(LongList list, long index) {
      return list.get(index);
    }

    @Specialization
    static Object get(ObjectList list, long index) {
      return list.get(index);
    }

    @Specialization
    static short get(ShortList list, long index) {
      return list.get(index);
    }

    @Specialization
    static String get(StringList list, long index) {
      return list.get(index);
    }

    @Specialization
    static Object get(RawArrayList list, long index) {
      return list.get(index);
    }
  }

  @NodeInfo(shortName = "List.Size")
  @GenerateUncached
  public abstract static class SizeNode extends Node {

    public abstract long execute(Object list);

    @Specialization
    static long size(BooleanList list) {
      return list.size();
    }

    @Specialization
    static long size(ByteList list) {
      return list.size();
    }

    @Specialization
    static long size(DoubleList list) {
      return list.size();
    }

    @Specialization
    static long size(FloatList list) {
      return list.size();
    }

    @Specialization
    static long size(IntList list) {
      return list.size();
    }

    @Specialization
    static long size(LongList list) {
      return list.size();
    }

    @Specialization
    static long size(ObjectList list) {
      return list.size();
    }

    @Specialization
    static long size(ShortList list) {
      return list.size();
    }

    @Specialization
    static long size(StringList list) {
      return list.size();
    }

    @Specialization
    static long size(RawArrayList list) {
      return list.size();
    }
  }

  @NodeInfo(shortName = "List.ToIterable")
  @GenerateUncached
  public abstract static class ToIterableNode extends Node {

    public abstract Object execute(Object list);

    @Specialization
    static ListIterable toIterable(BooleanList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(ByteList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(DoubleList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(FloatList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(IntList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(LongList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(ObjectList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(ShortList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(StringList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable toIterable(RawArrayList list) {
      return list.toIterable();
    }
  }

  @NodeInfo(shortName = "List.Sort")
  @GenerateUncached
  public abstract static class SortNode extends Node {

    public abstract Object execute(Object list);

    @Specialization
    static BooleanList sort(BooleanList list) {
      return list.sort();
    }

    @Specialization
    static ByteList sort(ByteList list) {
      return list.sort();
    }

    @Specialization
    static DoubleList sort(DoubleList list) {
      return list.sort();
    }

    @Specialization
    static FloatList sort(FloatList list) {
      return list.sort();
    }

    @Specialization
    static IntList sort(IntList list) {
      return list.sort();
    }

    @Specialization
    static LongList sort(LongList list) {
      return list.sort();
    }

    @Specialization
    static ObjectList sort(ObjectList list, @Cached OperatorNodes.CompareNode compare) {
      Object[] result = list.getInnerList().clone();
      Arrays.sort(result, compare::execute);
      return new ObjectList(result);
    }

    @Specialization
    static ShortList sort(ShortList list) {
      return list.sort();
    }

    @Specialization
    static StringList sort(StringList list) {
      return list.sort();
    }

    @Specialization
    static RawArrayList sort(RawArrayList list, @Cached OperatorNodes.CompareNode compare) {
      ArrayList<Object> aList = new ArrayList<>(list.getInnerList());
      aList.sort(compare::execute);
      return new RawArrayList(aList);
    }
  }

  @NodeInfo(shortName = "List.Take")
  @GenerateUncached
  public abstract static class TakeNode extends Node {

    public abstract Object execute(Object list, int num);

    @Specialization
    static BooleanList take(BooleanList list, int num) {
      return list.take(num);
    }

    @Specialization
    static ByteList take(ByteList list, int num) {
      return list.take(num);
    }

    @Specialization
    static DoubleList take(DoubleList list, int num) {
      return list.take(num);
    }

    @Specialization
    static FloatList take(FloatList list, int num) {
      return list.take(num);
    }

    @Specialization
    static IntList take(IntList list, int num) {
      return list.take(num);
    }

    @Specialization
    static LongList take(LongList list, int num) {
      return list.take(num);
    }

    @Specialization
    static ObjectList take(ObjectList list, int num) {
      return list.take(num);
    }

    @Specialization
    static ShortList take(ShortList list, int num) {
      return list.take(num);
    }

    @Specialization
    static StringList take(StringList list, int num) {
      return list.take(num);
    }

    @Specialization
    static RawArrayList take(RawArrayList list, int num) {
      return list.take(num);
    }
  }
}
