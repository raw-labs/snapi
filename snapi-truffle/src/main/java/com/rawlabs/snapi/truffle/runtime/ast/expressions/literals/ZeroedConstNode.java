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
import com.rawlabs.compiler.snapi.rql2.source.*;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;

public class ZeroedConstNode extends ExpressionNode {

  Rql2Type type;

  public ZeroedConstNode(Rql2Type type) {
    this.type = type;
  }

  @Override
  public final Object executeGeneric(VirtualFrame virtualFrame) {
      return switch (this.type) {
          case Rql2ByteType rql2ByteType -> (byte) 0;
          case Rql2ShortType rql2ShortType -> (short) 0;
          case Rql2IntType rql2IntType -> 0;
          case Rql2LongType rql2LongType -> (long) 0;
          case Rql2FloatType rql2FloatType -> (float) 0;
          case Rql2DoubleType rql2DoubleType -> (double) 0;
          case Rql2BoolType rql2BoolType -> false;
          case null, default -> null;
      };
  }
}
