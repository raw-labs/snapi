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

package com.rawlabs.snapi.truffle.runtime.operators;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.temporals.interval_package.IntervalNodes;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.Nullable;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.Tryable;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodes;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;
import com.rawlabs.snapi.truffle.runtime.primitives.*;
import com.rawlabs.snapi.truffle.runtime.record.DuplicateKeyRecord;
import com.rawlabs.snapi.truffle.runtime.record.PureRecord;
import com.rawlabs.snapi.truffle.runtime.record.RecordNodes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class OperatorNodes {

  @NodeInfo(shortName = "Operator.CompareUninlined")
  @GenerateUncached
  @GenerateInline(false)
  @ImportStatic(value = {Nullable.class, Tryable.class})
  public abstract static class CompareUninlinedNode extends Node {

    public abstract int execute(Object obj1, Object obj2);

    @Specialization
    static int doBoolean(
        Object left,
        Object right,
        @Bind("$node") Node thisNode,
        @Cached(inline = true) CompareNode compare) {
      return compare.execute(thisNode, left, right);
    }
  }

  @NodeInfo(shortName = "Operator.CompareKeys")
  @GenerateUncached
  @GenerateInline
  public abstract static class CompareKeys extends Node {

    public abstract int execute(Node node, Object[] left, Object[] right, int[] orderings);

    @Specialization
    static int doKeys(
        Node node,
        Object[] left,
        Object[] right,
        int[] orderings,
        @Bind("$node") Node thisNode,
        @Cached CompareNode compare) {

      for (int i = 0; i < left.length; i++) {
        int result = compare.execute(thisNode, left[i], right[i]);
        if (result != 0) {
          return result * orderings[i];
        }
      }
      return 0;
    }
  }

  @NodeInfo(shortName = "Operator.Compare")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(value = {Nullable.class, Tryable.class})
  public abstract static class CompareNode extends Node {

    public abstract int execute(Node node, Object obj1, Object obj2);

    @Specialization
    static int doBoolean(Node node, boolean left, boolean right) {
      return Boolean.compare(left, right);
    }

    @Specialization
    static int doByte(Node node, byte left, byte right) {
      return Byte.compare(left, right);
    }

    @Specialization
    static int doShort(Node node, short left, short right) {
      return Short.compare(left, right);
    }

    @Specialization
    static int doInt(Node node, int left, int right) {
      return Integer.compare(left, right);
    }

    @Specialization
    static int doLong(Node node, long left, long right) {
      return Long.compare(left, right);
    }

    @Specialization
    @TruffleBoundary
    static int doFloat(Node node, float left, float right) {
      return Float.compare(left, right);
    }

    @Specialization
    @TruffleBoundary
    static int doDouble(Node node, double left, double right) {
      return Double.compare(left, right);
    }

    @Specialization
    @TruffleBoundary
    static int doDecimal(Node node, DecimalObject left, DecimalObject right) {
      return left.getBigDecimal().compareTo(right.getBigDecimal());
    }

    @Specialization
    @TruffleBoundary
    static int doString(Node node, String left, String right) {
      return left.compareTo(right);
    }

    @Specialization
    @TruffleBoundary
    static int doDate(Node node, DateObject left, DateObject right) {
      LocalDate leftDate = left.getDate();
      LocalDate rightDate = right.getDate();
      if (leftDate.isBefore(rightDate)) {
        return -1;
      } else if (leftDate.isAfter(rightDate)) {
        return 1;
      } else {
        return 0;
      }
    }

    @Specialization
    @TruffleBoundary
    static int doTimestamp(Node node, TimestampObject left, TimestampObject right) {
      LocalDateTime leftDate = left.getTimestamp();
      LocalDateTime rightDate = right.getTimestamp();
      if (leftDate.isBefore(rightDate)) {
        return -1;
      } else if (leftDate.isAfter(rightDate)) {
        return 1;
      } else {
        return 0;
      }
    }

    @Specialization
    @TruffleBoundary
    static int doTime(Node node, TimeObject left, TimeObject right) {
      LocalTime leftTime = left.getTime();
      LocalTime rightTime = right.getTime();
      if (leftTime.isBefore(rightTime)) {
        return -1;
      } else if (leftTime.isAfter(rightTime)) {
        return 1;
      } else {
        return 0;
      }
    }

    @Specialization
    @TruffleBoundary
    static int doInterval(
        Node node,
        IntervalObject left,
        IntervalObject right,
        @Bind("$node") Node thisNode,
        @Cached IntervalNodes.IntervalCompareNode compareNode) {
      return compareNode.execute(thisNode, left, right);
    }

    @Specialization
    static int doRecord(
        Node node,
        PureRecord left,
        PureRecord right,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("compare") CompareNode compare,
        @Cached @Cached.Shared("getKey") RecordNodes.GetKeysNode getKeysNode,
        @Cached @Cached.Shared("getValue") RecordNodes.GetValueNode getValueNode) {
      Object[] leftKeys = getKeysNode.execute(thisNode, left);
      Object[] rightKeys = getKeysNode.execute(thisNode, right);
      if (leftKeys.length > rightKeys.length) {
        return 1;
      } else if (leftKeys.length < rightKeys.length) {
        return -1;
      }
      for (int i = 0; i < leftKeys.length; i++) {
        String leftKey = (String) leftKeys[i];
        String rightKey = (String) rightKeys[i];
        int result = compare.execute(thisNode, leftKey, rightKey);
        if (result != 0) {
          return result;
        }
        Object leftValue = getValueNode.execute(thisNode, left, leftKey);
        Object rightValue = getValueNode.execute(thisNode, right, rightKey);
        result = compare.execute(thisNode, leftValue, rightValue);
        if (result != 0) {
          return result;
        }
      }
      return 0;
    }

    @Specialization
    static int doRecord(
        Node node,
        DuplicateKeyRecord left,
        DuplicateKeyRecord right,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("compare") CompareNode compare,
        @Cached @Cached.Shared("getKey") RecordNodes.GetKeysNode getKeysNode,
        @Cached @Cached.Shared("getValue") RecordNodes.GetValueNode getValueNode) {
      Object[] leftKeys = getKeysNode.execute(thisNode, left);
      Object[] rightKeys = getKeysNode.execute(thisNode, right);
      if (leftKeys.length > rightKeys.length) {
        return 1;
      } else if (leftKeys.length < rightKeys.length) {
        return -1;
      }
      for (int i = 0; i < leftKeys.length; i++) {
        String leftKey = (String) leftKeys[i];
        String rightKey = (String) rightKeys[i];
        int result = compare.execute(thisNode, leftKey, rightKey);
        if (result != 0) {
          return result;
        }
        Object leftValue = getValueNode.execute(thisNode, left, leftKey);
        Object rightValue = getValueNode.execute(thisNode, right, rightKey);
        result = compare.execute(thisNode, leftValue, rightValue);
        if (result != 0) {
          return result;
        }
      }
      return 0;
    }

    @Specialization(guards = {"isError(left) || isError(right)"})
    static int doTryable(
        Node node,
        Object left,
        Object right,
        @Bind("$node") Node thisNode,
        @Cached TryableNullableNodes.GetErrorNode getErrorNode) {
      boolean leftIsFailure = Tryable.isError(left);
      boolean rightIsFailure = Tryable.isError(right);
      if (leftIsFailure && rightIsFailure) {
        return getErrorNode
            .execute(thisNode, left)
            .compareTo(getErrorNode.execute(thisNode, right));
      }
      if (leftIsFailure) {
        return -1;
      } else {
        return 1;
      }
    }

    @Specialization(guards = {"isNull(left) || isNull(right)"})
    static int doNullable(Node node, Object left, Object right) {
      // both are options
      boolean leftIsNull = Nullable.isNull(left);
      boolean rightIsNull = Nullable.isNull(right);

      if (leftIsNull && rightIsNull) {
        return 0;
      } else if (leftIsNull) {
        return -2;
      } else {
        return 2;
      }
    }

    @Specialization
    static int doIterable(
        Node node,
        Object left,
        Object right,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("compare") CompareNode compare,
        @Cached(inline = false) IterableNodes.GetGeneratorNode getGeneratorNodeLeft,
        @Cached(inline = false) IterableNodes.GetGeneratorNode getGeneratorNodeRight,
        @Cached(inline = false) GeneratorNodes.GeneratorHasNextNode hasNextNodeLeft,
        @Cached(inline = false) GeneratorNodes.GeneratorHasNextNode hasNextNodeRight,
        @Cached(inline = false) GeneratorNodes.GeneratorNextNode nextNodeLeft,
        @Cached(inline = false) GeneratorNodes.GeneratorNextNode nextNodeRight,
        @Cached(inline = false) GeneratorNodes.GeneratorInitNode initNodeLeft,
        @Cached(inline = false) GeneratorNodes.GeneratorInitNode initNodeRight,
        @Cached(inline = false) GeneratorNodes.GeneratorCloseNode closeNodeLeft,
        @Cached(inline = false) GeneratorNodes.GeneratorCloseNode closeNodeRight) {
      Object leftGenerator = getGeneratorNodeLeft.execute(thisNode, left);
      Object rightGenerator = getGeneratorNodeRight.execute(thisNode, right);
      try {
        initNodeLeft.execute(thisNode, leftGenerator);
        initNodeRight.execute(thisNode, rightGenerator);
        while (hasNextNodeLeft.execute(thisNode, leftGenerator)
            && hasNextNodeRight.execute(thisNode, rightGenerator)) {
          Object leftElement = nextNodeLeft.execute(thisNode, leftGenerator);
          Object rightElement = nextNodeRight.execute(thisNode, rightGenerator);
          int result = compare.execute(thisNode, leftElement, rightElement);
          if (result != 0) {
            return result;
          }
        }
        if (hasNextNodeLeft.execute(thisNode, leftGenerator)) {
          return 1;
        } else if (hasNextNodeRight.execute(thisNode, rightGenerator)) {
          return -1;
        } else {
          return 0;
        }
      } finally {
        closeNodeLeft.execute(thisNode, leftGenerator);
        closeNodeRight.execute(thisNode, rightGenerator);
      }
    }
  }

  @NodeInfo(shortName = "Operator.Add")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(value = {Nullable.class, Tryable.class})
  public abstract static class AddNode extends Node {

    public abstract Object execute(Node node, Object obj1, Object obj2);

    @Specialization
    static Object doByte(Node node, byte left, byte right) {
      return (byte) (left + right);
    }

    @Specialization
    static Object doShort(Node node, short left, short right) {
      return (short) (left + right);
    }

    @Specialization
    static Object doInt(Node node, int left, int right) {
      return left + right;
    }

    @Specialization
    static Object doLong(Node node, long left, long right) {
      return left + right;
    }

    @Specialization
    static Object doFloat(Node node, float left, float right) {
      return left + right;
    }

    @Specialization
    static Object doDouble(Node node, double left, double right) {
      return left + right;
    }

    @Specialization
    @TruffleBoundary
    static DecimalObject doDecimal(Node node, DecimalObject left, DecimalObject right) {
      return new DecimalObject(left.getBigDecimal().add(right.getBigDecimal()));
    }

    @Specialization
    @TruffleBoundary
    static String doString(Node node, String left, String right) {
      return left.concat(right);
    }

    @Specialization(guards = {"isNull(left) || isNull(right)"})
    static Object doNullableTryable(Node node, Object left, Object right) {
      if (Nullable.isNull(left) && Nullable.isNull(right)) {
        return 0;
      } else {
        return Nullable.isNull(left) ? right : left;
      }
    }

    @Specialization(guards = {"isError(left) || isError(right)"})
    static Object doTryable(
        Node node,
        Object left,
        Object right,
        @Bind("$node") Node thisNode,
        @Cached TryableNullableNodes.GetErrorNode getErrorNode) {
      if (Tryable.isError(left)) {
        throw new TruffleRuntimeException(getErrorNode.execute(thisNode, left));
      } else {
        throw new TruffleRuntimeException(getErrorNode.execute(thisNode, right));
      }
    }
  }
}
