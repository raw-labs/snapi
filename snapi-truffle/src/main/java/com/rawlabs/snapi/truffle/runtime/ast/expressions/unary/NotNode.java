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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.unary;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;

@NodeChild("value")
public abstract class NotNode extends ExpressionNode {

  @Specialization
  protected boolean not(boolean value) {
    return !value;
  }
}
