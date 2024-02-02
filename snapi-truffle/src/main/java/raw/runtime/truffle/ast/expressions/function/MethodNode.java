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
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.function.Function;
import raw.runtime.truffle.runtime.function.Lambda;

public final class MethodNode extends ExpressionNode {

  @CompilationFinal private final Function parentFunction;

  @CompilationFinal(dimensions = 1)
  private Object[] defaultArguments;

  @CompilationFinal private Object function;
  @Children private final ExpressionNode[] defaultArgumentExps;
  private final String name;
  private final boolean hasFreeVars;
  private final boolean hasOptionalArgs;

  public MethodNode(
      String name, Function f, ExpressionNode[] defaultArgumentExps, boolean hasFreeVars) {
    this.parentFunction = f;
    this.defaultArgumentExps = defaultArgumentExps;
    this.name = name;
    this.hasFreeVars = hasFreeVars;
    this.hasOptionalArgs = defaultArgumentExps.length > 0;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    // Method can be cached
    if (function == null) {
      // if the method has optional arguments, we need to create a closure
      if (hasOptionalArgs) {
        int nArgs = defaultArgumentExps.length;
        defaultArguments = new Object[nArgs];
        for (int i = 0; i < nArgs; i++) {
          if (defaultArgumentExps[i] != null) {
            defaultArguments[i] = defaultArgumentExps[i].executeGeneric(virtualFrame);
          } else {
            defaultArguments[i] = null;
          }
        }
        function =
            new Closure(
                this.parentFunction,
                defaultArguments,
                hasFreeVars ? virtualFrame.materialize() : null);
      }
      // Otherwise it is a lambda
      else {
        function = new Lambda(this.parentFunction.getCallTarget());
      }
      // Only put actual methods in registry
      if (name != null) {
        RawContext.get(this).getFunctionRegistry().register(name, function);
      }
    }
    return function;
  }
}
