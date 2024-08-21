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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.numeric.decimal_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.DecimalObject;
import java.math.BigDecimal;
import java.math.MathContext;

@NodeInfo(shortName = "Decimal.Round")
@NodeChild(value = "argument", type = ExpressionNode.class)
@NodeChild(value = "precession", type = ExpressionNode.class)
public abstract class DecimalRoundNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected DecimalObject fromInt(int argument, int precession) {
    return new DecimalObject((new BigDecimal(argument)).round(new MathContext(precession + 1)));
  }

  @Specialization
  @TruffleBoundary
  protected DecimalObject fromBigDecimal(DecimalObject argument, int precession) {
    return new DecimalObject(argument.getBigDecimal().round(new MathContext(precession + 1)));
  }
}
