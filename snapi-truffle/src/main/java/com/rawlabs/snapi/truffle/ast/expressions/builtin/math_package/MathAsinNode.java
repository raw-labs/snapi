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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.math_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;

// TODO: Check Acos comments
@NodeInfo(shortName = "Math.Asin")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class MathAsinNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected double doubleAsin(double argument) {
    return Math.asin(argument);
  }
}
