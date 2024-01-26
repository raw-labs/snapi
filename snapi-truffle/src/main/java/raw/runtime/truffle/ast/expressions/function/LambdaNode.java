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

package raw.runtime.truffle.ast.expressions.function;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.Function;
import raw.runtime.truffle.runtime.function.Lambda;

public class LambdaNode extends ExpressionNode {
  private final RootCallTarget callTarget;
  @CompilerDirectives.CompilationFinal private Lambda lambda;

  public LambdaNode(Function f) {
    callTarget = f.getCallTarget();
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame virtualFrame) {
    if (lambda == null) {
      lambda = new Lambda(callTarget, virtualFrame);
    }
    return lambda;
  }
}
