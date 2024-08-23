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

package com.rawlabs.snapi.truffle.ast.expressions.iterable.list;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.TypeGuards;
import com.rawlabs.snapi.truffle.runtime.list.*;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Take")
@NodeChild("list")
@NodeChild("num")
public abstract class ListTakeNode extends ExpressionNode {

  @Specialization
  protected Object take(Object list, long num, @Cached(inline = true) ListNodes.TakeNode takeNode) {
    return takeNode.execute(this, list, (int) num);
  }
}
