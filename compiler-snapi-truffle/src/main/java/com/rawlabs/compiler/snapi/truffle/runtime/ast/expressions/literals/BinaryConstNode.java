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
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.BinaryObject;

public class BinaryConstNode extends ExpressionNode {

  private final BinaryObject value;

  public BinaryConstNode(byte[] value) {
    this.value = new BinaryObject(value);
  }

  @Override
  public final BinaryObject executeBinary(VirtualFrame virtualFrame) {
    return value;
  }

  @Override
  public final Object executeGeneric(VirtualFrame virtualFrame) {
    return value;
  }
}
