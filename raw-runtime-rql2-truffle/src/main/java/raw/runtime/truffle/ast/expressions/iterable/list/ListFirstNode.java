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

import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2Type;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.list.ListLibrary;
import raw.runtime.truffle.runtime.option.*;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.First")
@NodeChild("list")
@NodeField(name = "resultType", type = Rql2Type.class)
public abstract class ListFirstNode extends ExpressionNode {

  protected abstract Rql2Type getResultType();

  @Specialization(
      guards = {"isByteKind(getResultType())"},
      limit = "3")
  protected ByteOption doByte(Object list, @CachedLibrary("list") ListLibrary lists) {
    if (lists.size(list) == 0) {
      return new ByteOption();
    }
    return new ByteOption((byte) lists.get(list, 0));
  }

  @Specialization(
      guards = {"isShortKind(getResultType())"},
      limit = "3")
  protected ShortOption doShort(Object list, @CachedLibrary("list") ListLibrary lists) {
    if (lists.size(list) == 0) {
      return new ShortOption();
    }
    return new ShortOption((short) lists.get(list, 0));
  }

  @Specialization(
      guards = {"isIntKind(getResultType())"},
      limit = "3")
  protected IntOption doInt(Object list, @CachedLibrary("list") ListLibrary lists) {
    if (lists.size(list) == 0) {
      return new IntOption();
    }
    return new IntOption((int) lists.get(list, 0));
  }

  @Specialization(
      guards = {"isLongKind(getResultType())"},
      limit = "3")
  protected LongOption doLong(Object list, @CachedLibrary("list") ListLibrary lists) {
    if (lists.size(list) == 0) {
      return new LongOption();
    }
    return new LongOption((long) lists.get(list, 0));
  }

  @Specialization(
      guards = {"isFloatKind(getResultType())"},
      limit = "3")
  protected FloatOption doFloat(Object list, @CachedLibrary("list") ListLibrary lists) {
    if (lists.size(list) == 0) {
      return new FloatOption();
    }
    return new FloatOption((float) lists.get(list, 0));
  }

  @Specialization(
      guards = {"isDoubleKind(getResultType())"},
      limit = "3")
  protected DoubleOption doDouble(Object list, @CachedLibrary("list") ListLibrary lists) {
    if (lists.size(list) == 0) {
      return new DoubleOption();
    }
    return new DoubleOption((double) lists.get(list, 0));
  }

  @Specialization(
      guards = {"isBooleanKind(getResultType())"},
      limit = "3")
  protected BooleanOption doBoolean(Object list, @CachedLibrary("list") ListLibrary lists) {
    if (lists.size(list) == 0) {
      return new BooleanOption();
    }
    return new BooleanOption((boolean) lists.get(list, 0));
  }

  @Specialization(
      guards = {"isStringKind(getResultType())"},
      limit = "3")
  protected StringOption doString(Object list, @CachedLibrary("list") ListLibrary lists) {
    if (lists.size(list) == 0) {
      new StringOption();
    }
    return new StringOption((String) lists.get(list, 0));
  }

  //    @Specialization(limit = "3")
  //    protected Object doObject(Object list, @CachedLibrary("list") ListLibrary lists) {
  //        if (lists.count(list) == 0) {
  //            return new ObjectOption();
  //        }
  //        Object v = lists.get(list, 0);
  //        OptionLibrary options = OptionLibrary.getFactory().create(v);
  //        if (options.isOption(v)) return v;
  //        return new ObjectOption(v);
  //    }

  @Specialization(
      limit = "3",
      guards = {"lists.size(list) != 0", "!options.isOption(lists.get(list, 0))"})
  protected Object doObject(
      Object list,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary("lists.get(list, 0)") OptionLibrary options) {
    return new ObjectOption(lists.get(list, 0));
  }

  @Specialization(
      limit = "3",
      guards = {"lists.size(list) != 0"})
  protected Object doObject(Object list, @CachedLibrary("list") ListLibrary lists) {
    return lists.get(list, 0);
  }

  @Specialization
  protected Object listGetFailure(Object list) {
    return new ObjectOption();
  }
}
