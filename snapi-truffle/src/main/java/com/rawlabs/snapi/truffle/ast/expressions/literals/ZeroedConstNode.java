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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.snapi.frontend.snapi.source.*;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;

public class ZeroedConstNode extends ExpressionNode {

  SnapiType type;

  public ZeroedConstNode(SnapiType type) {
    this.type = type;
  }

  @Override
  public final Object executeGeneric(VirtualFrame virtualFrame) {
      return switch (this.type) {
          case SnapiByteType snapiByteType -> (byte) 0;
          case SnapiShortType snapiShortType -> (short) 0;
          case SnapiIntType snapiIntType -> 0;
          case SnapiLongType snapiLongType -> (long) 0;
          case SnapiFloatType snapiFloatType -> (float) 0;
          case SnapiDoubleType snapiDoubleType -> (double) 0;
          case SnapiBoolType snapiBoolType -> false;
          case null, default -> null;
      };
  }
}
