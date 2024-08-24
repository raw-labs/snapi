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
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;

public class ZeroedConstNode extends ExpressionNode {

  SnapiType type;

  public ZeroedConstNode(SnapiType type) {
    this.type = type;
  }

  @Override
  public final Object executeGeneric(VirtualFrame virtualFrame) {
      return switch (this.type) {
          case SnapiByteType rql2ByteType -> (byte) 0;
          case SnapiShortType rql2ShortType -> (short) 0;
          case SnapiIntType rql2IntType -> 0;
          case SnapiLongType rql2LongType -> (long) 0;
          case SnapiFloatType rql2FloatType -> (float) 0;
          case SnapiDoubleType rql2DoubleType -> (double) 0;
          case SnapiBoolType rql2BoolType -> false;
          case null, default -> null;
      };
  }
}
