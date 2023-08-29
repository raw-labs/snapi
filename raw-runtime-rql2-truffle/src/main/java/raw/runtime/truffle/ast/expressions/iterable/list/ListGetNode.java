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

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.list.ListLibrary;
import raw.runtime.truffle.runtime.nullable_tryable.NullableTryableLibrary;
import raw.runtime.truffle.runtime.nullable_tryable.RuntimeNullableTryableHandler;
import raw.runtime.truffle.runtime.tryable.ErrorTryable;
import raw.runtime.truffle.runtime.tryable.TryableLibrary;

@NodeInfo(shortName = "List.Get")
@NodeChild("list")
@NodeChild("index")
public abstract class ListGetNode extends ExpressionNode {
  @Specialization(
      limit = "3",
      guards = {
        "lists.isElementReadable(list, index)",
        "triables.isTryable(lists.get(list, index))"
      })
  protected Object listGetTryable(
      Object list,
      int index,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary("lists.get(list, index)") TryableLibrary triables) {
    return lists.get(list, index);
  }

  @Specialization(
      limit = "3",
      guards = {"lists.isElementReadable(list, index)"})
  protected Object listGetTryable(
      Object list,
      int index,
      @CachedLibrary("list") ListLibrary lists,
      @CachedLibrary(limit = "1") NullableTryableLibrary nullableTryables) {
    Object v = lists.get(list, index);
    RuntimeNullableTryableHandler handler = new RuntimeNullableTryableHandler();
    return nullableTryables.boxTryable(handler, v);
  }

  @Specialization
  protected Object listGetFailure(Object list, int index) {
    return ErrorTryable.BuildFailure("index out of bounds");
  }
}
