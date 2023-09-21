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

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.function.Function;

public final class MethodRefNode extends ExpressionNode {

  @CompilationFinal private final String funcName;
  private Closure closure = null;

  @Children private final ExpressionNode[] defaultArgumentExps;

  public MethodRefNode(String funcName, ExpressionNode[] defaultArgumentExps) {
    this.funcName = funcName;
    this.defaultArgumentExps = defaultArgumentExps;
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame virtualFrame) {
    // Compute the closure and its default arguments when called the first time.
    // TODO (msb): I don't think this is the correct way to do it.
    if (closure == null) {
      int nArgs = defaultArgumentExps.length;
      Object[] defaultArguments = new Object[nArgs];
      for (int i = 0; i < nArgs; i++) {
        if (defaultArgumentExps[i] != null) {
          defaultArguments[i] = defaultArgumentExps[i].executeGeneric(virtualFrame);
        } else {
          defaultArguments[i] = null;
        }
      }
      Function function = RawContext.get(this).getFunctionRegistry().getFunction(funcName);
      closure = new Closure(function, defaultArguments);
    }
    return closure;
  }
}
