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

package raw.runtime.truffle.ast.expressions.builtin.math_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;

// TODO: A.Z make sure that the semantics are correct
@NodeInfo(shortName = "Math.Abs")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class MathAbsNode extends ExpressionNode {

  @Specialization(rewriteOn = ArithmeticException.class)
  @CompilerDirectives.TruffleBoundary
  protected int intAbs(int argument) {
    return argument < 0 ? Math.negateExact(argument) : argument;
  }

  @Specialization(rewriteOn = ArithmeticException.class, replaces = "intAbs")
  @CompilerDirectives.TruffleBoundary
  protected long longAbs(long argument) {
    return argument < 0 ? Math.negateExact(argument) : argument;
  }

  @Specialization(rewriteOn = ArithmeticException.class)
  @CompilerDirectives.TruffleBoundary
  protected float floatAbs(float argument) {
    if (Float.compare(Float.MIN_VALUE, argument) != 0) return argument < 0 ? -argument : argument;
    else throw new ArithmeticException();
  }

  @Specialization(rewriteOn = ArithmeticException.class, replaces = "floatAbs")
  @CompilerDirectives.TruffleBoundary
  protected double doubleAbs(double argument) {
    if (Double.compare(Double.MIN_VALUE, argument) != 0) return argument < 0 ? -argument : argument;
    else throw new ArithmeticException();
  }
}
