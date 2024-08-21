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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.literals;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;

public final class IntNode extends ExpressionNode {

  private final int value;

  public IntNode(int value) {
    this.value = value;
  }

  public IntNode(String value) {
    this.value = Integer.parseInt(value);
  }

  @Override
  public int executeInt(VirtualFrame virtualFrame) {
    return value;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    return value;
  }
}
