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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.iterable.list;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.list.ListNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "List.Get")
@NodeChild("list")
@NodeChild("index")
public abstract class ListGetNode extends ExpressionNode {
  @Specialization
  protected Object listGetTryable(
      Object list,
      int index,
      @Cached(inline = true) ListNodes.IsElementReadableNode isElementReadableNode,
      @Cached(inline = true) ListNodes.GetNode getNode) {
    if (isElementReadableNode.execute(this, list, index)) {
      return getNode.execute(this, list, index);
    }
    return new ErrorObject("index out of bounds");
  }
}
