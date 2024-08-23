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

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.ErrorObject;
import com.rawlabs.snapi.truffle.runtime.tryable_nullable.Tryable;
import com.rawlabs.snapi.truffle.runtime.tryable_nullable.TryableNullableNodes;

@NodeInfo(shortName = "Try.UnsafeGet")
@NodeChild("tryable")
@ImportStatic({Tryable.class})
public abstract class TryableUnsafeGetNode extends ExpressionNode {
  @Specialization(guards = "isError(tryable)")
  protected Object exec(
      ErrorObject tryable,
      @Cached(inline = true) TryableNullableNodes.GetErrorNode getFailureNode) {
    throw new TruffleRuntimeException(getFailureNode.execute(this, tryable), this);
  }

  @Specialization(guards = "!isError(tryable)")
  protected Object exec(Object tryable) {
    return tryable;
  }
}
