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

import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.function.Function;

public final class MethodNode extends ClosureNode {

  private final String name;

  public MethodNode(String name, Function f, ExpressionNode[] defaultArgumentExps) {
    super(f, defaultArgumentExps);
    this.name = name;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    Closure closure = (Closure)super.executeGeneric(virtualFrame);
    RawContext.get(this).getFunctionRegistry().register(name, closure);
    return closure;
  }
}
