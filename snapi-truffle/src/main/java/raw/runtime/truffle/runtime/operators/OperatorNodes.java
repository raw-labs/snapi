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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
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
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.primitives.*;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.tryable_nullable.Nullable;
import raw.runtime.truffle.tryable_nullable.Tryable;

public class OperatorNodes {

  @NodeInfo(shortName = "Operator.Compare")
  @GenerateUncached
  @ImportStatic(value = {Nullable.class, Tryable.class})
  public abstract static class CompareNode extends Node {

    public abstract int execute(Object obj1, Object obj2);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static int doBoolean(boolean left, boolean right) {
      return Boolean.compare(left, right);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static int doByte(byte left, byte right) {
      return Byte.compare(left, right);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static int doShort(short left, short right) {
      return Short.compare(left, right);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static int doInt(int left, int right) {
      return Integer.compare(left, right);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static int doLong(long left, long right) {
      return Long.compare(left, right);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static int doFloat(float left, float right) {
      return Float.compare(left, right);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static int doDouble(double left, double right) {
      return Double.compare(left, right);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static int doDecimal(DecimalObject left, DecimalObject right) {
      return left.getBigDecimal().compareTo(right.getBigDecimal());
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static int doString(String left, String right) {
      return left.compareTo(right);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static int doDate(DateObject left, DateObject right) {
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
    @CompilerDirectives.TruffleBoundary
    static int doTimestamp(TimestampObject left, TimestampObject right) {
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
    @CompilerDirectives.TruffleBoundary
    static int doTime(TimeObject left, TimeObject right) {
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
    @CompilerDirectives.TruffleBoundary
    static int doInterval(IntervalObject left, IntervalObject right) {
      return left.compareTo(right);
    }

    @Specialization(limit = "3")
    static int doRecord(
        RecordObject left,
        RecordObject right,
        @Cached CompareNode compare,
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
          int result = compare.execute(leftKey, rightKey);
          if (result != 0) {
            return result;
          }
          Object leftValue = lefts.readMember(left, leftKey);
          Object rightValue = rights.readMember(right, rightKey);
          result = compare.execute(leftValue, rightValue);
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
    static int doTryable(Object left, Object right) {
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
    static int doNullable(Object left, Object right) {
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

    @Specialization(limit = "3")
    //            guards = {"lefts.isIterable(left)", "rights.isIterable(right)"})
    static int doIterable(
        Object left,
        Object right,
        @Cached CompareNode compare,
        @Cached IterableNodes.GetGeneratorNode getGeneratorNodeLeft,
        @Cached IterableNodes.GetGeneratorNode getGeneratorNodeRight,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNodeLeft,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNodeRight,
        @Cached GeneratorNodes.GeneratorNextNode nextNodeLeft,
        @Cached GeneratorNodes.GeneratorNextNode nextNodeRight) {
      Object leftGenerator = getGeneratorNodeLeft.execute(left);
      Object rightGenerator = getGeneratorNodeRight.execute(right);
      while (hasNextNodeLeft.execute(leftGenerator) && hasNextNodeRight.execute(rightGenerator)) {
        Object leftElement = nextNodeLeft.execute(leftGenerator);
        Object rightElement = nextNodeRight.execute(rightGenerator);
        int result = compare.execute(leftElement, rightElement);
        if (result != 0) {
          return result;
        }
      }
      if (hasNextNodeLeft.execute(leftGenerator)) {
        return 1;
      } else if (hasNextNodeRight.execute(rightGenerator)) {
        return -1;
      } else {
        return 0;
      }
    }
  }

  @NodeInfo(shortName = "Operator.Add")
  @GenerateUncached
  @ImportStatic(value = {Nullable.class, Tryable.class})
  public abstract static class AddNode extends Node {

    public abstract Object execute(Object obj1, Object obj2);

    @Specialization
    static Object doByte(byte left, byte right) {
      return (byte) (left + right);
    }

    @Specialization
    static Object doShort(short left, short right) {
      return (short) (left + right);
    }

    @Specialization
    static Object doInt(int left, int right) {
      return left + right;
    }

    @Specialization
    static Object doLong(long left, long right) {
      return left + right;
    }

    @Specialization
    static Object doFloat(float left, float right) {
      return left + right;
    }

    @Specialization
    static Object doDouble(double left, double right) {
      return left + right;
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static DecimalObject doDecimal(DecimalObject left, DecimalObject right) {
      return new DecimalObject(left.getBigDecimal().add(right.getBigDecimal()));
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static Object doString(String left, String right) {
      return left.concat(right);
    }

    @Specialization(guards = {"isNull(left) || isNull(right)"})
    static Object doNullableTryable(Object left, Object right) {
      if (Nullable.isNull(left) && Nullable.isNull(right)) {
        return 0;
      } else {
        return Nullable.isNull(left) ? right : left;
      }
    }

    @Specialization(guards = {"isFailure(left) || isFailure(right)"})
    static Object doNTryable(Object left, Object right) {
      if (Tryable.isFailure(left)) {
        throw new RawTruffleRuntimeException(Tryable.getFailure(left));
      } else {
        throw new RawTruffleRuntimeException(Tryable.getFailure(right));
      }
    }
  }
}
