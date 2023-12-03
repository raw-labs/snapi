///*
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
//package raw.runtime.truffle.ast.tryable_nullable;
//
//import com.oracle.truffle.api.dsl.Cached;
//import com.oracle.truffle.api.dsl.GenerateUncached;
//import com.oracle.truffle.api.dsl.Specialization;
//import com.oracle.truffle.api.library.CachedLibrary;
//import com.oracle.truffle.api.nodes.Node;
//import com.oracle.truffle.api.nodes.NodeInfo;
//import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
//import raw.runtime.truffle.runtime.option.*;
//import raw.runtime.truffle.runtime.tryable.*;
//
//public class TryableNullableNodes {
//
//  @NodeInfo(shortName = "TryableNullable.BoxOption")
//  @GenerateUncached
//  public abstract static class BoxOptionNode extends Node {
//
//    public abstract Object execute(Object value);
//
//    @Specialization
//    static Object doByte(byte value) {
//      return new ByteOption(value);
//    }
//
//    @Specialization
//    static Object doShort(short value) {
//      return new ShortOption(value);
//    }
//
//    @Specialization
//    static Object doInt(int value) {
//      return new IntOption(value);
//    }
//
//    @Specialization
//    static Object doLong(long value) {
//      return new LongOption(value);
//    }
//
//    @Specialization
//    static Object doFloat(float value) {
//      return new FloatOption(value);
//    }
//
//    @Specialization
//    static Object doDouble(double value) {
//      return new DoubleOption(value);
//    }
//
//    @Specialization
//    static Object doBoolean(boolean value) {
//      return new BooleanOption(value);
//    }
//
//    @Specialization
//    static Object doString(String value) {
//      return new StringOption(value);
//    }
//
//    @Specialization(guards = "options.isOption(value)", limit = "3")
//    static Object doOption(Object value, @CachedLibrary("value") OptionLibrary options) {
//      return value;
//    }
//
//    @Specialization
//    static Object doObject(Object value) {
//      return new ObjectOption(value);
//    }
//  }
//
//  @NodeInfo(shortName = "TryableNullable.BoxTryable")
//  @GenerateUncached
//  public abstract static class BoxTryableNode extends Node {
//
//    public abstract Object execute(Object value);
//
//    @Specialization
//    static Object doByte(byte value) {
//      return ByteTryable.BuildSuccess(value);
//    }
//
//    @Specialization
//    static Object doShort(short value) {
//      return ShortTryable.BuildSuccess(value);
//    }
//
//    @Specialization
//    static Object doInt(int value) {
//      return IntTryable.BuildSuccess(value);
//    }
//
//    @Specialization
//    static Object doLong(long value) {
//      return LongTryable.BuildSuccess(value);
//    }
//
//    @Specialization
//    static Object doFloat(float value) {
//      return FloatTryable.BuildSuccess(value);
//    }
//
//    @Specialization
//    static Object doDouble(double value) {
//      return DoubleTryable.BuildSuccess(value);
//    }
//
//    @Specialization
//    static Object doBoolean(boolean value) {
//      return BooleanTryable.BuildSuccess(value);
//    }
//
//    @Specialization
//    static Object doString(String value) {
//      return StringTryable.BuildSuccess(value);
//    }
//
//    @Specialization(guards = "tryables.isTryable(value)", limit = "3")
//    static Object doTryable(Object value, @CachedLibrary("value") TryableLibrary tryables) {
//      return value;
//    }
//
//    @Specialization
//    static Object doObject(Object value) {
//      return ObjectTryable.BuildSuccess(value);
//    }
//  }
//
//  @NodeInfo(shortName = "TryableNullable.HandleOptionPredicate")
//  @GenerateUncached
//  public abstract static class HandleOptionPredicateNode extends Node {
//
//    public abstract Boolean execute(Object value, Boolean defaultValue);
//
//    @Specialization
//    static Boolean handleOptionPredicate(
//        Object value, Boolean defaultValue, @CachedLibrary(limit = "1") OptionLibrary options) {
//      if (value != null) {
//        if (options.isOption(value)) {
//          if (options.isDefined(value)) {
//            return (Boolean) options.get(value);
//          } else {
//            return defaultValue;
//          }
//        } else {
//          return (Boolean) value;
//        }
//      }
//      return defaultValue;
//    }
//  }
//
//  @NodeInfo(shortName = "TryableNullable.GetOrElse")
//  @GenerateUncached
//  public abstract static class GetOrElseNode extends Node {
//
//    public abstract Object execute(Object value, Object defaultValue);
//
//    @Specialization
//    static Object getOrElse(
//        Object value,
//        Object defaultValue,
//        @Cached("create()") GetOrElseOptionNode getOrElseOption,
//        @CachedLibrary(limit = "2") TryableLibrary tryables) {
//      if (tryables.isTryable(value)) {
//        if (tryables.isSuccess(value)) {
//          Object innerValue = tryables.success(value);
//          return getOrElseOption.execute(innerValue, defaultValue);
//        } else {
//          return defaultValue;
//        }
//      } else {
//        return getOrElseOption.execute(value, defaultValue);
//      }
//    }
//  }
//
//  @NodeInfo(shortName = "TryableNullable.GetOrElseOption")
//  @GenerateUncached
//  public abstract static class GetOrElseOptionNode extends Node {
//
//    public abstract Object execute(Object value, Object defaultValue);
//
//    @Specialization
//    static Object getOrElse(
//        Object value, Object defaultValue, @CachedLibrary(limit = "2") OptionLibrary nullables) {
//      if (nullables.isOption(value)) {
//        if (nullables.isDefined(value)) {
//          return nullables.get(value);
//        } else {
//          return defaultValue;
//        }
//      } else {
//        return value;
//      }
//    }
//  }
//
//  @NodeInfo(shortName = "TryableNullable.handleOptionTryablePredicate")
//  @GenerateUncached
//  public abstract static class HandleOptionTryablePredicateNode extends Node {
//
//    public abstract Boolean execute(Object value, Object defaultValue);
//
//    @Specialization
//    static Boolean handleOptionTryablePredicate(
//        Object maybeOptionTryable,
//        Boolean defaultValue,
//        @Cached("create()") HandleOptionPredicateNode handleOptionPredicateNode,
//        @CachedLibrary(limit = "2") TryableLibrary tryables) {
//      if (maybeOptionTryable != null) {
//        if (tryables.isTryable(maybeOptionTryable)) {
//          if (tryables.isSuccess(maybeOptionTryable)) {
//            Object success = tryables.success(maybeOptionTryable);
//            return handleOptionPredicateNode.execute(success, defaultValue);
//          } else if (defaultValue != null) {
//            return defaultValue;
//          } else {
//            throw new RawTruffleRuntimeException(tryables.failure(maybeOptionTryable));
//          }
//        } else {
//          return handleOptionPredicateNode.execute(maybeOptionTryable, defaultValue);
//        }
//      } else {
//        return defaultValue;
//      }
//    }
//  }
//
//  @NodeInfo(shortName = "TryableNullable.UnboxOption")
//  @GenerateUncached
//  public abstract static class UnboxOptionNode extends Node {
//
//    public abstract Object execute(Object value);
//
//    @Specialization
//    static Object unboxOption(
//        Object maybeOptionTryable, @CachedLibrary(limit = "1") OptionLibrary options) {
//      if (maybeOptionTryable != null) {
//        if (options.isOption(maybeOptionTryable)) {
//          if (options.isDefined(maybeOptionTryable)) {
//            return options.get(maybeOptionTryable);
//          } else {
//            return null;
//          }
//        } else {
//          return maybeOptionTryable;
//        }
//      }
//      return null;
//    }
//  }
//
//  @NodeInfo(shortName = "TryableNullable.Unbox")
//  @GenerateUncached
//  public abstract static class UnboxNode extends Node {
//
//    public abstract Object execute(Object value);
//
//    @Specialization
//    static Object unbox(
//        Object maybeOptionTryable,
//        @Cached("create()") UnboxOptionNode unboxOption,
//        @CachedLibrary(limit = "1") TryableLibrary tryables) {
//      if (maybeOptionTryable != null) {
//        if (tryables.isTryable(maybeOptionTryable)) {
//          if (tryables.isSuccess(maybeOptionTryable)) {
//            Object success = tryables.success(maybeOptionTryable);
//            return unboxOption.execute(success);
//          } else {
//            return tryables.failure(maybeOptionTryable);
//          }
//        } else {
//          return unboxOption.execute(maybeOptionTryable);
//        }
//      }
//      return null;
//    }
//  }
//
//  @NodeInfo(shortName = "TryableNullable.UnboxOption")
//  @GenerateUncached
//  public abstract static class UnboxUnsafeNode extends Node {
//
//    public abstract Object execute(Object value);
//
//    @Specialization
//    static Object unboxUnsafe(
//        Object maybeOptionTryable,
//        @Cached("create()") UnboxOptionNode unboxOption,
//        @CachedLibrary(limit = "1") TryableLibrary tryables) {
//      if (maybeOptionTryable != null) {
//        if (tryables.isTryable(maybeOptionTryable)) {
//          if (tryables.isSuccess(maybeOptionTryable)) {
//            Object success = tryables.success(maybeOptionTryable);
//            return unboxOption.execute(success);
//          } else {
//            throw new RawTruffleRuntimeException(tryables.failure(maybeOptionTryable));
//          }
//        } else {
//          return unboxOption.execute(maybeOptionTryable);
//        }
//      }
//      return null;
//    }
//  }
//}
