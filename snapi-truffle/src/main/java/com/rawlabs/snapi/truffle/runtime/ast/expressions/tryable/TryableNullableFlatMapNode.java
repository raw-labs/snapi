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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.tryable;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.function.FunctionExecuteNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.ErrorObject;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.NullObject;
import com.rawlabs.snapi.truffle.runtime.tryable_nullable.Nullable;
import com.rawlabs.snapi.truffle.runtime.tryable_nullable.Tryable;

@NodeInfo(shortName = "TryableNullable.FlatMap")
@NodeChild("tryable")
@NodeChild("function")
@ImportStatic({Nullable.class, Tryable.class})
public abstract class TryableNullableFlatMapNode extends ExpressionNode {

  @Specialization(guards = "isError(maybeTryableNullable)")
  protected static Object exec(ErrorObject maybeTryableNullable, Object function) {
    return maybeTryableNullable;
  }

  @Specialization(guards = "isNull(maybeTryableNullable)")
  protected static Object exec(NullObject maybeTryableNullable, Object function) {
    return maybeTryableNullable;
  }

  @Specialization(guards = {"!isError(maybeTryableNullable)", "!isNull(maybeTryableNullable)"})
  protected static Object exec(
      Object maybeTryableNullable,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(inline = true) FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode) {
    return functionExecuteOneNode.execute(thisNode, function, maybeTryableNullable);
  }
}
