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

package com.rawlabs.snapi.truffle.ast.expressions.option;

import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleUnexpectedNullException;
import com.rawlabs.snapi.truffle.runtime.primitives.NullObject;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.Nullable;

@NodeInfo(shortName = "Option.UnsafeGet")
@NodeChild("option")
@ImportStatic(Nullable.class)
public abstract class OptionUnsafeGetNode extends ExpressionNode {

  @Specialization(guards = "isNull(option)")
  protected Object doObject(NullObject option) {
    throw new TruffleUnexpectedNullException(this);
  }

  @Specialization(guards = "!isNull(option)")
  protected Object doObjectIsNotNull(Object option) {
    return option;
  }
}
