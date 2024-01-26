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
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.Function;
import raw.runtime.truffle.runtime.function.NonClosure;

public final class MethodNode extends ExpressionNode {

  @CompilerDirectives.CompilationFinal private final Function function;

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private Object[] defaultArguments;

  @CompilerDirectives.CompilationFinal private NonClosure nonClosure;
  @Node.Children private final ExpressionNode[] defaultArgumentExps;

  public MethodNode(Function f, ExpressionNode[] defaultArgumentExps) {
    this.function = f;
    this.defaultArgumentExps = defaultArgumentExps;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    if (nonClosure == null) {
      if (defaultArguments == null) {
        int nArgs = defaultArgumentExps.length;
        defaultArguments = new Object[nArgs];
        for (int i = 0; i < nArgs; i++) {
          if (defaultArgumentExps[i] != null) {
            defaultArguments[i] = defaultArgumentExps[i].executeGeneric(virtualFrame);
          } else {
            defaultArguments[i] = null;
          }
        }
        nonClosure = new NonClosure(this.function, defaultArguments, virtualFrame);
      }
    }
    return nonClosure;
  }
}
