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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.math_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;

@NodeInfo(shortName = "Math.Cos")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class MathCosNode extends ExpressionNode {
  @Specialization
  @TruffleBoundary
  protected double doubleCos(double argument) {
    return Math.cos(argument);
  }
}
