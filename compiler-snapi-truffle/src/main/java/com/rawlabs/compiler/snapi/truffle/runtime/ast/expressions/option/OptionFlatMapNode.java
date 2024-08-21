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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.option;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.function.FunctionExecuteNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.NullObject;
import com.rawlabs.compiler.snapi.truffle.runtime.tryable_nullable.Nullable;

@NodeInfo(shortName = "Option.FlatMap")
@NodeChild("option")
@NodeChild("function")
@ImportStatic(Nullable.class)
public abstract class OptionFlatMapNode extends ExpressionNode {

  @Specialization(guards = "isNull(option)")
  protected static Object exec(NullObject option, Object function) {
    return option;
  }

  @Specialization(guards = "!isNull(option)")
  protected static Object exec(
      Object option,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(inline = true) FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode) {
    return functionExecuteOneNode.execute(thisNode, function, option);
  }
}
