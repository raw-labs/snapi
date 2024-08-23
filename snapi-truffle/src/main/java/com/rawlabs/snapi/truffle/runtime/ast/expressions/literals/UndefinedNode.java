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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.literals;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleRuntimeException;

public class UndefinedNode extends ExpressionNode {

  public UndefinedNode() {}

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    throw new TruffleRuntimeException("Should never execute undefined node", this);
  }
}
