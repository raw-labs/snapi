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

package com.rawlabs.snapi.truffle.ast.expressions.literals;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;

public final class LongNode extends ExpressionNode {

  private final String value;

  public LongNode(String value) {
    this.value = value;
  }

  @Override
  public long executeLong(VirtualFrame virtualFrame) {
    return parseLong();
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    return parseLong();
  }

  @TruffleBoundary
  public long parseLong() {
    return Long.parseLong(value);
  }
}
