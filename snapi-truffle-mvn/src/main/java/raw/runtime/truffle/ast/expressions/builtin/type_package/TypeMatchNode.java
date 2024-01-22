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

package raw.runtime.truffle.ast.expressions.builtin.type_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.function.ClosureFactory;
import raw.runtime.truffle.runtime.or.OrObject;

public class TypeMatchNode extends ExpressionNode {
  @Child private ExpressionNode typeExp;

  @Children private final ExpressionNode[] closureExps;

  @Child
  Closure.ClosureExecuteOneNode closureExecuteOneNode =
      ClosureFactory.ClosureExecuteOneNodeGen.create();

  public TypeMatchNode(ExpressionNode child, ExpressionNode[] children) {
    this.typeExp = child;
    this.closureExps = children;
  }

  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    OrObject orType = (OrObject) this.typeExp.executeGeneric(frame);
    int index = orType.getIndex();
    Closure[] closures = new Closure[closureExps.length];
    for (int i = 0; i < closureExps.length; i++) {
      closures[i] = (Closure) closureExps[i].executeGeneric(frame);
    }
    return closureExecuteOneNode.execute(this, closures[index], orType.getValue());
  }
}
