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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.tryable;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.tryable_nullable.TryableNullableNodes;

@NodeInfo(shortName = "Tryable.GetFailure")
@NodeChild("tryable")
public abstract class TryableGetFailureNode extends ExpressionNode {
  @Specialization
  protected String getFailure(
      Object tryable, @Cached(inline = true) TryableNullableNodes.GetErrorNode getFailureNode) {
    return getFailureNode.execute(this, tryable);
  }
}
