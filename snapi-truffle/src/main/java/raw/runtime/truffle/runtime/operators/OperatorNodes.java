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

package raw.runtime.truffle.runtime.operators;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import raw.runtime.truffle.ast.expressions.builtin.temporals.interval_package.IntervalNodes;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.primitives.*;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.tryable_nullable.Nullable;
import raw.runtime.truffle.tryable_nullable.Tryable;

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

    @Specialization(limit = "3")
    static int doRecord(
        Node node,
        RecordObject left,
        RecordObject right,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("compare") CompareNode compare,
        @CachedLibrary("left") InteropLibrary lefts,
        @CachedLibrary(limit = "3") InteropLibrary arrays,
        @CachedLibrary("right") InteropLibrary rights) {
      try {
        Object leftKeys = lefts.getMembers(left);
        long leftSize = arrays.getArraySize(leftKeys);
        Object rightKeys = rights.getMembers(right);
        long rightSize = arrays.getArraySize(rightKeys);
        if (leftSize > rightSize) {
          return 1;
        } else if (leftSize < rightSize) {
          return -1;
        }
        for (int i = 0; i < leftSize; i++) {
          String leftKey = (String) arrays.readArrayElement(leftKeys, i);
          String rightKey = (String) arrays.readArrayElement(rightKeys, i);
          int result = compare.execute(thisNode, leftKey, rightKey);
          if (result != 0) {
            return result;
          }
          Object leftValue = lefts.readMember(left, leftKey);
          Object rightValue = rights.readMember(right, rightKey);
          result = compare.execute(thisNode, leftValue, rightValue);
          if (result != 0) {
            return result;
          }
        }
        return 0;
      } catch (InvalidArrayIndexException
          | UnsupportedMessageException
          | UnknownIdentifierException e) {
        throw new RawTruffleInternalErrorException(e);
      }
    }

    @Specialization(guards = {"isFailure(left) || isFailure(right)"})
    static int doTryable(Node node, Object left, Object right) {
      boolean leftIsFailure = Tryable.isFailure(left);
      boolean rightIsFailure = Tryable.isFailure(right);
      if (leftIsFailure && rightIsFailure) {
        return Tryable.getFailure(left).compareTo(Tryable.getFailure(right));
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

    @Specialization(guards = {"isFailure(left) || isFailure(right)"})
    static Object doTryable(Node node, Object left, Object right) {
      if (Tryable.isFailure(left)) {
        throw new RawTruffleRuntimeException(Tryable.getFailure(left));
      } else {
        throw new RawTruffleRuntimeException(Tryable.getFailure(right));
      }
    }
  }
}
