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

package com.rawlabs.snapi.truffle.ast.expressions.tryable;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodes;

@NodeInfo(shortName = "Tryable.isError")
@NodeChild("tryable")
public abstract class TryableIsFailureNode extends ExpressionNode {

  @Specialization
  protected boolean isFailureTryable(
      Object tryable, @Cached(inline = true) TryableNullableNodes.IsErrorNode isFailureNode) {
    return isFailureNode.execute(this, tryable);
  }
}
