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

package raw.runtime.truffle.ast.expressions.builtin.numeric.decimal_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import java.math.MathContext;
import raw.runtime.truffle.ExpressionNode;

@NodeInfo(shortName = "Decimal.Round")
@NodeChild(value = "argument", type = ExpressionNode.class)
@NodeChild(value = "precession", type = ExpressionNode.class)
public abstract class DecimalRoundNode extends ExpressionNode {

  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected BigDecimal fromInt(int argument, int precession) {
    return (new BigDecimal(argument)).round(new MathContext(precession + 1));
  }

  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected BigDecimal fromBigDecimal(BigDecimal argument, int precession) {
    return argument.round(new MathContext(precession + 1));
  }
}
