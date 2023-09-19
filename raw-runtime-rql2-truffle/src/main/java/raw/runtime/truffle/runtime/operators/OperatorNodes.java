package raw.runtime.truffle.runtime.operators;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.option.OptionLibrary;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimeObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.runtime.tryable.TryableLibrary;

public class OperatorNodes {

  @NodeInfo(shortName = "Operator.Compare")
  @GenerateUncached
  public abstract static class CompareNode extends Node {

    public abstract int execute(Object obj1, Object obj2);

    @Specialization
    static int doBoolean(boolean left, boolean right) {
      return Boolean.compare(left, right);
    }

    @Specialization
    static int doByte(byte left, byte right) {
      return Byte.compare(left, right);
    }

    @Specialization
    static int doShort(short left, short right) {
      return Short.compare(left, right);
    }

    @Specialization
    static int doInt(int left, int right) {
      return Integer.compare(left, right);
    }

    @Specialization
    static int doLong(long left, long right) {
      return Long.compare(left, right);
    }

    @Specialization
    static int doFloat(float left, float right) {
      return Float.compare(left, right);
    }

    @Specialization
    static int doDouble(double left, double right) {
      return Double.compare(left, right);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static int doDecimal(BigDecimal left, BigDecimal right) {
      return left.compareTo(right);
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

    @Specialization(
        limit = "3",
        guards = {
          "left != null",
          "right != null",
          "lefts.isIterable(left)",
          "rights.isIterable(right)"
        })
    static int doIterable(
        Object left,
        Object right,
        @Cached CompareNode compare,
        @CachedLibrary("left") IterableLibrary lefts,
        @CachedLibrary("right") IterableLibrary rights,
        @CachedLibrary(limit = "2") GeneratorLibrary generators) {
      Object leftGenerator = lefts.getGenerator(left);
      Object rightGenerator = rights.getGenerator(right);
      while (generators.hasNext(leftGenerator) && generators.hasNext(rightGenerator)) {
        Object leftElement = generators.next(leftGenerator);
        Object rightElement = generators.next(rightGenerator);
        int result = compare.execute(leftElement, rightElement);
        if (result != 0) {
          return result;
        }
      }
      if (generators.hasNext(leftGenerator)) {
        return 1;
      } else if (generators.hasNext(rightGenerator)) {
        return -1;
      } else {
        return 0;
      }
    }

    @Specialization(guards = "left == null || right == null")
    static int doNull(Object left, Object right) {
      if (left == null && right == null) {
        return 0;
      } else if (left == null) {
        return -2;
      } else {
        return 2;
      }
    }

    @Specialization(guards = {"tryables.isTryable(left)"})
    static int doTryable(
        Object left,
        Object right,
        @Cached CompareNode compare,
        @CachedLibrary(limit = "3") TryableLibrary tryables) {
      // both are tryables (maybe not nullable but that's handled recursively)
      assert (tryables.isTryable(right));
      boolean leftIsSuccess = tryables.isSuccess(left);
      boolean rightIsSuccess = tryables.isSuccess(right);
      if (leftIsSuccess) {
        if (rightIsSuccess) {
          Object leftValue = tryables.success(left);
          Object rightValue = tryables.success(right);
          return compare.execute(leftValue, rightValue);
        } else {
          return 1;
        }
      } else {
        if (rightIsSuccess) {
          return -1;
        } else {
          String leftFailure = tryables.failure(left);
          String rightFailure = tryables.failure(right);
          return compare.execute(leftFailure, rightFailure);
        }
      }
    }

    @Specialization(guards = {"options.isOption(left)"})
    static int doNullable(
        Object left,
        Object right,
        @Cached CompareNode compare,
        @CachedLibrary(limit = "3") OptionLibrary options) {
      // both are options
      assert (options.isOption(right));
      boolean leftIsDefined = options.isDefined(left);
      boolean rightIsDefined = options.isDefined(right);

      if (leftIsDefined) {
        if (rightIsDefined) {
          Object leftValue = options.get(left);
          Object rightValue = options.get(right);
          return compare.execute(leftValue, rightValue);
        } else {
          return 1;
        }
      } else {
        if (rightIsDefined) {
          return -1;
        } else {
          return 0;
        }
      }
    }
  }

  @NodeInfo(shortName = "Operator.Add")
  @GenerateUncached
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
    static Object doDecimal(BigDecimal left, BigDecimal right) {
      return left.add(right);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static Object doString(String left, String right) {
      return left.concat(right);
    }

    // Here a paradox is created because for max null could be considered the least value but
    // then what happens to max. The goal is to ignore null and keep the value. That's why this
    // method is the only one that returns -2 or 2. The other methods return -1 or 1.
    @Specialization(guards = "left == null || right == null")
    @CompilerDirectives.TruffleBoundary
    static Object doNull(Object left, Object right) {
      if (left == null && right == null) {
        return 0;
      } else return Objects.requireNonNullElse(left, right);
    }

    // It is interesting that @CachedLibrary("comparator") does not go to int case for Object
    // calls
    // instead it was going here again, and it was needed to make it
    // @CachedLibrary(limit = "1") to make it work.
    @Specialization(guards = {"left != null", "right != null"})
    static Object doNullableTryable(
        Object left,
        Object right,
        @Cached AddNode add,
        @Cached TryableNullableNodes.UnboxUnsafeNode unbox) {
      Object unboxedLeft = unbox.execute(left);
      Object unboxedRight = unbox.execute(right);
      return add.execute(unboxedLeft, unboxedRight);
    }
  }
}
