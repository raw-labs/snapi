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
import com.oracle.truffle.api.nodes.Node.Children;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.function.Function;

public final class MethodNode extends ExpressionNode {

  @CompilationFinal private final Function function;

  @CompilationFinal(dimensions = 1)
  private Object[] defaultArguments;

  @CompilationFinal private Closure closure;
  @Children private final ExpressionNode[] defaultArgumentExps;

  private final String name;
  private final boolean hasFreeVars;

  public MethodNode(
      String name, Function f, ExpressionNode[] defaultArgumentExps, boolean hasFreeVars) {
    this.function = f;
    this.defaultArgumentExps = defaultArgumentExps;
    this.name = name;
    this.hasFreeVars = hasFreeVars;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    // in case it is a FunAbs, we can cache the closure, otherwise it is executed only once.
    if (closure == null) {
      int nArgs = defaultArgumentExps.length;
      defaultArguments = new Object[nArgs];
      for (int i = 0; i < nArgs; i++) {
        if (defaultArgumentExps[i] != null) {
          defaultArguments[i] = defaultArgumentExps[i].executeGeneric(virtualFrame);
        } else {
          defaultArguments[i] = null;
        }
      }
      closure =
          new Closure(
              this.function, defaultArguments, hasFreeVars ? virtualFrame.materialize() : null);
      RawContext.get(this).getFunctionRegistry().register(name, closure);
    }
    return closure;
  }
}
